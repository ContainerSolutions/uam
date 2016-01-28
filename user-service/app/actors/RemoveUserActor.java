package actors;

import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;

public class RemoveUserActor extends UntypedActor {
	private static final ALogger logger = Logger.of(RemoveUserActor.class);

	public static class RemoveUserMessage {
		public String id;

		public RemoveUserMessage(String id) {
			this.id = id;
		}
	}

	private final OrientGraphFactory graphFactory;

	public RemoveUserActor(OrientGraphFactory graphFactory) {
		this.graphFactory = graphFactory;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof RemoveUserMessage) {
			removeUser(((RemoveUserMessage) msg).id);
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void removeUser(String id) {
		logger.info("Remove user started: " + id);
		OrientGraph graph = graphFactory.getTx();
		try {
			for (Vertex user : graph.getVerticesOfClass("User")) {
				if (StringUtils.equals(user.getProperty("uniqueId"), id)) {
					graph.removeVertex(user);
					break;
				}
			}

			graph.commit();
			sender().tell("Ok", self());
		} catch (Exception e) {
			logger.error("Failed to remove user: " + id, e);
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}
}