package actors;

public class JiraAccountActorProtocol {
	public static final String vertexClassName = "JiraAccount";

	public static class CreateAccount {
		public String flowId;
		public String userId;
		public String appId;
	}

	public static class RemoveAccount {
		public final String name;

		public RemoveAccount(String name) {
			this.name = name;
		}
	}

	public static class GetAccount {
		public final String name;

		public GetAccount(String name) {
			this.name = name;
		}
	}

	public static class GetAllAccounts {
	}
}