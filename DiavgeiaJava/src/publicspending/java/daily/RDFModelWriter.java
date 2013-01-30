package publicspending.java.daily;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

import diavgeia.java.ontology.Ontology;

public class RDFModelWriter {

	HelperMethods helper;
	Model model = ModelFactory.createDefaultModel(), invalidModel = ModelFactory.createDefaultModel();
	int val = 0, inval = 0, count = 0;
	HashSet<String> checkSet = new HashSet<String>();
	FileStructure fs;
	
	public RDFModelWriter(FileStructure fs){
		helper = new HelperMethods();
		this.fs = fs;
	}
	
	
	
	public boolean writeModels(){
			
			//SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
			//String folderDateString = dayFormat.format(new Date())+"/";		
			//folderDateString = "36-08-2012/";
			//String folderDateString2 = "36-08-2012";
		int chk = 0;
		try{			
			//String rdfLocationVariable = "C:/Users/marios/Desktop/Diavgeia/Test Auto/" + folderDateString + "Valid RDF/";
			String rdfLocationVariable = fs.validRDFDateFile;
			System.out.println("Writing valid model to file " + rdfLocationVariable + " .....");
    		//FileOutputStream fos = new FileOutputStream(rdfLocationVariable+folderDateString2+".rdf");
    		FileOutputStream fos = new FileOutputStream(rdfLocationVariable);
			//model.write(fos, "RDF/XML-ABBREV", rdfLocationVariable+folderDateString2+".rdf");
			model.write(fos, "RDF/XML-ABBREV", rdfLocationVariable);
			//model.write(fos, "N-TRIPLES", rdfLocationVariable);
			fos.close();
			chk++;
		}
		catch(Exception e){e.printStackTrace(); chk--;}	    		
		//Write the invalid entries in an RDF file.
		try{
			//String invalidRdfLocationVariable = "C:/Users/marios/Desktop/Diavgeia/Test Auto/" + folderDateString + "Invalid RDF/";
			String invalidRdfLocationVariable = fs.invalidRDFDateFile;
    		//FileOutputStream fos2 = new FileOutputStream(invalidRdfLocationVariable+folderDateString2+".rdf");
    		FileOutputStream fos2 = new FileOutputStream(invalidRdfLocationVariable);
			//invalidModel.write(fos2, "RDF/XML-ABBREV", invalidRdfLocationVariable+folderDateString2+".rdf");
			invalidModel.write(fos2, "RDF/XML-ABBREV", invalidRdfLocationVariable);
			//invalidModel.write(fos2, "N-TRIPLES", invalidRdfLocationVariable);
			fos2.close();			
			chk++;
		}
		catch(Exception e){e.printStackTrace(); chk--;}
		System.out.println("total count is " + count);
		if(chk==2) return true;
		else return false;
		//System.out.println("checkset contains " + checkSet.size() + " decisions");
	}
	
	public void AddToModel(HashMap<String, String> hashmap){
		
		boolean[] checks = new boolean[4];		
		checks[0] = VATChecker.checkAfm((String)hashmap.get("ΑΦΜ ΦΟΡΕΑ"), hashmap, "ΑΦΜ ΦΟΡΕΑ");		
		checks[1] = VATChecker.checkAfm((String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ"), hashmap, "ΑΦΜ ΑΝΑΔΟΧΟΥ");
		String cpvCode = (String)hashmap.get(" CPV Number") + " " + (String) hashmap.get("CPV Description");    				
		checks[2] = VATChecker.checkCpvString(cpvCode, hashmap);
		String[] el = helper.getDateElements((String)hashmap.get("date"));
		checks[3] = (el[0]!="Null");		
		if(checks[0] && checks[1] && checks[2]){
			addToCorrectModel(hashmap, model, checks);
			val++;
		}
		else{
			addToCorrectModel(hashmap, invalidModel, checks);
			inval++;
		}				
	}
	
	
	public void addToCorrectModel(HashMap<String, String> hashmap, Model model, boolean[] checks){
    	
		count++;
		//HashSet<String> agents = new HashSet<String>();
    	Resource decision = model.createResource(Ontology.instancePrefix + "decisions/" + (String)hashmap.get("ada"));
    	//checkSet.add(decision.getURI());
		Resource payment = model.createResource(Ontology.instancePrefix+"payments/"+(String)hashmap.get("ada"));
		Resource organization = model.createResource(Ontology.instancePrefix + "organizations/"+(String)hashmap.get("organizationUID"));    		
		Resource organizationUnit = model.createResource(Ontology.instancePrefix + "organizationUnits/"+(String)hashmap.get("organizationUnitUID"));
		Resource payer, payee;
		String correction = (String)hashmap.get("isCorrectionOfAda");
		ArrayList<String> correctives = getCorrectives(correction);
		for(String corrective : correctives){
			if(!correction.equals("Null"))
				decision.addProperty(Ontology.correctionOfAda, model.createResource(Ontology.instancePrefix + "decisions/" + corrective));
		}    	    	
		String rel = (String)hashmap.get("relativeAda");
    	ArrayList<String> relatives = getCorrectives(rel);
    	for(String relative : relatives){
			if(!relative.equals("Null"))
				decision.addProperty(Ontology.relativeAda, model.createResource(Ontology.instancePrefix + "decisions/" + relative));
		}    		
		
		if(!checks[0]){			
			payer = model.createResource(Ontology.instancePrefix + "paymentAgents/" + (String)hashmap.get("ΑΦΜ ΦΟΡΕΑ")+"Ada"+hashmap.get("ada"));
			payer.addProperty(Ontology.validAfm, "false");
		}
		else{
			payer = model.createResource(Ontology.instancePrefix + "paymentAgents/" + (String)hashmap.get("ΑΦΜ ΦΟΡΕΑ"));			
		}
		if(!checks[1]){
			payee = model.createResource(Ontology.instancePrefix + "paymentAgents/" + (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ")+"Ada"+hashmap.get("ada"));			
			payee.addProperty(Ontology.validAfm, "false");
		}
		else{
			payee = model.createResource(Ontology.instancePrefix + "paymentAgents/" + (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ"));			
		}
		
		//Tags
		String[] tags = hashmap.get("tagArray").split("#");
		for(String tg : tags){
			if(!tg.equals("Null") && !tg.equals(""))
				decision.addProperty(Ontology.tag, ResourceFactory.createResource(Ontology.instancePrefix+"tags/"+tg));
		}
		/*String tagId = hashmap.get("tagUIDs");
		if(!tagId.equals("Null") && !tagId.equals(""))
			decision.addProperty(Ontology.tag, ResourceFactory.createResource(Ontology.instancePrefix+"tags/"+(String)hashmap.get("tagUIDs")));*/
		
		//FEK
		String fekNumber = hashmap.get("FEK number");
		String fekIssue = hashmap.get("FEK issue");
		String fekYear = hashmap.get("FEK year");
		if(!fekNumber.equals("Null") && !fekNumber.equals("") && !fekIssue.equals("Null") && !fekIssue.equals("" )&& !fekYear.equals("Null") && !fekYear.equals(""))
			{decision.addProperty(Ontology.fek, model.createResource(Ontology.instancePrefix+"feks#"+fekNumber+fekIssue+fekYear)
															 				 .addProperty(Ontology.fekIssue, fekIssue)
															 				 .addProperty(Ontology.fekNumber, fekNumber)
															 				 .addProperty(Ontology.fekYear, fekYear));
			}
		//Decision Properties
		decision.addProperty(Ontology.ada, (String)hashmap.get("ada"));		
		if(!checks[3])
			decision.addProperty(Ontology.validDate, "false");
		String[] dateElements = helper.getDateElements((String)hashmap.get("date"));	
		String[] timestampElements = helper.getDateElements((String)hashmap.get("submissionTimestamp"));		
		Resource day, week, month, year;
		if(dateElements[2].equals("Null")){
			decision.addProperty(Ontology.date, model.createTypedLiteral(timestampElements[2], XSDDatatype.XSDdate));
			day = model.createResource(Ontology.instancePrefix+"days/"+timestampElements[2]).addProperty(RDF.type, Ontology.dayResource).addProperty(Ontology.date, timestampElements[2]);
			month = model.createResource(Ontology.instancePrefix+"months/"+timestampElements[1]).addProperty(RDF.type, Ontology.monthResource).addProperty(Ontology.date, timestampElements[1]);
			year = model.createResource(Ontology.instancePrefix+"years/"+timestampElements[0]).addProperty(RDF.type, Ontology.yearResource).addProperty(Ontology.date, timestampElements[0]);		
			//Weeks need special treatment
			week = model.createResource(Ontology.instancePrefix+"weeks/"+timestampElements[0]+"-w-"+timestampElements[3]).addProperty(RDF.type, Ontology.weekResource).addProperty(Ontology.date, timestampElements[0]+"-w-"+timestampElements[3]);
			decision.addProperty(Ontology.day,day).addProperty(Ontology.month, month).addProperty(Ontology.year, year).addProperty(Ontology.week, week);					
		}
		else{
			decision.addProperty(Ontology.date, model.createTypedLiteral(dateElements[2], XSDDatatype.XSDdate));
			day = model.createResource(Ontology.instancePrefix+"days/"+dateElements[2]).addProperty(RDF.type, Ontology.dayResource).addProperty(Ontology.date, dateElements[2]);
			month = model.createResource(Ontology.instancePrefix+"months/"+dateElements[1]).addProperty(RDF.type, Ontology.monthResource).addProperty(Ontology.date, dateElements[1]);
			year = model.createResource(Ontology.instancePrefix+"years/"+dateElements[0]).addProperty(RDF.type, Ontology.yearResource).addProperty(Ontology.date, dateElements[0]);		
			//Weeks need special treatment
			week = model.createResource(Ontology.instancePrefix+"weeks/"+dateElements[0]+"-w-"+dateElements[3]).addProperty(RDF.type, Ontology.weekResource).addProperty(Ontology.date, dateElements[0]+"-w-"+dateElements[3]);
			decision.addProperty(Ontology.day,day).addProperty(Ontology.month, month).addProperty(Ontology.year, year).addProperty(Ontology.week, week);			
		}
		decision.addProperty(Ontology.submissionTimeStamp, (String)hashmap.get("submissionTimestamp"));
		decision.addProperty(Ontology.url, (String)hashmap.get("url"));
		decision.addProperty(Ontology.documentUrl, (String)hashmap.get("documentUrl"));
		decision.addProperty(Ontology.protocolNumber, (String)hashmap.get("protocolNumber"));
		decision.addProperty(Ontology.decisionType, (String)hashmap.get("decisionType"));
		decision.addProperty(Ontology.refersTo, payment);
		decision.addProperty(Ontology.subject, (String)hashmap.get("subject"));
		decision.addProperty(Ontology.decisionOrganization, organization);
		decision.addProperty(Ontology.decisionOrganizationUnit, organizationUnit);
		decision.addProperty(RDF.type, Ontology.decisionResource);
		
		//Payment Properties		
		if(checks[2]){
			payment.addProperty(Ontology.cpv, ResourceFactory.createResource(Ontology.instancePrefix + "cpvCodes/" + (String)hashmap.get(" CPV Number")));
			payment.addProperty(Ontology.validCpv, "true");
		}
		else{
			payment.addProperty(Ontology.validCpv, "false");
			payment.addProperty(Ontology.cpv, (String)hashmap.get(" CPV Number"));
		}
		//Amounts need to be decorated
		String amount = (String)hashmap.get("ΠΟΣΟ ΔΑΠΑΝΗΣ / ΣΥΝΑΛΛΑΓΗΣ (ΜΕ ΦΠΑ)");
		payment.addProperty(Ontology.paymentAmountString, helper.decoratePaymentAmount(amount));     		    	
		payment.addProperty(Ontology.paymentAmount, model.createTypedLiteral(helper.decoratePaymentAmountDouble(amount)));
		payment.addProperty(Ontology.payer, payer);
		payment.addProperty(Ontology.payee, payee);
		payment.addProperty(Ontology.paymentCategory, (String)hashmap.get("ΚΑΤΗΓΟΡΙΑ ΔΑΠΑΝΗΣ"));
		payment.addProperty(Ontology.relativeKae, (String)hashmap.get("ΣΧΕΤΙΚΟΣ ΑΡΙΘΜΟΣ ΚΑΕ"));
		payment.addProperty(RDF.type, Ontology.paymentResource);
		
		//Other Properties
		payer.addProperty(Ontology.paymentAgentName, (String)hashmap.get("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
		payer.addProperty(Ontology.afm, (String)hashmap.get("ΑΦΜ ΦΟΡΕΑ"));
		payer.addProperty(RDF.type, Ontology.paymentAgentResource);		
		payee.addProperty(Ontology.paymentAgentName, (String)hashmap.get("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
		payee.addProperty(Ontology.afm, (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ"));
		payee.addProperty(RDF.type, Ontology.paymentAgentResource);		
		organization.addProperty(RDF.type, Ontology.organization);
		organization.addProperty(Ontology.organizationName, (String)hashmap.get("organization"));
		organizationUnit.addProperty(RDF.type, Ontology.organizationUnit);
		organizationUnit.addProperty(Ontology.organizationUnitName, (String)hashmap.get("organizationUnit"));  
				
		//Create binary relationship resource
		if(checks[0] && checks[1] && checks[2]){
			Resource bRel = model.createResource(Ontology.instancePrefix+"binaryRelationships/"+(String)hashmap.get("ΑΦΜ ΦΟΡΕΑ") + "-with-" + (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ"))
						    .addProperty(RDF.type, Ontology.binaryRelationshipResource)
						    .addProperty(Ontology.binaryPayer, payer)
						    .addProperty(Ontology.binaryPayee, payee);
		}
    }
	
	public ArrayList<String> getCorrectives(String correctionOfAda){
    	
    	ArrayList<String> correctives = new ArrayList<String>();
    	StringTokenizer st = new StringTokenizer(correctionOfAda, ",");
    	while(st.hasMoreElements()){
    		correctives.add(st.nextToken().replace(" ", ""));
    	}
    	return correctives;
    }
	
	public void rdfModelUploader(){
		VirtGraph graph = new VirtGraph (RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");	
    	Model remoteModel = new VirtModel(graph);
		String[] rdfFiles = new String[]{fs.validRDFDateFile, fs.invalidRDFDateFile};
		for(String rdfLocation : rdfFiles){
			InputStream in = FileManager.get().open(rdfLocation);
			if (in == null) {
			     throw new IllegalArgumentException(
			                                  "File: " + rdfLocation + " not found");
			}
			remoteModel.read(in,null);
			try {in.close();} catch (IOException e) {}
		}
		remoteModel.close();
	}
	
	public void rdfModelDirectUploader(Model model, Model invalidModel){
		VirtGraph graph = new VirtGraph (RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");	
    	Model remoteModel = new VirtModel(graph);
    	remoteModel.add(model);
    	remoteModel.add(invalidModel);
    	remoteModel.close();
	}
	
	
	
}
