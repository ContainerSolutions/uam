package controllers;

import akka.dispatch.Futures;
import akka.util.Timeout;
import com.diosoft.uam.ldap.ad.WindowsAccountAccess;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import play.Logger;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.Future;
import views.html.main;
import akka.actor.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static helpers.akka.AdAccountsActorProtocol.*;
import static helpers.akka.AdAccountStorageActorProtocol.*;
import static helpers.akka.AuditLogsActorProtocol.*;
import static akka.pattern.Patterns.ask;

@Singleton
public class AdMicroservice extends Controller {

    private static final Logger.ALogger LOG = Logger.of(AdMicroservice.class);

    private ActorSystem system;
    private ActorRef adActor;
    private ActorRef adStorageActor;
    private ActorRef adAuditActor;

    private Timeout timeout;

    @Inject
    public AdMicroservice(ActorSystem system,
                          @Named("adActor") ActorRef adActor,
                          @Named("adStorageActor") ActorRef adStorageActor,
                          @Named("adAuditActor") ActorRef adAuditActor) {
        this.system = system;
        this.adActor = adActor;
        this.adStorageActor = adStorageActor;
        this.adAuditActor = adAuditActor;

        this.timeout = new Timeout(30, TimeUnit.SECONDS);
    }

    public Result index() {
        LOG.debug("API console is shown");
        return ok(main.render("AD REST API"));
    }

    public Promise<Result> getAccounts() {
        return Promise.wrap(ask(adActor, new GetAllAdAccounts(), timeout)).map(response -> {
                    if (response instanceof Throwable) {
                        Throwable exception = (Throwable) response;
                        LOG.error("Can't get accounts", exception);
                        return internalServerError(getInternalErrorMessage(exception));
                    } else {
                        @SuppressWarnings("unchecked")
                        Collection<WindowsAccountAccess> accounts = (Collection<WindowsAccountAccess>) response;
                        if (accounts.size() < 1) {
                            LOG.warn("No account found in AD");
                            return notFound();
                        } else {
                            LOG.info("Get " + accounts.size() + " accounts in AD");
                            ArrayNode result = toJsonArray(accounts);
                            return ok(result);
                        }
                    }
                }
        );
    }

    public Promise<Result> getAccount(String id) {
        return Promise.wrap(ask(adActor, new GetAdAccountById(id), timeout)).map(response -> {
                    if (response instanceof Throwable) {
                        Throwable exception = (Throwable) response;
                        LOG.error("Can't get account", exception);
                        return internalServerError(getInternalErrorMessage(exception));
                    } else {
                        @SuppressWarnings("unchecked")
                        Collection<WindowsAccountAccess> accounts = (Collection<WindowsAccountAccess>) response;
                        if (accounts.size() < 1) {
                            LOG.info("No account found for id " + id);
                            return notFound();
                        } else if (accounts.size() == 1) {
                            LOG.info("Found account for id " + id);
                            ObjectNode result = toJsonObject(accounts.iterator().next());
                            return ok(result);
                        } else {
                            String warnMessage = "Found " + accounts.size() + " accounts for id " + id;
                            LOG.warn(warnMessage);
                            return internalServerError(warnMessage);
                        }
                    }
                }
        );
    }

    @SuppressWarnings("Duplicates")
    @BodyParser.Of(BodyParser.Json.class)
    public Promise<Result> deleteAccount() {
        JsonNode jsonRequest = request().body().asJson();
        LOG.debug("AD account deletion request: " + jsonRequest);

        DeleteAdAccount deleteAccountMessage = Json.fromJson(jsonRequest, DeleteAdAccount.class);
        String userId = deleteAccountMessage.id;
        DeleteAdAccountInfo deleteAccountInfoMessage = new DeleteAdAccountInfo(userId);
        //TODO get executor based on auth info
        RegisterAuditLog saveAuditLogMessage = new RegisterAuditLog(1L, userId, "AD", "admin", "delete");

        List<Future<Object>> futures = new ArrayList<>();
        futures.add(ask(adActor, deleteAccountMessage, timeout));
        futures.add(ask(adStorageActor, deleteAccountInfoMessage, timeout));
        futures.add(ask(adAuditActor, saveAuditLogMessage, timeout));

        return Promise.wrap(Futures.sequence(futures, system.dispatcher()))
                .map(responses -> handleDeleteAdAccountActorsResult(responses,deleteAccountMessage.firstName,
                        deleteAccountMessage.lastName,
                        userId,
                        deleteAccountMessage.email))
                .map(response -> {
                    if (response instanceof Throwable) {
                        Throwable exception = (Throwable) response;
                        //TODO process and test account not found case
                        LOG.error("Can't delete account " + userId, exception);
                        return internalServerError(getInternalErrorMessage(exception));
                    } else if (response instanceof Boolean) {
                        LOG.info("Successfully deleted account " + userId);
                        return ok();
                    } else {
                        LOG.error("Unexpected response for account " + userId + " delete request: " + response);
                        return internalServerError(response.toString());
                    }
                }
        );
    }

    @SuppressWarnings("Duplicates")
    @BodyParser.Of(BodyParser.Json.class)
    public Promise<Result> addAccount() {
        JsonNode jsonRequest = request().body().asJson();
        LOG.debug("AD account creation request: " + jsonRequest);

        CreateAdAccount createAccountMessage = Json.fromJson(jsonRequest, CreateAdAccount.class);
        String userId = createAccountMessage.id;
        SaveAdAccountInfo saveAccountInfoMessage = new SaveAdAccountInfo(userId,
                createAccountMessage.firstName,
                createAccountMessage.lastName,
                createAccountMessage.email);
        //TODO get executor based on auth info
        RegisterAuditLog saveAuditLogMessage = new RegisterAuditLog(1L, userId, "AD", "admin", "create");

        List<Future<Object>> futures = new ArrayList<>();
        futures.add(ask(adActor, createAccountMessage, timeout));
        futures.add(ask(adStorageActor, saveAccountInfoMessage, timeout));
        futures.add(ask(adAuditActor, saveAuditLogMessage, timeout));

        return Promise.wrap(Futures.sequence(futures, system.dispatcher()))
                .map(responses -> handleCreateAdAccountActorsResult(responses,
                        createAccountMessage.firstName,
                        createAccountMessage.lastName,
                        userId,
                        createAccountMessage.email))
                .map(response -> {
                    if (response instanceof Throwable) {
                        Throwable exception = (Throwable) response;
                        //TODO process and test account already exists case
                        LOG.error("Can't create account " + userId, exception);
                        return internalServerError(getInternalErrorMessage(exception));
                    } else if (response instanceof Boolean) {
                        LOG.info("Successfully created account " + userId);
                        return ok();
                    } else {
                        LOG.error("Unexpected response for account " + userId + " create request: " + response);
                        return internalServerError(response.toString());
                    }
                });
    }

    @SuppressWarnings("Duplicates")
    private Object handleCreateAdAccountActorsResult(Iterable<Object> responses, String firstName, String lastName, String userId, String email) {
        Iterator<Object> it = responses.iterator();
        Object adActorResult = it.next();
        Object adStorageActorResult = it.next();
        Object adAuditActorResult = it.next();

        if (adActorResult instanceof Throwable
                || adStorageActorResult instanceof Throwable
                || adAuditActorResult instanceof Throwable) {
            Object result = null;
            if(adAuditActorResult instanceof Boolean) {
//TODO add update log entry with error
//                adAuditActor.tell(updateAuditLogMessage, null);
                LOG.debug("Updated audit entry with exception info");
            } else {
                result = adStorageActorResult;
            }
            if(adStorageActorResult instanceof Boolean) {
//TODO add rollback action (consider account already exist case)
//                adStorageActor.tell(new DeleteAdAccountInfo(userId), null);
                LOG.debug("Rolled back account info record change");
            } else {
                result = adStorageActorResult;
            }
            if(adActorResult instanceof Boolean) {
//TODO add rollback action (consider account already exist case)
//                adActor.tell(new DeleteAdAccount(firstName, lastName, userId, email), null);
                LOG.debug("Rolled back created account");
            } else {
                result = adActorResult;
            }
            return result;
        } else {
            return Boolean.TRUE;
        }
    }

    @SuppressWarnings("Duplicates")
    private Object handleDeleteAdAccountActorsResult(Iterable<Object> responses, String firstName, String lastName, String userId, String email) {
        Iterator<Object> it = responses.iterator();
        Object adActorResult = it.next();
        Object adStorageActorResult = it.next();
        Object adAuditActorResult = it.next();

        if (adActorResult instanceof Throwable
                || adStorageActorResult instanceof Throwable
                || adAuditActorResult instanceof Throwable) {
            Object result = null;
            if(adAuditActorResult instanceof Boolean) {
//TODO add update log entry with error
//                adAuditActor.tell(updateAuditLogMessage, null);
                LOG.debug("Updated audit entry with exception info");
            } else {
                result = adStorageActorResult;
            }
            if(adStorageActorResult instanceof Boolean) {
//TODO add rollback action (consider account not found case)
//                adStorageActor.tell(new SaveAdAccountInfo(userId, firstName, lastName, email), null);
                LOG.debug("Rolled back account info record change");
            } else {
                result = adStorageActorResult;
            }
            if(adActorResult instanceof Boolean) {
//TODO add rollback action (consider account not found case)
//                adActor.tell(new CreateAdAccount(firstName, lastName, userId, email), null);
                LOG.debug("Rolled back deleted account");
            } else {
                result = adActorResult;
            }
            return result;
        } else {
            return Boolean.TRUE;
        }
    }

    private String getInternalErrorMessage(Throwable e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }

    private static ArrayNode toJsonArray(Collection<WindowsAccountAccess> accounts) {
        ArrayNode result = Json.newArray();
        for (WindowsAccountAccess account : accounts) {
            ObjectNode accObjNode = toJsonObject(account);
            result.add(accObjNode);
        }
        return result;
    }

    private static ObjectNode toJsonObject(WindowsAccountAccess account) {
        ObjectNode accObjNode = Json.newObject();
        accObjNode.put(WindowsAccountAccess.FIELD_FIRST_NAME, account.getFirstName());
        accObjNode.put(WindowsAccountAccess.FIELD_LAST_NAME, account.getLastName());
        accObjNode.put(WindowsAccountAccess.FIELD_ID, account.getId());
        accObjNode.put(WindowsAccountAccess.FIELD_EMAIL, account.getEmail());
        return accObjNode;
    }

}
