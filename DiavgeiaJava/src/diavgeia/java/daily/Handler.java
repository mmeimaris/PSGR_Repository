package diavgeia.java.daily;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.rdf.model.Model;

import diavgeia.java.ontology.Ontology;

public class Handler extends DefaultHandler {		
	
	String afm;
	Model model;
	String dbUrl = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/charset=UTF-8/log_enable=2";
	
	public Handler(String afm){
		this.afm = afm;
	}
	
	public void startDocument() throws SAXException {
					  	
		model = VirtModel.openDatabaseModel("DecisionsTesting", dbUrl, "marios", "dirtymarios");
		
	   }
	 	 	 	 
	 String thisElement = "";	 	 	 	 
	
	 public void startElement(String namespaceURI, String localName, 
         String qName, Attributes atts) throws SAXException {
		 			  		
	   thisElement = qName;	
	   if(thisElement.equals("ns1:onomasia")){
	   for (int att = 0; att < atts.getLength(); att++) {
       	   
		   String attName = atts.getQName(att);
		   if(attName.equals("xsi:nil") && atts.getValue(attName).toString().equals("1"))
			   System.out.println("HANDLER SAYS: NO NAME PROVIDED");
			   		  
   
		}    
	   }
	    
}
	 public void endElement(String namespaceURI, String localName, String qName)
			   throws SAXException {
				   		 		 				 		
			}
	 
	 public void characters(char[] ch, int start, int length)
         throws SAXException  {

		 String data = new String(ch, start, length);
		 if(thisElement.equals("ns1:onomasia")){
			 model.createResource(Ontology.instancePrefix+"payers#"+afm).addProperty(Ontology.gsisName, data);			 
		 }			 		 		
		 
	 }
	
}
