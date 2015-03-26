package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import com.hp.hpl.jena.query.Dataset;  
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;  
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.tdb.TDBFactory;  
import com.hp.hpl.jena.util.FileManager;

@Path("/vocabulary")
public class VocabularyAPI {
	
	/**
	 * Insert Vocabulary
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addVocabulary(String data) throws Exception {
		
		String returnString = null;
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		
		try {
			
			JSONObject partsData = new JSONObject(data);
			System.out.println( "jsonData: " + partsData.toString() );
			
			Dataset ds = TDBFactory.createDataset("VocabularyDataSet");
			Model model = ds.getDefaultModel();
			FileManager.get().readModel(model, "http://xmlns.com/foaf/spec/index.rdf");
			
			model.commit();
			model.close();
			ds.close(); 
			
			if( true ) {
				jsonObject.put("HTTP_CODE", "200");

				returnString = jsonArray.put(jsonObject).toString();
			} else {
				return Response.status(500).entity("Unable to enter Item").build();
			}
			
			System.out.println( "returnString: " + returnString );
			
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		
		return Response.ok(returnString).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnBrandParts(
				@QueryParam("queryStr") String queryStr)
				throws Exception {
		
		String returnString = null;
		JSONArray json = new JSONArray();
		
		try {
			
			if(queryStr == null) {
				return Response.status(400).entity("Error: please specify query for this search").build();
			}
			
			TextQuery.init();
			
	        // Base data
			Dataset ds1 = TDBFactory.createDataset("VocabularyDataSet");
			//Dataset ds1 = DatasetFactory.createMem() ;

	        // Define the index mapping 
	        EntityDefinition entDef = new EntityDefinition("uri", "text", RDFS.label.asNode()) ;

	        // Lucene, in memory.
	        Directory dir =  new RAMDirectory();
	        
	        // Join together into a dataset
	        Dataset ds = TextDatasetFactory.createLucene(ds1, dir, entDef, null) ;
	        
			
			//Dataset ds = DatasetFactory.assemble("text-config.ttl", "C:\\Users\\yexl\\Test\\test\\VocabularyDataSet") ;
			
	        Model model;
	        
	        ds.begin(ReadWrite.WRITE) ;
	        try {
	        	model = ds.getDefaultModel();
	    		//FileManager.get().readModel(model, "http://xmlns.com/foaf/spec/index.rdf");
	    		//FileManager.get().readModel(model, "http://dublincore.org/2012/06/14/dcterms.rdf");
	            ds.commit() ;
	        } finally { ds.end() ; }

			ResultSet res;
			
			String pre = StrUtils.strjoinNL
		            ("PREFIX text: <http://jena.apache.org/text#>"
		            , "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
		            , "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>") ;
			
			String qs = StrUtils.strjoinNL
		            ( "SELECT * "
		            , " { ?s text:query ('sdfsdafsd') "
		            , " }") ;
			
			//String qs = StrUtils.strjoinNL
	        //( "SELECT ?s "
	        //, " { ?s ?a ?b "
	        //, " }") ;
			
			ds.begin(ReadWrite.READ) ;
			Query q = QueryFactory.create(pre+"\n"+qs) ;
			QueryExecution qe = QueryExecutionFactory.create(q, model);
			try {
				res = qe.execSelect();
				while( res.hasNext()) {
					QuerySolution soln = res.next();
					Iterator<String> str = soln.varNames();
					RDFNode a = soln.get("?s");
					System.out.println(" " + a);
				}
			} finally {
				qe.close();
			}
			
			ds.end();
					
			ds.close(); 
			
			returnString = json.toString();
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		
		return Response.ok(returnString).build();
	}
}


