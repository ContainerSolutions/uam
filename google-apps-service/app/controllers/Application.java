package controllers;

import play.Configuration;
import play.libs.Json;
import play.libs.ws.WS;
import play.mvc.Controller;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import play.mvc.BodyParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import actors.Kernel;
import actors.UsersActor;
import play.libs.F.Promise;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

public class Application extends Controller
{
	private final String DIRECTORY_REST = Configuration.root().getString("google.directory.rest.url") ;
	private static final Timeout TIMEOUT = new Timeout(Duration.create(10, TimeUnit.SECONDS));
	private final String domain = "dio-soft.com";

	// add parameter for email information  liz@example.com
	private final Kernel kernel;

	public Application()
	{
		try
		{
			kernel = new Kernel();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(0);
			throw new IllegalStateException("Could not init Kernel " + ex.getMessage());
		}
	}

	public Promise<Result> getUserInfo(String email)
	{
		//TODO get user email by id
		return Promise.wrap(ask(kernel.users(), new UsersActor.GetUser(domain, email), TIMEOUT)).map(
		           response ->
		{
			if (response.equals("404"))
			{
				return notFound(email);

			}
			else{
				return ok(response.toString());
			}
		}
		       );

	}

	public Promise<Result> deleteUser(String email)
	{
		//TODO get user email by id
		return Promise.wrap(ask(kernel.users(), new UsersActor.DeleteUser(domain, email), TIMEOUT)).map(
		           response -> ok(response.toString())
		       );

	}

	@BodyParser.Of(BodyParser.Json.class)
	public Promise<Result> insert()
	{
		JsonNode body = request().body().asJson();
		String firstName = body.findPath("firstName").textValue();
		String lastName = body.findPath("lastName").textValue();
		String email = body.findPath("email").textValue();
		String password = body.findPath("password").textValue();
		String id = body.findPath("id").textValue();

		UsersActor.InsertUser insertUser = new UsersActor.InsertUser(
		    domain,
		    id,
		    email,
		    firstName,
		    lastName,
		    password
		);

		return Promise.wrap(ask(kernel.users(), insertUser, TIMEOUT)).map(
		           response -> ok(response.toString())
		       );
	}

}
