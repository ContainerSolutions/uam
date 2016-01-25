package actors.jira;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.ArrayNode;

import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http.MimeTypes;

public class GetAllAccountsActor extends UntypedActor {
	private static final ALogger logger = Logger.of(GetAllAccountsActor.class);

	public static class GetAllAccounts {
	}

	public static Props props(WSClient client, String url) {
		return Props.create(GetAllAccountsActor.class, () -> new GetAllAccountsActor(client, url));
	}

	private final WSClient client;
	private final String url;

	public GetAllAccountsActor(WSClient client, String url) {
		this.client = client;
		this.url = url;
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
		sender().tell(client.url(url + "/user/search?username=%25").setContentType(MimeTypes.JSON).setAuth("asirak", "secret")
				.get().map(response -> {
					if (response.getStatus() != 200) {
						return response.getBody();
					}
					
					ArrayNode result = Json.newArray();
				    response.asJson().forEach(jsonNode -> result.add(jsonNode.findValue("name")));
					logger.info("Result: " + result.toString());

					return result;
				}).get(10, TimeUnit.SECONDS), self());
	}
}