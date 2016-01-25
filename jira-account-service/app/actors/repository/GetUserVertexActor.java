package actors.repository;

import java.util.Arrays;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import actors.jira.CreateAccountActor.CreateJiraAccountMessage;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;

public class GetUserVertexActor extends UntypedActor {
	private static final ALogger logger = Logger.of(GetUserVertexActor.class);

	public static class GetUserData {
		public String userId;

		public GetUserData() {
		}

		public GetUserData(String userId) {
			this.userId = userId;
		}

		@Override
		public String toString() {
			return "GetUserData [userId=" + userId + "]";
		}

	}

	public static Props props(final String url) {
		return Props.create(GetUserVertexActor.class,
				() -> new GetUserVertexActor(new OrientGraphFactory(url).setupPool(1, 10)));
	}

	private final OrientGraphFactory graphFactory;

	public GetUserVertexActor(OrientGraphFactory graph) {
		this.graphFactory = graph;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof GetUserData) {
			getUserData((GetUserData) msg);
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void getUserData(GetUserData msg) {
		logger.info("Get user vertex started: " + msg.userId);
		OrientGraph graph = graphFactory.getTx();
		try {
			sender().tell(createMessage(graph.getVertex(msg.userId)), self());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			graph.rollback();
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}

	private CreateJiraAccountMessage createMessage(OrientVertex user) {
		return new CreateJiraAccountMessage(user.getProperty("uniqueId"), user.getProperty("email"),
				String.format("%s %s", user.getProperty("firstName"), user.getProperty("lastName")),
				Arrays.asList("jira-core"));
	}
}