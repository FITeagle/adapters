package org.fiteagle.adapters.motor.dm;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.fiteagle.adapters.motor.IAdapterListener;
import org.fiteagle.adapters.motor.IMotorAdapter;
import org.fiteagle.adapters.motor.MotorAdapter;


@Stateless(name ="MotorAdapter")
@Remote(IMotorAdapter.class)
public class MotorAdapterEJB implements IMotorAdapter {
	private final MotorAdapter adapter;

	public MotorAdapterEJB() {
		this.adapter = new MotorAdapter ();
	}

	@Override
	public String getAdapterDescription(String serializationFormat) {
		return this.adapter.getAdapterDescription(serializationFormat);
	}

	@Override
	public void registerForEvents(IAdapterListener adapterDM) {
		this.adapter.registerForEvents(adapterDM);
	}

	@Override
	public boolean createMotorInstance(int instanceNumber) {
		return this.adapter.createMotorInstance(instanceNumber);
	}

}
