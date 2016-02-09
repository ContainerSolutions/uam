Create service account credentials for Jira and OrientDB:
	vault write secret/jiraservice/orientdb user=admin password=admin
	vault write secret/jiraservice/jira user=admin password=secret