package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import commons.GoogleServiceFactoryImpl;
import commons.GoogleServiceFactory;
import commons.DirectoryHelper;
import commons.DirectoryHelperImpl;

import java.security.GeneralSecurityException;
import java.io.IOException;

public class Kernel
{
	private final ActorRef users;
	private final ActorRef auditLog;

	public Kernel() throws GeneralSecurityException, IOException

	{
		//Credentials orientDbCredentials = VaultHelper.getCredentials(configuration, token, orientDbKey);
		OrientGraphFactory graphFactory = new OrientGraphFactory(
		    "remote:192.168.1.12/UserAccessControl",
		    "admin",
		    "admin"
		).setupPool(1, 10);


		GoogleServiceFactory googleFactory = new GoogleServiceFactoryImpl();
		DirectoryHelper directoryHelper = new DirectoryHelperImpl();
		ActorSystem system = ActorSystem.create("GoogleActorSystem");

		ActorRef usersRepository = system.actorOf(FromConfig.getInstance().props(UsersRepositoryActor.props(graphFactory)), "users-repository");


		users = system.actorOf(FromConfig.getInstance().props(UsersActor.props(usersRepository, googleFactory, directoryHelper)), "users");
		auditLog = system.actorOf(FromConfig.getInstance().props(AuditLogsActor.props(graphFactory)), "audit");


	}

	public ActorRef users()
	{
		return users;
	}
	public ActorRef auditLog()
	{
		return users;
	}

}
