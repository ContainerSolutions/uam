package actors.repository;

import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import akka.actor.Status.Failure;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;

public class RemoveAccountVertexActor extends UntypedActor {
	private static final ALogger logger = Logger.of(RemoveAccountVertexActor.class);
	private static final String vertexClassName = "JiraAccount";

	public static class RemoveVertex {
		public final String name;

		public RemoveVertex(String name) {
			this.name = name;
		}
	}

	private final OrientGraphFactory graphFactory;

	public RemoveAccountVertexActor(OrientGraphFactory graphFactory) {
		this.graphFactory = graphFactory;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof RemoveVertex) {
			removeAccount((RemoveVertex) msg);
		} else if (msg instanceof Failure) {
			sender().tell(msg.toString(), self());
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void removeAccount(RemoveVertex msg) {
		logger.info("Remove account vertex started: " + msg.name);
		OrientGraph graph = graphFactory.getTx();
		try {
			for (Vertex account : graph.getVerticesOfClass(vertexClassName)) {
				if (StringUtils.equals(account.getProperty("name"), msg.name)) {
					graph.removeVertex(account);
					break;
				}
			}

			graph.commit();
			sender().tell("Ok", self());
			logger.info("Jira account vertex removed");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			graph.rollback();
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}
}