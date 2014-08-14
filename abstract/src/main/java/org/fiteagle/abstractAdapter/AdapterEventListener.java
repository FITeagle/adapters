package org.fiteagle.abstractAdapter;

import com.hp.hpl.jena.rdf.model.Model;

public interface AdapterEventListener {

    public void rdfChange(Model eventRDF);
}
