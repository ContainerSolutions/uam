# Deployment Instructions UAM

1.	Install mantl.io in your environment of choice.
Follow instruction available at https://github.com/ciscocloud/microservices-infrastructure
2.	Install OrientDB (http://orientdb.com/) (with Maraton or as external service)
3.	Install Jira instance (with Maraton or as external service)
4.	Locate your Activate Directory service or create if needed.
5.	Create Nginx Docker image on Maraton (or any other web server)
6.	Clone repository:
```bash 
git clone https://github.com/ContainerSolutions/dio-soft.git to you development/build environment
```
7.	Deploy Consul configuration located for each service in *$service/script/consul/keyval-\*.sh*
8.	Configure Vault according to https://www.vaultproject.io/docs/auth/userpass.html. You will need to enable auth backend.
9.	Run db init scripts in OrientDB located in *$service/script/orientdb/\*
10.	Create docker image for each module. All microcesrvices are created using activator. You can use built in docker support to create/publish your image
11.	Configure required services`s docker image in Maraton. You can use configuration described in conf/maraton/maraton_instructions.md. Maraton will automatically deploy your images.
12.	Build Nginx Docker image form Dockerfile in client-side and setup Maraton service for it.

13. Done
