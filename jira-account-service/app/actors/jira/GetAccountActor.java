package actors.jira;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.Props;
import akka.actor.UntypedActor;
import models.JiraUser;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.libs.ws.WSClient;

public class GetAccountActor extends UntypedActor {
	private static final ALogger logger = Logger.of(GetAccountActor.class);

	public static class GetAccount {
		public String name;

		public GetAccount() {
		}

		public GetAccount(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "GetAccount [name=" + name + "]";
		}
	}

	public static Props props(WSClient client, String url, String user, String password) {
		return Props.create(GetAccountActor.class, () -> new GetAccountActor(client, url, user, password));
	}

	private final WSClient client;
	private final String url;
	private final String user;
	private final String password;

	public GetAccountActor(WSClient client, String url, String user, String password) {
		this.client = client;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof GetAccount) {
			getAccount((GetAccount) msg);
		} else {
			logger.warn("unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void getAccount(GetAccount msg) {
		sender().tell(client.url(url + "/rest/api/2/user?username=" + msg.name).setAuth(user, password).get()
				.map(response -> {
					if (response.getStatus() != 200) {
						logger.error("Failed to get user data from Jira: " + response.getBody());
						return new Exception(response.getBody());
					}
					JsonNode jsonNode = response.asJson();

					JsonNode result = Json.toJson(new JiraUser(jsonNode.findValue("name").asText(),
							jsonNode.findValue("emailAddress").asText(), jsonNode.findValue("displayName").asText()));

					logger.info("Result: " + result.toString());

					return result.toString();
				}).get(10, TimeUnit.SECONDS), self());
	}
}