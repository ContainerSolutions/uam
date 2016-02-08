package commons;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ivy.core.search.OrganisationEntry;

import akka.actor.UntypedActor;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.admin.directory.Directory;

import java.security.GeneralSecurityException;

import com.google.api.services.admin.directory.model.*;

import akka.actor.ActorRef;

import commons.GoogleServiceFactory;
import com.google.api.services.admin.directory.model.User;



public class DirectoryHelperImpl implements DirectoryHelper
{
	@Override
	public List<String> executeGetUserGroups(Directory directory, String domain, String user)
	{
		try
		{
			List<Group> res = directory.groups().list()
			                  .setMaxResults(10)
			                  .setUserKey(user)
			                  .setDomain(domain)
			                  .execute().getGroups();

			List<String> groups = res.stream().map( group ->
			{
				return group.getName();
			}).collect(Collectors.toList());


			return groups;

		}
		catch ( IOException  ex)
		{
			ex.printStackTrace();
			//Send an error response
			throw new IllegalStateException(ex.getMessage());
		}


	}

	@Override
	public int executeInsertUser(
	    Directory directory,
	    String domain,
	    String primaryEmail,
	    String firstName,
	    String lastName,
	    String password
	)
	{

		try
		{

			User body = new User()
			.setPassword(password)
			.setName(new UserName()
			         .setGivenName(firstName)
			         .setFamilyName(lastName)
			        )
			.setPrimaryEmail(primaryEmail);
			//.setOrganizations(new OrganisationEntry();

			directory.users().insert(body)
			.execute();


			return 200;

		}
		catch ( IOException  ex)
		{
			ex.printStackTrace();
			//Send an error response
			throw new IllegalStateException(ex.getMessage());
		}
	}

	@Override
	public int executeDeleteUser(
	    Directory directory,
	    String domain,
	    String primaryEmail
	)
	{

		try
		{


			directory.users().delete(primaryEmail)
			.execute();


			return 200;

		}
		catch ( IOException  ex)
		{
			ex.printStackTrace();
			//Send an error response
			throw new IllegalStateException(ex.getMessage());
		}
	}

}
