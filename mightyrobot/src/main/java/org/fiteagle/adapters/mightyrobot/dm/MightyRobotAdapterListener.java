package org.fiteagle.adapters.mightyrobot.dm;

import org.fiteagle.adapters.mightyrobot.IAdapterListener;

//@Singleton
//@Startup
//@DependsOn("MightyRobotAdapter")
public class MightyRobotAdapterListener implements IAdapterListener {

//    private IMightyRobotAdapter motorLogic;
//
//    private static Logger LOGGER = Logger.getLogger(MightyRobotAdapterListener.class.toString());
//    private MightyRobotAdapterBean senderBean;
//
//    @Inject
//    public MightyRobotAdapterListener(MightyRobotAdapterBean senderBean) throws NamingException, JMSException {
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
//        this.motorLogic = (IMightyRobotAdapter) new InitialContext().lookup("java:module/MightyRobotAdapter");
//        // todo: this ideas doesn't work : not-serializable
//        // this.motorLogic.registerForEvents(this);
//    }
}
