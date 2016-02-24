package helpers.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.diosoft.uam.AccessManagerException;
import com.diosoft.uam.db.entry.AuditLogEntry;
import com.diosoft.uam.db.AuditLogStorage;
import helpers.akka.AuditLogsActorProtocol.RegisterAuditLog;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class AuditLogsActorTest extends JavaTestKit {

    private static ActorSystem system;

    public AuditLogsActorTest() {
        super(system);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testActor_Optimistic() throws Exception {
        //expected
        Boolean expected = Boolean.TRUE;
        AuditLogEntry expectedEntry = new AuditLogEntry(1L, "userId", "application", "executor", "action");
        //given
        RegisterAuditLog message = new RegisterAuditLog(1L, "userId", "application", "executor", "action");

        //when
        AuditLogStorage mockAuditLogStorage = Mockito.mock(AuditLogStorage.class);

        //then
        ActorRef target = system.actorOf(Props.create(AuditLogsActor.class, mockAuditLogStorage));
        target.tell(message, getRef());

        //assert
        expectMsgEquals(expected);
        Mockito.verify(mockAuditLogStorage).saveAuditLogEntry(expectedEntry);
    }

    @Test
    public void testActor_StorageException() throws Exception {
        //expected
        Class<AccessManagerException> expected = AccessManagerException.class;
        //given
        RegisterAuditLog message = new RegisterAuditLog(1L, "userId", "application", "executor", "action");

        //when
        AuditLogStorage mockAuditLogStorage = Mockito.mock(AuditLogStorage.class);
        Mockito.doThrow(new AccessManagerException("test"))
                .when(mockAuditLogStorage).saveAuditLogEntry(new AuditLogEntry(1L,"userId", "application", "executor", "action"));

        //then
        ActorRef target = system.actorOf(Props.create(AuditLogsActor.class, mockAuditLogStorage));
        target.tell(message, getRef());

        //assert
        expectMsgClass(expected);
    }

}
