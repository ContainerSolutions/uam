package com.diosoft.uam.db;

import com.diosoft.uam.AccessManagerException;
import com.diosoft.uam.db.entry.AccountEntry;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

public class AccountStorageTest {

    @Test
    public void testSaveAccount_Optimistic() throws Exception {
        //given
        AccountEntry accountEntry = new AccountEntry("testuser", "User", "Test", "utest@mantl.io");

        //when
        OrientGraphFactory mockGraphFactory = Mockito.mock(OrientGraphFactory.class);
        OrientGraph mockGraph = Mockito.mock(OrientGraph.class);
        OrientVertex mockUserVertex = Mockito.mock(OrientVertex.class);
        OrientVertex mockAccountVertex = Mockito.mock(OrientVertex.class);
        Mockito.when(mockGraphFactory.getTx()).thenReturn(mockGraph);
        Mockito.when(mockGraph.getVerticesOfClass("User")).thenReturn(Arrays.asList(mockUserVertex));
        Mockito.when(mockGraph.addVertex("AdAccount", "adaccount")).thenReturn(mockAccountVertex);
        Mockito.when(mockUserVertex.getProperty("uniqueId")).thenReturn("testuser");

        //then
        AccountStorage target = new AccountStorage(mockGraphFactory);
        target.saveAccount(accountEntry);

        //assert
        Mockito.verify(mockGraph).addVertex("AdAccount", "adaccount");
        Mockito.verify(mockAccountVertex).setProperty("user_id", "testuser");
        Mockito.verify(mockAccountVertex).setProperty("first_name", "User");
        Mockito.verify(mockAccountVertex).setProperty("last_name", "Test");
        Mockito.verify(mockAccountVertex).setProperty("email", "utest@mantl.io");

        Mockito.verify(mockUserVertex).addEdge("hasAccount", mockAccountVertex);
        Mockito.verify(mockGraph).commit();
        Mockito.verify(mockGraph).shutdown();

    }

    @Test(expected = AccessManagerException.class)
    public void testSaveAccount_UserNotFound() throws Exception {
        //given
        AccountEntry accountEntry = new AccountEntry("testuser", "User", "Test", "utest@mantl.io");

        //when
        OrientGraphFactory mockGraphFactory = Mockito.mock(OrientGraphFactory.class);
        OrientGraph mockGraph = Mockito.mock(OrientGraph.class);
        Mockito.when(mockGraphFactory.getTx()).thenReturn(mockGraph);
        Mockito.when(mockGraph.getVerticesOfClass("User")).thenReturn(Collections.emptyList());

        //then
        AccountStorage target = new AccountStorage(mockGraphFactory);
        target.saveAccount(accountEntry);

        //assert
        Mockito.verify(mockGraph).rollback();
        Mockito.verify(mockGraph).shutdown();

    }

    @Test
    public void testDeleteAccount_Optimistic() throws Exception {
        //given
        String accountId = "testuser";

        //when
        OrientGraphFactory mockGraphFactory = Mockito.mock(OrientGraphFactory.class);
        OrientGraph mockGraph = Mockito.mock(OrientGraph.class);
        OrientVertex mockAccountVertex1 = Mockito.mock(OrientVertex.class);
        OrientVertex mockAccountVertex2 = Mockito.mock(OrientVertex.class);
        Mockito.when(mockGraphFactory.getTx()).thenReturn(mockGraph);
        Mockito.when(mockGraph.getVerticesOfClass("AdAccount")).thenReturn(Arrays.asList(mockAccountVertex1, mockAccountVertex2));
        Mockito.when(mockAccountVertex2.getProperty("user_id")).thenReturn("testuser");

        //then
        AccountStorage target = new AccountStorage(mockGraphFactory);
        target.deleteAccount(accountId);

        //assert
        Mockito.verify(mockGraph).removeVertex(mockAccountVertex2);
        Mockito.verify(mockGraph).commit();
        Mockito.verify(mockGraph).shutdown();

    }

    @Test(expected = AccessManagerException.class)
    public void testDeleteAccount_AccountNotFound() throws Exception {
        //given
        String accountId = "testuser";

        //when
        OrientGraphFactory mockGraphFactory = Mockito.mock(OrientGraphFactory.class);
        OrientGraph mockGraph = Mockito.mock(OrientGraph.class);
        Mockito.when(mockGraphFactory.getTx()).thenReturn(mockGraph);
        Mockito.when(mockGraph.getVerticesOfClass("AdAccount")).thenReturn(Collections.emptyList());

        //then
        AccountStorage target = new AccountStorage(mockGraphFactory);
        target.deleteAccount(accountId);

        //assert
        Mockito.verify(mockGraph).rollback();
        Mockito.verify(mockGraph).shutdown();

    }

}