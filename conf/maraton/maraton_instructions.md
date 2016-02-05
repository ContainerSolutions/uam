1. Create ‘user-service’ Marathon application with following parameters:
	id: /user-service
	CPUs: 0.2
	Memory(MB): 128
	Disk Space(MB): 512
	docker image: <docker_login>/user-service
	network: Bridge
	containerPort: 9000
	hostPort: 0
	servicePort: 0
	protocol: tcp

2. Create ‘ad-account-service’ Marathon application with following parameters:
	id: /ad-account-service
	CPUs: 0.2
	Memory(MB): 128
	Disk Space(MB): 512
	docker image: <docker_login>/ad-account-service
	network: Bridge
	containerPort: 9000
	hostPort: 0
	servicePort: 0
	protocol: tcp	

3. Create ‘client-side’ Marathon application with following parameters:
	id: /client-side
	CPUs: 0.2
	Memory(MB): 128
	Disk Space(MB): 512
	docker image: <docker_login>/client-side
	network: Bridge
	containerPort: 80
	hostPort: 0
	servicePort: 0
	protocol: tcp	

4. Create ‘google-apps-service’ Marathon application with following parameters:
	id: /client-side
	CPUs: 0.2
	Memory(MB): 128
	Disk Space(MB): 512
	docker image: <docker_login>/google-apps-service
	network: Bridge
	containerPort: 9000
	hostPort: 0
	servicePort: 0
	protocol: tcp	

5. Create ‘jira-account-service’ Marathon application with following parameters:
	id: /client-side
	CPUs: 0.2
	Memory(MB): 128
	Disk Space(MB): 512
	docker image: <docker_login>/jira-account-service
	network: Bridge
	containerPort: 9000
	hostPort: 0
	servicePort: 0
	protocol: tcp

6. Create ‘jira’ Marathon application with following parameters:
	id: /jira
	CPUs: 0.5
	Memory(MB): 1024
	Disk Space(MB): 4096
	docker image: cptactionhank/atlassian-jira
	network: Bridge
	containerPort: 8080
	hostPort: 0
	servicePort: 0
	protocol: tcp

7. Complete JIRA demo setup and create admin account

8. Create ‘orientdb’ Marathon application with following parameters:
	id: /orientdb
	CPUs: 0.5
	Memory(MB): 1024
	Disk Space(MB): 4096
	docker image: orientdb/orientdb:2.1.5
	network: Bridge
	containerPort: 2424
	hostPort: 0
	servicePort: 0
	protocol: tcp
	environment variabls: 
		ORIENTDB_ROOT_PASSWORD = <generated_orientdb_root_pass>
	
9. Connect OrientDB Studio and create UserAccessControl database using sql schema 