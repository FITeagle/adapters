package org.fiteagle.adapters.motor.dm;

import org.fiteagle.adapters.motor.IAdapterListener;

//@Singleton
//@Startup
//@DependsOn("MotorAdapter")
public class MotorAdapterListener implements IAdapterListener {

//    private IMotorAdapter motorLogic;
//
//    private static Logger LOGGER = Logger.getLogger(MotorAdapterListener.class.toString());
//    private MotorAdapterBean senderBean;
//
//    @Inject
//    public MotorAdapterListener(MotorAdapterBean senderBean) throws NamingException, JMSException {
//        LOGGER.log(Level.INFO, "Sending a message via JMS message bus...");
//        this.senderBean = senderBean;
//        this.senderBean.sendMessage("Sent a message to the JMS message bus");
//    }

    @Override
    public void onAdapterMessage(String string) {
//        LOGGER.log(Level.ALL, "Received a message via adapter listener...");
//        try {
//            this.senderBean.sendMessage(string);
//        } catch (JMSException e) {
//            LOGGER.log(Level.INFO, e.getMessage());
//        }
    }

//    @PostConstruct
//    public void startup() throws NamingException {
//        this.motorLogic = (IMotorAdapter) new InitialContext().lookup("java:module/MotorAdapter");
//        // todo: this ideas doesn't work : not-serializable
//        // this.motorLogic.registerForEvents(this);
//    }
}
