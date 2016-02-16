package actors.jira;

import java.util.Arrays;
import java.util.List;

import actors.repository.CreateAccountVertexActor.CreateVertex;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import models.JiraUser;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http.MimeTypes;
import scala.concurrent.Future;

public class CreateAccountActor extends UntypedActor {
	private static final ALogger logger = Logger.of(CreateAccountActor.class);
	private static final String JIRA_CORE_KEY = "jira-core";

	public static class CreateJiraAccountMessage {
		public JiraUser jiraUser;

		public CreateJiraAccountMessage(JiraUser jiraUser) {
			this.jiraUser = jiraUser;
		}
	}

	public static Props props(ActorSystem system, ActorRef next, WSClient client, String url, String user, String password) {
		return Props.create(CreateAccountActor.class, () -> new CreateAccountActor(system, next, client, url, user, password));
	}

	private final ActorSystem system;
	private final ActorRef next;
	private final WSClient client;
	private final String url;
	private final String user;
	private final String password;

	
	public CreateAccountActor(ActorSystem system, ActorRef next, WSClient client, String url, String user, String password) {
		this.system = system;
		this.next = next;
		this.client = client;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof CreateJiraAccountMessage) {
			createJiraAccount((CreateJiraAccountMessage) msg);
		} else {
			logger.warn("unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void createJiraAccount(CreateJiraAccountMessage msg) {
		logger.info("Create jira account started: " + msg.jiraUser);
		Future<CreateVertex> future = client.url(url + "/rest/api/2/user").setContentType(MimeTypes.JSON)
				.setAuth(user, password).post(Json.toJson(new JiraPostBody(msg.jiraUser))).map(response -> {
					if (response.getStatus() != 201) {
						logger.error(String.format("Jira account [%s] was not created: %s", msg.jiraUser.id,
								response.getBody()));
						throw new RuntimeException(response.getBody());
					}

					logger.info(String.format("Jira account [%s] created", msg.jiraUser.id));
					return new CreateVertex(msg.jiraUser.id);
				}).wrapped();
		Patterns.pipe(future, system.dispatcher()).pipeTo(next, sender());
	}

	public static class JiraPostBody {
		public String name;
		public String emailAddress;
		public String displayName;
		public List<String> applicationKeys;

		public JiraPostBody() {}

		public JiraPostBody(JiraUser jiraUser) {
			this.name = jiraUser.id;
			this.emailAddress = jiraUser.email;
			this.displayName = jiraUser.displayName;
			this.applicationKeys = Arrays.asList(JIRA_CORE_KEY);
		}
	}
}