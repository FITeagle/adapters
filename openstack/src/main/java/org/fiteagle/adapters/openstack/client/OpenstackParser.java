package org.fiteagle.adapters.openstack.client;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.fiteagle.adapters.openstack.client.model.Images;
import org.fiteagle.adapters.openstack.client.model.Server;
import org.fiteagle.adapters.openstack.client.model.Servers;

import com.woorea.openstack.nova.model.Flavors;
import com.woorea.openstack.nova.model.FloatingIp;

public class OpenstackParser {

	private static JsonFactory factory = new JsonFactory();
	private static ObjectMapper mapper = new ObjectMapper(factory);

	public static Images parseToImages(String imagesString) throws JsonParseException,
			JsonMappingException, IOException {

		return mapper.readValue(imagesString, Images.class);
	}

	public static FloatingIp parseToFloatingIp(String floatingIpString) throws JsonParseException,
			JsonMappingException, IOException {

		return mapper.readValue(floatingIpString, FloatingIp.class);
	}

	public static Flavors parseToFlavors(String flavorsString)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(flavorsString, Flavors.class);
	}

	public static Server parseToServer(String serverString) throws JsonParseException,
			JsonMappingException, IOException {
		Server server = mapper.readValue(serverString, Server.class);
		return server;
	}

	public static Servers parseToServers(String serversString)
      throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(serversString, Servers.class);
	}
}
