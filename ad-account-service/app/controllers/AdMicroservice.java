package controllers;

import com.diosoft.uar.ldap.ad.WindowsAccountAccess;
import com.diosoft.uar.ldap.ad.WindowsAccountAccessFilter;
import com.diosoft.uar.ldap.ad.WindowsAccountAccessManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.main;

import java.util.Collection;

public class AdMicroservice extends Controller {

    public static final Logger.ALogger LOG = Logger.of(AdMicroservice.class);

    private WindowsAccountAccessManager accessManager;

    @Inject
    public AdMicroservice(WindowsAccountAccessManager adAccessManager) {
        this.accessManager = adAccessManager;
    }

    public Result getAccounts() {
        WindowsAccountAccessFilter filter = new WindowsAccountAccessFilter("*");
        try {
            Collection<WindowsAccountAccess> accounts = accessManager.list(filter);
            if(accounts.size() < 1) {
                LOG.warn("No account found in AD");
                return notFound();
            } else {
                LOG.info("Get " + accounts.size() + " accounts in AD");
                ArrayNode result = toJsonArray(accounts);
                return ok(result);
            }
        } catch (Exception e) {
            LOG.error("Can't get accounts", e);
            return internalServerError(getInternalErrorMessage(e));
        }
    }

    public Result getAccount(String login) {
        WindowsAccountAccessFilter filter = new WindowsAccountAccessFilter(login);
        try {
            Collection<WindowsAccountAccess> accounts = accessManager.list(filter);
            if(accounts.size() < 1) {
                LOG.info("No account found for login " + login);
                return notFound();
            } else if(accounts.size() == 1){
                LOG.info("Found account for login " + login);
                ObjectNode result = toJsonObject(accounts.iterator().next());
                return ok(result);
            } else {
                String warnMessage = "Found " + accounts.size() + " accounts for login " + login;
                LOG.warn(warnMessage);
                return internalServerError(warnMessage);
            }
        } catch (Exception e) {
            LOG.error("Can't get account", e);
            return internalServerError(getInternalErrorMessage(e));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result deleteAccount() {
        JsonNode request = request().body().asJson();
        LOG.debug("AD account deletion request: " + request);

        WindowsAccountAccess account = fromJsonObject(request);
        try {
            accessManager.revoke(account);
            LOG.info("Successfully deleted account " + account.getLogin());
            return ok(toJsonObject(account));
        } catch (Exception e) {
            LOG.error("Can't delete account " + account.getLogin(), e);
            return internalServerError(getInternalErrorMessage(e));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result addAccount() {
        JsonNode request = request().body().asJson();
        LOG.debug("AD account creation request: " + request);

        WindowsAccountAccess account = fromJsonObject(request);
        try {
            accessManager.grant(account);
            LOG.info("Successfully added account " + account.getLogin());
            return ok(toJsonObject(account));
        } catch (Exception e) {
            LOG.error("Can't create account", e);
            return internalServerError(getInternalErrorMessage(e));
        }
    }

    public Result index() {
        LOG.debug("API console is shown");
        return ok(main.render("AD REST API"));
    }

    private String getInternalErrorMessage(Exception e) {
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
        accObjNode.put(WindowsAccountAccess.FIELD_LOGIN, account.getLogin());
        accObjNode.put(WindowsAccountAccess.FIELD_EMAIL, account.getEmail());
        return accObjNode;
    }

    private static WindowsAccountAccess fromJsonObject(JsonNode request) {
        String login = request.get(WindowsAccountAccess.FIELD_LOGIN).asText();
        String firstname = request.get(WindowsAccountAccess.FIELD_FIRST_NAME).asText();
        String lastname = request.get(WindowsAccountAccess.FIELD_LAST_NAME).asText();
        String email = request.get(WindowsAccountAccess.FIELD_EMAIL).asText();

        return new WindowsAccountAccess(login, firstname, lastname, email);
    }
}
