package actors;

import java.util.Date;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import akka.actor.UntypedActor;
import models.User;
import play.Logger;
import play.Logger.ALogger;

public class CreateUserActor extends UntypedActor {
	private static final ALogger logger = Logger.of(CreateUserActor.class);

	public static class CreateUserMessage {
		public User user;

		public CreateUserMessage(User user) {
			this.user = user;
		}
	}

	private final OrientGraphFactory graphFactory;

	public CreateUserActor(OrientGraphFactory graphFactory) {
		this.graphFactory = graphFactory;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof CreateUserMessage) {
			createUser(((CreateUserMessage) msg).user);
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void createUser(User user) {
		logger.info("Get users started: " + user);
		OrientGraph graph = graphFactory.getTx();
		try {
			OrientVertex userVertex = graph.addVertex("User", "user");
			userVertex.setProperty("firstName", user.firstName);
			userVertex.setProperty("lastName", user.lastName);
			userVertex.setProperty("uniqueId", user.id);
			userVertex.setProperty("email", user.email);
			userVertex.setProperty("created", new Date());
			userVertex.setProperty("updated", new Date());
			userVertex.setProperty("active", true);

			graph.commit();
			sender().tell("Ok", self());
		} catch (Exception e) {
			logger.error("Failed to create user: " + user, e);
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}
}