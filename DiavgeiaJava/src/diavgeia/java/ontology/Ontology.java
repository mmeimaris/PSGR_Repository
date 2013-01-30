package diavgeia.java.ontology;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


public class Ontology {
	
	public static final String diavgeiaPrefix = "http://publicspending.medialab.ntua.gr/ontology#";
	public static final String instancePrefix = "http://publicspending.medialab.ntua.gr/resource/";
	//public static final String instancePrefix = "http://publicspending.medialab.ntua.gr/";
	public static final String foafPrefix = "http://xmlns.com/foaf/0.1/";
	public static final String paymentsPrefix = "http://reference.data.gov.uk/def/payment#";
	
	//Classes	
	public static final Resource paymentAgentResource;
	public static final Resource payerResource;
	public static final Resource payeeResource;
	public static final Resource signerResource;
	public static final Resource decisionResource;
	public static final Resource paymentResource;
	public static final Resource fekResource;
	public static final Resource tagResource;
	public static final Resource personResource;
	public static final Resource agentResource;
	
	//Object Properties
	public static final Property decisionOrganization;
	public static final Property decisionOrganizationUnit;
	public static final Property isReferencedBy;
	public static final Property organizationUnitPart;	
	public static final Property payee;
	public static final Property payer;
	public static final Property refersTo;
	public static final Property relativeFek;
	public static final Property tag;
	
	//Data Properties
	public static final Property ada;
	public static final Property afm;
	public static final Property cpv;
	public static final Property date;
	public static final Property decisionType;
	public static final Property description;
	public static final Property documentUrl;
	public static final Property fekIssue;
	public static final Property fekNumber;
	public static final Property fekYear;		
	public static final Property paymentAgentName;
	public static final Property paymentAmount;
	public static final Property paymentAmountString;
	public static final Property paymentCategory;
	public static final Property protocolNumber;
	public static final Property relativeKae;		
	public static final Property subject;
	public static final Property submissionTimeStamp;	
	public static final Property url;
	public static final Property shortName;
	
	//Static Ontology
	
	//Signer
	public static final Property signerId;	
	public static final Property signerFirstName;
	public static final Property signerLastName;
	public static final Property signerTitle;
	public static final Property signerPosition;
	public static final Property signerActive;
	public static final Resource signer;
	
	//Organization
	public static final Property organizationId;
	public static final Property organizationName;	
	public static final Resource organization;
	
	//Organization Unit
	public static final Property partOfOrganization;
	public static final Property organizationUnitName;
	public static final Property organizationUnitId;
	public static final Resource organizationUnit;
	
	//Tag
	public static final Property tagName;
	public static final Property tagId;	
	
	//Fek
	public static final Property fek;

	
	//Statistics
	public static final Property topPayment;
	public static final Property topPayer;
	public static final Property topPayee;
	public static final Property totalPaymentAmount;
	public static final Property aggregatePaymentAmount;
	public static final Property day;
	public static final Property month;
	public static final Property year;
	public static final Property week;
	public static final Property binaryPayer;
	public static final Property binaryPayee;
	public static final Resource dayResource;
	public static final Resource monthResource;
	public static final Resource yearResource;
	public static final Resource weekResource;
	public static final Resource overallResource;
	public static final Resource binaryRelationshipResource;
	
	//Tests
	public static final Property validAfm, validCpv, validDate;
	
	//GSIS
	public static final Property gsisName;
	public static final Property cpaGreekSubject;
	public static final Property postalZipCode;
	public static final Property cpaCode;
	public static final Property registrationDate;
	public static final Property stopDate;
	public static final Property doy;
	public static final Property postalCity;
	public static final Property postalStreetNumber;
	public static final Property postalStreetName;
	public static final Property phoneNumber;
	public static final Property faxNumber;
	public static final Property fpFlag;
	public static final Property deactivationFlag;
	public static final Property countOfBranches;
	public static final Property doyName;
	public static final Property firmDescription;
	public static final Property legalStatusDescription;
			
	//Ada Corrections
	public static final Property correctionOfAda;
	public static final Property relativeAda;
	
	//Names and stuff
	public static final Property validName;
	public static final Property validEngName;
	public static final Property searchEngName;
	
	
	static{
		
		fpFlag = ResourceFactory.createProperty(diavgeiaPrefix +"fpFlag");
		deactivationFlag = ResourceFactory.createProperty(diavgeiaPrefix + "deactivationFlag");
		countOfBranches = ResourceFactory.createProperty(diavgeiaPrefix + "countOfBranches");
		doyName = ResourceFactory.createProperty(diavgeiaPrefix + "doyName");
		firmDescription = ResourceFactory.createProperty(diavgeiaPrefix + "firmDescription");
				
		//Diavgeia
		organization = ResourceFactory.createResource(diavgeiaPrefix + "Organization");
		organizationUnit = ResourceFactory.createResource(diavgeiaPrefix + "OrganizationUnit");
		paymentAgentResource = ResourceFactory.createResource(diavgeiaPrefix + "PaymentAgent");
		payerResource = ResourceFactory.createResource(diavgeiaPrefix + "Payer");
		payeeResource = ResourceFactory.createResource(diavgeiaPrefix + "Payee");
		signerResource = ResourceFactory.createResource(diavgeiaPrefix + "Signer");
		decisionResource = ResourceFactory.createResource(diavgeiaPrefix + "Decision");
		paymentResource = ResourceFactory.createResource(diavgeiaPrefix + "Payment");		
		fekResource = ResourceFactory.createResource(diavgeiaPrefix + "Fek");
		tagResource = ResourceFactory.createResource(diavgeiaPrefix + "Tag");
		//Foaf
		personResource = ResourceFactory.createResource(foafPrefix + "Person");
		agentResource = ResourceFactory.createResource(foafPrefix + "Agent");
		
		
		decisionOrganization = ResourceFactory.createProperty(diavgeiaPrefix + "decisionOrganization");
		decisionOrganizationUnit = ResourceFactory.createProperty(diavgeiaPrefix + "decisionOrganizationUnit");
		isReferencedBy = ResourceFactory.createProperty(diavgeiaPrefix + "isReferencedBy");
		organizationUnitPart = ResourceFactory.createProperty(diavgeiaPrefix + "organizationUnitPart");
		partOfOrganization = ResourceFactory.createProperty(diavgeiaPrefix +"partOfOrganization");
		payee = ResourceFactory.createProperty(diavgeiaPrefix + "payee");
		payer = ResourceFactory.createProperty(diavgeiaPrefix + "payer");
		refersTo = ResourceFactory.createProperty(diavgeiaPrefix + "refersTo");
		relativeFek = ResourceFactory.createProperty(diavgeiaPrefix + "relativeFek");
		signer = ResourceFactory.createProperty(diavgeiaPrefix + "signer");
		tag = ResourceFactory.createProperty(diavgeiaPrefix + "tag");
		
		ada = ResourceFactory.createProperty(diavgeiaPrefix + "ada");
		afm = ResourceFactory.createProperty(diavgeiaPrefix + "afm");
		cpv = ResourceFactory.createProperty(diavgeiaPrefix + "cpv");
		date = ResourceFactory.createProperty(diavgeiaPrefix + "date");
		decisionType = ResourceFactory.createProperty(diavgeiaPrefix + "decisionType");
		description = ResourceFactory.createProperty(diavgeiaPrefix + "description");
		documentUrl = ResourceFactory.createProperty(diavgeiaPrefix + "documentUrl");
		fekIssue = ResourceFactory.createProperty(diavgeiaPrefix + "fekIssue");
		fekNumber = ResourceFactory.createProperty(diavgeiaPrefix + "fekNumber");
		fekYear = ResourceFactory.createProperty(diavgeiaPrefix + "fekyear");
		fek = ResourceFactory.createProperty(diavgeiaPrefix + "fek");
		organizationId = ResourceFactory.createProperty(diavgeiaPrefix + "organizationId");
		organizationName = ResourceFactory.createProperty(diavgeiaPrefix + "organizationName");
		organizationUnitId = ResourceFactory.createProperty(diavgeiaPrefix + "organizationUnitId");
		organizationUnitName = ResourceFactory.createProperty(diavgeiaPrefix + "organizationUnitName");
		paymentAgentName = ResourceFactory.createProperty(diavgeiaPrefix + "paymentAgentName");
		paymentAmount = ResourceFactory.createProperty(diavgeiaPrefix + "paymentAmount");
		paymentAmountString = ResourceFactory.createProperty(diavgeiaPrefix + "paymentAmountString");
		paymentCategory = ResourceFactory.createProperty(diavgeiaPrefix + "paymentCategory");
		protocolNumber = ResourceFactory.createProperty(diavgeiaPrefix + "protocolNumber");
		relativeKae = ResourceFactory.createProperty(diavgeiaPrefix + "relativeKae");
		signerActive = ResourceFactory.createProperty(diavgeiaPrefix + "signerActive");
		signerId = ResourceFactory.createProperty(diavgeiaPrefix + "signerId");
		signerPosition = ResourceFactory.createProperty(diavgeiaPrefix + "signerPosition");
		signerTitle = ResourceFactory.createProperty(diavgeiaPrefix + "signerTitle");
		signerFirstName = ResourceFactory.createProperty(diavgeiaPrefix + "signerFirstName");
		signerLastName = ResourceFactory.createProperty(diavgeiaPrefix + "signerLastName");
		subject = ResourceFactory.createProperty(diavgeiaPrefix + "subject");
		submissionTimeStamp = ResourceFactory.createProperty(diavgeiaPrefix + "submissionTimeStamp");
		tagId = ResourceFactory.createProperty(diavgeiaPrefix + "tagId");
		tagName = ResourceFactory.createProperty(diavgeiaPrefix + "tagName");
		url = ResourceFactory.createProperty(diavgeiaPrefix + "url");
		shortName = ResourceFactory.createProperty(diavgeiaPrefix + "shortName");
		
		//Statistics
		topPayment = ResourceFactory.createProperty(diavgeiaPrefix + "topPayment");
		topPayer = ResourceFactory.createProperty(diavgeiaPrefix + "topPayer");
		topPayee = ResourceFactory.createProperty(diavgeiaPrefix + "topPayee");
		totalPaymentAmount = ResourceFactory.createProperty(diavgeiaPrefix + "totalPaymentAmount");
		aggregatePaymentAmount = ResourceFactory.createProperty(diavgeiaPrefix + "aggregatePaymentAmount");
		day = ResourceFactory.createProperty(diavgeiaPrefix + "day");
		month = ResourceFactory.createProperty(diavgeiaPrefix + "month");
		year = ResourceFactory.createProperty(diavgeiaPrefix + "year");
		week = ResourceFactory.createProperty(diavgeiaPrefix + "week");		
		//Statistics - binary
		binaryRelationshipResource = ResourceFactory.createProperty(diavgeiaPrefix + "BinaryRelationship");
		binaryPayer = ResourceFactory.createProperty(diavgeiaPrefix + "binaryPayer");
		binaryPayee = ResourceFactory.createProperty(diavgeiaPrefix + "binaryPayee");
		
		dayResource = ResourceFactory.createResource(diavgeiaPrefix + "Day");
		monthResource = ResourceFactory.createResource(diavgeiaPrefix + "Month");
		yearResource = ResourceFactory.createResource(diavgeiaPrefix + "Year");
		weekResource = ResourceFactory.createResource(diavgeiaPrefix + "Week");
		overallResource = ResourceFactory.createResource(diavgeiaPrefix + "Overall");
		
		//Test Properties
		validAfm = ResourceFactory.createProperty(diavgeiaPrefix + "validAfm");
		validCpv = ResourceFactory.createProperty(diavgeiaPrefix + "validCpv");
		validDate = ResourceFactory.createProperty(diavgeiaPrefix + "validDate");
		
		//GSIS Properties
		gsisName = ResourceFactory.createProperty(diavgeiaPrefix + "gsisName");
		cpaGreekSubject = ResourceFactory.createProperty(diavgeiaPrefix + "cpaGreekSubject");
		postalZipCode = ResourceFactory.createProperty(diavgeiaPrefix + "postalZipCode");
		cpaCode = ResourceFactory.createProperty(diavgeiaPrefix + "cpaCode");
		registrationDate = ResourceFactory.createProperty(diavgeiaPrefix + "registrationDate");
		stopDate = ResourceFactory.createProperty(diavgeiaPrefix + "stopDate");
		doy = ResourceFactory.createProperty(diavgeiaPrefix + "doy");
		postalCity = ResourceFactory.createProperty(diavgeiaPrefix + "postalCity");
		postalStreetNumber = ResourceFactory.createProperty(diavgeiaPrefix + "postalStreetNumber");
		postalStreetName = ResourceFactory.createProperty(diavgeiaPrefix + "postalStreetName");
		phoneNumber = ResourceFactory.createProperty(diavgeiaPrefix + "phoneNumber");
		faxNumber = ResourceFactory.createProperty(diavgeiaPrefix + "faxNumber");
		legalStatusDescription = ResourceFactory.createProperty(diavgeiaPrefix + "legalStatus");
		//Corrections
		correctionOfAda = ResourceFactory.createProperty(diavgeiaPrefix + "correctionOfAda");
		relativeAda = ResourceFactory.createProperty(diavgeiaPrefix + "relativeAda");
		//Names
		validName = ResourceFactory.createProperty(diavgeiaPrefix + "validName");
		validEngName = ResourceFactory.createProperty(diavgeiaPrefix + "validEngName");
		searchEngName = ResourceFactory.createProperty(diavgeiaPrefix + "searchEngName");
		
	}
	
}
