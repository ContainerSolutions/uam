package commons;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.services.admin.directory.Directory;

public interface DirectoryHelper {

	List<String> executeGetUserGroups(Directory directory, String domain, String user);

	int executeInsertUser(Directory directory, String domain, String primaryEmail, String firstName, String lastName, String password);

	int executeDeleteUser(Directory directory, String domain, String primaryEmail);

	ObjectNode executeGetUser(Directory directory, String domain, String primaryEmail);

}
