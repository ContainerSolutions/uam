package commons;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.User;

public class GoogleServiceFactoryImpl implements GoogleServiceFactory {
	private static final String CLIENT_SECRET_KEY = "gappsservice/clientsecret";
	private static final String ADMIN_ACCOUNT_EMAIL = "pghukasyan@dio-soft.com";
	private static final String SERVICE_ACCOUNT_EMAIL = "763062422166-compute@developer.gserviceaccount.com";
	private final HttpTransport httpTransport;
	private final JsonFactory jsonFactory;
	private final FileDataStoreFactory dataStoreFactory;
	private final VaultSecretStore vaultStore;
	private static final List<String> SCOPES = new ArrayList<String>(DirectoryScopes.all());/* Arrays.asList( */
	// DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY,
	// DirectoryScopes.ADMIN_DIRECTORY_GROUP,
	// DirectoryScopes.ADMIN_DIRECTORY_USER,
	// DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER_READONLY,
	// DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY,
	// DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER
	/* ); */

	public GoogleServiceFactoryImpl() throws GeneralSecurityException, IOException {
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		jsonFactory = JacksonFactory.getDefaultInstance();
		File dataStoreDirectory = new File(System.getProperty("user.home"), ".credential/admin-directory");
		dataStoreFactory = new FileDataStoreFactory(dataStoreDirectory);
		vaultStore = VaultSecretStoreImpl.getVaultSecretStore(new StoreImpl());
	}

	@Override
	public Credential createDirectoryCredential() throws GeneralSecurityException, IOException {

		String clientSecret = vaultStore.read(CLIENT_SECRET_KEY).toString();
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new StringReader(clientSecret));

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					httpTransport, 
					jsonFactory,
					clientSecrets, 
					SCOPES)
				.setDataStoreFactory(dataStoreFactory)
				.setAccessType("offline")
				.build();

		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(ADMIN_ACCOUNT_EMAIL);
	}

	public Credential createDomainWideDirectoryCredential() throws GeneralSecurityException, IOException {

		try {
			// local code review (vtegza): key should be loaded from Vault @ 26.01.16
			File secFile = new File(
					GoogleServiceFactoryImpl.class.getResource("/uar-mantlio-0c5273f07730.p12").toURI());

			GoogleCredential cred = new GoogleCredential.Builder().setTransport(httpTransport)
					.setJsonFactory(jsonFactory).setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
					.setServiceAccountScopes(Collections.singletonList(DirectoryScopes.ADMIN_DIRECTORY_USER))
					.setServiceAccountUser(ADMIN_ACCOUNT_EMAIL).setServiceAccountPrivateKeyFromP12File(secFile).build();

			Directory service = new Directory.Builder(httpTransport, jsonFactory, null).setHttpRequestInitializer(cred)
					.build();

			List<User> results = service.users().list().setMaxResults(10)
					// .setCustomer("dio-soft.com")
					.setOrderBy("email").execute().getUsers();

			results.forEach(user -> {
				System.out.println(user.getName().getFullName());
			});

			return cred;
		} catch (URISyntaxException ex) {
			// inaproporate system sate
			throw new IllegalStateException(ex.getMessage());
		}
	}

	@Override
	public Directory createDirectoryService() throws IOException, GeneralSecurityException {
		// createDomainWideDirectoryCredential();

		Credential credential = createDirectoryCredential();

		Directory service = new Directory.Builder(httpTransport, jsonFactory, credential)
				.setApplicationName("uar-mantlio")
				// .setDomain("dio-soft.com")
				.build();
		return service;
	}

}
