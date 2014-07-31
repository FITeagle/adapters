package org.fiteagle.api.core;

public interface IResourceRepository {
	public static final String LIST_RESOURCES = "listResources";
	
	public static final String PROP_SERIALIZATION = "serialization";
	public static final String PROP_INSTANCE_ID = "instanceID";
	public static final String PROP_CONTROL = "control";
	
	public static final String SERIALIZATION_RDFXML_PLAIN = "RDF/XML";
	public static final String SERIALIZATION_RDFXML_ABBREV = "RDF/XML-ABBREV";
	public static final String SERIALIZATION_NTRIPLES = "NT";
	public static final String SERIALIZATION_TURTLE = "TTL";
	public static final String SERIALIZATION_N3 = "N3";
	public static final String SERIALIZATION_JSONLD = "JSON-LD";
	public static final String SERIALIZATION_RDFJSON = "RDFJSON";
	public static final String SERIALIZATION_TRIG = "TRIG";
	public static final String MESSAGE_FILTER = IMessageBus.TYPE_REQUEST + " = '" + LIST_RESOURCES + "'";
	
	// Jena needs: "TURTLE", "RDF/XML", "N-TRIPLE" !!!!!!!!!!

	public String listResources();
	public String listResources(String type);
	public String listResources(String query, String type);
	
	public String queryDatabse(String query, String type);
}
