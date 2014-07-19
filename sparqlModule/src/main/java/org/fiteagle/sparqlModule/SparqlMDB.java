package org.fiteagle.sparqlModule;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;

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
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class SparqlMDB implements MessageListener{
	
    public void onMessage(Message inMessage){
        try{
            String type = inMessage.getStringProperty("type");
            if(type.equals((String) "update")){
                String data = inMessage.getStringProperty("data");
                System.out.println("Sent data is:" + data + " and type is: " +type);
                System.out.println("Submit result : " + submitSparqlUpdate(data));
            } else if (type.equals("query")){
                String data = inMessage.getStringProperty("data");
            	submitSparqlQuery(data);
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
