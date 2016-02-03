package controllers;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import actors.AuditLogsActor;
import actors.jira.CreateAccountActor;
import actors.jira.CreateAccountActor.CreateJiraAccountMessage;
import actors.jira.GetAccountActor;
import actors.jira.GetAccountActor.GetAccount;
import actors.jira.GetAllAccountsActor;
import actors.jira.GetAllAccountsActor.GetAllAccounts;
import actors.jira.RemoveAccountActor;
import actors.jira.RemoveAccountActor.RemoveAccount;
import actors.repository.CreateAccountVertexActor;
import actors.repository.RemoveAccountVertexActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import configuration.MantlConfigFactory;
import configuration.ServiceAccountCredentials;
import models.JiraUser;
import play.Configuration;
import play.libs.F.Promise;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;

@Singleton
public class Application extends Controller
{
	private static final Timeout TIMEOUT = new Timeout(Duration.create(10, TimeUnit.SECONDS));
	private static final String serviceName = "jiraservice";

	private static final String consulUrlKey = "consul.url";
	private static final String vaultUrlKey = "jiraservice/vault/url";
	private static final String vaultUserKey = "jiraservice/vault/user";
	private static final String vaultPassKey = "jiraservice/vault/pass";

	private static final String orientDbUrlKey = "jiraservice/orientdb/url";
	private static final String orientDbKey = "jiraservice/orientdb";
	private static final String jiraUrlKey = "jiraservice/jira/url";
	private static final String jiraKey = "jiraservice/jira";

	private final ActorRef createActor;

	private final ActorRef getAllActor;
	private final ActorRef getAccountActor;

	private final ActorRef removeAccountActor;
	private final ActorRef auditLogsActor;

	// TODO move actors init to factory
	@Inject
	public Application(ActorSystem system)
	{
		Configuration configuration = MantlConfigFactory.load(consulUrlKey, serviceName);
		// TODO use Vault app-id auth
//		String token = MantlConfigFactory.generateToken(configuration.getString(vaultUrlKey),
//				configuration.getString(vaultUserKey), configuration.getString(vaultPassKey));

//		ServiceAccountCredentials orientDbCredentials = MantlConfigFactory
//				.getCredentials(configuration.getString(vaultUrlKey), token, orientDbKey);
		OrientGraphFactory graphFactory = new OrientGraphFactory(configuration.getString(orientDbUrlKey)).setupPool(1, 10);

//		ServiceAccountCredentials jiraCredentials = MantlConfigFactory
//				.getCredentials(configuration.getString(vaultUrlKey), token, jiraKey);
		ActorRef createVertexActor = system.actorOf(Props.create(CreateAccountVertexActor.class, graphFactory));
		createActor = system.actorOf(CreateAccountActor.props(createVertexActor, WS.client(),
		                             configuration.getString(jiraUrlKey), "admin", "secret"));

		getAllActor = system.actorOf(GetAllAccountsActor.props(WS.client(), configuration.getString(jiraUrlKey),
		                             "admin", "secret"));
		getAccountActor = system.actorOf(GetAccountActor.props(WS.client(), configuration.getString(jiraUrlKey),
		                                 "admin", "secret"));

		ActorRef removeAccountVertexActor = system.actorOf(Props.create(RemoveAccountVertexActor.class, graphFactory));
		removeAccountActor = system.actorOf(RemoveAccountActor.props(removeAccountVertexActor, WS.client(),
		                                    configuration.getString(jiraUrlKey), "admin", "secret"));
		auditLogsActor = system.actorOf(Props.create(AuditLogsActor.class, graphFactory));


	}

	// TODO add error handling for correct response statuses
	public Promise<Result> getAll()
	{
		return Promise.wrap(ask(getAllActor, new GetAllAccounts(), TIMEOUT)).map(response -> ok(response.toString()));
	}

	public Promise<Result> get(String name)
	{
		return Promise.wrap(ask(getAccountActor, new GetAccount(name), TIMEOUT))
		       .map(response -> ok(response.toString()));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> post() throws Exception
	{
		JiraUser user = Json.fromJson(request().body().asJson(), JiraUser.class);
		auditLogsActor.tell(new AuditLogsActor.SaveAuditLog(
		                        1L,
		                        user.id,
		                        "Jira",
		                        "admin",
		                        "create"
		                    ),
		                    null
		                   );


		return Promise.wrap(ask(createActor, new CreateJiraAccountMessage(user), TIMEOUT))
		       .map(response -> StringUtils.equals("Ok", response.toString()) ? created()
		            : internalServerError(response.toString()));
	}

	public Promise<Result> delete(String name) throws Exception
	{
		auditLogsActor.tell(new AuditLogsActor.SaveAuditLog(
		                        1L,
		                        name,
		                        "Jira",
		                        "admin",
		                        "delete"
		                    ),
		                    null
		                   );


		return Promise.wrap(ask(removeAccountActor, new RemoveAccount(name), TIMEOUT))
		       .map(response -> StringUtils.equals("Ok", response.toString()) ? noContent()
		            : internalServerError(response.toString()));
	}
}
