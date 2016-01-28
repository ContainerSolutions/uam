package actors;

import java.util.ArrayList;
import java.util.List;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import akka.actor.UntypedActor;
import models.User;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;

public class GetUsersActor extends UntypedActor {
	private static final ALogger logger = Logger.of(GetUsersActor.class);

	public static class GetUsersMessage {
	}

	private final OrientGraphFactory graphFactory;

	public GetUsersActor(OrientGraphFactory graphFactory) {
		this.graphFactory = graphFactory;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof GetUsersMessage) {
			getUsers((GetUsersMessage) msg);
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void getUsers(GetUsersMessage msg) {
		logger.info("Get users started");
		OrientGraph graph = graphFactory.getTx();
		try {
			List<User> data = new ArrayList<>();
			graph.getVerticesOfClass("User").forEach(user -> data.add(new User(user.getProperty("firstName"),
					user.getProperty("lastName"), user.getProperty("uniqueId"), user.getProperty("email"))));

			sender().tell(Json.toJson(data), self());
		} catch (Exception e) {
			logger.error("Failed to get users", e);
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}
}