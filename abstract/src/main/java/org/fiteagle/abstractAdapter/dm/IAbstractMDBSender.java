package org.fiteagle.abstractAdapter.dm;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Created by vju on 8/22/14.
 */


public interface IAbstractMDBSender {

    public void registerAdapter();

    public void unregisterAdapter();

    public void sendInformMessage(Model eventRDF);

}
