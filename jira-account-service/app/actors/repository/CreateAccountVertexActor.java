package actors.repository;


import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;

public class CreateAccountVertexActor extends UntypedActor {
	private static final ALogger logger = Logger.of(CreateAccountVertexActor.class);
	private static final String vertexClassName = "JiraAccount";

	public static class CreateVertex {
		public String userId;
		public String flowId;
		public String appId;

		public CreateVertex() {
		}

		public CreateVertex(String userId, String flowId, String appId) {
			this.userId = userId;
			this.flowId = flowId;
			this.appId = appId;
		}

		@Override
		public String toString() {
			return "CreateVertex [userId=" + userId + ", flowId=" + flowId + ", appId=" + appId + "]";
		}

	}

	public static Props props(final String url) {
		return Props.create(CreateAccountVertexActor.class,
				() -> new CreateAccountVertexActor(new OrientGraphFactory(url).setupPool(1, 10)));
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
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void createAccount(CreateVertex msg) {
		logger.info("Create account vertex started: " + msg);
		OrientGraph graph = graphFactory.getTx();
		try {
			OrientVertex user = graph.getVertex(msg.userId);
			OrientVertex flow = graph.getVertex(msg.flowId);
			OrientVertex app = graph.getVertex(msg.appId);

			OrientVertex account = graph.addVertex(vertexClassName, StringUtils.lowerCase(vertexClassName));
			account.setProperty("name", user.getProperty("uniqueId"));
			account.addEdge("createdIn", flow);
			user.addEdge("hasAccount", account);
			app.addEdge("forApplication", account);

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
}