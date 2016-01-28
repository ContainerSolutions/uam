package controllers;

import play.libs.F.Promise;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

    public Result index() {
        return ok("Your new application is ready.");
    }

    public Promise<Result> getAll() {
		return Promise.pure(badRequest("not yet implemented"));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> post() {
		return Promise.pure(badRequest("not yet implemented"));
	}

	public Promise<Result> delete(String id) {
		return Promise.pure(badRequest("not yet implemented"));
	}
}
