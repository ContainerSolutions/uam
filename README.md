# Deployment Instructions UAM

1.	Install mantl.io [mantl.io](https://github.com/ciscocloud/microservices-infrastructure) in your environment of choice.
Follow [instructions](https://github.com/CiscoCloud/microservices-infrastructure/blob/master/README.md)
2.	Install [OrientDB](http://orientdb.com/) (with Maraton or as external service)
3.	Install Jira instance (with Maraton or as external service)
4.	Locate your Activate Directory service or create if needed.
5.	Create Nginx Docker image on Maraton (or any other web server)
6.	Clone repository:
```bash 
git clone https://github.com/ContainerSolutions/dio-soft.git to you development/build environment
```
7.	Deploy Consul configuration located for each service in _$service/script/consul/keyval-\*.sh_
8.	[Configure] Vault. Use auth init instrivtions in _conf/vault/vault_instructions.md_ and service instructions in _$service/script/vault/_
9.	Run db init scripts in OrientDB located in _$service/script/orientdb/_
10.	Create docker image for each module. All microcesrvices are created using activator. You can use built in docker support to create/publish your image
11.	Configure required services`s docker image in Maraton. You can use configuration described in conf/maraton/maraton_instructions.md. Maraton will automatically deploy your images.
12.	Build Nginx Docker image form Dockerfile in client-side and setup Maraton service for it.
13. Done
