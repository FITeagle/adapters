package org.fiteagle.adapters.openstack;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.prefs.Preferences;

import org.fiteagle.adapters.common.AdapterConfiguration;
import org.fiteagle.adapters.openstack.client.OfflineTestClient;
import org.fiteagle.adapters.openstack.client.OpenstackClient;
import org.fiteagle.adapters.openstack.client.Utils;
import org.fiteagle.adapters.openstack.client.model.Image;
import org.fiteagle.adapters.openstack.client.model.Server;

import com.woorea.openstack.nova.model.Flavor;
import com.woorea.openstack.nova.model.FloatingIp;

public class OpenstackVMAdapter extends ResourceAdapter implements
		OpenstackResourceAdapter {

	private static boolean loaded = false;

	public static boolean utilsConfigured = false;

	private OpenstackClient client;
	private Image image;
	private List<Flavor> flavorsList;
	private Server server=new Server();
	private String keyPairName;
	private String vmName;
	private String imageId;
	private String flavorId;

	private String floatingIp = null;

	private static boolean offlineTestMode = false;

	public OpenstackVMAdapter() {
		super();
		if (!utilsConfigured) {
			this.configureUtils();
		}
		this.setType("org.fiteagle.adapters.openstack.OpenstackVMAdapter");
	}

	private void configureUtils() {
		Preferences preferences = Preferences.userNodeForPackage(getClass());

		if (preferences.get("floating_ip_pool_name", null) != null)
			Utils.FLOATINGIP_POOL_NAME = preferences.get(
					"floating_ip_pool_name", null);
		if (preferences.get("keystone_auth_URL", null) != null)
			Utils.KEYSTONE_AUTH_URL = preferences
					.get("keystone_auth_URL", null);
		if (preferences.get("keystone_endpoint", null) != null)
			Utils.KEYSTONE_ENDPOINT = preferences
					.get("keystone_endpoint", null);
		if (preferences.get("keystone_password", null) != null)
			Utils.KEYSTONE_PASSWORD = preferences
					.get("keystone_password", null);
		if (preferences.get("keystone_username", null) != null)
			Utils.KEYSTONE_USERNAME = preferences
					.get("keystone_username", null);
		if (preferences.get("net_endpoint", null) != null)
			Utils.NET_ENDPOINT = preferences.get("net_endpoint", null);
		if (preferences.get("net_name", null) != null)
			Utils.NET_NAME = preferences.get("net_name", null);
		if (preferences.get("nova_endpoint", null) != null)
			Utils.NOVA_ENDPOINT = preferences.get("nova_endpoint", null);
		if (preferences.get("tenant_name", null) != null)
			Utils.TENANT_NAME = preferences.get("tenant_name", null);

	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
		this.getClient().deleteKeyPair(this.getKeyPairName());
		this.getClient().deleteServer(this.getServer().getId());
	}

	@Override
	public void create() {
	}

	@Override
	public void configure(AdapterConfiguration configuration) {

		String sshPubKey = configuration.getUsers().get(0).getSshPublicKeys().get(0);
		this.getClient().addKeyPair(keyPairName, sshPubKey);
		
		System.out.println("creating key pair: "+this.getKeyPairName());
		
		Server createdServer = this.getClient().createServer(this.imageId, this.flavorId,
				this.vmName, this.keyPairName);
		
		System.out.println("creating server(vm) with image id: "+this.imageId);
		
		this.setServer(createdServer);
		
		System.out
				.println("configure on openstack adapter is called configuring the ip ");
		FloatingIp floatingIp = this.getClient().addFloatingIp();
		
		System.out.println("adding a floating ip..");
		
		this.setFloatingIp(floatingIp.getIp());
		this.server = this.getClient().getServerDetails(server.getId());
		this.getClient().allocateFloatingIpForServer(server.getId(),
				floatingIp.getIp());
		
		System.out.println("allocating floating ip for server "+server.getId());
	}

	@Override
	public void release() {
		this.getClient().deleteKeyPair(this.getKeyPairName());
		this.getClient().deleteServer(this.getServer().getId());
	}

	public static List<ResourceAdapter> getJavaInstances() {
		return new ArrayList<ResourceAdapter>();
	}

	public static boolean isLoaded() {
		return loaded;
	}

	public static void setLoaded(boolean ld) {
		loaded = ld;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public List<Flavor> getFlavorsList() {
		return flavorsList;
	}

	public void setFlavorsList(List<Flavor> flavorsList) {
		this.flavorsList = flavorsList;
	}

	public OpenstackClient getClient() {
		if (this.client == null) {
			if (offlineTestMode) {
				this.client = new OfflineTestClient();
			} else {
				this.client = new OpenstackClient();
			}
		}
		return client;
	}

	public void setClient(OpenstackClient client) {
		this.client = client;
	}

	public static boolean isOfflineTestMode() {
		return offlineTestMode;
	}

	public static void setOfflineTestMode(boolean offlineTestMode) {
		OpenstackVMAdapter.offlineTestMode = offlineTestMode;
	}

	public HashMap<String, String> getImageProperties() {

		HashMap<String, String> imageProperties = new HashMap<String, String>();

		imageProperties.put(OpenstackResourceAdapter.IMAGE_ID, image.getId());
		imageProperties.put(OpenstackResourceAdapter.IMAGE_NAME,
				image.getName());
		imageProperties.put(OpenstackResourceAdapter.IMAGE_MINDISK, image
				.getMinDisk().toString());

		if (image.getCreated() != null)
			imageProperties.put(OpenstackResourceAdapter.IMAGE_CREATED,
					getLongValueAsStringOfCalendar((image.getCreated())));
		imageProperties.put(OpenstackResourceAdapter.IMAGE_MINRAM, image
				.getMinRam().toString());
		imageProperties.put(OpenstackResourceAdapter.IMAGE_OSEXTIMG_SIZE, image
				.getSize().toString());
		imageProperties.put(OpenstackResourceAdapter.IMAGE_PROGRESS, image
				.getProgress().toString());
		imageProperties.put(OpenstackResourceAdapter.IMAGE_STATUS,
				image.getStatus());
		if (image.getUpdated() != null)
			imageProperties.put(OpenstackResourceAdapter.IMAGE_UPDATED,
					getLongValueAsStringOfCalendar(image.getUpdated()));

		return imageProperties;
	}

	public List<HashMap<String, String>> getFlavorsProperties() {

		List<HashMap<String, String>> resultList = new ArrayList<HashMap<String, String>>();

		if (this.flavorsList == null || this.flavorsList.isEmpty())
			return null;

		for (Iterator<Flavor> iterator = flavorsList.iterator(); iterator.hasNext();) {

			Flavor flavor = (Flavor) iterator.next();
			HashMap<String, String> tmpProperties = new HashMap<String, String>();
			tmpProperties.put(OpenstackResourceAdapter.FLAVOR_OSFLVDISABLED,
					flavor.getDisabled().toString());
			tmpProperties.put(OpenstackResourceAdapter.FLAVOR_DISK,
					flavor.getDisk());
			tmpProperties.put(OpenstackResourceAdapter.FLAVOR_ID,
					flavor.getId());
			tmpProperties.put(OpenstackResourceAdapter.FLAVOR_NAME,
					flavor.getName());
			tmpProperties.put(
					OpenstackResourceAdapter.FLAVOR_OSFLAVORACCESSISPUBLIC,
					flavor.isPublic().toString());
			tmpProperties.put(OpenstackResourceAdapter.FLAVOR_VCPUS,
					flavor.getVcpus());
			tmpProperties.put(
					OpenstackResourceAdapter.FLAVOR_OSFLVEXTDATAEPHEMERAL,
					flavor.getEphemeral().toString());
			tmpProperties.put(OpenstackResourceAdapter.FLAVOR_RAM, flavor
					.getRam().toString());
			tmpProperties.put(OpenstackResourceAdapter.FLAVOR_RXTXFACTOR,
					flavor.getRxtxFactor().toString());
			tmpProperties.put(OpenstackResourceAdapter.FLAVOR_SWAP,
					flavor.getSwap());

			resultList.add(tmpProperties);
		}

		return resultList;

	}

	 @Override
	public OpenstackResourceAdapter create(String imageId, String flavorId,
			String vmName, String keyPairName) {

		if (vmName == null || vmName.compareTo("") == 0) {
			vmName = generateRandomString();
		}
		
		if(keyPairName==null || keyPairName.compareTo("")==0){
			keyPairName=generateRandomString();
		}

		OpenstackVMAdapter openstackVM = new OpenstackVMAdapter();

		openstackVM.setVmName(vmName);
		openstackVM.setImageId(imageId);
		openstackVM.setFlavorId(flavorId);
		openstackVM.setKeyPairName(keyPairName);
		openstackVM.setImage(this.image);
		openstackVM.setFlavorsList(this.flavorsList);
		return openstackVM;
	}

	private String generateRandomString() {
		return UUID.randomUUID().toString();
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	@Override
	public HashMap<String, String> getVMProperties() {
		HashMap<String, String> vmProperties = new HashMap<String, String>();

		if (server.getAccessIPv4() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_AccessIPv4,
					server.getAccessIPv4());
		if (server.getAccessIPv6() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_AccessIPv6,
					server.getAccessIPv6());

		if (server.getConfigDrive() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_ConfigDrive,
					server.getConfigDrive());

		if (server.getCreated() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_Created,
					server.getCreated());

		if (server.getFlavor() != null && server.getFlavor().getId() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_FlavorId, server
					.getFlavor().getId());

		if (server.getHostId() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_HostId,
					server.getHostId());

		if (server.getId() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_Id, server.getId());

		if (server.getImage() != null && server.getImage().getId() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_ImageId, server
					.getImage().getId());

		if (server.getKeyName() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_KeyName,
					server.getKeyName());

		if (server.getName() != null)
			vmProperties
					.put(OpenstackResourceAdapter.VM_Name, server.getName());

		if (server.getDiskConfig() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_OSDCFDiskConfig,
					server.getDiskConfig());

		if (server.getAvailabilityZone() != null)
			vmProperties.put(
					OpenstackResourceAdapter.VM_OSEXTAZAvailabilityZone,
					server.getAvailabilityZone());

		if (server.getPowerState() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_OSEXTSTSPowerState,
					server.getPowerState());

		if (server.getTaskState() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_OSEXTSTSTaskState,
					server.getTaskState());

		if (server.getVmState() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_OSEXTSTSVmState,
					server.getVmState());

		if (server.getProgress() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_Progress, server
					.getProgress().toString());

		if (server.getStatus() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_Status,
					server.getStatus());

		if (server.getTenantId() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_TenantId,
					server.getTenantId());

		if (server.getUpdated() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_Updated,
					server.getUpdated());

		if (server.getUserId() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_UserId,
					server.getUserId());

		if (this.getFloatingIp() != null)
			vmProperties.put(OpenstackResourceAdapter.VM_FloatingIP,
					this.getFloatingIp());

		return vmProperties;

	}

	public String getFloatingIp() {
		return floatingIp;
	}

	public void setFloatingIp(String floatingIp) {
		this.floatingIp = floatingIp;
	}

	public String getKeyPairName() {
		return keyPairName;
	}

	public void setKeyPairName(String keyPairName) {
		this.keyPairName = keyPairName;
	}

	private String getLongValueAsStringOfCalendar(Calendar calendar) {
		return String.valueOf(calendar.getTimeInMillis());
	}

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getFlavorId() {
		return flavorId;
	}

	public void setFlavorId(String flavorId) {
		this.flavorId = flavorId;
	}

	@Override
	public String getParentNodeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentNodeId(String nodeId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkAndSetRAReady() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkStatus() {
		// TODO Auto-generated method stub
		
	}




	

}
