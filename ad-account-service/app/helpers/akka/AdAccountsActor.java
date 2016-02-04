package helpers.akka;

import akka.actor.UntypedActor;
import com.diosoft.uam.AccessManagerException;
import com.diosoft.uam.ldap.ad.WindowsAccountAccess;
import com.diosoft.uam.ldap.ad.WindowsAccountAccessFilter;
import com.diosoft.uam.ldap.ad.WindowsAccountAccessManager;
import com.google.inject.Inject;
import helpers.akka.AdAccountsActorProtocol.*;
import play.Logger;

public class AdAccountsActor extends UntypedActor {

    private static final Logger.ALogger LOG = Logger.of(AdAccountsActor.class);

    private WindowsAccountAccessManager accessManager;

    @Inject
    public AdAccountsActor(WindowsAccountAccessManager accessManager) {
        this.accessManager = accessManager;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(LOG.isDebugEnabled()) {
            LOG.debug("Message received: " + message);
        }
        if (message instanceof GetAllAdAccounts) {
            Object result = getAccountsImpl("*");
            sender().tell(result, self());
        } else if (message instanceof GetAdAccountById) {
            Object result = getAccountsImpl(((GetAdAccountById) message).id);
            sender().tell(result, self());
        } else if (message instanceof CreateAdAccount) {
            Object result = createAccountImpl((CreateAdAccount) message);
            sender().tell(result, self());
        } else if (message instanceof DeleteAdAccount) {
            Object result = deleteAccountImpl((DeleteAdAccount) message);
            sender().tell(result, self());
        } else {
            LOG.warn("Unhandled message: " + message);
            unhandled(message);
        }
    }

    private Object getAccountsImpl(String id) throws AccessManagerException {
        Object result;
        try {
            result = accessManager.list(new WindowsAccountAccessFilter(id));
        } catch (Exception e) {
            LOG.error("Can't get accounts for " + id, e);
            result = e;
        }
        return result;
    }

    private Object createAccountImpl(CreateAdAccount createMessage) throws AccessManagerException {
        Object result;
        try {
            WindowsAccountAccess windowsAccountAccess = new WindowsAccountAccess(createMessage.id,
                    createMessage.firstName,
                    createMessage.lastName,
                    createMessage.email);

            accessManager.grant(windowsAccountAccess);
            LOG.info("Successfully created ad account for " + createMessage.id);
            result = Boolean.TRUE;
        } catch (Exception e) {
            LOG.error("Can't create account for " + createMessage.id, e);
            result = e;
        }
        return result;
    }

    private Object deleteAccountImpl(DeleteAdAccount deleteMessage) throws AccessManagerException {
        Object result;
        try {
            WindowsAccountAccess access = new WindowsAccountAccess(deleteMessage.id,
                    deleteMessage.firstName,
                    deleteMessage.lastName,
                    deleteMessage.email);

            accessManager.revoke(access);
            LOG.info("Successfully revoked ad account for " + deleteMessage.id);
            result = Boolean.TRUE;
        } catch (Exception e) {
            LOG.error("Can't delete account for " + deleteMessage.id, e);
            result = e;
        }
        return result;
    }

}
