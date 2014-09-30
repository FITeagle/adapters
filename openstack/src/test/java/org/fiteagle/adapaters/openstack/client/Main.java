package org.fiteagle.adapaters.openstack.client;

import org.codehaus.jackson.map.ObjectMapper;
import org.fiteagle.adapters.openstack.OpenstackVMAdapter;
import org.fiteagle.adapters.openstack.client.OpenstackClient;
import org.fiteagle.adapters.openstack.client.model.Image;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.ServerForCreate;

import com.woorea.openstack.nova.model.Flavor;
import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.FloatingIp;
import com.woorea.openstack.nova.model.FloatingIpDomains;
import com.woorea.openstack.nova.model.FloatingIpPools;

public class Main {
	static String serverName = "testServer";
	static String imageId = "";
	static String flavorId = "1";
	static String keyPairName = "testKeyPair";
	static String tentantId="";
	
	public static void main(String[] args) {
		
		new OpenstackVMAdapter();
		OpenstackClient client = new OpenstackClient();
//		listFlavors(client);
//		listImages(client);
		listonlyPrivateImages(client);
//		client.checkEveryThing();
//		getServerDetail(client);
		
//		getFloatingIps(client);
//		createServer(client);
//		addFloatingIp(client);
		
//		getFloatingIpPools(client);
		
//		client.addKeyPair("test", "");
//		Server server = client.createServer(imageId, flavorId, serverName, keyPairName);
////		
//		Server serverDetails = client.getServerDetails(server.getId());
////		Server serverDetails = client.getServerDetails("");
////		
//		FloatingIp floatingIp = client.addFloatingIp();
		
//		client.allocateFloatingIpForServer("", floatingIp.getIp());
//		client.allocateFloatingIpForServer(serverDetails.getId(), floatingIp.getIp());
////		client.allocateFloatingIpForServer(server.getId(), "");
		
		
//		client.deleteServer("");
		
//		client.deleteKeyPair(keyPairName);
//		String network = client.getNetworkId();
//		System.out.println(network);

	}

	private static void addFloatingIp(OpenstackClient client) {
		FloatingIp floatingIp = client.addFloatingIp();
		System.out.println(floatingIp);
	}

	private static void getFloatingIpPools(OpenstackClient client) {
		FloatingIpPools floaingIpPools = client.getFloatingIpPools();
		System.out.println(floaingIpPools);
	}

	private static void getServerDetail(OpenstackClient client) {
		Server serverDetail = client.getServerDetails(client.createServer(imageId, flavorId, serverName, keyPairName).getId());
		System.out.println(serverDetail);
	}

	private static void createServer(OpenstackClient client) {
		org.fiteagle.adapters.openstack.client.model.Server server = client.createServer(imageId, flavorId, serverName, keyPairName);
		System.out.println(server);
	}

	private static void listImages(OpenstackClient client) {
		Images images = client.listImages();
		for (Image image : images) {
			System.out.println(image);
		}
	}
	
	private static void listonlyPrivateImages(OpenstackClient client) {
//		Images images = client.listOnlyPrivateImages();
		client.listOnlyPrivateImages();
//		for (Image image : images) {
//			System.out.println(image);
//		}
	}

	private static void listFlavors(OpenstackClient client) {
		Flavors flavors = client.listFlavors();
		for (Flavor flavor : flavors) {
			System.out.println(flavor);
		}
	}

}
