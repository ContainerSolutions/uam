package controllers;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import actors.CreateJiraAccountActor;
import actors.CreateJiraAccountActor.CreateJiraAccountMessage;
import actors.CreateJiraAccountVertexActor;
import actors.CreateJiraAccountVertexActor.CreateVertex;
import actors.GetUserVertexActor;
import actors.GetUserVertexActor.GetUserData;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;
import play.Configuration;
import play.Logger;
import play.Logger.ALogger;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

@Singleton
public class Application extends Controller {
	private static final Timeout TIMEOUT = new Timeout(Duration.create(50, TimeUnit.SECONDS));
	private static final ALogger logger = Logger.of(Application.class);

	private final ActorRef userVertexActor;
	private final ActorRef createActor;
	private final ActorRef createVertexActor;

	@Inject
	public Application(ActorSystem system, Configuration configuration) {
		userVertexActor = system.actorOf(GetUserVertexActor.props(configuration.getString("orientdb.url")));
		createActor = system.actorOf(CreateJiraAccountActor.props(WS.client(), configuration.getString("jira.url")));
		createVertexActor = system.actorOf(CreateJiraAccountVertexActor.props(configuration.getString("orientdb.url")));
	}

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
	public Promise<Result> post() throws Exception {
		JsonNode request = request().body().asJson();
		logger.info("Create Jira account: " + request);

		CreateVertex createVertex = Json.fromJson(request, CreateVertex.class);
		CreateJiraAccountMessage createMessage = (CreateJiraAccountMessage) Await.result(
				ask(userVertexActor, new GetUserData(createVertex.userId), TIMEOUT), TIMEOUT.duration());

		String status = Await.result(ask(createActor, createMessage, TIMEOUT), TIMEOUT.duration()).toString();
		if (!StringUtils.equals("Ok", status)) {
			return Promise.pure(badRequest(status));
		}

		return Promise.wrap(ask(createVertexActor, createVertex, TIMEOUT)).map(response -> ok((String) response));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> put(String name) {
		return Promise.pure(internalServerError("not implemented"));
	}

	public Promise<Result> delete(String name) {
		return Promise.pure(internalServerError("not implemented"));
	}

}
