package org.fiteagle.adapters.mightyrobot.dm;

import org.fiteagle.abstractAdapter.dm.AbstractMDBListener;

import javax.jms.Message;

/**
 * Created by vju on 8/20/14.
 */
public class MightyRobotAdapterMDBListener extends AbstractMDBListener{

    @Override
    public String responseConfigure(Message requestMessage){
         return null;
    }

    @Override
    public String responseDiscover(Message requestMessage) {
        return null;
    }

    @Override
    public String responseCreate(Message requestMessage) {
        return null;
    }

    @Override
    public String responseRelease(Message requestMessage) {
        return null;
    }

    @Override
    public boolean messageBelongsToAdapter(Message requestMessage) {
        return false;
    }
}
