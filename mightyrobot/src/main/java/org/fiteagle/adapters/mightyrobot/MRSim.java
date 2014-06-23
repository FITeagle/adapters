package org.fiteagle.adapters.mightyrobot;

public class MRSim{

	private Boolean dancing = false;
	private boolean exploded = false;
	private Integer headRotation = 0; // in degrees


	/**
	* this robot can dance!
	*/
	public void setDancing(Boolean dancing){
		if (!this.exploded){
			this.dancing = dancing;
		}
	}
	/**
	* getter
	*/
	public Boolean getDancing(){
		if (this.exploded){
			return null;
		} else {
			return this.dancing;
		}
	}

	/**
	* this robot can turn its head! numbers in degrees
	*/
	public void setHeadRotation(Integer headRotation){
		if (!this.exploded){
			this.headRotation = headRotation;
		}
	}
	/**
	* getter
	*/
	public Integer getHeadRotation(){
		if (this.exploded){
			return null;
		} else {
			return this.headRotation;
		}
	}

	/**
	* this robot can explode!
	*/
	public void explode(){
		this.exploded = true;
	}
	/**
	* getter for exploded state
	*/
	public boolean getExploded(){
		return this.exploded;
	}

}
