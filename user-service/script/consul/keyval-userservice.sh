#!/usr/bin/env bash

###  ------------------------------- ###
###  consul keyval init scripts ###
###  ------------------------------- ### 

curl -X PUT -d 'remote:52.25.246.25:2424/UserAccessControl' http://52.34.21.216:8500/v1/kv/userservice/orientdb/url
