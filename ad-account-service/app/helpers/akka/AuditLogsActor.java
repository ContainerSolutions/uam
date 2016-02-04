package helpers.akka;

import akka.actor.UntypedActor;
import com.diosoft.uam.db.AuditLogStorage;
import com.diosoft.uam.db.entry.AuditLogEntry;
import com.google.inject.Inject;
import helpers.akka.AuditLogsActorProtocol.RegisterAuditLog;
import play.Logger;

public class AuditLogsActor extends UntypedActor {

    private static final Logger.ALogger LOG = Logger.of(AuditLogsActor.class);

    private final AuditLogStorage auditLogStorage;

    @Inject
    public AuditLogsActor(AuditLogStorage auditLogStorage) {
        this.auditLogStorage = auditLogStorage;
    }

    public void onReceive(Object message) throws Exception {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Message received: " + message);
        }
        if (message instanceof AuditLogsActorProtocol.RegisterAuditLog) {
            Object result = registerAuditActionImpl((RegisterAuditLog) message);
            sender().tell(result, self());
        } else {
            LOG.warn("Unhandled message: " + message);
            unhandled(message);
        }
    }

    private Object registerAuditActionImpl(RegisterAuditLog message) {
        try {
            AuditLogEntry auditEntry = new AuditLogEntry(
                    message.requestNumber,
                    message.userId,
                    message.application,
                    message.executor,
                    message.action);
            auditLogStorage.saveAuditLogEntry(auditEntry);
            LOG.info("Successfully registered audit action for " + message.userId);
            return Boolean.TRUE;
        } catch (Exception e) {
            LOG.error("Can't register audit action log for " + message.userId, e);
            return e;
        }
    }

}
