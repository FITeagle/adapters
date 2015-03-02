package org.fiteagle.adapters.tosca.client;

import info.openmultinet.ontology.translators.tosca.jaxb.Definitions;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.fiteagle.abstractAdapter.AbstractAdapter.InstanceNotFoundException;
import org.fiteagle.abstractAdapter.AbstractAdapter.InvalidRequestException;
import org.fiteagle.abstractAdapter.AbstractAdapter.ProcessingException;

public class ToscaClientDummy implements IToscaClient {
  
  @Override
  public Definitions getAllDefinitions() throws ProcessingException {
    System.err.println("Method not implemented in dummy client");
    return null;
  }
  
  @Override
  public Definitions getAllTypes() throws ProcessingException {
    InputStream input = getClass().getResourceAsStream("/response-types.xml");
    Definitions definitions = convertToDefinitions(input);
    return definitions;
  }
  
  @Override
  public Definitions getDefinitions(String id) throws InstanceNotFoundException, ProcessingException {
    System.err.println("Method not implemented in dummy client");
    return null;
  }
  
  @Override
  public Definitions getSingleNodeDefinitions(String id) throws InstanceNotFoundException, ProcessingException {
    System.err.println("Method not implemented in dummy client");
    return null;
  }
  
  @Override
  public void deleteDefinitions(String id) throws ProcessingException {
    System.err.println("Method not implemented in dummy client");
  }
  
  @Override
  public Definitions updateDefinitions(String id, String definitionsString) throws ProcessingException {
    System.err.println("Method not implemented in dummy client");
    return null;
  }
  
  @Override
  public Definitions createDefinitions(String definitionsString) throws ProcessingException, InvalidRequestException {
    InputStream input = getClass().getResourceAsStream("/response-dummy.xml");
    Definitions definitions = convertToDefinitions(input);
    return definitions;
  }
  
  private Definitions convertToDefinitions(InputStream input){
    Definitions definitions = null;
    try {
      JAXBContext context = JAXBContext.newInstance(Definitions.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      definitions = unmarshaller.unmarshal(new StreamSource(input), Definitions.class).getValue();
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return definitions;
  }
}
