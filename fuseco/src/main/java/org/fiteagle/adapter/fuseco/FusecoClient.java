package org.fiteagle.adapter.fuseco;

/**
 * defines methods related to Fuseco client e.g., get.. set...
 * @author alaa.alloush
 *
 */

public class FusecoClient {

	public FusecoClient(FusecoAdapter adapter){
		super();
		this.adapter = adapter; // new FusecoAdapter();
	}
	
	/**
	 * this will be used later to change FusecoClient parameters.
	 * now we still have no parameters for FusecoClient.
	 */
	private FusecoAdapter adapter;
	
	public String toString(){
		return "FusecoClient";
	}

}
