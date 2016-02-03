package actors;

import java.util.Date;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import akka.actor.UntypedActor;
import models.User;
import play.Logger;
import play.Logger.ALogger;
import java.time.LocalDateTime;

public class AuditLogsActor extends UntypedActor
{
	private static final ALogger logger = Logger.of(AuditLogsActor.class);

	private final OrientGraphFactory graphFactory;

	public AuditLogsActor(OrientGraphFactory graphFactory)
	{
		this.graphFactory = graphFactory;
	}
	@Override
	public void postStop() throws Exception
	{
		super.postStop();
		graphFactory.close();
	}
	public void onReceive(Object msg) throws Exception
	{
		if (msg instanceof SaveAuditLog)
		{
			SaveAuditLog message = (SaveAuditLog) msg;
			OrientGraph graph = graphFactory.getTx();
			try
			{
//				OSequence seq = graph.getRawGraph().getMetadata().getSequenceLibrary().getSequence("rnseq");
				OrientVertex auditLogVertex = graph.addVertex("AuditLog", "auditlog");
//				auditLogVertex.setProperty("request_number", seq.next());
				auditLogVertex.setProperty("user_id", message.userId);
				auditLogVertex.setProperty("datetime", LocalDateTime.now());
				auditLogVertex.setProperty("application", message.application);
				auditLogVertex.setProperty("executor", message.executor);
				auditLogVertex.setProperty("action", message.action);

				graph.commit();
				sender().tell("done", self());
			}
			catch (Exception e)
			{
				logger.error("Failed to create audit log for: " + message.userId, e);
				sender().tell(e.getMessage(), self());
			}
			finally
			{
				graph.shutdown();
			}



		}
	}


	public static class SaveAuditLog
	{

		private Long requestNumber;
		private String userId;
		private String application;
		private String executor;
		private String action;

		public SaveAuditLog(
		    Long requestNumber,
		    String userId,
		    String application,
		    String executor,
		    String action
		)
		{
			this.requestNumber = requestNumber;
			this.userId = userId;
			this.application = application;
			this.executor = executor;
			this.action = action;
		}
	}

}
