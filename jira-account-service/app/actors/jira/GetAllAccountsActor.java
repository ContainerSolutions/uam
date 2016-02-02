package actors.jira;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import akka.actor.Props;
import akka.actor.UntypedActor;
import models.JiraUser;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.libs.ws.WSClient;

public class GetAllAccountsActor extends UntypedActor {
	private static final ALogger logger = Logger.of(GetAllAccountsActor.class);

	public static class GetAllAccounts {
	}

	public static Props props(WSClient client, String url, String user, String password) {
		return Props.create(GetAllAccountsActor.class, () -> new GetAllAccountsActor(client, url, user, password));
	}

	private final WSClient client;
	private final String url;
	private final String user;
	private final String password;

	public GetAllAccountsActor(WSClient client, String url, String user, String password) {
		this.client = client;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof GetAllAccounts) {
			getAllAccounts();
		} else {
			logger.warn("unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void getAllAccounts() {
		// TODO move timeout to consul config
		sender().tell(
				client.url(url + "/rest/api/2/user/search?username=%25").setAuth(user, password).get().map(response -> {
					if (response.getStatus() != 200) {
						return response.getBody();
					}

					ArrayNode result = Json.newArray();
					response.asJson().forEach(jsonNode -> result.add(createJiraUserJson(jsonNode)));
					logger.info("Result: " + result.toString());

					return result.toString();
				}).get(10, TimeUnit.SECONDS), self());
	}

	private JsonNode createJiraUserJson(JsonNode jsonNode) {
		return Json.toJson(new JiraUser(jsonNode.findValue("name").asText(),
				jsonNode.findValue("emailAddress").asText(), jsonNode.findValue("displayName").asText()));
	}
}