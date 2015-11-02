package org.fiteagle.adapters.tosca;


import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import info.openmultinet.ontology.vocabulary.*;
import org.fiteagle.abstractAdapter.AbstractAdapter;
import org.fiteagle.adapters.tosca.client.IToscaClient;
import org.fiteagle.adapters.tosca.client.OrchestratorClient;
import org.fiteagle.adapters.tosca.client.ToscaClient;
import org.fiteagle.adapters.tosca.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

/**
 * Created by dne on 27.10.15.
 */
public class ToscaAdapterBuilder {

    Logger LOGGER = Logger.getLogger(this.getClass().getName());
    List<Service> services;
    List<Location> locations;
    List<Datacenter> datacenters;

    IToscaClient toscaClient;

    OrchestratorClient oscoClient;


    public ToscaAdapterBuilder(OrchestratorClient client, IToscaClient client1) {
        this.oscoClient = client;
        this.toscaClient = client1;
        this.services = oscoClient.getServices();
        this.datacenters = oscoClient.getDataCenters();
        this.locations = getLocations();

    }

    private List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        for(Datacenter d: datacenters){
            LOGGER.log(INFO, d.getName() );
            LOGGER.log(INFO, d.getType() );
            locations.add(d.getLocation());
        }
        return  locations;
    }

    public void handleAdapter(AbstractAdapter adapter) {
        addServices(adapter);
        addLocations(adapter);
        


    }

    private void addLocations(AbstractAdapter adapter) {
        for(Location location: locations){

            Resource resource = adapter.getAdapterDescriptionModel().createResource(adapter.getAdapterABox().getURI() + "/" + location.getName());
            resource.addProperty(RDF.type, Omn_resource.Location);
            resource.addProperty(RDFS.label,location.getName());
            resource.addProperty(Omn_lifecycle.hasID,location.getId());
            resource.addProperty(Geonames.countryCode,"xx");
            resource.addProperty(Wgs84.lat,"" +location.getLatitude());
            resource.addProperty(Wgs84.long_,""+location.getLongitude());

            adapter.getAdapterABox().addProperty(Omn_resource.hasLocation, resource);

        }
    }

    private void addServices(AbstractAdapter adapter) {
        for(Service service: services){

            Resource resource = adapter.getAdapterDescriptionModel().createResource(adapter.getAdapterABox().getURI() + "/"+ service.getServiceType());
            resource.addProperty(RDFS.subClassOf, Omn.Resource);
            addConfiguration(service,resource);
            adapter.getAdapterABox().addProperty(Omn_lifecycle.canImplement, resource);

        }
    }

    private void addConfiguration(Service service, Resource resource) {
        Configuration configuration = service.getConfiguration();
        List<Parameter> parameters = configuration.getParameters();
        for(Parameter p: parameters){
            Property property = resource.getModel().createProperty(Osco.NS,p.getConfig_key());
            resource.addProperty(property,p.getConfig_value());
        }

    }

    public List<Datacenter> getDatacenters() {
        return datacenters;
    }
}
