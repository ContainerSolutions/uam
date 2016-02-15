package actors;

import org.apache.commons.lang.StringUtils;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientBaseGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import akka.actor.Props;

import akka.actor.Status.Failure;
import akka.actor.UntypedActor;
import play.Logger;
import play.Logger.ALogger;

public class UsersRepositoryActor extends UntypedActor
{

	private static final ALogger logger = Logger.of(UsersRepositoryActor.class);

	private final OrientGraphFactory graphFactory;
	private static final String vertexClassName = "GoogleUser";

	public static Props props(final OrientGraphFactory graphFactory)
	{
		return Props.create(UsersRepositoryActor.class, () -> new UsersRepositoryActor(graphFactory));

	}


	public UsersRepositoryActor(OrientGraphFactory graph)
	{
		this.graphFactory = graph;
	}

	@Override
	public void postStop() throws Exception
	{
		super.postStop();
		graphFactory.close();
	}

	public void onReceive(Object msg) throws Exception
	{
		if (msg instanceof CreateUserVertex)
		{
			createUser((CreateUserVertex) msg);
		}
		else if (msg instanceof Failure)
		{
			sender().tell(msg.toString(), self());
		}
		else
		{
			logger.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private void createUser(CreateUserVertex msg)
	{
		logger.info("Create account vertex started: " + msg.userUniqueId);
		OrientGraph graph = graphFactory.getTx();
		try
		{
			Vertex user = getUserVertex(msg.userUniqueId, graph);

			OrientVertex account = graph.addVertex(vertexClassName, "googleuser");
			account.setProperty("name", msg.userUniqueId);
			account.setProperty("primaryEmail", msg.primaryEmail);
			user.addEdge("hasAccount", account);

			graph.commit();
			sender().tell("Ok", self());
			logger.info("Google user vertex created");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			graph.rollback();
			sender().tell(e.getMessage(), self());
		}
		finally
		{
			graph.shutdown();
		}
	}
	//local code review (vtegza): should qurty by id and not this kind of iterationg over ALL verticies
	private Vertex getUserVertex(String userUniqueId, OrientBaseGraph graph) throws Exception
	{
		for (Vertex user : graph.getVerticesOfClass("User"))
		{
			if (StringUtils.equals(user.getProperty("uniqueId"), userUniqueId))
			{
				return user;
			}
		}

		throw new Exception("User vertex not found: " + userUniqueId);
	}

	private void removeAccount(DeleteUserVertex msg)
	{
		logger.info("Remove account vertex started: " + msg.userUniqueId);
		OrientGraph graph = graphFactory.getTx();
		try
		{
			for (Vertex account : graph.getVerticesOfClass(vertexClassName))
			{
				if (StringUtils.equals(account.getProperty("unquiId"), msg.userUniqueId))
				{
					graph.removeVertex(account);
					break;
				}
			}

			graph.commit();
			sender().tell("Ok", self());
			logger.info("Google account vertex removed");
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			graph.rollback();
			sender().tell(e.getMessage(), self());
		}
		finally
		{
			graph.shutdown();
		}
	}

	public static class CreateUserVertex
	{
		public String userUniqueId;
		public String primaryEmail;

		public CreateUserVertex(String userUniqueId, String primaryEmail)
		{
			this.userUniqueId = userUniqueId;
			this.primaryEmail = primaryEmail;
		}
	}

	public static class DeleteUserVertex
	{
		public String userUniqueId;

		public DeleteUserVertex(String userUniqueId)
		{
			this.userUniqueId = userUniqueId;
		}
	}


}
