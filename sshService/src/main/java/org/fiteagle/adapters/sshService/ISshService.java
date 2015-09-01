package org.fiteagle.adapters.sshService;

import info.openmultinet.ontology.vocabulary.Omn_lifecycle;

/**
 * 
 * @author AlaaAlloush
 *
 */
public interface ISshService {
  
  String SSH_SERVICE = "SshService";
  String SEMANTIC_DESCRIPTION_PATH = "ontologies/sshservice.ttl";
  String PASSWORD = "password";
  String IP = "ip";
  String LOCALHOST_IP = "127.0.0.1";
  String LOCAL_HOST = "localhost";
  String PRIVATE_KEY_PATH = "privateKeyPath";
  String PRIVATE_KEY_PASSWORD = "privateKeyPassword";
  String COMPONENT_ID = "componentID";
  String DEFAULT_ADAPTER_INSTANCE = "PhysicalNodeAdapter-1";
  String SSH_PORT = "port";
  String USERNAME = "username";
  String SSH_KEYS = "ssh-keys";
  String ADAPTER_INSTANCES = "adapterInstances";
  
}
