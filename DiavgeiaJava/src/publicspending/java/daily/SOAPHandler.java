package publicspending.java.daily;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import publicspending.java.control.OrganizationMap;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

import diavgeia.java.ontology.Ontology;

public class SOAPHandler {

	//static String soapRequestLocation = "C:/Users/marios/Desktop/soap.xml";
	//static String soapResponseLocation = "C:/Users/marios/Desktop/response.xml";
	//static String xml1 = "<soapenv:Envelope xmlns:soapenv= \"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:rgw= \"http://gr/gsis/rgwsbasstoixn/RgWsBasStoixN.wsdl\" xmlns:typ= \"http://gr/gsis/rgwsbasstoixn/RgWsBasStoixN.wsdl/types/\" xmlns:xsi= \"http://www.w3.org/2001/XMLSchema-instance\" ><soapenv:Header/><soapenv:Body><rgw:rgWsBasStoixN><pAfm xsi:type=\"xsd:string\">";
	static String xml1 = "<soapenv:Envelope xmlns:soapenv= " +
		"\"http://schemas.xmlsoap.org/soap/envelope/\" " +
		"xmlns:rgw= \"http://gr/gsis/rgwsbasstoixn/RgWsBasStoixN.wsdl\" " +
		"xmlns:typ= \"http://gr/gsis/rgwsbasstoixn/RgWsBasStoixN.wsdl/types/\" " +
		"xmlns:xsi= \"http://www.w3.org/2001/XMLSchema-instance\" >" +
		"<soapenv:Header/><soapenv:Body>" +
	    "<rgw:rgWsBasStoixEpit>" +
	    "<pAfm xsi:type=\"xsd:string\">";
	  static String xml2 = "</pAfm>" +
	    "<pBasStoixEpitRec_out> " +
	    "<typ:residenceParDescription></typ:residenceParDescription>"+ 
	              "<typ:assTxpActualAfm></typ:assTxpActualAfm> " +
	              "<typ:actLongDescr></typ:actLongDescr> " +
	              "<typ:postalZipCode></typ:postalZipCode> " +
	              "<typ:INiFlagDescr></typ:INiFlagDescr>" + 
	              "<typ:registDate>2011-01-01</typ:registDate> " +
	              "<typ:stopDate>2011-01-01</typ:stopDate>" + 
	              "<typ:parDescription></typ:parDescription>" +
	              "<typ:doyDescr></typ:doyDescr> " +
	              "<typ:residenceZipCode></typ:residenceZipCode>" + 
	              "<typ:deactivationFlagDescr></typ:deactivationFlagDescr> " +
	              "<typ:legalStatusDescr></typ:legalStatusDescr> " +
	              "<typ:firmPhone></typ:firmPhone> " +
	              "<typ:firmFax></typ:firmFax> " +
	              "<typ:afm></typ:afm>" + 
	              "<typ:facActivity>0</typ:facActivity> " +
	              "<typ:countOfBranches>0</typ:countOfBranches>" + 
	              "<typ:deactivationFlag>1</typ:deactivationFlag>" +
	              "<typ:postalAddressNo></typ:postalAddressNo> " +
	              "<typ:firmFlagDescr></typ:firmFlagDescr> " +
	              "<typ:postalAddress></typ:postalAddress> " +
	              "<typ:doy></typ:doy> " +
	              "<typ:onomasia></typ:onomasia> " +
	              "<typ:commerTitle></typ:commerTitle> " +
	              "</pBasStoixEpitRec_out>" + 
	           "<pCallSeqId_out>0</pCallSeqId_out><pErrorRec_out> <typ:errorDescr></typ:errorDescr> <typ:errorCode></typ:errorCode>" + 
	           "</pErrorRec_out>" +
	           "</rgw:rgWsBasStoixEpit>" +
	           "</soapenv:Body> " +
	           "</soapenv:Envelope>";
	//static String xml2= "</pAfm><pBasStoixNRec_out><typ:actLongDescr></typ:actLongDescr><typ:postalZipCode></typ:postalZipCode><typ:facActivity>0</typ:facActivity><typ:registDate>2011-01-01</typ:registDate><typ:stopDate>2011-01-01</typ:stopDate><typ:doyDescr></typ:doyDescr><typ:parDescription></typ:parDescription><typ:deactivationFlag>1</typ:deactivationFlag><typ:postalAddressNo></typ:postalAddressNo><typ:postalAddress></typ:postalAddress><typ:doy></typ:doy><typ:firmPhone></typ:firmPhone><typ:onomasia></typ:onomasia><typ:firmFax></typ:firmFax><typ:afm></typ:afm><typ:commerTitle></typ:commerTitle></pBasStoixNRec_out><pCallSeqId_out>0</pCallSeqId_out><pErrorRec_out><typ:errorDescr></typ:errorDescr><typ:errorCode></typ:errorCode></pErrorRec_out></rgw:rgWsBasStoixN></soapenv:Body></soapenv:Envelope>";
	static Model model, localModel;	
	static VirtGraph graph;
	static HashSet<String> afmSet ;//= new HashSet<String>();	
	FileStructure fs;
	GreeklishFactory gf = new GreeklishFactory();
	
	public SOAPHandler(FileStructure fs){		
		this.fs = fs;
		/*int count = 0; Long index = new Long(0);		
		while(count<1){			
			soapHandler(index, 0);			
			count++;
		}*/
		
		
	}
	
	
	public void soapHandler(int mode){
		//public void soapHandler(Long index, int mode){

		try{	
			int payeeCount = 0, payerCount = 0;			
			localModel = ModelFactory.createDefaultModel();			
			XMLReader xmlReader = null;
			//getPayerAfmList();			
			afmSet = new HashSet<String>();
			if(mode==0)
				getPayeeAfmList();
			else if (mode==1){				
				getUpdateAfmList();
			}
			else if(mode==2)
				getDebtorAfmList();
			//getAgentLegalAfmList("OFFSET " + index.toString());
			//ArrayList<String> payeeAfmList = getPayeeAfmList();
			//if(!deleteMultipleNames())
				//System.out.println("Could not delete multiple name tuples.");
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			spfactory.setValidating(false);
			SAXParser saxParser = spfactory.newSAXParser();
			xmlReader = saxParser.getXMLReader();        	    			       
			//	Create the message
			MessageFactory mf = MessageFactory.newInstance();
        
			Iterator payerAfmIt = afmSet.iterator();
			//Iterator payeeAfmIt = payeeAfmList.iterator();
			while(payerAfmIt.hasNext()){				
  		
				String afm = (String)payerAfmIt.next();
				String xml = xml1+afm+xml2;				
				try{
					PrintWriter out = new PrintWriter(new FileWriter(fs.soapRequestLocation));
					out.println(xml);
					out.close();
				}
				catch(Exception e){e.printStackTrace(); continue;}  		
				SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
				SOAPConnection connection = scf.createConnection();
				SOAPFactory sf = SOAPFactory.newInstance();
				SOAPMessage message = mf.createMessage();                           
        //	Create objects for the message parts            
				SOAPPart soapPart = message.getSOAPPart();        
				FileInputStream is = new FileInputStream(fs.soapRequestLocation);  
				//MHPWS NA TO KANW EDW MESA GIA NA MEIWSW TO XRONO???
        // 	Set contents of message   
				StreamSource ss = new StreamSource(is);
				soapPart.setContent(ss);
				is.close();
        //	Set the destination
				URL endpoint = new URL("https://www1.gsis.gr/wsgsis/RgWsBasStoixN/RgWsBasStoixNSoapHttpPort");
        //	Send the message
				SOAPMessage response = connection.call(message, endpoint);        
				SOAPElement m;         
        //	Close the connection
				connection.close();
        //	Create a transformer
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
        //	Retrieve content of the response
				Source content = response.getSOAPPart().getContent();
        //	Display it on the console				
				FileOutputStream fos = new FileOutputStream(fs.soapResponseLocation);
				StreamResult result = new StreamResult(fos);
				transformer.transform(content, result);
				SOAPResponseHandler handler = new SOAPResponseHandler(afm);
				xmlReader.setContentHandler(handler);
				xmlReader.setErrorHandler(handler);				
				InputSource cpv = new InputSource(fs.soapResponseLocation);
				if(mode==1){
					deleteOldGsisData(afm);
				}
				try{
					xmlReader.parse(cpv);								
					localModel.createResource(Ontology.instancePrefix + "paymentAgents/" +afm)
							  .addProperty(Ontology.legalStatusDescription, handler.getLegalStatusDesc())
							 .addProperty(Ontology.gsisName, handler.getName())
							 //.addProperty(OWL.sameAs, ResourceFactory.createResource(Ontology.instancePrefix+"payers/"+afm))
							 .addProperty(RDF.type, OrganizationMap.entityCheckArrays(handler.getLegalStatusDesc()))
							 .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/organizationsOntology#profitStatus"), OrganizationMap.profitCheckArrays(handler.getLegalStatusDesc()))
							 .addProperty(RDF.type, Ontology.paymentAgentResource)
							 .addProperty(Ontology.cpaCode, handler.getCpaCode())
							 .addProperty(Ontology.cpaGreekSubject, handler.getCpaSubject())
							 .addProperty(Ontology.doy, handler.getDoy())
							 .addProperty(Ontology.faxNumber, handler.getFaxNumber())
							 .addProperty(Ontology.phoneNumber, handler.getPhoneNumber())
							 .addProperty(Ontology.postalCity, handler.getPostalCity())
							 .addProperty(Ontology.postalStreetName, handler.getPostalStreetName())
							 .addProperty(Ontology.postalStreetNumber, handler.getPostalStreetNumber())
							 .addProperty(Ontology.postalZipCode, handler.getPostalZipCode())
							 .addProperty(Ontology.registrationDate, handler.getRegistrationDate())
							 //.addProperty(Ontology.deactivationFlag, handler.getDeactivationFlag())
						     .addProperty(Ontology.fpFlag, handler.getFpFlag())
						     .addProperty(Ontology.firmDescription, handler.getFirmDescr())
						     .addProperty(Ontology.countOfBranches, handler.getBranches())
						     .addProperty(Ontology.doyName, handler.getDoyDescr())
							 .addProperty(Ontology.stopDate, handler.getStopDate());
					if(!handler.getName().equals("Null")){						
						localModel.createResource(Ontology.instancePrefix + "paymentAgents/" +afm).addProperty(Ontology.validName, handler.getName())
																								  .addProperty(Ontology.validEngName, gf.greeklishGenerator(handler.getName()));
					}
					payerCount++;
				}
				catch(SAXException e){ e.printStackTrace();}
				
			}
			
			try {
				FileOutputStream fos2 = new FileOutputStream(fs.gsisLocation);
				localModel.write(fos2, "RDF/XML-ABBREV", fs.gsisLocation);
				System.out.println("Successfully stored: " + payerCount +  " agents locally.");
				//Write the invalid entries in an RDF file.							
				InputStream in = FileManager.get().open(fs.gsisLocation);
				if (in == null) {
				     throw new IllegalArgumentException(
				                                  "File: " + fs.gsisLocation + " not found");
				}
				System.out.println("Uploading to Virtuoso...");
				model = VirtModel.openDatabaseModel(RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");
				model.read(in,null);					
				localModel.close();
				model.close();
				System.out.println("Done.");
			} 
	        catch (FileNotFoundException e) {			
				e.printStackTrace();					
			}
		
		
		//model.close();	
		
		}
		catch(Exception e){e.printStackTrace();}		
		
	}
	
    
    private static void getPayeeAfmList(){
 	   
    	model = VirtModel.openDatabaseModel(RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");			
		graph = (VirtGraph) model.getGraph();
    	
	 	String query =  SPARQLQueries.ruleSet + SPARQLQueries.prefixes +
		 				   "SELECT distinct ?afm " +
		 				   "FROM <" + RoutineInvoker.graphName +"> " +
		 				   "WHERE { " +
		 				   "?payer psgr:afm ?afm . " +
		 				   "?payer psgr:validAfm \"true\" ." +
		 				   //"?payment psgr:payee ?payer . " +
		 				   //"?payment psgr:paymentAmount ?amount ." +		 				  		 				   
		 				   "OPTIONAL {?payer psgr:gsisName ?name} " +
		 				   //"FILTER((xsd:decimal(?amount)>100)) " + 
		 				   "FILTER(!BOUND(?name)) }";
		 				   //"} ORDER BY DESC((sum(xsd:decimal(?amount)))) LIMIT 10000";		 				  

	 //System.out.println(query);
 	   VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
 	   ResultSet results = vqe.execSelect();
 	   //HashSet<String> afmList = new HashSet<String>();
 	   while (results.hasNext()) {
 		   QuerySolution rs = results.nextSolution();
 		   RDFNode afm = rs.get("afm"); 		   
 			   afmSet.add(afm.toString()); 		        
 	   } 	   
 	   vqe.close();
 	   model.close();
 	   System.out.println(afmSet.size() + " Payment Agents."); 	    	    	   
    }        
    
    private static void getDebtorAfmList(){
  	   
    	model = VirtModel.openDatabaseModel(RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");			
		graph = (VirtGraph) model.getGraph();
    	
	 	String query =  SPARQLQueries.ruleSet + SPARQLQueries.prefixes +
		 				   "SELECT distinct ?agent " +
		 				   "FROM <" + RoutineInvoker.graphName +"> " +
		 				   "WHERE { " +
		 				   "?agent a psgr:PaymentAgent ." +		 				  		 				   
		 				   "OPTIONAL {?agent psgr:afm ?afm} " +
		 				   //"FILTER((xsd:decimal(?amount)>100)) " + 
		 				   "FILTER(!BOUND(?afm)) }";
		 				   //"} ORDER BY DESC((sum(xsd:decimal(?amount)))) LIMIT 10000";		 				  

	 //System.out.println(query);
 	   VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
 	   ResultSet results = vqe.execSelect();
 	   //HashSet<String> afmList = new HashSet<String>();
 	   while (results.hasNext()) {
 		   QuerySolution rs = results.nextSolution();
 		   RDFNode agent = rs.get("agent");
 		   String afm = agent.toString().substring(agent.toString().indexOf("Agents/")+7);
 		   System.out.println(afm);
 			   afmSet.add(afm.toString()); 		        
 	   } 	   
 	   vqe.close();
 	   model.close();
 	   System.out.println(afmSet.size() + " Payment Agents."); 	    	    	   
    }        
    
    private static void getAgentLegalAfmList(String index){
  	   
    	model = VirtModel.openDatabaseModel(RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");			
		graph = (VirtGraph) model.getGraph();
    	
	 	String query =  SPARQLQueries.ruleSet + SPARQLQueries.prefixes +
		 				   "SELECT distinct ?afm " +
		 				   "FROM <" + RoutineInvoker.graphName +"> " +
		 				   "WHERE { " +
		 				   "?payer psgr:afm ?afm . " +
		 				   "?payer psgr:validAfm \"true\" ." +
		 				   "?payment psgr:payee ?payer . " +
		 				   "?payment psgr:paymentAmount ?amount ." +
		 				   "?payer psgr:gsisName ?name ." +
		 				   "OPTIONAL {?payer psgr:legalStatus ?status} " +		 				   
		 				   "FILTER(!BOUND(?status)) " +
		 				   "} ORDER BY DESC((sum(xsd:decimal(?amount)))) LIMIT 10000 ";		 				  

	   System.out.println(query);
 	   VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
 	   ResultSet results = vqe.execSelect();
 	   //HashSet<String> afmList = new HashSet<String>();
 	   while (results.hasNext()) {
 		   QuerySolution rs = results.nextSolution();
 		   RDFNode afm = rs.get("afm");
 		   //if(!afmList.contains(afm.toString()))
 			   afmSet.add(afm.toString()); 		   
     
 	   } 	   
 	   vqe.close();
 	   model.close();
 	   System.out.println(afmSet.size() + " Payment Agents."); 	    	   
 	   //return afmList;
    }        
    
    private static void getPayerAfmList(){
  	   
    	model = VirtModel.openDatabaseModel(RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");			
    	graph = (VirtGraph) model.getGraph();
		
  	   String query = 	SPARQLQueries.ruleSet + SPARQLQueries.prefixes +
  			 "SELECT distinct ?afm " +
			   "FROM <" + RoutineInvoker.graphName +"> " +
			   "WHERE { " +
			   "?payer psgr:afm ?afm . " +		 				  
			   "?payment psgr:payer ?payer . " +
			   //"?payment psgr:paymentAmount ?amount ." +		 				  		 				   
			   //"OPTIONAL {?payer psgr:gsisName ?name} " +
			   //"FILTER((xsd:decimal(?amount)>500)) " + 
			   //"FILTER(!BOUND(?name)) " +
			   //"} ORDER BY DESC(xsd:decimal(?amount)) LIMIT 10000";
			   "} LIMIT 10000";

  	
  	   VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
  	   ResultSet results = vqe.execSelect();
  	   //ArrayList<String> afmList = new ArrayList<String>();
  	   while (results.hasNext()) {
  		   QuerySolution rs = results.nextSolution();
  		   RDFNode afm = rs.get("afm");
  		 //  if(!afmList.contains(afm.toString()))
  			   afmSet.add(afm.toString()); 		   
      
  	   } 	   
  	   vqe.close();
  	   model.close();
  	   System.out.println(afmSet.size() + " Payees."); 	    	   
  	   //return afmList;
     }        
    
    public void removeOutdated(){
    	
    	String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("", connectionString, "marios", "dirtymarios");
		VirtModel remoteModel = new VirtModel(graph); 
    	Iterator it = afmSet.iterator();
    	while(it.hasNext()){
    		String outdatedAfm = (String) it.next();
    		remoteModel.createResource(Ontology.instancePrefix+"paymentAgents/"+outdatedAfm);
    	}
    }
    
    public void getUpdateAfmList(){
    
    	String updateQuery = SPARQLQueries.updateQuery();
    	//System.out.println(updateQuery);
    	String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph (RoutineInvoker.graphName, connectionString, "marios", "dirtymarios");
		System.out.println("Performing updates...");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (updateQuery,graph);
		ResultSet results = vqe.execSelect();		
		//Model toWrite = ModelFactory.createDefaultModel();		
		while(results.hasNext()){
			QuerySolution rs = results.nextSolution();
			//RDFNode agent = rs.get("agent");		
			Literal afm = rs.getLiteral("afm");
			afmSet.add(afm.getString());
			
		}vqe.close();
		graph.close();
		//System.out.println(afmSet.size());
		//removeOutdated();
    }
    
    public void deleteOldGsisData(String afm){
    	    	    		
    	String deleteQuery = SPARQLQueries.deleteQuery(Ontology.instancePrefix+"paymentAgents/"+afm);
    	String deleteNeedsUpdateQuery = SPARQLQueries.deleteNeedsUpdateQuery(Ontology.instancePrefix+"paymentAgents/"+afm); 
    	System.out.println(deleteNeedsUpdateQuery);
    	String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph (RoutineInvoker.graphName, connectionString, "marios", "dirtymarios");
		System.out.println("Performing updates...");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (deleteQuery,graph);
		vqe.execSelect();		
		VirtGraph analyticsGraph = new VirtGraph (RoutineInvoker.analyticsGraphName, connectionString, "marios", "dirtymarios");
		VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (deleteNeedsUpdateQuery,analyticsGraph);
		vqe2.execSelect();
    	//System.out.println("Press Any Key To Continue...");				
        //new java.util.Scanner(System.in).nextLine();
    }
    
    
    

}
