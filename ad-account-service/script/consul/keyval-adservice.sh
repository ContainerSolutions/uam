#!/usr/bin/env bash

###  ------------------------------- ###
###  consul keyval init scripts ###
###  ------------------------------- ### 

curl -X PUT -d 'CN=Ad Joiner,CN=Users,DC=mantlio,DC=local' http://52.34.21.216:8500/v1/kv/adservice/ad/bind/dn
curl -X PUT -d '''s8DZ/t"pngGW{]6' http://52.34.21.216:8500/v1/kv/adservice/ad/bind/password
curl -X PUT -d 'CN=Users,DC=mantlio,DC=local' http://52.34.21.216:8500/v1/kv/adservice/ad/cn/users
curl -X PUT -d '52.34.21.216' http://52.34.21.216:8500/v1/kv/adservice/ad/host
curl -X PUT -d '636' http://52.34.21.216:8500/v1/kv/adservice/ad/port
curl -X PUT -d 'Password1' http://52.34.21.216:8500/v1/kv/adservice/default/password
curl -X PUT -d '5' http://52.34.21.216:8500/v1/kv/adservice/pool/size
curl -X PUT -d 'remote:52.25.246.25:2424/UserAccessControl' http://52.34.21.216:8500/v1/kv/adservice/orientdb/url
