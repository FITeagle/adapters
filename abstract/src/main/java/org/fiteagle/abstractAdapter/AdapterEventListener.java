package org.fiteagle.abstractAdapter;

import com.hp.hpl.jena.rdf.model.Model;

public interface AdapterEventListener {

    void rdfChange(Model eventRDF, String requestID);
}
