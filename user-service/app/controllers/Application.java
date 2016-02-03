package controllers;

import java.util.concurrent.TimeUnit;

import actors.*;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

import actors.UserEventsActor.GetUserEventsMessage;
import actors.GetUsersActor.GetUsersMessage;
import actors.RemoveUserActor.RemoveUserMessage;
import actors.CreateUserActor.CreateUserMessage;
import actors.UpdateUserActor.UpdateUserMessage;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import configurations.MantlConfigFactory;
import models.User;
import play.libs.F.Promise;
import play.Configuration;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

@Singleton
public class Application extends Controller
{

	private static final Timeout TIMEOUT = new Timeout(20, TimeUnit.SECONDS);
	private static final String serviceName = "userservice";
	private static final String consulUrlKey = "consul.url";
	private static final String orientDbUrlKey = "userservice/orientdb/url";

	private final ActorRef getUsersActor;
	private final ActorRef createUserActor;
	private final ActorRef updateUserActor;
	private final ActorRef removeUserActor;
	private final ActorRef userEventsActor;
	private final ActorRef auditLogsActor;

	@Inject
	public Application(ActorSystem system)
	{
		Configuration configuration = MantlConfigFactory.load(consulUrlKey, serviceName);
		OrientGraphFactory graphFactory = new OrientGraphFactory(configuration.getString(orientDbUrlKey));
		getUsersActor = system.actorOf(Props.create(GetUsersActor.class, graphFactory));
		createUserActor = system.actorOf(Props.create(CreateUserActor.class, graphFactory));
		updateUserActor = system.actorOf(Props.create(UpdateUserActor.class, graphFactory));
		removeUserActor = system.actorOf(Props.create(RemoveUserActor.class, graphFactory));
		userEventsActor = system.actorOf(Props.create(UserEventsActor.class, graphFactory));
		auditLogsActor = system.actorOf(Props.create(AuditLogsActor.class, graphFactory));

	}

	public Result index()
	{
		return ok("Your new application is ready.");
	}

	public Promise<Result> getAll()
	{
		return Promise.wrap(Patterns.ask(getUsersActor, new GetUsersMessage(), TIMEOUT))
		       .map(response -> ok(response.toString()));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> post()
	{
		User user = Json.fromJson(request().body().asJson(), User.class);
		auditLogsActor.tell(new AuditLogsActor.SaveAuditLog(
		                        1L,
		                        user.id,
		                        "UAR",
		                        "admin",
		                        "create"
		                    ),
		                    null
		                   );


		return Promise.wrap(Patterns.ask(createUserActor, new CreateUserMessage(user), TIMEOUT))
		       .map(response -> StringUtils.equals("Ok", response.toString()) ? created()
		            : internalServerError(response.toString()));
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> update(String id)
	{
		return Promise.wrap(Patterns.ask(updateUserActor, new UpdateUserMessage(id, Json.fromJson(request().body().asJson(), User.class)), TIMEOUT))
		       .map(response -> StringUtils.equals("Ok", response.toString()) ? ok()
		            : internalServerError(response.toString()));
	}

	public Promise<Result> delete(String id)
	{
		auditLogsActor.tell(new AuditLogsActor.SaveAuditLog(
		                        1L,
		                        id,
		                        "UAR",
		                        "admin",
		                        "delete"
		                    ),
		                    null
		                   );


		return Promise.wrap(Patterns.ask(removeUserActor, new RemoveUserMessage(id), TIMEOUT))
		       .map(response -> StringUtils.equals("Ok", response.toString()) ? noContent()
		            : internalServerError(response.toString()));
	}

	public Promise<Result> events(String id)
	{
		return Promise.wrap(Patterns.ask(userEventsActor, new GetUserEventsMessage(id), TIMEOUT))
		       .map(response ->
		{
			if (response instanceof Throwable)
			{
				return internalServerError(((Throwable) response).getMessage());
			}
			else {
				return ok(response.toString());
			}
		}
		           );
	}

}
