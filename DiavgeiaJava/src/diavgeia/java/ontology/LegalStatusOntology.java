package diavgeia.java.ontology;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class LegalStatusOntology {
	
	public static final String diavgeiaPrefix = "http://publicspending.medialab.ntua.gr/ontology#";
	public static final String orgPrefix = "http://publicspending.medialab.ntua.gr/organizationsOntology#";
	public static final String instancePrefix = "http://publicspending.medialab.ntua.gr/resource/";
	
	//Top Level Resources
	public static final Resource freelancerEntity;
	public static final Resource physicalEntity;
	public static final Resource otherLegalEntity;
	public static final Resource plcEntity;
	public static final Resource ltdEntity;
	public static final Resource generalPartnershipEntity;
	public static final Resource limitedPartnershipEntity;
	public static final Resource publicLegalEntity;
	
	
	static {		
		freelancerEntity = ResourceFactory.createProperty(orgPrefix + "FreeLancer");
		physicalEntity = ResourceFactory.createProperty(orgPrefix + "PhysicalEntity");
		otherLegalEntity = ResourceFactory.createProperty(orgPrefix + "OtherLegalEntity");
		plcEntity = ResourceFactory.createProperty(orgPrefix + "PlcEntity");
		ltdEntity = ResourceFactory.createProperty(orgPrefix + "LtdEntity");
		generalPartnershipEntity = ResourceFactory.createProperty(orgPrefix + "GeneralPartnershipEntity");
		limitedPartnershipEntity = ResourceFactory.createProperty(orgPrefix + "LimitedPartnershipEntity");
		publicLegalEntity = ResourceFactory.createProperty(orgPrefix + "PublicLegalEntity");
		//notForProfitEntity = ResourceFactory.createProperty(orgPrefix + "NotForProfitEntity");
		//physicalEntity = ResourceFactory.createProperty(orgPrefix + "PhysicalEntity");
		//nonPhysicalEntity = ResourceFactory.createProperty(orgPrefix + "NonPhysicalEntity");
		
	}
	
	//ForProfit Subclass Resources
	
	
	
}
