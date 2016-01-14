package actors;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.FluentIterable;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import actors.JiraAccountActorProtocol.CreateAccount;
import actors.JiraAccountActorProtocol.GetAccount;
import actors.JiraAccountActorProtocol.GetAllAccounts;
import actors.JiraAccountActorProtocol.RemoveAccount;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class JiraAccountActor extends UntypedActor {

	public static Props props(final String url) {
		return Props.create(JiraAccountActor.class, () -> new JiraAccountActor(new OrientGraphFactory(url).setupPool(1, 10)));
	}

	private final OrientGraphFactory graphFactory;

	public JiraAccountActor(OrientGraphFactory graph) {
		this.graphFactory = graph;
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof GetAllAccounts) {
			getAllAccounts((GetAllAccounts) msg);
		} else if (msg instanceof GetAccount) {
			getAccount((GetAccount) msg);
		} else if (msg instanceof RemoveAccount) {
			removeAccount((RemoveAccount) msg);
		} else if (msg instanceof CreateAccount) {
			createAccount((CreateAccount) msg);
		}
	}

	private void getAllAccounts(GetAllAccounts msg) {
		OrientGraphNoTx graph = graphFactory.getNoTx();
		try {
			sender().tell(FluentIterable.from(graph.getVerticesOfClass(JiraAccountActorProtocol.vertexClassName))
					.filter(OrientVertex.class).transform(this::toMap).toList(), self());
		} finally {
			graph.shutdown();
		}
	}

	private void getAccount(GetAccount msg) {
		OrientGraphNoTx graph = graphFactory.getNoTx();
		try {
			sender().tell(FluentIterable.from(graph.getVerticesOfClass(JiraAccountActorProtocol.vertexClassName))
					.filter(OrientVertex.class).filter(input -> StringUtils.equals(msg.name, input.getProperty("name")))
					.transform(this::toMap).toList(), self());
		} finally {
			graph.shutdown();
		}
	}

	private void createAccount(CreateAccount msg) {
		OrientGraph graph = graphFactory.getTx();
		try {
			OrientVertex user = graph.getVertex(msg.userId);
			OrientVertex flow = graph.getVertex(msg.flowId);
			OrientVertex app = graph.getVertex(msg.appId);
						
			OrientVertex account = graph.addVertex(JiraAccountActorProtocol.vertexClassName, StringUtils.lowerCase(JiraAccountActorProtocol.vertexClassName));
			account.setProperty("name", user.getProperty("uniqueId"));
			account.addEdge("createdIn", flow);
			user.addEdge("hasAccount", account);
			app.addEdge("forApplication", account);

			sender().tell("Ok", self());
		} catch (Exception e) {
			graph.rollback();
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}

	private void removeAccount(RemoveAccount msg) {
		OrientGraph graph = graphFactory.getTx();
		try {
			FluentIterable.from(graph.getVerticesOfClass(JiraAccountActorProtocol.vertexClassName))
					.filter(OrientVertex.class).filter(input -> StringUtils.equals(msg.name, input.getProperty("name")))
					.forEach(graph::removeVertex);

			sender().tell("Ok", self());
		} catch (Exception e) {
			graph.rollback();
			sender().tell(e.getMessage(), self());
		} finally {
			graph.shutdown();
		}
	}

	private Map<String, Object> toMap(OrientVertex input) {
		Map<String, Object> props = new HashMap<>();
		props.put("id", input.getIdentity().toString());
		for (String key : input.getPropertyKeys()) {
			props.put(key, input.getProperty(key));
		}

		return props;
	}

}