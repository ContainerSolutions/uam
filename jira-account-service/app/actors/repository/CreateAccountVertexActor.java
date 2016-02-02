package actors.repository;

import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import akka.actor.Status.Failure;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;

public class CreateAccountVertexActor extends UntypedActor {
	private static final ALogger logger = Logger.of(CreateAccountVertexActor.class);

	public static class CreateVertex {
		public String userUniqueId;

		public CreateVertex() {
		}

		public CreateVertex(String userUniqueId) {
			this.userUniqueId = userUniqueId;
		}
	}

	private final OrientGraphFactory graphFactory;

	public CreateAccountVertexActor(OrientGraphFactory graph) {
		this.graphFactory = graph;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof CreateVertex) {
			createAccount((CreateVertex) msg);
		} else if (msg instanceof Failure) {
			sender().tell(msg.toString(), self());
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void createAccount(CreateVertex msg) {
		logger.info("Create account vertex started: " + msg.userUniqueId);
		OrientGraph graph = graphFactory.getTx();
		try {
			Vertex user = getUserVertex(msg.userUniqueId, graph);

			OrientVertex account = graph.addVertex("JiraAccount", "jiraaccount");
			account.setProperty("name", msg.userUniqueId);
			user.addEdge("hasAccount", account);

			graph.commit();
			sender().tell("Ok", self());
			logger.info("Jira account vertex created");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			graph.rollback();
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}

	private Vertex getUserVertex(String userUniqueId, OrientBaseGraph graph) throws Exception {
		for (Vertex user : graph.getVerticesOfClass("User")) {
			if (StringUtils.equals(user.getProperty("uniqueId"), userUniqueId)) {
				return user;
			}
		}

		throw new Exception("User vertex not found: " + userUniqueId);
	}
}