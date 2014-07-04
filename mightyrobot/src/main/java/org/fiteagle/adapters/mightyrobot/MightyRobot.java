package org.fiteagle.adapters.mightyrobot;


public class MightyRobot {

    private boolean dancing;
	private boolean exploded;
    private int headRotation;
    private String nickname;

    private MightyRobotAdapter owningAdapter;
    private int instanceID;

    public MightyRobot(MightyRobotAdapter owningAdapter, int instanceID) {
    
        this.dancing = false;
        this.exploded = false;
        this.headRotation = 0;
        this.nickname = "Mecha";
        this.owningAdapter = owningAdapter;
        this.instanceID = instanceID;
    }
    
    public String toString(){
        String reply = "";
    	if (this.getExploded()){
    		reply = "Might Robot " + this.nickname + " exploded due to unforeseen circumstances. Sorry about that.";
    	} else {
    		reply = "Mighty Robot " + this.nickname + " bids you a fond welcome. The robot is currently " 
    				+ ((this.getDancing()) ? "" : "not ")
    				+ "dancing, with its head turned by " + this.getHeadRotation() + " Degrees.";
    	}
        return reply;
    }
 
    public boolean getExploded() {
		return exploded;
	}

	public void setExploded(boolean exploded) {
		this.exploded = exploded;
	}

	public boolean getDancing() {
		return dancing;
	}

	public void setDancing(Boolean dancing) {
		this.dancing = dancing;
	}

	public int getHeadRotation() {
		return headRotation;
	}

	public void setHeadRotation(int headRotation) {
		this.headRotation = (headRotation % 360);
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public MightyRobotAdapter getOwningAdapter() {
		return owningAdapter;
	}

	public void setOwningAdapter(MightyRobotAdapter owningAdapter) {
		this.owningAdapter = owningAdapter;
	}
   
}

