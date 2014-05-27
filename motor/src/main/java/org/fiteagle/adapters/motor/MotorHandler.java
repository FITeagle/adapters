package org.fiteagle.adapters.motor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MotorHandler {
    
    
    HashMap<Integer, MotorInstance> motorList = new HashMap<Integer, MotorInstance>();
    int currentIndex = 0;
    
    public boolean createMotorInstance(int motorInstanceID){
        
        MotorInstance newMotor = new MotorInstance();
        
        if(motorList.containsKey(motorInstanceID)){
            return false;
        }
        
        motorList.put(motorInstanceID, newMotor);
        return true;
        
    }
    
    public boolean terminateMotorInstance(int motorInstanceID){
        
        if(motorList.containsKey(motorInstanceID)){
            motorList.remove(motorInstanceID);
            return true;
        }
        
        return false;        
    }
    
    public MotorInstance getMotorInstance(int motorInstanceID){
        
        return motorList.get(motorInstanceID);
    }
    
    public HashMap<Integer, MotorInstance> getAllMotorInstances(){
        
        
        
        return motorList;        
    }

}
