package controllers;

import play.*;
import play.libs.F.Promise;
import play.mvc.*;

public class Application extends Controller {

    public Result index() {
        return ok("Your new application is ready.");
    }
    
    public Promise<Result> getAll() {
        return Promise.pure(internalServerError("not implemented"));
    }
    
    public Promise<Result> get(String name) {
    	return Promise.pure(internalServerError("not implemented"));
    }
    
    @BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> post() {
    	return Promise.pure(internalServerError("not implemented"));
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Promise<Result> put(String name) {
    	return Promise.pure(internalServerError("not implemented"));
    }
    
    public Promise<Result> delete(String name) {
    	return Promise.pure(internalServerError("not implemented"));
    }
    
}
