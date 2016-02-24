package helpers.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.diosoft.uam.AccessManagerException;
import com.diosoft.uam.db.AccountStorage;
import com.diosoft.uam.db.entry.AccountEntry;
import helpers.akka.AdAccountStorageActorProtocol.DeleteAdAccountInfo;
import helpers.akka.AdAccountStorageActorProtocol.SaveAdAccountInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class AdAccountStorageActorTest extends JavaTestKit {

    private static ActorSystem system;

    @BeforeClass
    public static void setUp() throws Exception {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    public AdAccountStorageActorTest() {
        super(system);
    }

    @Test
    public void testOnReceive_SaveAdAccountInfo_Optimistic() throws Exception {
        //expected
        Boolean expected = Boolean.TRUE;

        //given
        SaveAdAccountInfo saveMessage = new SaveAdAccountInfo("testuser", "User", "Test", "test@mantl.io");

        //when
        AccountStorage mockAccountStorage = Mockito.mock(AccountStorage.class);

        //then
        ActorRef target = system.actorOf(Props.create(AdAccountStorageActor.class, mockAccountStorage));
        target.tell(saveMessage, getRef());

        //assert
        expectMsgEquals(expected);
        Mockito.verify(mockAccountStorage).saveAccount(new AccountEntry("testuser", "User", "Test", "test@mantl.io"));

    }

    @Test
    public void testOnReceive_SaveAdAccountInfo_StorageException() throws Exception {
        //expected
        Class<AccessManagerException> expected = AccessManagerException.class;

        //given
        SaveAdAccountInfo saveMessage = new SaveAdAccountInfo("testuser", "User", "Test", "test@mantl.io");

        //when
        AccountStorage mockAccountStorage = Mockito.mock(AccountStorage.class);
        Mockito.doThrow(new AccessManagerException("test", null))
                .when(mockAccountStorage).saveAccount(new AccountEntry("testuser", "User", "Test", "test@mantl.io"));

        //then
        ActorRef target = system.actorOf(Props.create(AdAccountStorageActor.class, mockAccountStorage));
        target.tell(saveMessage, getRef());

        //assert
        expectMsgClass(expected);

    }

    //DELETE
    @Test
    public void testOnReceive_DeleteAdAccountInfo_Optimistic() throws Exception {
        //expected
        Boolean expected = Boolean.TRUE;

        //given
        DeleteAdAccountInfo deleteMessage = new DeleteAdAccountInfo("testuser");

        //when
        AccountStorage mockAccountStorage = Mockito.mock(AccountStorage.class);

        //then
        ActorRef target = system.actorOf(Props.create(AdAccountStorageActor.class, mockAccountStorage));
        target.tell(deleteMessage, getRef());

        //assert
        expectMsgEquals(expected);
        Mockito.verify(mockAccountStorage).deleteAccount("testuser");

    }

    @Test
    public void testOnReceive_DeleteAdAccountInfo_StorageException() throws Exception {
        //expected
        Class<AccessManagerException> expected = AccessManagerException.class;

        //given
        DeleteAdAccountInfo deleteMessage = new DeleteAdAccountInfo("testuser");

        //when
        AccountStorage mockAccountStorage = Mockito.mock(AccountStorage.class);
        Mockito.doThrow(new AccessManagerException("test", null))
                .when(mockAccountStorage).deleteAccount("testuser");

        //then
        ActorRef target = system.actorOf(Props.create(AdAccountStorageActor.class, mockAccountStorage));
        target.tell(deleteMessage, getRef());

        //assert
        expectMsgClass(expected);

    }

    @Test
    public void testOnReceive_Unhandled() throws Exception {
        //given
        String aMessage = "unhandled";

        //when
        AccountStorage mockAccountStorage = Mockito.mock(AccountStorage.class);

        //then
        ActorRef target = system.actorOf(Props.create(AdAccountStorageActor.class, mockAccountStorage));
        target.tell(aMessage, getRef());

        //assert
        expectNoMsg();

    }

}