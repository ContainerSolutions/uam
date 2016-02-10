package actors;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import akka.actor.UntypedActor;
import models.User;
import play.Logger;
import play.Logger.ALogger;

public class UpdateUserActor extends UntypedActor {
	private static final ALogger logger = Logger.of(UpdateUserActor.class);

	public static class UpdateUserMessage {
		public String id;
		public User user;

		public UpdateUserMessage(String id, User user) {
			this.id = id;
			this.user = user;
		}
	}

	private final OrientGraphFactory graphFactory;

	public UpdateUserActor(OrientGraphFactory graphFactory) {
		this.graphFactory = graphFactory;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof UpdateUserMessage) {
			UpdateUserMessage message = (UpdateUserMessage) msg;
			updateUser(message.id, message.user);
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void updateUser(String id, User user) {
		logger.info("Update user started: " + id);
		OrientGraph graph = graphFactory.getTx();
		try {
			for (Vertex userVertex : graph.getVertices("User.uniqueId", id)) {
				userVertex.setProperty("firstName", user.firstName);
				userVertex.setProperty("lastName", user.lastName);
				userVertex.setProperty("uniqueId", user.id);
				userVertex.setProperty("email", user.email);
				userVertex.setProperty("updated", new Date());
			}

			graph.commit();
			sender().tell("Ok", self());
		} catch (Exception e) {
			logger.error("Failed to update user: " + id, e);
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}
}