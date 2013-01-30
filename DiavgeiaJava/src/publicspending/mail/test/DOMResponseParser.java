package publicspending.mail.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import publicspending.java.control.DOMHelper;

public class DOMResponseParser {
	DOMHelper dmh = new DOMHelper();	
	String count, total, order, from;
	
	public HashSet<HashMap<String, String>> getXMLresponse(String request){		
		try {
			//test
			request = "http://opendata.diavgeia.gov.gr/api/decisions?datefrom=10-12-2012&dateTo=14-12-2012&type=27&count=500&from=1";
			//end test
			System.out.println(request);	
			boolean validResponse = false;
			//ArrayList<HashMap<String, String>> decisionList = new ArrayList<HashMap<String, String>>();
			HashSet<HashMap<String, String>> decisionSet = new HashSet<HashMap<String, String>>();			
			while(!validResponse){
				try{
					validResponse = true;
					Node decisions = initialParseXMLResponse(request);
					NodeList decisionsList = decisions.getChildNodes();					
					decisionsList = decisions.getChildNodes();
					for(int decisionCount = 0; decisionCount<decisionsList.getLength();decisionCount++){
						Node decision = decisionsList.item(decisionCount);				
						Decision decisionObject = getDecisionFields(decision);
						if(decisionObject==null) {throw new Exception();}
						decisionSet.add(createHashMap(decisionObject));
					}					
				}catch(Exception e){validResponse = false;}				
			}
			return decisionSet;
		}
		catch ( Exception e ) {
		    e.printStackTrace();
		    return null;
			}
	}
	
	public HashMap<String, String> createHashMap(Decision decision){
		HashMap<String, String> decisionMap = new HashMap<String, String>();
		decisionMap.put("ada", decision.getAda());
    	decisionMap.put("isCorrectionOfAda", decision.getCorrectionOfAda());
    	decisionMap.put("relativeAda", decision.getRelativeAda());
    	decisionMap.put("submissionTimestamp", decision.getSubmissionTimestamp());
    	decisionMap.put("url", decision.getUrl());
    	decisionMap.put("documentUrl", decision.getDocumentUrl());
    	decisionMap.put("protocolNumber", decision.getProtocolNumber());
    	decisionMap.put("decisionType", decision.getDecisionTypeId());
    	decisionMap.put("subject", decision.getSubject());
    	decisionMap.put("date", decision.getDate());
    	decisionMap.put("organization", "");
    	decisionMap.put("organizationUID", decision.getOrgId());
    	decisionMap.put("organizationUnit", "");
    	decisionMap.put("organizationUnitUID", decision.getOrgUnitId());
    	decisionMap.put("FEK number", decision.getFekNumber());
    	decisionMap.put("FEK issue", decision.getFekIssue());
    	decisionMap.put("FEK year", decision.getFekYear());
    	decisionMap.put("tagLabels", "");
    	decisionMap.put("tagUIDs", "");
    	decisionMap.put("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)", decision.getPayerName());
    	decisionMap.put("ΑΦΜ ΦΟΡΕΑ", decision.getPayerAfm());
    	decisionMap.put("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)", decision.getPayeeName());
    	decisionMap.put("ΑΦΜ ΑΝΑΔΟΧΟΥ", decision.getPayeeAfm());
    	decisionMap.put("hasVAT", "");
    	decisionMap.put("isValidVAT", "");
    	decisionMap.put("CPV Description", decision.getCpv());
    	decisionMap.put(" CPV Number", "");
    	decisionMap.put("ΣΧΕΤΙΚΟΣ ΑΡΙΘΜΟΣ ΚΑΕ", decision.getKae());
    	decisionMap.put("ΚΑΤΗΓΟΡΙΑ ΔΑΠΑΝΗΣ", decision.getPaymentCategory());
    	decisionMap.put("ΠΟΣΟ ΔΑΠΑΝΗΣ / ΣΥΝΑΛΛΑΓΗΣ (ΜΕ ΦΠΑ)", decision.getAmount());
    	decisionMap.put("ΠΕΡΙΓΡΑΦΗ ΔΑΠΑΝΗΣ", decision.getDescription());
    	decisionMap.put("signer", decision.getSignerId());
    	decisionMap.put("tagArray", decision.getTagArray());
    	return decisionMap;
	}
	public Decision getDecisionFields(Node decision){
		NodeList decisionData = decision.getChildNodes();
		Decision decisionObject = new Decision();
		//top level metadata
		decisionObject.setAda(dmh.getNodeValue("ns3:ada", decisionData));
		decisionObject.setSubmissionTimestamp(dmh.getNodeValue("ns3:submissionTimestamp", decisionData));
		decisionObject.setUrl(dmh.getNodeValue("ns3:url", decisionData));
		decisionObject.setDocumentUrl(dmh.getNodeValue("ns3:documentUrl", decisionData));
		//secondary level metadata
		Node metadataNode = dmh.getNode("ns3:metadata", decisionData);
		NodeList metadataList = metadataNode.getChildNodes();
		decisionObject.setProtocolNumber(dmh.getNodeValue("ns3:protocolNumber", metadataList));
		decisionObject.setDate(dmh.getNodeValue("ns3:date", metadataList));
		decisionObject.setSubject(dmh.getNodeValue("ns3:subject", metadataList));
		decisionObject.setOrgId(dmh.getNodeValue("ns3:organizationId", metadataList));
		decisionObject.setOrgUnitId(dmh.getNodeValue("ns3:organizationUnitId", metadataList));
		decisionObject.setOrgId(dmh.getNodeValue("ns3:organizationId", metadataList));
		decisionObject.setOrgUnitId(dmh.getNodeValue("ns3:organizationUnitId", metadataList));
		decisionObject.setDecisionTypeId(dmh.getNodeValue("ns3:decisionTypeId", metadataList));
		decisionObject.setSignerId(dmh.getNodeValue("ns3:signerId", metadataList));
		//tag metadata
		Node tagNode = dmh.getNode("ns3:tags",metadataList);
		NodeList tagList = tagNode.getChildNodes();
		String tagsArray = "";
		for(int tagCount=0; tagCount<tagList.getLength(); tagCount++){			
			Node tagN = (Node)tagList.item(tagCount);
			tagsArray += dmh.getNodeValue(tagN)+"#";			
		}
		decisionObject.setTagArray(tagsArray);
		//fek metadata
		String[] fekMetadata = getFEKMetadata(metadataList);
		decisionObject.setFekIssue(fekMetadata[0]);		
		decisionObject.setFekNumber(fekMetadata[1]);
		decisionObject.setFekYear(fekMetadata[2]);
		//payment extrafields
		if(decisionObject.getDecisionTypeId().equals("27")){
			String[] paymentExtraFields = getPaymentExtraFields(metadataList);
			if(paymentExtraFields==null) return null;
			else
			{
				decisionObject.setPayerAfm(paymentExtraFields[0]);
				decisionObject.setPayerName(paymentExtraFields[1]);
				decisionObject.setPayeeAfm(paymentExtraFields[2]);
				decisionObject.setPayeeName(paymentExtraFields[3]);
				decisionObject.setDescription(paymentExtraFields[4]);
				decisionObject.setAmount(paymentExtraFields[5]);
				decisionObject.setCpv(paymentExtraFields[6]);
				decisionObject.setKae(paymentExtraFields[7]);
				decisionObject.setPaymentCategory(paymentExtraFields[8]);
			
			}
		}
		return decisionObject;
	}
	
	public String[] getPaymentExtraFields(NodeList metadataList){
		try{
			Node extrafields = dmh.getNode("ns3:extraFields", metadataList);
			NodeList extrasList = extrafields.getChildNodes();
			String[] extrasArray = new String[extrasList.getLength()];
			for(int i=0;i<extrasList.getLength();i++){
				Node nextExtra = (Node) extrasList.item(i);
				NodeList valueList = nextExtra.getChildNodes();
				NamedNodeMap attrMap = nextExtra.getAttributes();
				Node attributeNode = attrMap.getNamedItem("name");			
				if(dmh.getNodeValue(attributeNode).equals("afm_forea")){
					extrasArray[0] = dmh.getNodeValue("ns3:value", valueList);
				}
				else if(dmh.getNodeValue(attributeNode).equals("eponimia_forea")){
					extrasArray[1] = dmh.getNodeValue("ns3:value", valueList);
				}
				else if(dmh.getNodeValue(attributeNode).equals("afm_anadoxou")){
					extrasArray[2] = dmh.getNodeValue("ns3:value", valueList);
				}
				else if(dmh.getNodeValue(attributeNode).equals("eponimia_anadoxou")){
					extrasArray[3] = dmh.getNodeValue("ns3:value", valueList);
				} 
				else if(dmh.getNodeValue(attributeNode).equals("perigrafi_antikeimenou")){
					extrasArray[4] = dmh.getNodeValue("ns3:value", valueList);
				} 
				else if(dmh.getNodeValue(attributeNode).equals("poso_dapanis")){
					extrasArray[5] = dmh.getNodeValue("ns3:value", valueList);
				}
				else if(dmh.getNodeValue(attributeNode).equals("cpv")){
					extrasArray[6] = dmh.getNodeValue("ns3:value", valueList);
				} 
				else if(dmh.getNodeValue(attributeNode).equals("arithmos_kae_dapani")){
					extrasArray[7] = dmh.getNodeValue("ns3:value", valueList);
				} 
				else if(dmh.getNodeValue(attributeNode).equals("kathgoria_dapanis")){
					extrasArray[8] = dmh.getNodeValue("ns3:value", valueList);
				} 					
			}
			return extrasArray;
		}catch(Exception e){return null;}
		
	}
	
	public String[] getFEKMetadata(NodeList metadataList){
		String fekIssue, fekNumber, fekYear;
		try{
			Node fekNode = dmh.getNode("ns3:relativeFEK", metadataList);
			NodeList fekList = fekNode.getChildNodes();
			fekIssue = dmh.getNodeValue("ns3:issue", fekList);
			fekYear = dmh.getNodeValue("ns3:year", fekList);
			fekNumber = dmh.getNodeValue("ns3:fekNumber", fekList);
			if(fekIssue.equals("0")) fekIssue = "";
			if(fekYear.equals("0")) fekYear = "";
			if(fekNumber.equals("0")) fekNumber = "";
			
		}catch(Exception e){
			fekIssue="";
			fekYear="";
			fekNumber="";
		}
		return new String[] {fekIssue, fekNumber, fekYear};
	}
	public Node initialParseXMLResponse(String request) throws Exception {
		
		String response = httpGet(request);		
		Node[] rootNodes = getResponseRootNodes(response);				
	    Node decisions = rootNodes[0];
	    Node queryInfo = rootNodes[1];	    
	    if(!setResponseDetails(queryInfo)) {
	    	while(!setResponseDetails(queryInfo)){
	    		System.out.println("Problem with setting response details. Trying Again.");	    	
	    	}		    	
	    }
	    if(getTotal().equals("0")){
	    	//check again
	    	while(getTotal().equals("0")){
	    		System.out.println("Call failed. Trying again.");
		    	response = httpGet(request);			
				rootNodes = getResponseRootNodes(response);
				decisions = rootNodes[0];
				queryInfo = rootNodes[1];
				if(!setResponseDetails(queryInfo)) {System.out.println("Problem with setting response details.");}
	    	}	
	    }
	    else {
	    	System.out.println("-----------------------");
	    	System.out.println("Success.");
	 	    System.out.println("Clarity API call info: ");
	 	    System.out.println("Count: " + count);
	 	    System.out.println("From: " + from);
	 	    System.out.println("Order: " + order);
	 	    System.out.println("Total: " + total);
	 	    System.out.println("-----------------------");
	 	    return decisions;
	    }//return something	    
	    return null;
	}
	    
	public boolean setResponseDetails(Node queryInfo){
		try{
			NodeList queryInfoNodes = queryInfo.getChildNodes();		
		    setCount(dmh.getNodeValue("count", queryInfoNodes));
		    setFrom(dmh.getNodeValue("from", queryInfoNodes));
		    setOrder(dmh.getNodeValue("order", queryInfoNodes));
		    setTotal(dmh.getNodeValue("total", queryInfoNodes));
		    return true;
		}catch(Exception e){e.printStackTrace(); return false;}
	}
	
	public Node[] getResponseRootNodes(String response) throws Exception{
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));
	    Document doc = parser.getDocument();
	    NodeList root = doc.getChildNodes();
	    Node rootNode = dmh.getNode("ns2:findDecisionsResponse", root);
	    NodeList children = rootNode.getChildNodes();
	    Node decisions = dmh.getNode("ns2:decisions", children);
	    Node queryInfo = dmh.getNode("queryInfo", children);
	    Node[] rootNodes = new Node[] {decisions, queryInfo};
	    return rootNodes;
	   
	}
	
	public static String httpGet(String urlStr) throws IOException {
		  URL url = new URL(urlStr);
		  HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }
		  	/*int errorCounter = 0;
			  while(conn.getResponseCode() !=200){
				  if(errorCounter==0){
					  System.out.println("Getting error from remote server... Trying again.");
					  errorCounter++;
				  }			  
			  }*/
		  

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
		}
	
	
	//Query Info setters and getters
	public String getCount(){
		return this.count;
	}
	public void setCount(String count){
		this.count = count;
	}
	
	public String getFrom(){
		return this.from;
	}
	public void setFrom(String from){
		this.from = from;
	}
	
	public String getOrder(){
		return this.order;
	}
	
	public void setOrder(String order){
		this.order = order;
	}
	
	public String getTotal(){
		return this.total;
	}
	public void setTotal(String total){
		this.total = total;
	}
	
}
