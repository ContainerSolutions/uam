package actors;

import java.util.List;
import java.util.stream.Collectors;

import akka.actor.UntypedActor;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.admin.directory.Directory;

import java.security.GeneralSecurityException;

import com.google.api.services.admin.directory.model.*;
import akka.actor.Props;

import akka.actor.ActorRef;

import commons.GoogleServiceFactory;
import commons.DirectoryHelper;


public class GroupsActor extends UntypedActor
{
	private final GoogleServiceFactory gFactory;
	private final DirectoryHelper directoryHelper;
	private Directory directory;

	public static Props props(final GoogleServiceFactory gFactory, final DirectoryHelper directoryHelper)
	{
		return Props.create( GroupsActor.class, () -> new GroupsActor(gFactory, directoryHelper));

	}

	public GroupsActor(GoogleServiceFactory gFactory, DirectoryHelper directoryHelper)
	{
		this.gFactory = gFactory;
		this.directoryHelper = directoryHelper;
	}

	@Override
	public void onReceive(Object msg)
	{
		if (msg instanceof InitializeMe)
		{

			try
			{
				directory = gFactory.creatDirectoryService();
			}
			catch (GeneralSecurityException | IOException  ex)
			{
				ex.printStackTrace();
				//Send an error response
			}
		}
		else if (msg instanceof GetUserGroups)
		{

			GetUserGroups message = (GetUserGroups) msg;

			List<String> groups = directoryHelper.executeGetUserGroups(directory, message.getDomain(), message.getUser());
			System.out.println("requested " + message.getUser() + " " + message.getDomain() + " " + groups );
			getSender().tell(groups, getSelf());
		}
	}

	public static class InitializeMe {}

	public static class AllGroups
	{

	}

	public static class GetUserGroups
	{

		private final String domain;
		private final String user;

		public GetUserGroups(String domain, String user)
		{
			this.domain = domain;
			this.user = user;
		}

		public String getUser()
		{
			return user;
		}

		public String getDomain()
		{

			return domain;
		}

	}

}
