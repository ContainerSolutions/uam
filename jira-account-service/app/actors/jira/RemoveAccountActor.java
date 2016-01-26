package actors.jira;

import java.util.concurrent.TimeUnit;

import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;
import play.libs.ws.WSClient;

public class RemoveAccountActor extends UntypedActor {
	private static final ALogger logger = Logger.of(RemoveAccountActor.class);

	public static class RemoveAccount {
		public String name;

		public RemoveAccount() {
		}

		public RemoveAccount(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "GetAccount [name=" + name + "]";
		}
	}

	public static Props props(WSClient client, String url, String user, String password) {
		return Props.create(RemoveAccountActor.class, () -> new RemoveAccountActor(client, url, user, password));
	}

	private final WSClient client;
	private final String url;
	private final String user;
	private final String password;

	public RemoveAccountActor(WSClient client, String url, String user, String password) {
		this.client = client;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof RemoveAccount) {
			removeAccount((RemoveAccount) msg);
		} else {
			logger.warn("unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void removeAccount(RemoveAccount msg) {
		sender().tell(
				client.url(url + "/user?username=" + msg.name).setAuth(user, password).delete().map(response -> {
					if (response.getStatus() != 204) {
						return response.getBody();
					}

					return "Ok";
				}).get(10, TimeUnit.SECONDS), self());
	}
}