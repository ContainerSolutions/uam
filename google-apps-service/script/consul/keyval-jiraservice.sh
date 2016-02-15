#!/usr/bin/env bash

###  ------------------------------- ###
###  consul keyval init scripts ###
###  ------------------------------- ### 

curl -X PUT -d '10' http://52.34.21.216:8500/v1/kv/gappsservice/vault/timeout
curl -X PUT -d 'https://52.34.21.216:8200' http://52.34.21.216:8500/v1/kv/gappsservice/vault/url
