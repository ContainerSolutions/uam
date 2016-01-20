package actors;

import java.util.List;
import java.util.concurrent.TimeUnit;

import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http.MimeTypes;

public class CreateJiraAccountActor extends UntypedActor {
	private static final ALogger logger = Logger.of(CreateJiraAccountActor.class);

	public static class CreateJiraAccountMessage {
		public String name;
		public String emailAddress;
		public String displayName;
		public List<String> applicationKeys;

		public CreateJiraAccountMessage() {
		}

		public CreateJiraAccountMessage(String name, String email, String displayName, List<String> applicationKeys) {
			this.name = name;
			this.emailAddress = email;
			this.displayName = displayName;
			this.applicationKeys = applicationKeys;
		}

		@Override
		public String toString() {
			return "CreateJiraAccountMessage [name=" + name + ", emailAddress=" + emailAddress + ", displayName="
					+ displayName + ", applicationKeys=" + applicationKeys + "]";
		}
	}

	public static Props props(WSClient client, String url) {
		return Props.create(CreateJiraAccountActor.class, () -> new CreateJiraAccountActor(client, url));
	}

	private final WSClient client;
	private final String url;

	public CreateJiraAccountActor(WSClient client, String url) {
		this.client = client;
		this.url = url;
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
		logger.info("Create jira account started: " + msg);

		sender().tell(client.url(url + "/user").setContentType(MimeTypes.JSON).setAuth("asirak", "secret")
				.post(Json.toJson(msg)).map(response -> {
					if (response.getStatus() != 201) {
						return response.getBody();
					}
					return "Ok";
				}).get(10, TimeUnit.SECONDS), self());
	}
}