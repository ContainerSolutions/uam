package controllers;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import actors.jira.CreateAccountActor;
import actors.jira.GetAllAccountsActor;
import actors.jira.GetAccountActor;
import actors.jira.RemoveAccountActor;
import actors.jira.CreateAccountActor.CreateJiraAccountMessage;
import actors.jira.GetAllAccountsActor.GetAllAccounts;
import actors.jira.GetAccountActor.GetAccount;
import actors.jira.RemoveAccountActor.RemoveAccount;
import actors.repository.CreateAccountVertexActor;
import actors.repository.GetUserVertexActor;
import actors.repository.RemoveAccountVertexActor;
import actors.repository.CreateAccountVertexActor.CreateVertex;
import actors.repository.GetUserVertexActor.GetUserData;
import actors.repository.RemoveAccountVertexActor.RemoveVertex;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import configuration.MantlConfigFactory;
import configuration.ServiceAccountCredentials;
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
	private static final Timeout TIMEOUT = new Timeout(Duration.create(10, TimeUnit.SECONDS));
	private static final ALogger logger = Logger.of(Application.class);
	private static final String serviceName = "jiraservice";

	private static final String consulUrlKey = "consul.url";
	private static final String vaultUrlKey = "vault/url";
	private static final String vaultUserKey = "vault/user";
	private static final String vaultPassKey = "vault/pass";

	private static final String orientDbUrlKey = "jiraservice/orientdb/url";
	private static final String orientDbKey = "jiraservice/orientdb";
	private static final String jiraUrlKey = "jiraservice/jira/url";
	private static final String jiraKey = "jiraservice/jira";

	private final ActorRef userVertexActor;
	private final ActorRef createActor;
	private final ActorRef createVertexActor;

	private final ActorRef getAllActor;
	private final ActorRef getAccountActor;

	private final ActorRef removeAccountActor;
	private final ActorRef removeAccountVertexActor;

	@Inject
	public Application(ActorSystem system) {
		Configuration configuration = MantlConfigFactory.load(consulUrlKey, serviceName);
		String token = MantlConfigFactory.generateToken(configuration.getString(vaultUrlKey), configuration.getString(vaultUserKey), configuration.getString(vaultPassKey));

		ServiceAccountCredentials orientDbCredentials = MantlConfigFactory.getCredentials(configuration.getString(vaultUrlKey), token, orientDbKey);
		OrientGraphFactory graphFactory = new OrientGraphFactory(configuration.getString(orientDbUrlKey), orientDbCredentials.getUser(), orientDbCredentials.getPassword()).setupPool(1, 10);
		
		userVertexActor = system.actorOf(Props.create(GetUserVertexActor.class, graphFactory));
		createVertexActor = system.actorOf(Props.create(CreateAccountVertexActor.class, graphFactory));
		removeAccountVertexActor = system.actorOf(Props.create(RemoveAccountVertexActor.class, graphFactory));
		
		ServiceAccountCredentials jiraCredentials = MantlConfigFactory.getCredentials(configuration.getString(vaultUrlKey), token, jiraKey);
		createActor = system.actorOf(CreateAccountActor.props(WS.client(), configuration.getString(jiraUrlKey), jiraCredentials.getUser(), jiraCredentials.getPassword()));

		getAllActor = system.actorOf(GetAllAccountsActor.props(WS.client(), configuration.getString(jiraUrlKey), jiraCredentials.getUser(), jiraCredentials.getPassword()));
		getAccountActor = system.actorOf(GetAccountActor.props(WS.client(), configuration.getString(jiraUrlKey), jiraCredentials.getUser(), jiraCredentials.getPassword()));
		removeAccountActor = system.actorOf(RemoveAccountActor.props(WS.client(), configuration.getString(jiraUrlKey), jiraCredentials.getUser(), jiraCredentials.getPassword()));
	}

	public Result index() {
		return ok("Welcome");
	}

	public Promise<Result> getAll() {
		return Promise.wrap(ask(getAllActor, new GetAllAccounts(), TIMEOUT)).map(response -> ok((String) response));
	}

	public Promise<Result> get(String name) {
		return Promise.wrap(ask(getAccountActor, new GetAccount(name), TIMEOUT))
				.map(response -> ok((String) response));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> post() throws Exception {
		JsonNode request = request().body().asJson();
		logger.info("Create Jira account: " + request);

		CreateVertex createVertex = Json.fromJson(request, CreateVertex.class);
		CreateJiraAccountMessage createMessage = (CreateJiraAccountMessage) Await
				.result(ask(userVertexActor, new GetUserData(createVertex.userId), TIMEOUT), TIMEOUT.duration());

		String status = Await.result(ask(createActor, createMessage, TIMEOUT), TIMEOUT.duration()).toString();
		if (!StringUtils.equals("Ok", status)) {
			return Promise.pure(badRequest(status));
		}

		return Promise.wrap(ask(createVertexActor, createVertex, TIMEOUT)).map(response -> ok((String) response));
	}

	public Promise<Result> delete(String name) throws Exception {
		String status = Await.result(ask(removeAccountActor, new RemoveAccount(name), TIMEOUT), TIMEOUT.duration())
				.toString();
		if (!StringUtils.equals("Ok", status)) {
			return Promise.pure(internalServerError(status));
		}
		return Promise.wrap(ask(removeAccountVertexActor, new RemoveVertex(name), TIMEOUT))
				.map(response -> ok((String) response));
	}
}
