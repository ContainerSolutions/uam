#!/usr/bin/env bash

###  ------------------------------- ###
###  consul keyval init scripts ###
###  ------------------------------- ### 

curl -X PUT -d 'http://52.89.196.134:4752' http://52.34.21.216:8500/v1/kv/jiraservice/jira/url
curl -X PUT -d 'http://192.168.99.100:32771' http://52.34.21.216:8500/v1/kv/jiraservice/vault/url
curl -X PUT -d 'service-account' http://52.34.21.216:8500/v1/kv/jiraservice/vault/user
curl -X PUT -d 'supersecret' http://52.34.21.216:8500/v1/kv/jiraservice/vault/pass
curl -X PUT -d 'remote:52.25.246.25:2424/UserAccessControl' http://52.34.21.216:8500/v1/kv/jiraservice/orientdb/url
