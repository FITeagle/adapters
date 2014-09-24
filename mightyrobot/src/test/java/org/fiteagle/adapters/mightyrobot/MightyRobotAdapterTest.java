package org.fiteagle.adapters.mightyrobot;

import org.junit.Assert;
import org.junit.Test;

public class MightyRobotAdapterTest {

    @Test
    public void testSingleton() {
        MightyRobotAdapter mightyRobotAdapterOne = MightyRobotAdapter.getInstance();
        
        MightyRobotAdapter mightyRobotAdapterTwo = MightyRobotAdapter.getInstance();
        
        Assert.assertEquals(mightyRobotAdapterOne, mightyRobotAdapterTwo);
        
    }

    
}

