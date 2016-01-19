package controllers;

import play.Configuration;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import play.mvc.BodyParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Application extends Controller {
     private final String DIRECTORY_REST = Configuration.root().getString("google.directory.rest.url") ;
     // add parameter for email information  liz@example.com
     public Result getUserInfo(String email) {

	     System.out.println("Here is url : " + DIRECTORY_REST + " and parameter : " + email );
		String response = WS.client().url(DIRECTORY_REST + "users/" + email).get().get(5000).asJson().toString();
		return ok(Json.toJson("Here Is response from Google : " + response));
    }
	
    @BodyParser.Of(BodyParser.Json.class)
    public Result insert(){
		JsonNode body = request().body().asJson();
	    String firstName = body.findPath("firstName").textValue();			
	    String lastName = body.findPath("lastName").textValue();
	    String email = body.findPath("email").textValue();
	    String password = body.findPath("password").textValue();
	    //Formulate Json for g directory
	    ObjectNode gbody = Json.newObject();
	    ObjectNode gname = Json.newObject();

	    gname.put("familyName", lastName);
	    gname.put("givenName", firstName);
	    gbody.set("name", gname);
	    gbody.put("password", password);
	    gbody.put("primaryEmail", email);

	    String status = WS.client().url(DIRECTORY_REST + "users/").post(gbody.toString()).get(5000).getStatusText();

	    //Showing current status -  need to Authorise
	    return ok(status);
	}

}
