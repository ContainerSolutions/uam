CONNECT remote:localhost/UserAccessControl admin admin;

CREATE CLASS User EXTENDS V;
CREATE PROPERTY User.firstName STRING;
CREATE PROPERTY User.lastName STRING;
CREATE PROPERTY User.uniqueId STRING;
CREATE PROPERTY User.email STRING;
CREATE PROPERTY User.created DATETIME;
CREATE PROPERTY User.updated DATETIME;
CREATE PROPERTY User.active BOOLEAN;
CREATE INDEX User.uniqueId UNIQUE;

CREATE CLASS Account EXTENDS V ABSTRACT;

CREATE CLASS JiraAccount EXTENDS Account;
CREATE PROPERTY Account.name STRING;

CREATE CLASS AdAccount EXTENDS Account;
CREATE CLASS GAppsAccount EXTENDS Account;
CREATE CLASS GitAccount EXTENDS Account;

CREATE CLASS Flow EXTENDS V;

CREATE CLASS Template EXTENDS V;

CREATE CLASS Application EXTENDS V;
