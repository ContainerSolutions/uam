package commons;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.UserName;

import play.libs.Json;

public class DirectoryHelperImpl implements DirectoryHelper {
	@Override
	public List<String> executeGetUserGroups(Directory directory, String domain, String user) {
		try {
			List<Group> res = directory.groups().list().setMaxResults(10).setUserKey(user).setDomain(domain).execute().getGroups();

			List<String> groups = res.stream().map(Group::getName).collect(Collectors.toList());

			return groups;

		} catch (IOException ex) {
			ex.printStackTrace();
			// Send an error response
			throw new IllegalStateException(ex.getMessage());
		}

	}

	@Override
	public int executeInsertUser(Directory directory, String domain, String primaryEmail, String firstName,
			String lastName, String password) {

		try {

			User body = new User().setPassword(password)
					.setName(new UserName().setGivenName(firstName).setFamilyName(lastName))
					.setPrimaryEmail(primaryEmail);
			// .setOrganizations(new OrganisationEntry();

			directory.users().insert(body).execute();

			return 200;

		} catch (IOException ex) {
			ex.printStackTrace();
			// Send an error response
			throw new IllegalStateException(ex.getMessage());
		}
	}

	@Override
	public int executeDeleteUser(Directory directory, String domain, String primaryEmail) {

		try {

			directory.users().delete(primaryEmail).execute();

			return 200;

		} catch (IOException ex) {
			ex.printStackTrace();
			// Send an error response
			throw new IllegalStateException(ex.getMessage());
		}
	}

	@Override
	public ObjectNode executeGetUser(Directory directory, String domain, String primaryEmail) {

		try {

			User user = directory.users().get(primaryEmail).execute();

			ObjectNode userInfo = Json.newObject();

			userInfo.put("lastName", user.getName().getGivenName());
			userInfo.put("firstName", user.getName().getFamilyName());
			userInfo.put("primaryEmail", user.getPrimaryEmail());

			return userInfo;

		} catch (IOException ex) {
			ex.printStackTrace();
			// Send an error response
			throw new IllegalStateException(ex.getMessage());
		}
	}

}
