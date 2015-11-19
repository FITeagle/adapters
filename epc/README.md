FITeagle :: Adapter :: EPC
=============================
- Level: Demonstration
- Technologies: REST, Websocket, EJB, JMS
- Summary: Test implementation of EPC Adapter within a WildFly Environment
- Target Product: FITeagle
- Source: <https://github.com/fiteagle/adapters/>

Build and Deploy the Adapter
----------------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. Type this command to build and deploy the archive:

        mvn clean install wildfly:deploy

4. This will deploy `target/epc.war` to the running instance of the server. Look at the WildFly Application Server console or Server log and you should see log messages corresponding to the deployment of the message-driven beans and the JMS destinations.

Access the Adapter
------------------

The adapter can be accessed via different delivery mechanisms: direct REST, Websocket, EJB or JMS (via Northbound REST or Web GUI)

If you want to access this Adapter via REST you have to use:
www.localhost[Or other URI]:8080/lterf/epc
as the path. The normal and old Path "localhost:8080/epc" is not working anymore for this Adapter.


JMS/MDB Delivery Mechanism
--------------------------------

TODO


EJB Delivery Mechanism
-----------------------------

TODO


WebSocket Delivery Mechanism
-----------------------------

TODO



REST Delivery Mechanism
----------------------------

TODO


Undeploy the Adapter
--------------------

1. Make sure you have started the FITeagle environment (WildFly).
2. Open a command line and navigate to the root directory of this project.
3. When you are finished testing, type this command to undeploy the adapter:

        mvn wildfly:undeploy
