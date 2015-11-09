package org.fiteagle.adapters.Open5GCore;

import java.util.logging.Logger;




public class Open5GCoreClient {
    private static final Logger LOGGER = Logger.getLogger(Open5GCoreClient.class.toString());

    
    private final Open5GCoreAdapter owningAdapter;
    private final String instanceName;

public Open5GCoreClient(Open5GCoreAdapter owningAdapter, String instanceName){

	this.owningAdapter = owningAdapter;
	this.instanceName = instanceName;
}
    

}
