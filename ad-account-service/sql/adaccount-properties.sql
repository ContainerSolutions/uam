CONNECT remote:localhost/UserAccessControl admin admin;

CREATE PROPERTY AdAccount.first_name STRING;
CREATE PROPERTY AdAccount.last_name STRING;
CREATE PROPERTY AdAccount.user_id STRING;
CREATE PROPERTY AdAccount.email STRING;
CREATE INDEX AdAccount.id UNIQUE;

