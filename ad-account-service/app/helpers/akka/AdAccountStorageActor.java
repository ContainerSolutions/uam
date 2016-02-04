package helpers.akka;

import akka.actor.UntypedActor;
import com.diosoft.uam.db.AccountStorage;
import com.diosoft.uam.db.entry.AccountEntry;
import com.google.inject.Inject;
import helpers.akka.AdAccountStorageActorProtocol.*;
import play.Logger;
import play.Logger.ALogger;

public class AdAccountStorageActor extends UntypedActor {
	private static final ALogger LOG = Logger.of(AdAccountStorageActor.class);

	private final AccountStorage accountStorage;

	@Inject
	public AdAccountStorageActor(AccountStorage accountStorage) {
		this.accountStorage = accountStorage;
	}

	public void onReceive(Object msg) throws Exception {
		if (msg instanceof SaveAdAccountInfo) {
			Object result = createAccountImpl((SaveAdAccountInfo) msg);
			sender().tell(result, self());
		} else if (msg instanceof DeleteAdAccountInfo) {
			Object result = removeAccountImpl((DeleteAdAccountInfo) msg);
			sender().tell(result, self());
		} else {
			LOG.warn("Unhandled msg: " + msg.getClass());
			unhandled(msg);
		}
	}

	private Object createAccountImpl(SaveAdAccountInfo msg) {
		try {
			AccountEntry adAccount = new AccountEntry(msg.id, msg.firstName, msg.lastName, msg.email);
			accountStorage.saveAccount(adAccount);

			LOG.info("Successfully saved ad account info for " + msg.id);
			return Boolean.TRUE;
		} catch (Exception e) {
			LOG.error("Can't save ad account info for " + msg.id, e);
			return e;
		}
	}

	private Object removeAccountImpl(DeleteAdAccountInfo msg) {
		try {
			accountStorage.deleteAccount(msg.id);

			LOG.info("Successfully deleted ad account info for " + msg.id);
			return Boolean.TRUE;
		} catch (Exception e) {
			LOG.error("Can't delete ad account info for " + msg.id, e);
			return e;
		}
	}


}