package org.fiteagle.adapters.mightyrobot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;

public class MRHandler {
	
	static String instanceFileName = "mightyrobot" + File.separator + "InstanceList";
	static String instanceCountFileName = "mightyrobot" + File.separator + "InstanceCount";
	static String instanceDirectoryPath = "mightyrobot" + File.separator + "Instances";
		
	/**
	 * Returns the description of this resourced
	 * @return String containing the description of this resource
	 */
	public static String getDescription(){
		setupWorkingDirectoy();
		return 
				"HERE GOES THE DESCRIPTION TEXT\n";		
	}	
	
	/**
	 * Sets the description of a specific instance
	 * @param instanceName name of the instance that is supplosed to be updated with the given description
	 * @param description description the given instance should get
	 * @return
	 */
	public static String putInstanceDescription(String instanceName, String description){
		setupWorkingDirectoy();
		// name must be non-empty
		if (instanceName == null || instanceName == "") {
			return "Instance description not found - faulty name given";
		}
		String returnValue = "Instance description not added - instance does not exist.";		
		
		BufferedWriter fileWriter = null;
		try {
			// try to find the file
			File descriptionFile = new File(instanceDirectoryPath + File.separator + instanceName);
			if (descriptionFile.exists()){
				//write new description to the instance file, if it exists, the instance exists
				fileWriter = new BufferedWriter(new FileWriter(instanceDirectoryPath + File.separator + instanceName));
				fileWriter.write(description);
				returnValue = "Description for instance " + instanceName +  " was set to given description.";
			}
		} catch (Exception e){
			System.out.println("Error in putInstanceDescription()");
			//e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (Exception e) {
				System.out.println("Error in closing fileReader of putInstanceDescription()");
				//e.printStackTrace();
			}			
		}
		
		return 
				returnValue + "\n";
	}
	
	/**
	 * Returns the description of a specific instance of this adapter
	 * @param instanceName name of the instance that is queried for its description
	 * @return String containing the description of this instance
	 */
	public static String getInstanceDescription(String instanceName){
		setupWorkingDirectoy();
		// name must be non-empty
		if (instanceName == null || instanceName == "") {
			return "Instance description not found - faulty name given";
		}
		String instanceDescription = "Instance description not found - instance does not exist.";
		BufferedReader fileReader = null;
		try {
			// try to find the file
			File descriptionFile = new File(instanceDirectoryPath + File.separator + instanceName);
			if (descriptionFile.exists()){
				// get description from the file, if it exists, the instance exists
				instanceDescription = "";
				fileReader = new BufferedReader(new FileReader(descriptionFile));
				String temp;
				while ((temp = fileReader.readLine()) != null){
					instanceDescription += temp;
				}
			}
		} catch (Exception e){
			System.out.println("Error in getInstanceDescription()");
			//e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (Exception e) {
				System.out.println("Error in closing fileReader of getInstanceDescription()");
				//e.printStackTrace();
			}			
		}
		
		return 
				instanceDescription + "\n";		
	}
	
	/**
	 * Returns the number of instances and their names
	 * @return String containing number of instances and for each instance its name in a single line
	 */
	public static String getInstances(){
		setupWorkingDirectoy();
		String instanceListing = getInstanceCount();
		// parse instance file, if at least 1 instance found
		if (instanceListing != "0"){
			instanceListing += "\nAvailable Instances:\n";
			instanceListing += getAllInstanceNames();
		} else {
			instanceListing += "\n";
		}
		
		return
				"Number of instances: " + instanceListing;
	}
	
	/**
	 * Tries to provision a new instance with the given name, if it does not yet exist
	 * @param instanceName String, name to be used for the new instance
	 * @return boolean indicating if an instance was provisioned(true) or not (false)
	 */
	public static boolean provisionInstance(String instanceName){
		setupWorkingDirectoy();
		// name must be non-empty
		if (instanceName == null || instanceName == "") {
			return false;
		}
		// get current number of instances and possibly all names of available instances
		String numberOfInstances = getInstanceCount(), instances = "";
		if (numberOfInstances != "0") {
			instances = getAllInstanceNames();
		}
		
		//don't add instances with the same name again
		if (instances != ""){
			if (instanceExists(instances, instanceName)){
				//the name already exists, discard, leave, run for your life, get the hell out of here!
				return false;
			}
		}
		
		// otherwise, add new instance
		addInstance(instanceName, numberOfInstances, instances);
		return true;
	}
	
	/**
	 * Tries to delete the instance with the given name, if it exists
	 * @param instanceName String, name of the instance to be deleted
	 * @return boolean indicating if an instance was deleted(true) or not (false)
	 */
	public static boolean deleteInstance(String instanceName){
		setupWorkingDirectoy();
		// name must be non-empty
		if (instanceName == "") {
			return false;
		}
		// get current number of instances and possibly all names of available instances
		String numberOfInstances = getInstanceCount(), instances = "";
		if (numberOfInstances != "0") {
			instances = getAllInstanceNames();
		} else {
			return false;
		}
		
		//only try to delete existing instances
		if (instances != ""){
			if (!instanceExists(instances, instanceName)){
				//the name does not exist! discard, leave, run for your life, get the hell out of here!
				return false;
			}
		}
		
		// otherwise, delete
		deleteInstance(instanceName, numberOfInstances, instances);	
		return true;
	}
	
	// checks if given instance already exists
	private static boolean instanceExists(String instances, String instanceName){
		String [] instanceNames = instances.split("\n");
		for (String temp : instanceNames){
			if (temp.equals(instanceName)){
				// the name already exists! 
				return true;
			}
		}
		return false;
	}
	
	// returns the number of instances currently running
	private static String getInstanceCount(){
	// if nothing is found, return 0 eventually
		String instanceCount = "0";
		BufferedReader fileReader = null;
		try {
			// parse instance count file
			fileReader = new BufferedReader(new FileReader(new File(instanceCountFileName)));
			String temp = "";
			temp = fileReader.readLine();
			if (temp != "" && temp != null){
				instanceCount = temp;
			}
		} catch (Exception e){
			System.out.println("Error in getInstanceCount()");
			//e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (Exception e) {
				System.out.println("Error in closing fileReader of getInstanceCount()");
				//e.printStackTrace();
			}			
		}
		return instanceCount;
	}
	
	// returns the names of all currently running instances, one per line
	private static String getAllInstanceNames(){
		String instances = "";
		BufferedReader fileReader = null;
		try {
			String temp = null;
			fileReader = new BufferedReader(new FileReader (new File(instanceFileName)));
			while ((temp = fileReader.readLine()) != null){
				instances+= temp + "\n";
			}
			
		} catch (Exception e){
			System.out.println("Error in getAllInstanceNames()");
			//e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (Exception e) {
				System.out.println("Error in closing fileReader of getAllInstanceNames()");
				//e.printStackTrace();
			}			
		}
			
		return instances;
	}
		
	// registers a new instance with the given name
	private static void addInstance(String instanceName, String oldNumberOfInstances, String oldInstances){
		BufferedWriter fileWriter = null;
		try {
			//write new number to the count file
			fileWriter = new BufferedWriter(new FileWriter(instanceCountFileName));
			fileWriter.write((Integer.parseInt(oldNumberOfInstances) + 1) + "");
			fileWriter.close();
			//add entry of new instance name to the listing file
			fileWriter = new BufferedWriter(new FileWriter(instanceFileName));
			fileWriter.write(oldInstances + instanceName);
			fileWriter.close();
			//create new file for that specific instance
			fileWriter = new BufferedWriter(new FileWriter(instanceDirectoryPath + File.separator + instanceName));
			fileWriter.write("");
		} catch (Exception e){
			System.out.println("Error in addInstance()");
			//e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (Exception e) {
				System.out.println("Error in closing fileReader of addInstance()");
				//e.printStackTrace();
			}			
		}
	}
	
	// deletes the instance with the given name
	private static void deleteInstance(String instanceName, String oldNumberOfInstances, String oldInstances){
		String newInstances = "", newNumberOfInstances = "0";
		if (!oldNumberOfInstances.equals("1")){
			// construct new instance list without the given instance that will be deleted
			String [] instanceNames = oldInstances.split("\n");
			boolean first = true;
			for (int i = 0; i < instanceNames.length; i++){
				if (!instanceNames[i].equals(instanceName)){
					if (first){
						newInstances += instanceNames[i];
					} else {
						newInstances += "\n" + instanceNames[i];
					}
				}
			}			
			newNumberOfInstances = (Integer.parseInt(oldNumberOfInstances) - 1) + "";
		}
		
		BufferedWriter fileWriter = null;
		try {
			if (newInstances != "" && newNumberOfInstances != "0"){
				//write new number to the count file
				fileWriter = new BufferedWriter(new FileWriter(instanceCountFileName));
				fileWriter.write(newNumberOfInstances);
				fileWriter.close();
				//add remaining instance names to the listing file
				fileWriter = new BufferedWriter(new FileWriter(instanceFileName));
				fileWriter.write(newInstances);
			} else {
				Files.deleteIfExists(new File(instanceCountFileName).toPath());
				Files.deleteIfExists(new File(instanceFileName).toPath());
			}
			// delete instance specific file
			Files.deleteIfExists(new File(instanceDirectoryPath + File.separator + instanceName).toPath());
		} catch (Exception e){
			System.out.println("Error in deleteInstances");
			//e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (Exception e) {
				System.out.println("Error in closing fileReader of deleteInstance");
				//e.printStackTrace();
			}			
		}
	}	

	// sets up working directory for temporary storage of adapter data
	private static void setupWorkingDirectoy(){
		try {
			File workingDirectory = new File("mightyrobot");
/*			if (!workingDirectory.exists()){
				//workingDirectory.mkdir();
				System.out.println("mightyrobot dir created");
			} else {
				System.out.println("mightyrobot dir exists");
			}*/
			workingDirectory = new File(instanceDirectoryPath);
			if (!workingDirectory.exists()){
				workingDirectory.mkdirs();
				System.out.println("mightyrobot directories created");
			} else {
				System.out.println("mightyrobot directories exist");
			}			
		} catch (Exception e){
			e.printStackTrace();
		}
	}	
	
}
