package helpers.guice;

import helpers.akka.AdAccountsActor;
import com.diosoft.uar.AccessManagerException;
import com.diosoft.uar.ldap.ad.WindowsAccountAccessManager;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.F;
import play.libs.akka.AkkaGuiceSupport;

import javax.net.SocketFactory;
import javax.net.ssl.TrustManager;
import java.security.GeneralSecurityException;

public class AdGuiceModule extends AbstractModule implements AkkaGuiceSupport {

    private static final Logger.ALogger LOG = Logger.of(AdGuiceModule.class);

    @Override
    protected void configure() {
        LOG.debug("Binding [adActor]");
        bindActor(AdAccountsActor.class, "adActor");
    }

    @Inject @Provides
    public LDAPConnectionPool buildLdapConnectionPool(Configuration configuration, ApplicationLifecycle lifecycle) throws LDAPException, GeneralSecurityException {
        LOG.debug("Building  [LDAPConnectionPool]");

        String configHost = configuration.getString("host");
        int configPort = configuration.getInt("port");
        String configBindDn = configuration.getString("bind.dn");
        String configBindPass = configuration.getString("bind.password");
        int configConnectionPoolSize = configuration.getInt("pool.size");

        TrustManager trustManager = new TrustAllTrustManager();
        SocketFactory sslSocketFactory = new SSLUtil(trustManager).createSSLSocketFactory("SSLv3");
        LDAPConnection ldapConnection = new LDAPConnection(sslSocketFactory, configHost, configPort, configBindDn, configBindPass);

        LDAPConnectionPool ldapConnectionPool = new LDAPConnectionPool(ldapConnection, configConnectionPoolSize);

        lifecycle.addStopHook(() -> {
            ldapConnectionPool.close();
            return F.Promise.pure(null);
        });

        return ldapConnectionPool;
    }

    @Inject @Provides
    public WindowsAccountAccessManager buildAccessManager(LDAPConnectionPool ldapConnectionPool, Configuration configuration) throws AccessManagerException {
        LOG.debug("Building  [WindowsAccountAccessManager]");

        String configCnUsers = configuration.getString("cn.users");
        String configDefaultPass = configuration.getString("default.password");

        return new WindowsAccountAccessManager(ldapConnectionPool, configCnUsers, configDefaultPass);
    }

}
