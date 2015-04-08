package dao;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.query.text.EntityDefinition;
import org.apache.jena.query.text.TextDatasetFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

public class VocabularyDAO {
	
	private static Dataset dataset = null;
	
	// get a dataset with lucene index, lucene index is used for search.
	private static Dataset getDataset() throws Exception {
		
		if (dataset != null){
			return dataset;
		}
		
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
        Dataset dataset = TextDatasetFactory.createLucene(datasetPrev, dir, entDef, null) ;
		
		return dataset;
	}
	
	// add new vocabulary
	public int insertVocabulary(String name, String uri) throws Exception{
		
		if (name.isEmpty() ){
			return 500;
		}
		
		if (uri.isEmpty()){
			return 500;
		}
		
		Dataset dataset = getDataset();
		
		Model model = ModelFactory.createDefaultModel();
		FileManager.get().readModel( model, uri, "RDF/XML" );
		dataset.addNamedModel(name, model);
		
		model.close();
		
		return 200;
	}
	
	//delete vocabulary based on vocabulary name
	public int deleteVocabulary(String name) throws Exception{
		
		if (name.isEmpty()){
			return 500;
		}
		
		Dataset dataset = getDataset();
		
		if (dataset.containsNamedModel(name)){
			dataset.removeNamedModel(name);	
		}
		
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
		
		Dataset dataset = getDataset();
		
		if (dataset.containsNamedModel(name)){
			Model model = ModelFactory.createDefaultModel();
			FileManager.get().readModel( model, newUri, "RDF/XML" );
			
			dataset.replaceNamedModel(name, model);
		}
		
		return 200;
	}
	
	//search vocabulary based on keyword
	public Iterator<String> searchVocabulary(String keyword) throws Exception{
		
		List<String> resultList = new ArrayList<String>();
		
		Dataset dataset = getDataset();
		
		ResultSet res;
		
		String pre = StrUtils.strjoinNL
	            ("PREFIX text: <http://jena.apache.org/text#>"
	            , "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
	            , "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>") ;
		
		String qs = StrUtils.strjoinNL
	            ( "SELECT DISTINCT ?s "
	            , "{ { ?s text:query ('*f*') }"
	            , "UNION { GRAPH ?g { ?s text:query ('*f*') } }} ") ;
		
		//qs = StrUtils.strjoinNL( "SELECT *{ { ?s ?p ?o } UNION { GRAPH ?g { ?s ?p ?o } }}") ;
        
		Query q = QueryFactory.create(pre+"\n"+qs) ;
		
		QueryExecution qe = QueryExecutionFactory.create(q, dataset);
		try {
			res = qe.execSelect();
			while( res.hasNext()) {
				QuerySolution soln = res.next();
				//Iterator<String> str = soln.varNames();
				RDFNode a = soln.get("?s");
				resultList.add(a.toString());
			}
			
			return resultList.iterator();
		} finally {
			qe.close();
		}
	}
	
	//get a list of vocabulary name
	public Iterator<String> getAllVocabularyName() throws Exception{
		
		Dataset dataset = getDataset();
		
		return dataset.listNames();
	}
}
