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


import akka.actor.UntypedActor;

import commons.DirectoryHelper;


public class UsersActor extends UntypedActor
{

	private final GoogleServiceFactory gFactory;
	private final DirectoryHelper directoryHelper;
	private Directory directory;

	public static Props props(final GoogleServiceFactory gFactory, final DirectoryHelper directoryHelper)
	{
		return Props.create( UsersActor.class, () -> new UsersActor(gFactory, directoryHelper));

	}

	public UsersActor(GoogleServiceFactory gFactory, DirectoryHelper directoryHelper)
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
				directory = gFactory.createDirectoryService();

			}
			catch (GeneralSecurityException | IOException  ex)
			{
				ex.printStackTrace();
				//Send an error response
			}
		}
		else if (msg instanceof InsertUser)
		{

			InsertUser message = (InsertUser) msg;

			int status = directoryHelper.executeInsertUser(
			                 directory,
			                 message.domain,
			                 message.primaryEmail,
			                 message.firstName,
			                 message.lastName,
			                 message.password
			             );
			if (status == 200)
				getSender().tell("done", getSelf());
			else
				getSender().tell("fail", getSelf());
		}
	}

	public static class InitializeMe {}

	public static class InsertUser
	{
		private final String domain;
		private final String id;
		private final String primaryEmail;
		private final String firstName;
		private final String lastName;
		private final String password;

		public InsertUser(
		    String domain,
		    String id,
		    String primaryEmail,
		    String firstName,
		    String lastName,
		    String password
		)
		{
			this.domain = domain;
			this.id = id;
			this.primaryEmail = primaryEmail;
			this.firstName = firstName;
			this.lastName = lastName;
			this.password = password;

		}
	}
}
