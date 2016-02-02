package actors.jira;

import actors.repository.RemoveAccountVertexActor.RemoveVertex;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import play.Logger;
import play.Logger.ALogger;
import play.libs.ws.WSClient;
import scala.concurrent.Future;

public class RemoveAccountActor extends UntypedActor {
	private static final ALogger logger = Logger.of(RemoveAccountActor.class);

	public static class RemoveAccount {
		public final String name;

		public RemoveAccount(String name) {
			this.name = name;
		}
	}

	public static Props props(ActorRef next, WSClient client, String url, String user, String password) {
		return Props.create(RemoveAccountActor.class, () -> new RemoveAccountActor(next, client, url, user, password));
	}

	private final ActorRef next;
	private final WSClient client;
	private final String url;
	private final String user;
	private final String password;

	public RemoveAccountActor(ActorRef next, WSClient client, String url, String user, String password) {
		this.next = next;
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
		logger.info(String.format("Remove Jira account [%s] started", msg.name));
		Future<RemoveVertex> future = client.url(url + "/rest/api/2/user?username=" + msg.name).setAuth(user, password).delete()
				.map(response -> {
					if (response.getStatus() != 204) {
						logger.error(String.format("Jira account [%s] was not removed: %s", msg.name, response.getBody()));
						throw new RuntimeException(response.getBody());
					}

					logger.info(String.format("Jira account [%s] removed", msg.name));
					return new RemoveVertex(msg.name);
				}).wrapped();
		Patterns.pipe(future, context().dispatcher().prepare()).pipeTo(next, sender());
	}
}