package org.fiteagle.adapters.testbed;

import java.io.InputStream;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class OntologyReader {

    static{
        testbedModel = loadModel("ontologies/testbedAdapter.ttl", "TURTLE");
    }

    private static Model testbedModel;
    public static Model getTestbedModel(){
        return testbedModel;
    }

    public static Model loadModel(String filename, String serialization) {
        Model fiteagle = ModelFactory.createDefaultModel();

        InputStream in2 = FileManager.get().open(filename);
        if (in2 == null) {
            throw new IllegalArgumentException("Ontology File: " + filename + " not found");
        }

        fiteagle.read(in2, null, serialization);

        return fiteagle;
    }
}
