CONNECT remote:localhost/UserAccessControl admin admin;

CREATE CLASS AuditLog EXTENDS V;
CREATE PROPERTY AuditLog.user_id STRING;
CREATE PROPERTY AuditLog.request_number DOUBLE;
CREATE PROPERTY AuditLog.executor STRING;
CREATE PROPERTY AuditLog.datetime DATETIME;
CREATE PROPERTY AuditLog.application STRING;
CREATE PROPERTY AuditLog.action STRING;
//CREATE INDEX AuditLog.request_number UNIQUE;
//CREATE INDEX AuditLog.user_id DICTIONARY;

