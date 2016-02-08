package commons;

import java.io.IOException;
import java.net.URISyntaxException;

import java.security.GeneralSecurityException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.admin.directory.Directory;

public interface GoogleServiceFactory
{

	Credential createDirectoryCredential() throws GeneralSecurityException, IOException;

	Directory createDirectoryService() throws IOException, GeneralSecurityException;


}
