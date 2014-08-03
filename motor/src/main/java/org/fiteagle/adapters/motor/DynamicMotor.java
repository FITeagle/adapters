package org.fiteagle.adapters.motor;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DynamicMotor extends Motor {

    public DynamicMotor(MotorAdapter owningAdapter, int instanceID) {
        super(owningAdapter, instanceID);
        this.isDynamicThreadRunning = false;
        this.isDynamic = false;
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
                makeMotorDynamic();

            } else if (!this.isDynamic && isDynamicThreadRunning) {
                makeMotorStatic();
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private void makeMotorDynamic() throws NamingException {
        IMotorAdapterDynamic dynamicThread;
        dynamicThread = (IMotorAdapterDynamic) new InitialContext().lookup("java:module/MotorAdapterDynamic");

        dynamicThread.startThread(getInstanceID());

        isDynamicThreadRunning = true;
    }

    private void makeMotorStatic() throws NamingException {
        IMotorAdapterDynamic dynamicThread = (IMotorAdapterDynamic) new InitialContext().lookup("java:module/MotorAdapterDynamic");
        dynamicThread.endThread(getInstanceID());

        isDynamicThreadRunning = false;
    }

    public boolean isDynamic() {
        return this.isDynamic;
    }

}
