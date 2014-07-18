package org.fiteagle.sparqlModule;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
/**
 * Created by vju on 7/18/14.
 */
@MessageDriven(mappedName="jms/SparqlMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/sparqlModule"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class SparqlMDB implements MessageListener{
    public void onMessage(Message inMessage){
        try{
            String type = inMessage.getStringProperty("type");
            if(type.equals((String) "update")){
                String data = inMessage.getStringProperty("data");
                System.out.println("Sent data is:" + data + " and type is: " +type);
            }

        }catch(Exception e){
            System.out.println("error is:" + e.getMessage());

        }
            System.out.println("DAFUUUUUUUUUUUUUUUUUUUUUUUUUUQQQQQQ");
    }
}
