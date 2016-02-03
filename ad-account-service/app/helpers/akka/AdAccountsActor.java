package helpers.akka;

import akka.actor.UntypedActor;
import com.diosoft.uar.AccessManagerException;
import com.diosoft.uar.ldap.ad.WindowsAccountAccess;
import com.diosoft.uar.ldap.ad.WindowsAccountAccessFilter;
import com.diosoft.uar.ldap.ad.WindowsAccountAccessManager;
import com.google.inject.Inject;
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
        if (message instanceof AdAccountsActorProtocol.GetAllAdAccounts) {
            Object result = getAccountsImpl("*");
            sender().tell(result, self());
        } else if (message instanceof AdAccountsActorProtocol.GetAdAccountById) {
            Object result = getAccountsImpl(((AdAccountsActorProtocol.GetAdAccountById) message).id);
            sender().tell(result, self());
        } else if (message instanceof AdAccountsActorProtocol.CreateAdAccount) {
            Object result = createAccountImpl((AdAccountsActorProtocol.CreateAdAccount) message);
            sender().tell(result, self());
        } else if (message instanceof AdAccountsActorProtocol.DeleteAdAccount) {
            Object result = deleteAccountImpl((AdAccountsActorProtocol.DeleteAdAccount) message);
            sender().tell(result, self());
        } else {
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

    private Object createAccountImpl(AdAccountsActorProtocol.CreateAdAccount createMessage) throws AccessManagerException {
        Object result;
        try {
            WindowsAccountAccess windowsAccountAccess = new WindowsAccountAccess(createMessage.id,
                    createMessage.firstName,
                    createMessage.lastName,
                    createMessage.email);

            accessManager.grant(windowsAccountAccess);
            result = Boolean.TRUE;
        } catch (Exception e) {
            LOG.error("Can't create account for " + createMessage.id, e);
            result = e;
        }
        return result;
    }

    private Object deleteAccountImpl(AdAccountsActorProtocol.DeleteAdAccount deleteMessage) throws AccessManagerException {
        Object result;
        try {
            WindowsAccountAccess access = new WindowsAccountAccess(deleteMessage.id,
                    deleteMessage.firstName,
                    deleteMessage.lastName,
                    deleteMessage.email);

            accessManager.revoke(access);
            result = Boolean.TRUE;
        } catch (Exception e) {
            LOG.error("Can't delete account for " + deleteMessage.id, e);
            result = e;
        }
        return result;
    }

}
