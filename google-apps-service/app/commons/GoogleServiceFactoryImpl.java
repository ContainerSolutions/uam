package commons;

import java.util.Arrays;
import java.util.List;
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

public class GoogleServiceFactoryImpl implements GoogleServiceFactory
{

	private final HttpTransport httpTransport;
	private final JsonFactory jsonFactory;
	private final FileDataStoreFactory dataStoreFactory;
	private static final List<String> SCOPES = Arrays.asList(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY);

	public GoogleServiceFactoryImpl() throws GeneralSecurityException, IOException
	{
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		jsonFactory = JacksonFactory.getDefaultInstance();
		dataStoreFactory = new FileDataStoreFactory(new java.io.File(System.getProperty("user.home"), ".credential/admin-directory"));


	}
	@Override
	public Credential createDirectoryCredential() throws GeneralSecurityException, IOException
	{

		//local code review (vtegza): key should be loaded from Vault @ 26.01.16
		InputStream in = GoogleServiceFactoryImpl.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, SCOPES)
		.setDataStoreFactory(dataStoreFactory)
		.setAccessType("offline")
		.build();

		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("pghukasyan@dio-soft.com");
		return credential;
	}


}
