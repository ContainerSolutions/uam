package actors.jira;

import java.util.List;
import java.util.concurrent.TimeUnit;

import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http.MimeTypes;

public class CreateAccountActor extends UntypedActor {
	private static final ALogger logger = Logger.of(CreateAccountActor.class);

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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((applicationKeys == null) ? 0 : applicationKeys.hashCode());
			result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
			result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CreateJiraAccountMessage other = (CreateJiraAccountMessage) obj;
			if (applicationKeys == null) {
				if (other.applicationKeys != null)
					return false;
			} else if (!applicationKeys.equals(other.applicationKeys))
				return false;
			if (displayName == null) {
				if (other.displayName != null)
					return false;
			} else if (!displayName.equals(other.displayName))
				return false;
			if (emailAddress == null) {
				if (other.emailAddress != null)
					return false;
			} else if (!emailAddress.equals(other.emailAddress))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "CreateJiraAccountMessage [name=" + name + ", emailAddress=" + emailAddress + ", displayName="
					+ displayName + ", applicationKeys=" + applicationKeys + "]";
		}

	}

	public static Props props(WSClient client, String url) {
		return Props.create(CreateAccountActor.class, () -> new CreateAccountActor(client, url));
	}

	private final WSClient client;
	private final String url;

	public CreateAccountActor(WSClient client, String url) {
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