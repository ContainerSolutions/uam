package controllers;

import akka.util.Timeout;
import com.diosoft.uar.ldap.ad.WindowsAccountAccess;
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
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static helpers.akka.AdAccountsActorProtocol.*;
import static akka.pattern.Patterns.ask;

@Singleton
public class AdMicroservice extends Controller {

    private static final Logger.ALogger LOG = Logger.of(AdMicroservice.class);

    private ActorRef adActor;
    private Timeout timeout;

    @Inject
    public AdMicroservice(@Named("adActor") ActorRef adActor) {
        this.adActor = adActor;
        this.timeout = new Timeout(2, TimeUnit.SECONDS);
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

        DeleteAdAccount deleteMessage = Json.fromJson(jsonRequest, DeleteAdAccount.class);
        return Promise.wrap(ask(adActor, deleteMessage, timeout)).map(response -> {
                    if (response instanceof Throwable) {
                        Throwable exception = (Throwable) response;
                        //TODO process and test account not found case
                        LOG.error("Can't delete account " + deleteMessage.id, exception);
                        return internalServerError(getInternalErrorMessage(exception));
                    } else if (response instanceof Boolean) {
                        LOG.info("Successfully deleted account " + deleteMessage.id);
                        return ok();
                    } else {
                        LOG.error("Unexpected response for account " + deleteMessage.id + " delete request: " + response);
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

        CreateAdAccount createMessage = Json.fromJson(jsonRequest, CreateAdAccount.class);
        return Promise.wrap(ask(adActor, createMessage, timeout)).map(response -> {
                    if (response instanceof Throwable) {
                        Throwable exception = (Throwable) response;
                        //TODO process and test account already exists case
                        LOG.error("Can't create account " + createMessage.id, exception);
                        return internalServerError(getInternalErrorMessage(exception));
                    } else if (response instanceof Boolean) {
                        LOG.info("Successfully created account " + createMessage.id);
                        return ok();
                    } else {
                        LOG.error("Unexpected response for account " + createMessage.id + " create request: " + response);
                        return internalServerError(response.toString());
                    }
                }
        );
    }

    public Result index() {
        LOG.debug("API console is shown");
        return ok(main.render("AD REST API"));
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
