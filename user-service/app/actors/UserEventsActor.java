package actors;

import akka.actor.UntypedActor;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import models.User;
import models.UserEvent;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

public class UserEventsActor extends UntypedActor {
	private static final ALogger logger = Logger.of(UserEventsActor.class);

	public static class GetUserEventsMessage {
		public String id;

		public GetUserEventsMessage(String id) {
			this.id = id;
		}
	}

	private final OrientGraphFactory graphFactory;

	public UserEventsActor(OrientGraphFactory graphFactory) {
		this.graphFactory = graphFactory;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof GetUserEventsMessage) {
			getUserEvents(((GetUserEventsMessage) msg).id);
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void getUserEvents(String id) {
		logger.info("Get user events started");
		OrientGraph graph = graphFactory.getTx();
		try {
			List<UserEvent> data = new ArrayList<>();
			graph.getVertices("AuditLog.user_id", id).forEach(userEvent -> {
					data.add(new UserEvent(
							userEvent.getProperty("datetime"),
							userEvent.getProperty("application"),
							userEvent.getProperty("action"),
							userEvent.getProperty("executor"),
							userEvent.getProperty("request_number")));
			});

			sender().tell(Json.toJson(data), self());
		} catch (Exception e) {
			logger.error("Failed to get user events", e);
			sender().tell(e, self());
		} finally {
			graph.shutdown();
		}
	}
}