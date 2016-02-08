package helpers.guice;

import com.google.inject.Singleton;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import helpers.akka.AdAccountStorageActor;
import helpers.akka.AdAccountsActor;
import com.diosoft.uam.AccessManagerException;
import com.diosoft.uam.ldap.ad.WindowsAccountAccessManager;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import helpers.akka.AuditLogsActor;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.F;
import play.libs.akka.AkkaGuiceSupport;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.net.SocketFactory;
import javax.net.ssl.TrustManager;
import java.security.GeneralSecurityException;
import java.util.*;

public class AdGuiceModule extends AbstractModule implements AkkaGuiceSupport {

    private static final Logger.ALogger LOG = Logger.of(AdGuiceModule.class);

    public static final String CONSUL_URL = "consul.url";

    private static final String ORIENTDB_URL = "adservice/orientdb/url";
    private static final String AD_HOST = "adservice/ad/host";
    private static final String AD_PORT = "adservice/ad/port";
    private static final String AD_BIND_DN = "adservice/ad/bind/dn";
    private static final String AD_BIND_PASSWORD = "adservice/ad/bind/password";
    private static final String AD_CN_USERS = "adservice/ad/cn/users";
    private static final String POOL_SIZE = "adservice/pool/size";
    private static final String DEFAULT_PASSWORD = "adservice/default/password";

    @Override
    protected void configure() {
        LOG.debug("Binding [adActor]");
        bindActor(AdAccountsActor.class, "adActor");
        bindActor(AdAccountStorageActor.class, "adStorageActor");
        bindActor(AuditLogsActor.class, "adAuditActor");

    }

    @Inject @Provides @Singleton
    public static Properties loadConfiguration(Configuration configuration, WSClient ws) {
        LOG.debug("Loading [Properties]");
        String consulUrl = configuration.getString(CONSUL_URL);

        Properties propertiesConfig = new Properties();

        if(consulUrl != null && !"".equals(consulUrl)) {
            WSResponse response = ws.url(consulUrl).get().get(10000);
            if (response.getStatus() == 200) {
                response.asJson().forEach(jsonNode -> propertiesConfig.put(jsonNode.get("Key").asText(),
                        new String(Base64.getDecoder().decode(jsonNode.get("Value").asText()))));
                LOG.info("Loaded configuration: " + propertiesConfig);
            } else {
                LOG.warn("Can't read configuration from consul: " + response.getStatusText());
            }
        }
        return propertiesConfig;
    }


    @Inject @Provides @Singleton
    public LDAPConnectionPool buildLdapConnectionPool(ApplicationLifecycle lifecycle, Properties configuration) throws LDAPException, GeneralSecurityException {
        LOG.debug("Building [LDAPConnectionPool]");

        String configHost = configuration.getProperty(AD_HOST);
        int configPort = Integer.parseInt(configuration.getProperty(AD_PORT));
        String configBindDn = configuration.getProperty(AD_BIND_DN);
        String configBindPass = configuration.getProperty(AD_BIND_PASSWORD);
        int configConnectionPoolSize = Integer.parseInt(configuration.getProperty(POOL_SIZE));

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

    @Inject @Provides @Singleton
    public WindowsAccountAccessManager buildAccessManager(LDAPConnectionPool ldapConnectionPool, Properties configuration) throws AccessManagerException {
        LOG.debug("Building [WindowsAccountAccessManager]");

        String configCnUsers = configuration.getProperty(AD_CN_USERS);
        String configDefaultPass = configuration.getProperty(DEFAULT_PASSWORD);

        return new WindowsAccountAccessManager(ldapConnectionPool, configCnUsers, configDefaultPass);
    }

    @Inject @Provides @Singleton
    public OrientGraphFactory buildOrientGraphFactory(ApplicationLifecycle lifecycle, Properties configuration) throws LDAPException, GeneralSecurityException {
        LOG.debug("Building [OrientGraphFactory]");

        OrientGraphFactory factory = new OrientGraphFactory(configuration.getProperty(ORIENTDB_URL));

        lifecycle.addStopHook(() -> {
            factory.close();
            return F.Promise.pure(null);
        });

        return factory;
    }

}
