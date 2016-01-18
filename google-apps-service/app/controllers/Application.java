package controllers;

import play.Configuration;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
     private final String DIRECTORY_REST = Configuration.root().getString("google.directory.rest.url") ;
     public Result index() {
	     
	     System.out.println("Here is url : " + DIRECTORY_REST);
		String response = WS.client().url(DIRECTORY_REST + "users/liz@example.com").get().get(5000).asJson().toString();
		return ok(Json.toJson("Here Is response from Google : " + response));
    }

}
