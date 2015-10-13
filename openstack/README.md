FITeagle :: Adapter :: OpenStack
=============================
- Level: Demonstration
- Technologies: SFA
- Summary: Implementation of an OpenStack Adapter with SFA in a Wildfly Environment
- Target Product: FITeagle
- Source: <https://github.com/fiteagle/adapters/>

Build and Deploy the Adapter
----------------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. Type this command to build and deploy the archive:

        mvn clean install wildfly:deploy

4. This will deploy `target/openstack.war` to the running instance of the server. Look at the JBoss Application Server console or Server log and you should see log messages corresponding to the deployment of the message-driven beans and the JMS destinations.

Access the Adapter
------------------

The adapter can be accessed via different delivery mechanisms: direct REST, SFA,EJB or JMS (via Northbound REST or Web GUI).
Allthough we strongly recommend to use a SFA compatible Client like jFed, where you have complete access to all functions of the Adapter.
At the moment all other delivery mechanisms are in a beta stadium and just usefull for developing or testing.


Configure the Config-File properly
--------------------------------
In your home folder is a hidden Directory named ".fiteagle" where you can find all neccessary config files for each part of the Software.
You will need to edit the "OpenStackAdapter.properties" file and fill out all missing Values which you can get from your OpenStack Server.

At the end of the Config you have the possibility to add some Default-Flavours.
A Default-Flavour is a combination of an Image/Snapshot which is associated to a Flavour-ID, so the experimenter don't has/can choose between different Flavours.
A Default-Flavour will also be listed as a Sliver when calling a ListRessources operation via SFA.

This is one Example-Configuration to understand the structure better:
					{
					  "Resource_namespace": "http://localhost/resource/",
					  "hostname": "localhost",
					  "Local_namespace": "http://localhost/",
					  "adapterInstances": [
					    {
					 "default_keypair_id": "User1",
					 "componentID": "http://localhost/resource/OpenStackAdapter",
					 "Local_namespace": "http://localhost/",
					 "tenant_name": "test",
					 "keystone_password": "password1",
					 "keystone_endpoint": "http://openstack.testserver.com:5000/v2.0",
					 "net_name": "test-net",
					 "default_flavor_id": "6c818f87-6c9b-4a12-87a7-09457c0a2bec",
					 "Resource_namespace": "http://localhost/resource/",
					 "hostname": "localhost",
					 "net_endpoint": "http://openstack.testserver.com:9696/v2.0",
					 "glance_endpoint": "http://openstack.testserver.com:9292/v2",
					 "password": "password2",
					 "keystone_username": "Username",
					 "keystone_auth_URL": "http://openstack.testserver.com:5000/v2.0",
					 "floating_ip_pool_name": "ext-net",
					 "default_image_id": "37d7f526-916e-4bd3-b15a-18768fc95842",
					 "nova_endpoint": "http://openstack.testserver.com:8774/v1.1",
					 "defaultFlavours":[
						{	
						"Ubuntu-64bit":[{"diskImage":"37d7f526-916e-4bd3-b15a-18768fc95842","flavourName":"6c818f87-6c9b-4a12-87a7-09457c0a2bec"}],
						"Ubuntu-with-adapters":[{"diskImage":"074b99ed-0e8a-4307-8e59-87ca9f22ca53","flavourName":"6c818f87-6c9b-4a12-87a7-09457c0a2bec"}],
						"demo_device_mgmt":[{"diskImage":"48b591a3-6993-4738-aaf0-0fe350cde7d3","flavourName":"92b956de-bd7a-4cc3-abb1-9cf2679d33ea"}]
						} 
					} 
						]
					} 
					  ]
						} 

You can change this Config-File on the fly, and it will be updated automatically.
Please check your Wildfly Log after every changes for any Exceptions like typos or a HTTP-Exception.


SFA Delivery Mechanism
--------------------------------

### Experimenting via SFA Rspec calls


The example RSpec/XML files that are used for some SFA calls can be found in the adapters/openstack directory.

* Create a single Virtual Machine using an RSpec/XML:
		  *<rspec type="request" generated="2014-07-11T10:20:39Z" xsi:schemaLocation="http://www.geni.net/resources/rspec/3 http://www.geni.net/resources/rspec/3/request.xsd " xmlns:client="http://www.protogeni.net/resources/rspec/ext/client/1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.geni.net/resources/rspec/3">
		*<node client_id="myVM-1" component_id="http://localhost/resource/OpenStackAdapter"
		      component_manager_id="urn:publicId:IDN+localhost+authority+am"
		      component_name="OpenStackAdapter" exclusive="false">
		      <sliver_type name="http://localhost/resource/Ubuntu-64bit"/>
		 * </node>
		*</rspec>
  
* Create one or more Virtual Machines using an RSpec/XML with an other "client-id":
		*Just add another Node to the Rspec:
		*<node client_id="myVM-2" component_id="http://localhost/resource/OpenStackAdapter"
		      component_manager_id="urn:publicId:IDN+localhost+authority+am"
		      component_name="OpenStackAdapter" exclusive="false">
		      <sliver_type name="http://localhost/resource/Ubuntu-64bit"/>
		 * </node>


REST Delivery Mechanism
----------------------------

Under Construction


Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy

