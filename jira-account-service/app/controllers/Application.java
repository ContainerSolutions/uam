package controllers;

import static akka.pattern.Patterns.ask;

import com.google.inject.Inject;

import actors.JiraAccountActor;
import actors.JiraAccountActorProtocol.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import play.*;
import play.libs.Json;
import play.libs.F.Promise;
import play.mvc.*;

public class Application extends Controller {
	private static final String URL_PARAM = "orientdb.url";
	private static final int TIMEOUT = 10000;

	private final ActorRef accountActor;

	@Inject
	public Application(ActorSystem system, Configuration configuration) {
		accountActor = system.actorOf(JiraAccountActor.props(configuration.getString(URL_PARAM)));
	}

	public Result index() {
		return ok("Your new application is ready.");
	}

	public Promise<Result> getAll() {
		return Promise.wrap(ask(accountActor, new GetAllAccounts(), TIMEOUT))
				.map(response -> ok(Json.toJson(response)));
	}

	public Promise<Result> get(String name) {
		return Promise.wrap(ask(accountActor, new GetAccount(name), TIMEOUT))
				.map(response -> ok(Json.toJson(response)));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> post() {
		return Promise.wrap(ask(accountActor, Json.fromJson(request().body().asJson(), CreateAccount.class), TIMEOUT))
				.map(response -> ok((String) response));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> put(String name) {
		return Promise.pure(internalServerError("not implemented"));
	}

	public Promise<Result> delete(String name) {
		return Promise.wrap(ask(accountActor, new RemoveAccount(name), TIMEOUT))
				.map(response -> ok(Json.toJson(response)));
	}

}
