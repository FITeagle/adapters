package org.fiteagle.adapter.fuseco;

import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



/**
 * 
 * @author alaa.alloush
 *
 */

public interface IFusecoAdapter {

	public void setStatus(AdapterStatus status);
	public AdapterStatus getStatus();
	
	//public void setExpirationTime(Date expirationTime);
	
	//public Date getExpirationTime();
	
	public void configure(AdapterConfiguration configuration); // create
	
	public void release();
	
	public void create(String name);
	
	/**
	 * 
	 * @param input
	 * @param serializationFormat
	 * @return
	 */
	//public String control(InputStream input, String serializationFormat);
	
	
	
	/**
	 * terminate an instance
	 * @param InstanceNR, the ID of the created instance
	 * @return true if the instance was successfully terminated
	 */
	//public boolean instanceTermination(int InstanceNR);
	
	/**
	 * 
	 * @param InstanceNR
	 * @param serializationFormat
	 * @return
	 */
	//public String instanceMonitoring(int InstanceNR, String serializationFormat);
	
	/**
	 * 
	 * @param serializationFormat
	 * @return
	 */
	//public String getAdapterDescription(String serializationFormat);
	
	/**
	 * 
	 * @param serializationFormat
	 * @return
	 */
	//public String getAllInstances(String serializationFormat);
	
	
	/**
	 *  
	 * @param newListener
	 * @return
	 */
	//public boolean addChangeListener(PropertyChangeListener newListener);
	
}
