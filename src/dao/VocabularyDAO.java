package dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.query.text.EntityDefinition;
import org.apache.jena.query.text.TextDatasetFactory;
import org.apache.jena.query.text.TextQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

public class VocabularyDAO {
	
	private static Dataset dataset = null;
	
	// get a dataset with lucene index, lucene index is used for search.
	private static Dataset getDataset(boolean addLuceneIndex) throws Exception {
		
		//if (dataset != null){
		//	return dataset;
		//}
		 	
		TextQuery.init();
			
		Dataset datasetPrev = TDBFactory.createDataset("C:\\VocabDataset");
			
	    // Define the index mapping 
	    EntityDefinition entDef = new EntityDefinition("uri", "text", RDFS.label.asNode());
	        
	    Directory dir = null;

	    try{
		    // Lucene, index file
		    dir =   FSDirectory.open(new File("index-directory"));
	    }
	    catch(Exception e){
	        	
	    }
			
	    // Join together into a dataset
	    dataset = TextDatasetFactory.createLucene(datasetPrev, dir, entDef, null) ;

		return dataset;
	}
	
	// add new vocabulary
	public int insertVocabulary(String name, String prefix, String location) throws Exception{
		
		if (name.isEmpty() ){
			return 500;
		}
		
		if (location.isEmpty()){
			return 500;
		}
		
		if (prefix.isEmpty()){
			return 500;
		}
		
		Dataset dataset = getDataset(true);
		
		Model model = ModelFactory.createDefaultModel();
		FileManager.get().readModel( model, location, "RDF/XML" );
		dataset.begin(ReadWrite.WRITE);
		
		dataset.addNamedModel(name + "<>" + prefix, model);
		
		dataset.commit();
		dataset.end();
		dataset.close();
		
		model.close();
		
		return 200;
	}
	
	//delete vocabulary based on vocabulary name
	public int deleteVocabulary(String name) throws Exception{
		
		if (name.isEmpty()){
			return 500;
		}
		
		Dataset dataset = getDataset(false);
		
		dataset.begin(ReadWrite.WRITE);
		
		if (dataset.containsNamedModel(name)){
			dataset.removeNamedModel(name);	
		}
		
		dataset.commit();
		dataset.end();
		dataset.close();

		
		return 200;
	}
	
	//update vocabulary with a new uri
	public int updataVocabulary(String name, String newUri) throws Exception{
		
		if (name.isEmpty()){
			return 500;
		}
		
		if (newUri.isEmpty()){
			return 500;
		}
		
		Dataset dataset = getDataset(false);
		
		dataset.begin(ReadWrite.WRITE);
		
		if (dataset.containsNamedModel(name)){
			Model model = ModelFactory.createDefaultModel();
			com.hp.hpl.jena.util.FileManager.get().readModel( model, newUri, "RDF/XML" );
			
			dataset.replaceNamedModel(name, model);
		}
		
		dataset.commit();
		dataset.end();
		dataset.close();

		return 200;
	}
	
	//search vocabulary based on keyword
	public Iterator<String> searchVocabulary(String keyword) throws Exception{
		
		List<String> resultList = new ArrayList<String>();
		
		Dataset dataset = getDataset(false);
		
		dataset.begin(ReadWrite.READ);
		
		Iterator<String> names = dataset.listNames();
		Map<String, String> strmap = new HashMap<String, String>();
		
		while(names.hasNext()){
			String name = names.next();
			String[] strs = name.split("<>");
			if (strs.length > 1){
				strmap.put(strs[1], strs[0]);
			}
		}
		
		ResultSet res;

		String pre = StrUtils.strjoinNL
	            ("PREFIX text: <http://jena.apache.org/text#>"
	            , "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
	            , "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>") ;
		
		String qs = StrUtils.strjoinNL
	            ( "SELECT DISTINCT ?s "
	            , "{ { ?s text:query ('*" + keyword + "*') }"
	            , "UNION { GRAPH ?g { ?s text:query ('*" + keyword + "*') } }} ") ;
		
		//qs = StrUtils.strjoinNL( "SELECT *{ { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }}") ;
        
		Query q = QueryFactory.create(pre+"\n"+qs) ;
		
		QueryExecution qe = QueryExecutionFactory.create(q, dataset);
		try {
			res = qe.execSelect();
			while( res.hasNext()) {
				QuerySolution soln = res.next();
				RDFNode a = soln.get("?s");
				
				resultList.add(strmap.get(a.asResource().getNameSpace()) + ":" + a.asResource().getLocalName());
			}
		} finally {
			qe.close();
		}
		
		dataset.commit();
		dataset.end();
		dataset.close();
		
		return resultList.iterator();
	}
	
	//get a list of vocabulary name
	public Iterator<String> getAllVocabularyName() throws Exception{
		
		Dataset dataset = getDataset(false);
		Iterator<String> it;
		
		dataset.begin(ReadWrite.READ);
		
		try {
			 it = dataset.listNames();
			 dataset.commit() ;
	    } finally { dataset.end() ; }
		
		dataset.close();

		return it;
	}
}
