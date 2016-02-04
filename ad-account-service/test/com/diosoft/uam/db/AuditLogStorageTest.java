package com.diosoft.uam.db;

import com.diosoft.uam.db.entry.AuditLogEntry;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

public class AuditLogStorageTest {

    @Test
    public void testSaveAuditLogEntry_Optimistic() throws Exception {
        //given
        AuditLogEntry auditEntry = new AuditLogEntry(1L, "userId", "application", "executor", "action");
        //when
        OrientGraphFactory mockGraphFactory = Mockito.mock(OrientGraphFactory.class);
        OrientGraph mockGraph = Mockito.mock(OrientGraph.class);
        OrientVertex mockVertex = Mockito.mock(OrientVertex.class);
        Mockito.when(mockGraphFactory.getTx()).thenReturn(mockGraph);
        Mockito.when(mockGraph.addVertex("AuditLog", "auditlog")).thenReturn(mockVertex);

        //then
        AuditLogStorage target = new AuditLogStorage(mockGraphFactory);
        target.saveAuditLogEntry(auditEntry);

        //assert
        Mockito.verify(mockGraph).commit();
//        Mockito.verify(mockVertex).setProperty("request_number", 1L);
        Mockito.verify(mockVertex).setProperty("user_id", "userId");
        Mockito.verify(mockVertex).setProperty(Mockito.eq("datetime"), Mockito.any(Date.class));
        Mockito.verify(mockVertex).setProperty("application", "application");
        Mockito.verify(mockVertex).setProperty("executor", "executor");
        Mockito.verify(mockVertex).setProperty("action", "action");
    }
}