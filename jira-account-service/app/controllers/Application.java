package controllers;

import static akka.pattern.Patterns.ask;

import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import actors.repository.AuditLogsActor;
import actors.jira.CreateAccountActor.CreateJiraAccountMessage;
import actors.jira.GetAccountActor.GetAccount;
import actors.jira.GetAllAccountsActor.GetAllAccounts;
import actors.jira.RemoveAccountActor.RemoveAccount;
import akka.actor.ActorRef;
import akka.util.Timeout;
import models.JiraUser;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;

@Singleton
public class Application extends Controller {
	private static final Timeout TIMEOUT = new Timeout(Duration.create(10, TimeUnit.SECONDS));

	private final ActorRef createActor;

	private final ActorRef getAllActor;
	private final ActorRef getAccountActor;

	private final ActorRef removeAccountActor;
	private final ActorRef auditLogsActor;

	@Inject
	public Application(@Named("createActor") ActorRef createActor, @Named("getAllActor") ActorRef getAllActor,
			@Named("getAccountActor") ActorRef getAccountActor,
			@Named("removeAccountActor") ActorRef removeAccountActor,
			@Named("auditLogsActor") ActorRef auditLogsActor) {
		this.createActor = createActor;
		this.getAllActor = getAllActor;
		this.getAccountActor = getAccountActor;
		this.removeAccountActor = removeAccountActor;
		this.auditLogsActor = auditLogsActor;
	}

	public Promise<Result> getAll() {
		return Promise.wrap(ask(getAllActor, new GetAllAccounts(), TIMEOUT)).map(this::handleActorResponse);
	}

	public Promise<Result> get(String name) {
		return Promise.wrap(ask(getAccountActor, new GetAccount(name), TIMEOUT)).map(this::handleActorResponse);
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> post() throws Exception {
		JiraUser user = Json.fromJson(request().body().asJson(), JiraUser.class);
		auditLogsActor.tell(new AuditLogsActor.SaveAuditLog(1L, user.id, "Jira", "admin", "create"), null);

		return Promise.wrap(ask(createActor, new CreateJiraAccountMessage(user), TIMEOUT))
				.map(response -> StringUtils.equals("Ok", response.toString()) ? created()
						: internalServerError(response.toString()));
	}

	public Promise<Result> delete(String name) throws Exception {
		auditLogsActor.tell(new AuditLogsActor.SaveAuditLog(1L, name, "Jira", "admin", "delete"), null);

		return Promise.wrap(ask(removeAccountActor, new RemoveAccount(name), TIMEOUT))
				.map(response -> StringUtils.equals("Ok", response.toString()) ? noContent()
						: internalServerError(response.toString()));
	}

	private Result handleActorResponse(Object response) {
		if (response instanceof Throwable) {
			return internalServerError(((Throwable) response).getMessage());
		}

		return ok(response.toString());
	}
}
