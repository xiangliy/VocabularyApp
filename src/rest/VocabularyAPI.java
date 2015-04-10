package rest;

import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import dao.VocabularyDAO;

@Path("/vocabulary")
public class VocabularyAPI {
	//add vocabulary
	@Path("/add")
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response addVocabulary(String data) throws Exception {
		
		String returnString = null;
		JSONObject jsonObject = new JSONObject();
		VocabularyDAO dao = new VocabularyDAO();
		
		try {
			
			JSONObject partsData = new JSONObject(data);
			System.out.println( "jsonData: " + partsData.toString() );
			
			int httpcode = dao.insertVocabulary(partsData.optString("name"), partsData.optString("prefix"), partsData.optString("location"));
			
			if( httpcode == 200 ) {
				jsonObject.put("HTTP_CODE", "200");
				jsonObject.put("Message", "Vocabulary has been added successfully");

				returnString = jsonObject.toString();
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
	
	//get a list of vocabulary name
	@Path("/getAllVocabulary")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response returnAllVocabulary() throws Exception {
		
		String returnString = null;
		Response rb = null;	
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		
		try {
			
			VocabularyDAO dao = new VocabularyDAO();
			
			Iterator<String> it = dao.getAllVocabularyName();
			
			json = getJsonFromObject(it);
			
			jsonObject.put("result", json);
			jsonObject.put("HTTP_CODE", "200");
			jsonObject.put("Message", "Get Vocabulary successfully");
			
			returnString = jsonObject.toString();
			
			rb = Response.ok(returnString).build();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return rb;
	}
	
	//search vocabulary based on keyword
	@Path("/search/{keyword}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchVocabulary(
				@PathParam("keyword") String keyword)
				throws Exception {
		
		String returnString = null;
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		
		try {
			VocabularyDAO dao = new VocabularyDAO();
			
			Iterator<String> it = dao.searchVocabulary(keyword);
			
			json = getJsonFromObject(it);
			
			jsonObject.put("result", json);
			jsonObject.put("HTTP_CODE", "200");
			
			returnString = jsonObject.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		
		return Response.ok(returnString).build();
	}
	
	//delete vocabulary based on vocabulary name
	@Path("/delete/{name}")
	@DELETE
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteVocabulary(@PathParam("name") String name) 
								throws Exception {
		
		int http_code;
		String returnString = null;
		JSONObject jsonObject = new JSONObject();
		VocabularyDAO dao = new VocabularyDAO();
		
		try {			
			http_code = dao.deleteVocabulary(name);
			
			if(http_code == 200) {
				jsonObject.put("HTTP_CODE", "200");
				jsonObject.put("MSG", "Vocabulary has been deleted successfully");
			} else {
				return Response.status(500).entity("Server was not able to process your request").build();
			}
			
			returnString = jsonObject.toString();
			
		} catch(Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		
		return Response.ok(returnString).build();
	}
	
	//update vocabulary with a new uri
	@POST
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateVocabulary(String incomingData) throws Exception {
		
		String returnString = null;
		VocabularyDAO dao = new VocabularyDAO();
		
		try {
			System.out.println("incomingData: " + incomingData);
			
			JSONObject partsData = new JSONObject(incomingData);
			System.out.println( "jsonData: " + partsData.toString() );
			
			int http_code = dao.updataVocabulary(partsData.optString("name"), partsData.optString("uri"));
			
			if( http_code == 200 ) {
				returnString = "data updated";
			} else {
				return Response.status(500).entity("Unable to process Item").build();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity("Server was not able to process your request").build();
		}
		
		return Response.ok(returnString).build();
	}
	
	private JSONArray getJsonFromObject(Iterator<String> it) throws JSONException
	{
	    JSONArray jsonArray = new JSONArray();
	    
	    while (it.hasNext()){
	    	String vocabulary = it.next();
	    	JSONObject formDetailsJson = new JSONObject();
	    	String[] strs = vocabulary.split("<>");
	    	

			if (strs.length > 1){
				formDetailsJson.put("name", strs[0]);
				formDetailsJson.put("value", vocabulary);
				jsonArray.put(formDetailsJson).toString();
			}
	    }
	    
	    return jsonArray;
	}
}


