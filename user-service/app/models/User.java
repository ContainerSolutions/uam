package models;

public class User {
	public String firstName;
	public String lastName;
	public String id;
	public String email;
	
	public User() {}

	public User(String firstName, String lastName, String id, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id;
		this.email = email;
	}
}
