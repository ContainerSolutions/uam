package com.diosoft.uam.db;

import com.diosoft.uam.AccessManagerException;
import com.diosoft.uam.db.entry.AuditLogEntry;
import com.google.inject.Inject;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;
import play.Logger;

import java.util.Date;

public class AuditLogStorage {
    private static final Logger.ALogger LOG = Logger.of(AuditLogStorage.class);

    private final OrientGraphFactory graphFactory;

    @Inject
    public AuditLogStorage(OrientGraphFactory graphFactory) {
        this.graphFactory = graphFactory;
    }

    public void saveAuditLogEntry(AuditLogEntry entry) throws AccessManagerException {
        OrientGraph graph = graphFactory.getTx();
        try {
            OrientVertex auditLogVertex = graph.addVertex(AuditLogEntry.VERTEX_AUDIT_LOG, "auditlog");
//            OSequence seq = graph.getRawGraph().getMetadata().getSequenceLibrary().getSequence("rnseq");
//            auditLogVertex.setProperty(AuditLogEntry.PROPERTY_REQUEST_NUMBER, seq.next());
            auditLogVertex.setProperty(AuditLogEntry.PROPERTY_USER_ID, entry.getUserId());
            auditLogVertex.setProperty(AuditLogEntry.PROPERTY_DATETIME, new Date());
            auditLogVertex.setProperty(AuditLogEntry.PROPERTY_APPLICATION, entry.getApplication());
            auditLogVertex.setProperty(AuditLogEntry.PROPERTY_EXECUTOR, entry.getExecutor());
            auditLogVertex.setProperty(AuditLogEntry.PROPERTY_ACTION, entry.getAction());

            graph.commit();
            LOG.info("Successfully saved audit log entry for " + entry.getUserId());
        } catch (Exception e) {
            graph.rollback();
            String errorMessage = "Can't save audit log entry for " + entry.getUserId();
            LOG.error(errorMessage, e);
            throw new AccessManagerException(errorMessage, e);
        } finally {
            graph.shutdown();
        }
    }

}
