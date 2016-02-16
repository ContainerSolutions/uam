package controllers;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import actors.AuditLogsActor;
import actors.jira.CreateAccountActor;
import actors.jira.GetAccountActor;
import actors.jira.GetAllAccountsActor;
import actors.jira.RemoveAccountActor;
import actors.repository.CreateAccountVertexActor;
import actors.repository.RemoveAccountVertexActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import configuration.ConsulConfigFactory;
import configuration.VaultHelper;
import configuration.VaultHelper.Credentials;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;
import play.libs.F;
import play.libs.akka.AkkaGuiceSupport;
import play.libs.ws.WSClient;

public class JiraGuiceModule extends AbstractModule implements AkkaGuiceSupport {

	private static final Logger.ALogger LOGGER = Logger.of(JiraGuiceModule.class);
	private static final String SERVICE_NAME = "jiraservice";
	private static final String JIRA_URL = "jiraservice/jira/url";
	private static final String JIRA_SERVICE_ACCOUNT = "jiraservice/jira";
	private static final String ORIENTDB_URL = "jiraservice/orientdb/url";
	private static final String ORIENTDB_SERVICE_ACCOUNT = "jiraservice/orientdb";

	@Override
	protected void configure() {
	}

	@Provides
	@Singleton
	@Named("consulConfiguration")
	public static Configuration loadConfiguration(Configuration configuration, WSClient client) {
		LOGGER.info("Building [consulConfiguration]");
		return ConsulConfigFactory.load(configuration, client, SERVICE_NAME);
	}

	@Provides
	@Singleton
	@Named("jiraServiceAccount")
	public Credentials getJiraServiceAccount(@Named("consulConfiguration") Configuration configuration) {
		LOGGER.info("Building [jiraServiceAccount]");
		String token = VaultHelper.generateToken(configuration);
		return VaultHelper.getCredentials(configuration, token, JIRA_SERVICE_ACCOUNT);
	}

	@Provides
	@Singleton
	@Named("orientDbServiceAccount")
	public Credentials getOrientDbServiceAccount(@Named("consulConfiguration") Configuration configuration) {
		LOGGER.info("Building [orientdbServiceAccount]");
		String token = VaultHelper.generateToken(configuration);
		return VaultHelper.getCredentials(configuration, token, ORIENTDB_SERVICE_ACCOUNT);
	}

	@Provides
	@Named("createActor")
	public ActorRef getCreateAccountActor(ActorSystem system, @Named("createVertexActor") ActorRef next,
			WSClient client, @Named("consulConfiguration") Configuration configuration,
			@Named("jiraServiceAccount") Credentials jiraServiceAccount) {
		LOGGER.info("Building [CreateAccountActor]");
		return system.actorOf(CreateAccountActor.props(system, next, client, configuration.getString(JIRA_URL),
				jiraServiceAccount.getUser(), jiraServiceAccount.getPassword()));
	}

	@Provides
	@Named("createVertexActor")
	public ActorRef getCreateAccountVertexActor(ActorSystem system, OrientGraphFactory graphFactory) {
		LOGGER.info("Building [CreateAccountVertexActor]");
		return system.actorOf(Props.create(CreateAccountVertexActor.class, graphFactory));
	}

	@Provides
	@Singleton
	public OrientGraphFactory buildOrientGraphFactory(ApplicationLifecycle lifecycle,
			@Named("consulConfiguration") Configuration configuration,
			@Named("orientDbServiceAccount") Credentials orientDbServiceAccount) {

		LOGGER.info("Building [OrientGraphFactory]");
		OrientGraphFactory factory = new OrientGraphFactory(configuration.getString(ORIENTDB_URL),
				orientDbServiceAccount.getUser(), orientDbServiceAccount.getPassword());

		lifecycle.addStopHook(() -> {
			factory.close();
			return F.Promise.pure(null);
		});

		return factory;
	}

	@Provides
	@Named("auditLogsActor")
	public ActorRef getAuditLogsActor(ActorSystem system, OrientGraphFactory graphFactory) {
		LOGGER.info("Building [AuditLogsActor]");
		return system.actorOf(Props.create(AuditLogsActor.class, graphFactory));
	}

	@Provides
	@Named("removeAccountActor")
	public ActorRef getRemoveAccountActor(ActorSystem system, @Named("removeAccountVertexActor") ActorRef next,
			WSClient client, @Named("consulConfiguration") Configuration configuration,
			@Named("jiraServiceAccount") Credentials jiraServiceAccount) {
		LOGGER.info("Building [CreateAccountActor]");
		return system.actorOf(RemoveAccountActor.props(next, client, configuration.getString(JIRA_URL),
				jiraServiceAccount.getUser(), jiraServiceAccount.getPassword()));
	}

	@Provides
	@Named("removeAccountVertexActor")
	public ActorRef getRemoveAccountVertexActor(ActorSystem system, OrientGraphFactory graphFactory) {
		LOGGER.info("Building [RemoveAccountVertexActor]");
		return system.actorOf(Props.create(RemoveAccountVertexActor.class, graphFactory));
	}

	@Provides
	@Named("getAccountActor")
	public ActorRef getGetAccountActor(ActorSystem system, WSClient client,
			@Named("consulConfiguration") Configuration configuration,
			@Named("jiraServiceAccount") Credentials jiraServiceAccount) {
		LOGGER.info("Building [GetAccountActor]");
		return system.actorOf(GetAccountActor.props(client, configuration.getString(JIRA_URL),
				jiraServiceAccount.getUser(), jiraServiceAccount.getPassword()));
	}
	

	@Provides
	@Named("getAllActor")
	public ActorRef getGetAllAccountsActor(ActorSystem system, WSClient client,
			@Named("consulConfiguration") Configuration configuration,
			@Named("jiraServiceAccount") Credentials jiraServiceAccount) {
		LOGGER.info("Building [GetAccountActor]");
		return system.actorOf(GetAllAccountsActor.props(client, configuration.getString(JIRA_URL),
				jiraServiceAccount.getUser(), jiraServiceAccount.getPassword()));
	}
}