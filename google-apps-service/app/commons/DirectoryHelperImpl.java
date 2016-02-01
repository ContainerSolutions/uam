package commons;
import java.util.List;
import java.util.stream.Collectors;

import akka.actor.UntypedActor;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.admin.directory.Directory;

import java.security.GeneralSecurityException;

import com.google.api.services.admin.directory.model.*;

import akka.actor.ActorRef;

import commons.GoogleServiceFactory;



public class DirectoryHelperImpl implements DirectoryHelper
{
	@Override
	public List<String> excetuteGetUserGroups(Directory directory, String domain, String user)
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


}
