package actors.repository;

import java.util.Arrays;
import java.util.concurrent.Callable;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import actors.jira.CreateAccountActor.CreateJiraAccountMessage;
import actors.jira.CreateAccountActor.JiraPostMessage;
import actors.repository.CreateAccountVertexActor.CreateVertex;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import play.Logger;
import play.Logger.ALogger;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

public class GetUserVertexActor extends UntypedActor {
	private static final ALogger logger = Logger.of(GetUserVertexActor.class);

	private final ActorRef next;
	private final OrientGraphFactory graphFactory;

	public GetUserVertexActor(ActorRef next, OrientGraphFactory graphFactory) {
		this.next = next;
		this.graphFactory = graphFactory;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof CreateVertex) {
			getUserData((CreateVertex) msg);
		} else {
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void getUserData(CreateVertex msg) {
		logger.info("Get user vertex started: " + msg.userId);

		final ExecutionContext context = context().dispatcher().prepare();
		Future<CreateJiraAccountMessage> future = Futures.future(new Callable<CreateJiraAccountMessage>() {
			public CreateJiraAccountMessage call() throws Exception {
				OrientGraph graph = graphFactory.getTx();
				try {
					OrientVertex user = graph.getVertex(msg.userId);
					return new CreateJiraAccountMessage(msg,
							new JiraPostMessage(user.getProperty("uniqueId"), user.getProperty("email"),
									String.format("%s %s", user.getProperty("firstName"), user.getProperty("lastName")),
									Arrays.asList("jira-core")));
				} finally {
					graph.shutdown();
				}
			};
		}, context);

		Patterns.pipe(future, context).pipeTo(next, sender());
	}
}