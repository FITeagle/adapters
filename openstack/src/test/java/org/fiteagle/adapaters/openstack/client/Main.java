package org.fiteagle.adapaters.openstack.client;

import org.fiteagle.adapters.openstack.client.OpenstackClient;
import org.fiteagle.adapters.openstack.client.model.Image;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;

import com.woorea.openstack.nova.model.Flavor;
import com.woorea.openstack.nova.model.Flavors;

public class Main {
	static String serverName = "testServer";
	static String imageId = "f4603773-82cb-4931-9b6f-919335dfdc79";
	static String flavorId = "1";
	static String keyPairName = "mitja_tub";
	
	public static void main(String[] args) {
		
		OpenstackClient client = OpenstackClient.getInstance();
		
//		Servers servers = client.listServers();
//		for(Server server : servers.getList()){
//		  System.out.println(server);
//		}
		
//		listFlavors(client);
		listImages(client);
//		getServerDetail(client);
//		Server server = client.createServer(imageId, flavorId, serverName, keyPairName);
//		client.deleteServer(server.getId());
		
//		client.addKeyPair("test", "");
//		Server serverDetails = client.getServerDetails(server.getId());
//		Server serverDetails = client.getServerDetails("");
//		client.deleteKeyPair(keyPairName);
//		String network = client.getNetworkId();
//		System.out.println(network);
	}

	private static void getServerDetail(OpenstackClient client) {
		Server serverDetail = client.getServerDetails(client.createServer(imageId, flavorId, serverName, keyPairName).getId());
		System.out.println(serverDetail);
	}

	private static void listImages(OpenstackClient client) {
		Images images = client.listImages();
		for (Image image : images) {
			System.out.println(image);
		}
	}
	
	private static void listFlavors(OpenstackClient client) {
		Flavors flavors = client.listFlavors();
		for (Flavor flavor : flavors) {
			System.out.println(flavor);
		}
	}

}
