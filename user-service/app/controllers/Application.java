package controllers;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import actors.GetUsersActor;
import actors.GetUsersActor.GetUsersMessage;
import actors.RemoveUserActor;
import actors.RemoveUserActor.RemoveUserMessage;
import actors.CreateUserActor;
import actors.CreateUserActor.CreateUserMessage;
import actors.UpdateUserActor;
import actors.UpdateUserActor.UpdateUserMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import models.User;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

@Singleton
public class Application extends Controller {

	private static final Timeout TIMEOUT = new Timeout(10, TimeUnit.SECONDS);
	private final ActorRef getUsersActor;
	private final ActorRef createUserActor;
	private final ActorRef updateUserActor;
	private final ActorRef removeUserActor;

	@Inject
	public Application(ActorSystem system) {
		OrientGraphFactory graphFactory = new OrientGraphFactory("remote:192.168.99.100:32782/UserAccessControl");
		getUsersActor = system.actorOf(Props.create(GetUsersActor.class, graphFactory));
		createUserActor = system.actorOf(Props.create(CreateUserActor.class, graphFactory));
		updateUserActor = system.actorOf(Props.create(UpdateUserActor.class, graphFactory));
		removeUserActor = system.actorOf(Props.create(RemoveUserActor.class, graphFactory));
	}

	public Result index() {
		return ok("Your new application is ready.");
	}

	public Promise<Result> getAll() {
		return Promise.wrap(Patterns.ask(getUsersActor, new GetUsersMessage(), TIMEOUT))
				.map(response -> ok(response.toString()));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> post() {
		return Promise.wrap(Patterns.ask(createUserActor, new CreateUserMessage(Json.fromJson(request().body().asJson(), User.class)), TIMEOUT))
				.map(response -> StringUtils.equals("Ok", response.toString()) ? created()
						: internalServerError(response.toString()));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> update(String id) {
		return Promise.wrap(Patterns.ask(updateUserActor, new UpdateUserMessage(id, Json.fromJson(request().body().asJson(), User.class)), TIMEOUT))
				.map(response -> StringUtils.equals("Ok", response.toString()) ? ok()
						: internalServerError(response.toString()));
	}

	public Promise<Result> delete(String id) {
		return Promise.wrap(Patterns.ask(removeUserActor, new RemoveUserMessage(id), TIMEOUT))
				.map(response -> StringUtils.equals("Ok", response.toString()) ? noContent()
						: internalServerError(response.toString()));
	}
}
