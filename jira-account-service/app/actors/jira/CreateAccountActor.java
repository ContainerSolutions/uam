package actors.jira;

import java.util.List;

import actors.repository.CreateAccountVertexActor.CreateVertex;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status.Failure;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http.MimeTypes;
import scala.concurrent.Future;

public class CreateAccountActor extends UntypedActor {
	private static final ALogger logger = Logger.of(CreateAccountActor.class);

	public static class CreateJiraAccountMessage {
		public CreateVertex orientDbMessage;
		public JiraPostMessage jiraMessage;

		public CreateJiraAccountMessage(CreateVertex orientDbMessage, JiraPostMessage jiraMessage) {
			this.orientDbMessage = orientDbMessage;
			this.jiraMessage = jiraMessage;
		}
	}
	
	public static class JiraPostMessage {
		public String name;
		public String emailAddress;
		public String displayName;
		public List<String> applicationKeys;

		public JiraPostMessage(String name, String emailAddress, String displayName, List<String> applicationKeys) {
			this.name = name;
			this.emailAddress = emailAddress;
			this.displayName = displayName;
			this.applicationKeys = applicationKeys;
		}

		@Override
		public String toString() {
			return "JiraPostMessage [name=" + name + ", emailAddress=" + emailAddress + ", displayName=" + displayName
					+ ", applicationKeys=" + applicationKeys + "]";
		}
	}

	public static Props props(ActorRef next, WSClient client, String url, String user, String password) {
		return Props.create(CreateAccountActor.class, () -> new CreateAccountActor(next, client, url, user, password));
	}

	private final ActorRef next;
	private final WSClient client;
	private final String url;
	private final String user;
	private final String password;

	public CreateAccountActor(ActorRef next, WSClient client, String url, String user, String password) {
		this.next = next;
		this.client = client;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof CreateJiraAccountMessage) {
			createJiraAccount((CreateJiraAccountMessage) msg);
		} else if (msg instanceof Failure) {
			sender().tell(msg.toString(), self());
		} else {
			logger.warn("unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void createJiraAccount(CreateJiraAccountMessage msg) {
		logger.info("Create jira account started: " + msg.jiraMessage);

		Future<CreateVertex> future = client.url(url + "/user").setContentType(MimeTypes.JSON).setAuth(user, password).post(Json.toJson(msg.jiraMessage))
				.map(response -> {
					if (response.getStatus() != 201) {
						logger.error(String.format("Jira account [%s] was not created: %s", msg.jiraMessage.name, response.getBody()));
						throw new RuntimeException(response.getBody());
					}

					logger.info(String.format("Jira account [%s] created", msg.jiraMessage.name));
					return msg.orientDbMessage;
				}).wrapped();
		Patterns.pipe(future, context().dispatcher().prepare()).pipeTo(next, sender());
	}
}