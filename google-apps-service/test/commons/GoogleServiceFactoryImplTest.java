package commons;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.auth.oauth2.Credential;

import java.security.GeneralSecurityException;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.*;
import com.google.api.services.admin.directory.Directory;

public class GoogleServiceFactoryImplTest
{

	@Test
	public void testCreateDirectoryCredential() throws Exception
	{


		//test class
		GoogleServiceFactoryImpl testClass = new GoogleServiceFactoryImpl();

		Credential returnedValue = testClass.createDirectoryCredential();

		//asserts
		Assert.assertNotNull(returnedValue);

	}

	@Test
	public void testCreateDirectoryService() throws Exception
	{
		//test class
		GoogleServiceFactoryImpl testClass = new GoogleServiceFactoryImpl();

		Directory service = testClass.creatDirectoryService();

		List<User> results = service.users().list()
		                     .setMaxResults(10)
		                     .setOrderBy("email")
		                     .setDomain("dio-soft.com")
		                     .execute().getUsers();
		/*         List<Group> res = service.groups().list()*/
		//.setMaxResults(10)
		//.setUserKey("vtegza@dio-soft.com")
		//.setDomain("dio-soft.com")
		//.execute().getGroups();

		//res.forEach( group ->
		//{
		//System.out.println(group.getEmail());
		/*});*/


		Assert.assertEquals(10, results.size());
	}

}
