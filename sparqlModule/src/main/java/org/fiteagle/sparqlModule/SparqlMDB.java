package org.fiteagle.sparqlModule;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.*;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.modify.UpdateProcessRemote;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;
/**
 * Created by vju on 7/18/14.
 */
@MessageDriven(mappedName="jms/SparqlMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/topic/core"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "topic = 'Fuseki'")
})
public class SparqlMDB implements MessageListener{

    @Resource(mappedName = "java:/topic/core")
    private Topic topic;

    @Inject
    private JMSContext jmsContext;
    public void onMessage(Message inMessage){
        System.out.println("received message in SparqlMDB  1");
        try{
            String type = inMessage.getStringProperty("type");
            if(type.equals((String) "update")){
                String data = inMessage.getStringProperty("data");
                System.out.println("Sent data is:" + data + " and type is: " +type);
                System.out.println("Submit result : " + submitSparqlUpdate(data));
            } else if (type.equals("query")){
                String data = inMessage.getStringProperty("data");
            	submitSparqlQuery(data);
            }else if(type.equals("getAdapterList")){
                System.out.println("received message in SparqlMDB  2 ");
                Message m = jmsContext.createMessage();
                System.out.println("Correlation ID is: " + inMessage.getJMSCorrelationID() );
                m.setJMSCorrelationID(inMessage.getJMSCorrelationID());

                m.setStringProperty("response","Goes here");
                jmsContext.createProducer().send((Queue) inMessage.getJMSReplyTo(), m);
            }
        }catch(Exception e){
           e.printStackTrace();
        }
    }
    
    private boolean submitSparqlUpdate(String data){
    	try { 
			String updateURL = "http://localhost:3030/ds/update";
			System.out.println("Posting \n\n" + data + "\n\nto " + updateURL);
			UpdateRequest updateRequest = UpdateFactory.create(data);
			UpdateProcessRemote uPR = (UpdateProcessRemote) 
					UpdateExecutionFactory.createRemote(updateRequest, updateURL);
			uPR.execute();
			return true;
 		} catch (Exception e){
 			e.printStackTrace();
 		}
		return false;
	}

    /**
     * DUNNO IF THIS WORKS; NEEDS TESTING
     * @param data
     * @return
     */
    private boolean submitSparqlQuery(String data){
    	try { 
			String updateURL = "http://localhost:3030/ds/query";
			System.out.println("Querying \n\n" + data + "\n\nfrom " + updateURL);

			Query query = QueryFactory.create(data);
			QueryExecution qE = QueryExecutionFactory.sparqlService(updateURL, query);
			ResultSet results = qE.execSelect();	
			if (true){
				System.out.println(results.nextSolution());
			}

			return true;
 		} catch (Exception e){
 			e.printStackTrace();
 		}
		return false;
	}
    
}
