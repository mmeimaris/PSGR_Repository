package publicspending.java.control;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SOAPResponseHandler extends DefaultHandler{

	private String afm, cpaGreekSubject="Null", postalZipCode="Null", cpaCode="Null", 
		   registrationDate="Null", stopDate="Null", doy="Null", postalCity="Null", postalStreetNumber="Null", 
		   postalStreetName="Null", phoneNumber="Null", faxNumber="Null", fpFlag = "Null", branches = "Null", name="Null", 
		   firmDescr = "Null", doyDescr = "Null", deactivationFlag = "Null", legalStatusDesc = "Null";
	
	private StringBuilder buf;
	
	public SOAPResponseHandler(String afm){
		this.afm = afm;		
		
	}
	
	public void startDocument() throws SAXException {					  		
		
	   }
	 	 	 	 
	 String thisElement = "";	 	 	 	 
	
	 public void startElement(String namespaceURI, String localName, 
         String qName, Attributes atts) throws SAXException {
		 			  		
	   thisElement = qName;	
	   if(thisElement.equals("ns1:onomasia"))
		   buf = new StringBuilder();
	   
}
	 public void endElement(String namespaceURI, String localName, String qName)
			   throws SAXException {
		 
		 
		 if(thisElement.equals("ns1:onomasia") && !buf.toString().equals(""))
			 name = buf.toString();
		 	 
			}
	 
	 public void characters(char[] ch, int start, int length)
         throws SAXException  {
		 		 
		 
		 
		String data = new String(ch, start, length);
		if(data.equals(""))
			data = "Null";
		 if(thisElement.equals("ns1:errorCode") && data.equals("RG_NOT_INDIVIDUAL_NF")){
			 //System.out.println("Individual not found, afm " + afm);
			 throw new SAXException();
		 }
		 
		 if (thisElement.equals("ns1:onomasia") && buf!=null) {
		        for (int i=start; i<start+length; i++) {
		            buf.append(ch[i]);
		        }
		    }
		 /*if(thisElement.equals("ns1:onomasia")){
			 if(!data.equals(""))
				 name = data;			 
		 }*/
		 if (thisElement.equals("ns1:actLongDescr")){
			 //if(!data.equals(""))
				 cpaGreekSubject = data;			 
		 }
		 else if (thisElement.equals("ns1:postalZipCode")){
			 //if(!data.equals(""))
				 postalZipCode = data;			 
		 }
		 else if (thisElement.equals("ns1:facActivity")){
			 //if(!data.equals(""))
				 cpaCode = data;			 
		 }
		 else if (thisElement.equals("ns1:registDate")){
			 //if(!data.equals(""))
				 registrationDate = data;			 
		 }
		 else if (thisElement.equals("ns1:stopDate")){
			 //if(!data.equals(""))
				 stopDate = data;			 
		 }
		 else if (thisElement.equals("ns1:doy")){
			 //if(!data.equals(""))
				 doy = data;			 
		 }
		 else if (thisElement.equals("ns1:parDescription")){
			 //if(!data.equals(""))
				 postalCity = data;			 
		 }
		 else if (thisElement.equals("ns1:postalAddressNo")){
			 //if(!data.equals(""))
				 postalStreetNumber = data;			 
		 }
		 else if (thisElement.equals("ns1:postalAddress")){
			 //if(!data.equals(""))
				 postalStreetName = data;			 
		 }
		 else if (thisElement.equals("ns1:firmPhone")){
			 //if(!data.equals(""))
				 phoneNumber = data;			 
		 }
		 else if (thisElement.equals("ns1:firmFax")){
			 //if(!data.equals(""))
				 faxNumber = data;			 
		 }
		 else if (thisElement.equals("ns1:INiFlagDescr")){
			 //if(!data.equals(""))			 
				 fpFlag = data;			 
		 }
		 else if (thisElement.equals("ns1:doyDescr")){
			 //if(!data.equals(""))			 
				 doyDescr = data;			 
		 }
		 else if (thisElement.equals("ns1:countOfBranches")){
			 //if(!data.equals(""))			 
				 branches = data;			 
		 }
		 else if (thisElement.equals("ns1:firmFlagDescr")){
			 //if(!data.equals(""))			 
				 firmDescr = data;			 
		 }
		 else if (thisElement.equals("ns1:deactivationFlag")){
			 //if(!data.equals(""))			 
			 deactivationFlag = data;			 
		 }
		 else if (thisElement.equals("ns1:legalStatusDescr")){
			 //if(!data.equals(""))			 
			 legalStatusDesc = data;			 
		 }		 
	 }
	 
	 public String getName(){
		 return name;
	 }
	 
	 public String getCpaSubject(){
		 return cpaGreekSubject;
	 }
	 
	 public String getPostalZipCode(){
		 return postalZipCode;
	 }
	 
	 public String getCpaCode(){
		 return cpaCode;
	 }
	 
	 public String getRegistrationDate(){
		 return registrationDate;
	 }
	 
	 public String getStopDate(){
		 return stopDate;
	 }
	 
	 public String getDoy(){
		 return doy;
	 }
	 
	 public String getPostalCity(){
		 return postalCity;
	 }
	 
	 public String getPostalStreetNumber(){
		 return postalStreetNumber;
	 }
	 
	 public String getPostalStreetName(){
		 return postalStreetName;
	 }
	 
	 public String getPhoneNumber(){
		 return phoneNumber;
	 }
	 
	 public String getFpFlag(){
		 return fpFlag;
	 }
	 public String getBranches(){
		 return branches;
	 }
	 public String getFirmDescr(){
		 return firmDescr;
	 }
	 public String getDoyDescr(){
		 return doyDescr;
	 }
	 public String getDeactivationFlag(){
		 return deactivationFlag;
	 }
	 public String getFaxNumber(){
		 return faxNumber;
	 }
	 
	 public String getLegalStatusDesc(){
		 return legalStatusDesc;
	 }
}

