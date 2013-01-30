package publicspending.java.control;

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

import publicspending.java.daily.GreeklishFactory;
import publicspending.java.daily.RoutineInvoker;
import publicspending.java.daily.SPARQLQueries;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
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
	GreeklishFactory gf = new GreeklishFactory();
	
	public SOAPHandler(HashSet<String> afmSet){				
		int count = 0; Long index = new Long(0);
		this.afmSet = afmSet;
		while(count<1){			
			soapHandler(index);			
			count++;
		}		
	}
	
	
	public void soapHandler(Long index){

		try{	
			int payerCount = 0;			
			localModel = ModelFactory.createDefaultModel();			
			XMLReader xmlReader = null;			
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			spfactory.setValidating(false);
			SAXParser saxParser = spfactory.newSAXParser();
			xmlReader = saxParser.getXMLReader();        	    			       
			//	Create the message
			MessageFactory mf = MessageFactory.newInstance();
        
			Iterator payerAfmIt = afmSet.iterator();			
			while(payerAfmIt.hasNext()){				
  		
				String afm = (String)payerAfmIt.next();
				String xml = xml1+afm+xml2;				
				try{
					PrintWriter out = new PrintWriter(new FileWriter("request.xml"));
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
				FileInputStream is = new FileInputStream("request.xml");  
				//MHPWS NA TO KANW EDW MESA GIA NA MEIWSW TO XRONO???
        // 	Set contents of message   
				StreamSource ss = new StreamSource(is);
				soapPart.setContent(ss);
				is.close();
        //	Set the destination
				URL endpoint = new URL("https://www1.gsis.gr/wsgsis/RgWsBasStoixN/RgWsBasStoixNSoapHttpPort");
        //	Send the message
				SOAPMessage response = connection.call(message, endpoint);        				        
        //	Close the connection
				connection.close();
        //	Create a transformer
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
        //	Retrieve content of the response
				Source content = response.getSOAPPart().getContent();
        //	Display it on the console				
				try{
				FileOutputStream fos = new FileOutputStream("response.xml");
				StreamResult result = new StreamResult(fos);				
				transformer.transform(content, result);
				fos.close();
				SOAPResponseHandler handler = new SOAPResponseHandler(afm);
				xmlReader.setContentHandler(handler);
				xmlReader.setErrorHandler(handler);				
				InputSource cpv = new InputSource("response.xml");
				
				try{
					xmlReader.parse(cpv);								
					localModel.createResource(Ontology.instancePrefix + "paymentAgents/" +afm)
							  .addProperty(Ontology.legalStatusDescription, handler.getLegalStatusDesc())
							 .addProperty(Ontology.gsisName, handler.getName())
							 //.addProperty(OWL.sameAs, ResourceFactory.createResource(Ontology.instancePrefix+"payers/"+afm))
							 .addProperty(RDF.type, Ontology.paymentAgentResource)
							 //.addProperty(RDF.type, OrganizationMap.entityCheckArrays(handler.getLegalStatusDesc()))
							 //.addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/organizationsOntology#profitStatus"), OrganizationMap.profitCheckArrays(handler.getLegalStatusDesc()))
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
							 .addProperty(Ontology.deactivationFlag, handler.getDeactivationFlag())
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
				}catch(Exception e){System.out.println("auth h malakia");}	
			}
			
			try {
				FileOutputStream fos2 = new FileOutputStream("C:/Users/marios/Desktop/Diavgeia/BR/gsis2000.rdf");
				localModel.write(fos2, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Diavgeia/BR/gsis2000.rdf");
				System.out.println("Successfully stored: " + payerCount +  " agents locally.");														
				localModel.close();				
				System.out.println("Done.");
			} 
	        catch (FileNotFoundException e) {			
				e.printStackTrace();					
			}
		
		
		//model.close();	
		
		}
		catch(Exception e){e.printStackTrace();}		
		
	}

}
