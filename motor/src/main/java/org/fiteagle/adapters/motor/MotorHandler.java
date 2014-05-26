package org.fiteagle.adapters.motor;

import java.util.ArrayList;

public class MotorHandler {
    
    
    ArrayList<MotorInstance> motorList = new ArrayList<MotorInstance>();
    int currentIndex = 0;
    
    public int createMotorInstance(){
        
        MotorInstance newMotor = new MotorInstance();
        motorList.add(currentIndex,newMotor);
        
        return currentIndex++;
        
    }
    
    public boolean terminateMotorInstance(int motorInstanceID){
        
        if(motorList.remove(motorInstanceID) != null){
            return true;
        }
        
        return false;        
    }
    
    public MotorInstance getMotorInstance(int motorInstanceID){
        
        return motorList.get(motorInstanceID);
    }
    
    public ArrayList<MotorInstance> getAllMotorInstances(){
        
        return motorList;        
    }

}
