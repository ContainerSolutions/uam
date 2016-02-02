package models;

public class JiraUser {
	public String id;
	public String email;
	public String displayName;
	
	public JiraUser() {}

	public JiraUser(String id, String email, String displayName) {
		this.id = id;
		this.email = email;
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return "JiraUser [id=" + id + ", email=" + email + ", displayName=" + displayName + "]";
	}
}
