FITeagle :: Adapter :: SshService
=============================

- Compatible OS: Linux&Mac
- Target Product: FITeagle
- Source: <https://github.com/fiteagle/adapters/>


Build and Deploy the Adapter
----------------------------

NOTE: 	If you just used bootstrap you don't have to do this.
		The SshAdapter is part of the Core-Tools and will be deployed automatically
		You can check it anyway in the JBoss Application Server GUI under http://localhost:9990/console/App.html#deployments 
		if it is allready deployed.

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project. (for Example: workspace/adapters/Motor)
3. Type this command to build and deploy the archive:

        mvn clean install wildfly:deploy

4. This will deploy `target/sshService.war` to the running instance of the server. Look at the JBoss Application Server console or Server log and you should see log messages corresponding to the deployment of the message-driven beans and the JMS destinations. The previous command creates properties file under ~/.fiteagle/SshService.properties containing all default and required properties for this adapter with empty values. You have to set values for this properties to your values and then deploy the adapter again to activate new changes using the same command in step 3.
```
{
  "Resource_namespace": "http://localhost/resource/",
  "hostname": "localhost",
  "Local_namespace": "http://localhost/",
  "adapterInstances": [
    {
      "privateKeyPath": "change to the path of your private key",
      "password": "change to your sudo passowrd if it's required",
      "componentID": "change to your adapter instance name. e.g., http://localhost/resource/PhysicalNodeAdapter-1",
      "privateKeyPassword": "change to the password of your private key",
      "ip": "IP address of the machine where to create ssh service",
      "username": "change to the user-name used to connect with the machine where to create ssh service"
    }
  ]
}
```
Because of some commands we use due to the creation of new Users and the SSH-Credentials we sometimes need sudo rights on the machine.
Therefore, we need you to change the password-variable in the Property-File to your correct root-password. If you want to add additional adapter instances, you have to add a new list to adapterInstances in the properties file. In particular, adapter instances should be separated by ","  as follows: 
```
{
  "Resource_namespace": "http://localhost/resource/",
  "hostname": "localhost",
  "Local_namespace": "http://localhost/",
  "adapterInstances": [
    {
      "privateKeyPath": "change to the path of your private key",
      "password": "change to your sudo passowrd if it's required",
      "componentID": "change to your adapter instance name. e.g., http://localhost/resource/PhysicalNodeAdapter-1",
      "privateKeyPassword": "change to the password of your private key",
      "ip": "IP address of the machine where to create ssh service",
      "username": "change to the user-name used to connect with the machine where to create ssh service"
    }, 
    {
      "privateKeyPath": "",
      "password": "",
      "componentID": "",
      "privateKeyPassword": "",
      "ip": "",
      "username": ""
   }
  ]
}
```
Don't forget to deploy the adapter to activate new changes.


REST Delivery Mechanism
----------------------------

The adapter will be accessible at the following URL via REST: <http://localhost:8080/sshService/PhysicalNodeAdapter-1>

## Experimenting via curl REST calls
If you dont want to write your own ttl-file, you can use our test-file for experimenting under:
/workspace/adapters/sshService/src/test/resources/createSSHAccess.ttl

###Create new (Currently not working, but will be available again soon)
curl -i -X POST -d @***Directory of your TTL-File*** localhost:8080/sshService/PhysicalNodeAdapter-1/instances

###Delete existing Access (Currently not working, but will be available again soon)
curl -i -X DELETE -d @***Directory of your TTL-File*** localhost:8080/sshService/PhysicalNodeAdapter-1/instances/**Username to delete**


###One example Turtle file for creating a new Ssh-Instance:
```
@prefix ssh:    <http://open-multinet.info/ontology/resource/ssh#> .
@prefix ssh-adapter:  <http://open-multinet.info/ontology/resource/ssh-adapter#> .
@prefix av:    <http://federation.av.tu-berlin.de/about#> .
@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .
@prefix local: <http://localhost/resource#> .
@prefix omn:   <http://open-multinet.info/ontology/omn#> .
@prefix omn-service: <http://open-multinet.info/ontology/omn-service#> .


ssh:PhysicalNodeAdapter-1 a ssh:SshService ;
          omn-service:publickey "YOUR PUBLIC KEY HERE"^^xsd:string ;
          omn-service:username "YOUR USERNAME HERE"^^xsd:string .
```    
 



Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy