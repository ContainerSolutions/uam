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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;


import akka.actor.UntypedActor;

import commons.DirectoryHelper;


public class UsersActor extends UntypedActor
{

	private final GoogleServiceFactory gFactory;
	private final DirectoryHelper directoryHelper;
	private Directory directory;
	private final ActorRef repoActor;

	public static Props props(ActorRef repoActor , final GoogleServiceFactory gFactory, final DirectoryHelper directoryHelper)
	{
		return Props.create( UsersActor.class, () -> new UsersActor(repoActor, gFactory, directoryHelper));

	}

	public UsersActor(ActorRef repoActor, GoogleServiceFactory gFactory, DirectoryHelper directoryHelper) throws GeneralSecurityException, IOException

	{
		this.gFactory = gFactory;
		this.directoryHelper = directoryHelper;
		this.repoActor = repoActor;
		directory = gFactory.createDirectoryService();

	}

	@Override
	public void onReceive(Object msg)
	{
		if (msg instanceof InsertUser)
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
			{
				//SaveUser
				//repoActor.tell(objMsg, self());
				getSender().tell("done", getSelf());
			}
			else
				getSender().tell("fail", getSelf());
		}
		else if (msg instanceof DeleteUser)
		{

			DeleteUser message = (DeleteUser) msg;

			int status = directoryHelper.executeDeleteUser(
			                 directory,
			                 message.domain,
			                 message.primaryEmail
			             );
			if (status == 200)
				getSender().tell("done", getSelf());
			else
				getSender().tell("fail", getSelf());
		}
		else if (msg instanceof GetUser)
		{

			GetUser message = (GetUser) msg;

			ObjectNode response = directoryHelper.executeGetUser(
			                          directory,
			                          message.domain,
			                          message.primaryEmail
			                      );
			if (response != null)
				getSender().tell(response, getSelf());
			else
				getSender().tell("404", getSelf());
		}

	}

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
	public static class DeleteUser
	{
		private final String domain;
		private final String primaryEmail;

		public DeleteUser(
		    String domain,
		    String primaryEmail
		)
		{
			this.domain = domain;
			this.primaryEmail = primaryEmail;
		}
	}

	public static class GetUser
	{
		private final String domain;
		private final String primaryEmail;

		public GetUser(
		    String domain,
		    String primaryEmail
		)
		{
			this.domain = domain;
			this.primaryEmail = primaryEmail;
		}
	}


}
