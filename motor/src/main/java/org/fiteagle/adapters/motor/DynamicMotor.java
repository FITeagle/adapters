package org.fiteagle.adapters.motor;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DynamicMotor extends Motor {

    public DynamicMotor(MotorAdapter owningAdapter, int instanceID) {
        super(owningAdapter, instanceID);
        this.isDynamicThreadRunning = false;
        // setIsDynamic(true);
    }

    public DynamicMotor() {
        super();
    }

    // SIMULATE DYNAMICALLY CHANGING MOTOR

    private boolean isDynamic;
    private boolean isDynamicThreadRunning;

    public void setIsDynamic(boolean state) {
        this.isDynamic = state;

        try {
            if (this.isDynamic && !isDynamicThreadRunning) {

                IMotorAdapterDynamic dynamicThread;
                dynamicThread = (IMotorAdapterDynamic) new InitialContext().lookup("java:module/MotorAdapterDynamic");

                dynamicThread.startThread(getInstanceID());

                isDynamicThreadRunning = true;

            } else if (!this.isDynamic && isDynamicThreadRunning) {

                IMotorAdapterDynamic dynamicThread = (IMotorAdapterDynamic) new InitialContext().lookup("java:module/MotorAdapterDynamic");
                dynamicThread.endThread(getInstanceID());

                isDynamicThreadRunning = false;
            }
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean getIsDynamic() {
        return this.isDynamic;
    }

}
