package org.fiteagle.adapters.stopwatch.dm;

import org.fiteagle.adapters.stopwatch.IAdapterListener;

//@Singleton
//@Startup
//@DependsOn("StopwatchAdapter")
public class StopwatchAdapterListener implements IAdapterListener {

//    private IStopwatchAdapter motorLogic;
//
//    private static Logger LOGGER = Logger.getLogger(StopwatchAdapterListener.class.toString());
//    private StopwatchAdapterBean senderBean;
//
//    @Inject
//    public StopwatchAdapterListener(StopwatchAdapterBean senderBean) throws NamingException, JMSException {
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
//        this.motorLogic = (IStopwatchAdapter) new InitialContext().lookup("java:module/StopwatchAdapter");
//        // todo: this ideas doesn't work : not-serializable
//        // this.motorLogic.registerForEvents(this);
//    }
}
