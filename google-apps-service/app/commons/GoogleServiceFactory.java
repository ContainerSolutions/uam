package commons;

import java.io.IOException;

import java.security.GeneralSecurityException;
import com.google.api.client.auth.oauth2.Credential;

public interface GoogleServiceFactory
{

	Credential createDirectoryCredential() throws GeneralSecurityException, IOException;

}
