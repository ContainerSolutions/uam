package actors.jira;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.Props;
import akka.actor.UntypedActor;
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

	public static Props props(WSClient client, String url) {
		return Props.create(GetAccountActor.class, () -> new GetAccountActor(client, url));
	}

	private final WSClient client;
	private final String url;

	public GetAccountActor(WSClient client, String url) {
		this.client = client;
		this.url = url;
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
		sender().tell(client.url(url + "/user?username=" + msg.name).setAuth("asirak", "secret").get().map(response -> {
			if (response.getStatus() != 200) {
				return response.getBody();
			}
			JsonNode jsonNode = response.asJson();

			ObjectNode result = Json.newObject();
			result.set("name", jsonNode.findValue("name"));
			result.set("email", jsonNode.findValue("emailAddress"));
			result.set("displayName", jsonNode.findValue("displayName"));
			result.set("active", jsonNode.findValue("active"));

			logger.info("Result: " + result.toString());

			return result;
		}).get(10, TimeUnit.SECONDS), self());
	}
}