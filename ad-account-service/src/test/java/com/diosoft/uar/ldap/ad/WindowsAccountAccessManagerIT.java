package com.diosoft.uar.ldap.ad;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.net.SocketFactory;
import javax.net.ssl.TrustManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;


public class WindowsAccountAccessManagerIT {

    private static String configHost;
    private static Integer configPort;
    private static String configCnUsers;
    private static String configBindDn;
    private static String configBindPass;
    private static String configDefaultPass;

    @BeforeClass
    public static void init() throws Exception {
        Properties properties = new Properties();
        properties.load(WindowsAccountAccessManagerIT.class.getClassLoader().getResourceAsStream("ldap.properties"));

        configHost = properties.getProperty("host");
        configPort = Integer.parseInt(properties.getProperty("port"));
        configCnUsers = properties.getProperty("cn.users");
        configBindDn = properties.getProperty("bind.dn");
        configBindPass = properties.getProperty("bind.password");
        configDefaultPass = properties.getProperty("default.password");
    }

    @Test
    public void testADIntegration_Optimistic() throws Exception {
        //expected
        Collection<WindowsAccountAccess> expectedCreated = new ArrayList<>();
        expectedCreated.add(new WindowsAccountAccess("testuser", "MantlIO", "Test", "testuser@mantlio.local"));

        //given
        WindowsAccountAccess access = new WindowsAccountAccess("testuser", "MantlIO", "Test", "testuser@mantlio.local");

        //when
        if(listTestuser(access.getName()).size() != 0) {
            revokeTestuser(access);
        }
        //then
        grantTestuser(access);
        Collection<WindowsAccountAccess> actualCreated = listTestuser(access.getName());
        revokeTestuser(access);
        Collection<WindowsAccountAccess> actualDeleted = listTestuser(access.getName());

        Assert.assertEquals(expectedCreated,actualCreated);
        Assert.assertTrue(actualDeleted.size() == 0);

    }


    public void grantTestuser(WindowsAccountAccess access) throws Exception {
        TrustManager trustManager = new TrustAllTrustManager();
        SocketFactory sslSocketFactory = new SSLUtil(trustManager).createSSLSocketFactory("SSLv3");
        LDAPConnection ldapConnection = new LDAPConnection(sslSocketFactory, configHost, configPort, configBindDn, configBindPass);

        WindowsAccountAccessManager target = new WindowsAccountAccessManager(ldapConnection, configCnUsers, configDefaultPass);
        target.grant(access);
        ldapConnection.close();

    }

    public void revokeTestuser(WindowsAccountAccess access) throws Exception {
        TrustManager trustManager = new TrustAllTrustManager();
        SocketFactory sslSocketFactory = new SSLUtil(trustManager).createSSLSocketFactory("SSLv3");
        LDAPConnection ldapConnection = new LDAPConnection(sslSocketFactory, configHost, configPort, configBindDn, configBindPass);

        WindowsAccountAccessManager target = new WindowsAccountAccessManager(ldapConnection, configCnUsers, configDefaultPass);
        target.revoke(access);
        ldapConnection.close();

    }

    public Collection<WindowsAccountAccess> listTestuser(String testUser) throws Exception {
        WindowsAccountAccessFilter filter = new WindowsAccountAccessFilter(testUser);

        TrustManager trustManager = new TrustAllTrustManager();
        SocketFactory sslSocketFactory = new SSLUtil(trustManager).createSSLSocketFactory("SSLv3");
        LDAPConnection ldapConnection = new LDAPConnection(sslSocketFactory, configHost, configPort, configBindDn, configBindPass);

        //then
        try {
            WindowsAccountAccessManager target = new WindowsAccountAccessManager(ldapConnection, configCnUsers, configDefaultPass);
            return target.list(filter);
        } finally {
            ldapConnection.close();
        }

    }

}