package com.diosoft.uar.ldap.ad;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import org.junit.*;

import javax.net.SocketFactory;
import javax.net.ssl.TrustManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class WindowsAccountAccessManagerIT {

    private static LDAPConnectionPool ldapConnectionPool;

    private static String configCnUsers;
    private static String configDefaultPass;

    @BeforeClass
    public static void init() throws Exception {
        Properties properties = new Properties();
        properties.load(WindowsAccountAccessManagerIT.class.getClassLoader().getResourceAsStream("ldap.properties"));

        String configHost = properties.getProperty("host");
        int configPort = Integer.parseInt(properties.getProperty("port"));
        String configBindDn = properties.getProperty("bind.dn");
        String configBindPass = properties.getProperty("bind.password");

        TrustManager trustManager = new TrustAllTrustManager();
        SocketFactory sslSocketFactory = new SSLUtil(trustManager).createSSLSocketFactory("SSLv3");
        LDAPConnection ldapConnection = new LDAPConnection(sslSocketFactory, configHost, configPort, configBindDn, configBindPass);
        ldapConnectionPool = new LDAPConnectionPool(ldapConnection, 4);

        configCnUsers = properties.getProperty("cn.users");
        configDefaultPass = properties.getProperty("default.password");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if(ldapConnectionPool != null) {
            ldapConnectionPool.close();
        }
    }

    @Test
    public void testADIntegration_Optimistic() throws Exception {
        //expected
        Collection<WindowsAccountAccess> expectedCreated = new ArrayList<>();
        expectedCreated.add(new WindowsAccountAccess("testuser", "MantlIO", "Test", "testuser@mantlio.local"));

        //given
        WindowsAccountAccess access = new WindowsAccountAccess("testuser", "MantlIO", "Test", "testuser@mantlio.local");

        //when
        if(listTestUser(access.getId()).size() != 0) {
            revokeTestUser(access);
        }
        //then
        grantTestUser(access);
        Collection<WindowsAccountAccess> actualCreated = listTestUser(access.getId());
        revokeTestUser(access);
        Collection<WindowsAccountAccess> actualDeleted = listTestUser(access.getId());

        Assert.assertEquals(expectedCreated,actualCreated);
        Assert.assertTrue(actualDeleted.size() == 0);

    }


    private void grantTestUser(WindowsAccountAccess access) throws Exception {

        WindowsAccountAccessManager target = new WindowsAccountAccessManager(ldapConnectionPool, configCnUsers, configDefaultPass);
        target.grant(access);

    }

    private void revokeTestUser(WindowsAccountAccess access) throws Exception {
        WindowsAccountAccessManager target = new WindowsAccountAccessManager(ldapConnectionPool, configCnUsers, configDefaultPass);
        target.revoke(access);

    }

    private Collection<WindowsAccountAccess> listTestUser(String testUser) throws Exception {
        WindowsAccountAccessFilter filter = new WindowsAccountAccessFilter(testUser);

        //then
        WindowsAccountAccessManager target = new WindowsAccountAccessManager(ldapConnectionPool, configCnUsers, configDefaultPass);
        return target.list(filter);

    }

}