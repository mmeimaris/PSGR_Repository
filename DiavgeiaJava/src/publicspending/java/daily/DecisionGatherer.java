package publicspending.java.daily;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import au.com.bytecode.opencsv.CSVWriter;

public class DecisionGatherer {

	ArrayList<HashMap<String, String>> decisionList;	
	HashSet<HashMap<String, String>> decisionSet;
	FileStructure fs;
	String[] entries = "ada#isCorrectionOfAda#relativeAda#submissionTimestamp#url#documentUrl#protocolNumber#decisionType#subject#date#organization#organizationUID#organizationUnit#organizationUnitUID#FEK number#FEK issue#FEK year#tagLabels#tagUIDs#Î•Î Î©Î�Î¥ÎœÎ™Î‘ Î¦ÎŸÎ¡Î•Î‘ (ÎšÎ•Î¦Î‘Î›Î‘Î™Î‘ Î•Î›Î›Î—Î�Î™ÎšÎ‘)#Î‘Î¦Îœ Î¦ÎŸÎ¡Î•Î‘#Î•Î Î©Î�Î¥ÎœÎ™Î‘ Î‘Î�Î‘Î”ÎŸÎ§ÎŸÎ¥ (ÎšÎ•Î¦Î‘Î›Î‘Î™Î‘ Î•Î›Î›Î—Î�Î™ÎšÎ‘)#Î‘Î¦Îœ Î‘Î�Î‘Î”ÎŸÎ§ÎŸÎ¥#hasVAT#isValidVAT#CPV Description# CPV Number#Î£Î§Î•Î¤Î™ÎšÎŸÎ£ Î‘Î¡Î™Î˜ÎœÎŸÎ£ ÎšÎ‘Î•#ÎšÎ‘Î¤Î—Î“ÎŸÎ¡Î™Î‘ Î”Î‘Î Î‘Î�Î—Î£#Î ÎŸÎ£ÎŸ Î”Î‘Î Î‘Î�Î—Î£ / Î£Î¥Î�Î‘Î›Î›Î‘Î“Î—Î£ (ÎœÎ• Î¦Î Î‘)".split("#");
	
	public DecisionGatherer(FileStructure fs){
		this.fs = fs;
		this.fs.setCsvPath();
	}
	
	public boolean writeDecisionCount(String total){
		
		 try{
	    	  // Create file 
			 
			 //new File(FileStructure.filesFolder).mkdirs();			 
	    	 FileWriter fstream = new FileWriter(fs.decisionCountFile);
	    	 BufferedWriter out = new BufferedWriter(fstream);
	    	 out.write(total);	    	 
	    	 out.close();
	    	 return true;
	    	 }catch (Exception e){//Catch exception if any
		    	 System.err.println("Error: " + e.getMessage());
		    	 return false;
	    	 }	  
	    
	}
	
	public String readDecisionCount(){		
		String ret = "";
		System.out.println("Decision Count File Path is : " + fs.decisionCountFile);
		try{
			//System.out.println("Decision Count File Path is : " + fs.decisionCountFile);
			BufferedReader br = new BufferedReader(new FileReader(fs.decisionCountFile));		
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        String everything = sb.toString();
	        if(everything.equals(""))
	        	everything = "0";
	        return everything;
	    } finally {
	        br.close();	        
	    }
		}catch(Exception e){return ret;}
	}
	
	public Integer getFromParameter(String old, String current){
		
		//System.out.println(current + "   " + old + " m");
		Integer fromParam = Integer.parseInt(current.replaceAll("\r", "").replaceAll("\n", ""))-Integer.parseInt(old.replaceAll("\r", "").replaceAll("\n", ""));
		//System.out.println("From Parameter is " + fromParam);
		return fromParam;
	}
	
	/*
	 * This method fetches decisions in JSON form, as prescribed by the Clarity API	 
	 */
	public ArrayList<HashMap<String, String>> getDecisions(double fetchCount) throws IOException {
		
		//decisionList = new ArrayList<HashMap<String, String>>();
		decisionSet = new HashSet<HashMap<String, String>>();
		String oldTotal = readDecisionCount();	
		oldTotal = oldTotal.replaceAll("\n", "").replaceAll("\r", "");			
		int requiredCallsCount = 0;		
		String yesterday = fs.yesterday, today  = fs.today;		
		//yesterday = "26-10-2012";
		//Test dates
		yesterday = "10-12-2012";
		//today = "28-11-2012";
		//Test dates
		
		JSONObject model = null;		  
		
		//CSV writer initialization takes place here
		CSVWriter writer = new CSVWriter(new FileWriter(fs.csvDateFile), ';');		
		writer.writeNext(entries); 
		
		System.out.println("Sending request to Diavgeia.");
		String request = "http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&output=json_full&count="+(int)fetchCount;
		System.out.println(request);
		String response = httpGet(request);
		System.out.println(response);
	    String jsonTxt = response;	    	           	          
	    JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );        	        
	    model = json.getJSONObject("model");
	    JSONObject queryInfo = model.getJSONObject("queryInfo"); 
	    String total = queryInfo.getString("total");
	    while(total.equals("0")){
	    	response = httpGet(request);
	    	System.out.println(response);
	    	jsonTxt = response;	    	           	          
		    json = (JSONObject) JSONSerializer.toJSON( jsonTxt );        	        
		    model = json.getJSONObject("model");
		    queryInfo = model.getJSONObject("queryInfo"); 
		    total = queryInfo.getString("total");
	    }	    
	    System.out.println("Writing total decision count...");	    
	    System.out.println("Done");	    
	    Integer fromParam = getFromParameter(oldTotal, total);	    
	    Double reqLong = new Double(fromParam / fetchCount) ;
	    requiredCallsCount = (int)Math.ceil(reqLong);
	    System.out.println("have to catch decisions " + fromParam + " with old total " + oldTotal + " and new total " + total);
	    if(fromParam<10)
	    	fromParam=0;
	    System.out.println(reqLong + "    " + requiredCallsCount);
	    Integer remainingLast = fromParam % (int)fetchCount;
	    //System.out.println(remainingLast);
	    int i = 1;
	    Integer fromParameter = Integer.parseInt(oldTotal);
	    while(i<=requiredCallsCount){
	    	System.out.println("Call " + i);	    	
	    	//Integer fromParameter = (Integer.parseInt(oldTotal) + ((i-1)*500+1));
	    	String responseCall = "";
	    	fromParameter = Integer.parseInt(oldTotal) + ((i-1)*(int)fetchCount+1);
	    	if(i<requiredCallsCount){	    		
	    		responseCall = httpGet("http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+(int)fetchCount+"&output=json_full&from="+fromParameter);
	    		System.out.println("http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+(int)fetchCount+"&output=json_full&from="+fromParameter);
	    	}	    		    
	    	else if(i==requiredCallsCount){	    		
	    		responseCall = httpGet("http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+remainingLast+"&output=json_full&from="+fromParameter);
	    		System.out.println("http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+remainingLast+"&output=json_full&from="+fromParameter);
	    		//System.out.println("edw eimaste! " + responseCall);
	    	}
	    	System.out.println(fromParameter);
	    	 
	    	//System.out.println("gia na doume.... " + responseCall);
	    	String jsonTxtCall = responseCall;	    		    	
	    	JSONObject jsonCall = (JSONObject) JSONSerializer.toJSON( jsonTxtCall );        	        
	 	    JSONObject modelCall = jsonCall.getJSONObject("model");
	 	    JSONObject queryInfoCall = modelCall.getJSONObject("queryInfo"); 
	 	    String totalCall = queryInfoCall.getString("total");
	 	    String countCall = queryInfoCall.getString("count");
	 	    while(totalCall.equals("0")){
	 	    	System.out.println("Getting shitty responses. O kotsidas to taktopoihse re, mhn anhsyxeite.");
	 	    	if(i<requiredCallsCount){	    		
		    		responseCall = httpGet("http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+(int)fetchCount+"&output=json_full&from="+fromParameter);
		    		System.out.println("http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+(int)fetchCount+"&output=json_full&from="+fromParameter);
		    	}	    		    
		    	else if(i==requiredCallsCount){	    		
		    		responseCall = httpGet("http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+remainingLast+"&output=json_full&from="+fromParameter);
		    		System.out.println("http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+remainingLast+"&output=json_full&from="+fromParameter);
		    		//System.out.println("edw eimaste! " + responseCall);
		    	}
		    	System.out.println(fromParameter);
		    	 
		    	//System.out.println("gia na doume.... " + responseCall);
		    	jsonTxtCall = responseCall;	    		    	
		    	jsonCall = (JSONObject) JSONSerializer.toJSON( jsonTxtCall );        	        
		 	    modelCall = jsonCall.getJSONObject("model");
		 	    queryInfoCall = modelCall.getJSONObject("queryInfo"); 
		 	    totalCall = queryInfoCall.getString("total");
		 	    countCall = queryInfoCall.getString("count");
	 	    }
	 	   /* String countCall = queryInfoCall.getString("count");
	 	    String orderCall = queryInfoCall.getString("order");
	 	    String fromCall = queryInfoCall.getString("from");*/
	 	   //JSONArray expandedDecisions = model.getJSONArray("expandedDecisions");	
	 	    JSONArray expandedDecisions = null; 
	 	    try{ expandedDecisions = modelCall.getJSONArray("expandedDecisions");	 	    
	 	    if (expandedDecisions!=null){	 	    	
	        	Iterator it = expandedDecisions.iterator();
	        	int decisionCount = 0;
	        	while(it.hasNext()){
		        	HashMap<String, String> decisionMap = new HashMap<String, String>();
		        	JSONObject decision = (JSONObject) it.next();	        	
		        	JSONObject metadata = decision.getJSONObject("metadata");	
		        	String url = decision.getString("url");
		        	String documentUrl = decision.getString("documentUrl");
		        	String ada = decision.getString("ada");
		        	String submissionTimestamp = decision.getString("submissionTimestamp");
		        	Date subDate = new Date(Long.parseLong(submissionTimestamp));
		        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'+03:00'");
		        	submissionTimestamp = sdf.format(subDate);
		        	//System.out.println(submissionTimestamp);
		        	String date = metadata.getString("date");
		        	if(!date.equals("null")){
			        	Date ddate = new Date(Long.parseLong(date));
			        	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'+03:00'");
			        	date = sdf2.format(ddate);
		        	}
		        	//Tags
		        	JSONObject tags = metadata.getJSONObject("tags");
		        	JSONArray tag = tags.getJSONArray("tag");
		        	Iterator tagsIt = tag.iterator();
		        	String tagId = null, tagLabel = null, tagArray = "";
		        	
		        	try{
		        	while(tagsIt.hasNext()){
		        		JSONObject tagOb = (JSONObject) tagsIt.next();
		        		 tagId = tagOb.getString("uid");	        		
		        		tagLabel = tagOb.getString("label");
		        		tagArray += tagId+"#";
		        		//System.out.println("tagId: " + tagId + " tagLabel: " + tagLabel);
		        	}
		        	}catch(Exception e){}
		        	JSONObject decisionType = metadata.getJSONObject("decisionType");
		        	String decisionTypeId = decisionType.getString("uid");
		        	String decisionTypeLabel = decisionType.getString("label");
		        	String signer = metadata.getString("signer"); 
		        	JSONObject organization = metadata.getJSONObject("organization");
		        	String orgId = null, orgLabel = null, orgLatinName = null;
		        	try{
		        		orgId = organization.getString("uid");
		        		orgLabel = organization.getString("label");
		        		orgLatinName = organization.getString("latinName");
		        	}catch(Exception e){}        	
		        	String protocolNumber = metadata.getString("protocolNumber");	        	
		        	//System.out.println(protocolNumber);
		        	String subject = metadata.getString("subject");
		        	String email = metadata.getString("email");
		        	String correctionOfAda = metadata.getString("isCorrectionOfAda");
		        	if(correctionOfAda==null || correctionOfAda.equals("null"))
		        		correctionOfAda = "";
		        	String orgUnitId = null, orgUnitLabel = null;
		        	try{
		        		JSONObject orgUnit = metadata.getJSONObject("organizationUnit");
		        		orgUnitId = orgUnit.getString("uid");
		        		orgUnitLabel = orgUnit.getString("label");
		        	}catch(Exception e){}        		
		        	String fekYear = null, fekNumber = null, fekIssue = null;        	
		        	try{
			        	JSONObject relativeFek = metadata.getJSONObject("relativeFEK");
			        	fekYear = relativeFek.getString("year");
			        	fekNumber = relativeFek.getString("fekNumber");
			        	fekIssue = relativeFek.getString("issue");
			        	if(fekYear.equals("0"))
			        		fekYear= "";
			        	if(fekNumber.equals("0"))
			        		fekNumber= "";
			        	if(fekIssue.equals("0"))
			        		fekIssue= "";
		        	}
		        	catch(Exception e){}
		        	String relativeAda = metadata.getString("relativeAda");
		        	if(relativeAda==null || relativeAda.equals("null"))
		        		relativeAda = "";
		        	String payerAfm = null, payerName = null, payeeAfm = null, payeeName = null, description = null, amount = null, cpv = null, kae = null, paymentCategory = null;
		        	boolean payBool = true;
		        	try{
		        	JSONObject extraFields = metadata.getJSONObject("extraFields");	        	
		        	JSONArray extraField = extraFields.getJSONArray("extraField");
		        	Iterator extraIt = extraField.iterator();        	
		        	while(extraIt.hasNext()){        		
		        		JSONObject extraOb = (JSONObject) extraIt.next();
		        		String name = extraOb.getString("name");
		        		if(name.equals("afm_forea"))
		        			payerAfm = extraOb.getString("value");
		        		else if(name.equals("eponimia_forea"))
		        			payerName = extraOb.getString("value");
		        		else if(name.equals("afm_anadoxou"))
		        			payeeAfm = extraOb.getString("value");
		        		else if(name.equals("eponimia_anadoxou"))
		        			payeeName = extraOb.getString("value");
		        		else if(name.equals("perigrafi_antikeimenou")){
		        			description = extraOb.getString("value");        			
		        		}
		        		else if(name.equals("poso_dapanis"))
		        			amount = extraOb.getString("value");
		        		else if(name.equals("cpv")){
		        			cpv = extraOb.getString("value");
		        			//System.out.println(cpv);
		        		}
		        		else if(name.equals("arithmos_kae_dapani"))
		        			kae = extraOb.getString("value");
		        		else if(name.equals("kathgoria_dapanis"))
		        			paymentCategory = extraOb.getString("value");
	        	}   
	        	}catch(Exception e){
	        		e.printStackTrace();
	        		System.out.println("Will break now. Response was: " + responseCall);
	        		//break;	        		
	        		payBool = false;
	        	}
	        		        	
	        	String[] fieldsArray = {ada,correctionOfAda,relativeAda,
						   submissionTimestamp,url,documentUrl,protocolNumber,decisionTypeLabel,subject
						   ,date,orgLabel,orgId,orgUnitLabel,orgUnitId
						   ,fekNumber,fekIssue,fekYear,tagLabel,tagId,payerName,payerAfm,payeeName,payeeAfm,"","",cpv,"",
						   kae,paymentCategory,amount, description, signer, tagArray};
	        	for(int y=0; y<fieldsArray.length;y++){
	        		if(fieldsArray[y]==null || fieldsArray[y].equals("null") || fieldsArray[y].equals(""))
	        			fieldsArray[y] = "Null";	        		
	        	}
	        	/*if(amount.equals("Null")) 
	        	{
	        		System.out.println("Amount is null... Breaking iteration.");
	        		break;
	        	}*/
	        	//String[] decisionEntries = fields.split("#");
	        	//System.out.println(Arrays.toString(fieldsArray));
	            if(payBool){
	            //writer.writeNext(decisionEntries);        
		        	writer.writeNext(fieldsArray);	
		        	//"hasVAT#isValidVAT#CPV Description# CPV Number#Î£Î§Î•Î¤Î™ÎšÎŸÎ£ Î‘Î¡Î™Î˜ÎœÎŸÎ£ ÎšÎ‘Î•#ÎšÎ‘Î¤Î—Î“ÎŸÎ¡Î™Î‘ Î”Î‘Î Î‘Î�Î—Î£#Î ÎŸÎ£ÎŸ Î”Î‘Î Î‘Î�Î—Î£ / Î£Î¥Î�Î‘Î›Î›Î‘Î“Î—Î£ (ÎœÎ• Î¦Î Î‘)".split("#");
		        	decisionMap.put("ada", fieldsArray[0]);
		        	decisionMap.put("isCorrectionOfAda", fieldsArray[1]);
		        	decisionMap.put("relativeAda", fieldsArray[2]);
		        	decisionMap.put("submissionTimestamp", fieldsArray[3]);
		        	decisionMap.put("url", fieldsArray[4]);
		        	decisionMap.put("documentUrl", fieldsArray[5]);
		        	decisionMap.put("protocolNumber", fieldsArray[6]);
		        	decisionMap.put("decisionType", fieldsArray[7]);
		        	decisionMap.put("subject", fieldsArray[8]);
		        	decisionMap.put("date", fieldsArray[9]);
		        	decisionMap.put("organization", fieldsArray[10]);
		        	decisionMap.put("organizationUID", fieldsArray[11]);
		        	decisionMap.put("organizationUnit", fieldsArray[12]);
		        	decisionMap.put("organizationUnitUID", fieldsArray[13]);
		        	decisionMap.put("FEK number", fieldsArray[14]);
		        	decisionMap.put("FEK issue", fieldsArray[15]);
		        	decisionMap.put("FEK year", fieldsArray[16]);
		        	decisionMap.put("tagLabels", fieldsArray[17]);
		        	decisionMap.put("tagUIDs", fieldsArray[18]);
		        	decisionMap.put("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)", fieldsArray[19]);
		        	decisionMap.put("ΑΦΜ ΦΟΡΕΑ", fieldsArray[20]);
		        	decisionMap.put("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)", fieldsArray[21]);
		        	decisionMap.put("ΑΦΜ ΑΝΑΔΟΧΟΥ", fieldsArray[22]);
		        	decisionMap.put("hasVAT", fieldsArray[23]);
		        	decisionMap.put("isValidVAT", fieldsArray[24]);
		        	decisionMap.put("CPV Description", fieldsArray[25]);
		        	decisionMap.put(" CPV Number", fieldsArray[26]);
		        	decisionMap.put("ΣΧΕΤΙΚΟΣ ΑΡΙΘΜΟΣ ΚΑΕ", fieldsArray[27]);
		        	decisionMap.put("ΚΑΤΗΓΟΡΙΑ ΔΑΠΑΝΗΣ", fieldsArray[28]);
		        	decisionMap.put("ΠΟΣΟ ΔΑΠΑΝΗΣ / ΣΥΝΑΛΛΑΓΗΣ (ΜΕ ΦΠΑ)", fieldsArray[29]);
		        	decisionMap.put("ΠΕΡΙΓΡΑΦΗ ΔΑΠΑΝΗΣ", fieldsArray[30]);
		        	decisionMap.put("signer", fieldsArray[31]);
		        	decisionMap.put("tagArray", fieldsArray[32]);
		        	//decisionList.add(decisionMap);
		        	decisionSet.add(decisionMap);
		        	decisionCount++;
	            }
	            else
	            {
	            	System.out.println(ada);
	            }
	        }
	        	if(Integer.parseInt(countCall)==decisionCount) i++;
	    }
	    else
	    {System.out.println("Returned null");} // edw xeirizomai to expandedDecisions==null
	 	   }catch(Exception e){e.printStackTrace();}
	    }
        writer.close();
        if(decisionSet.size()>0){
        	writeDecisionCount(Integer.toString(Integer.parseInt(oldTotal)+decisionSet.size()));
        	decisionList = new ArrayList<HashMap<String,String>>(decisionSet);
        	return decisionList;
        }
        else return null;
        
	}
	
	
public ArrayList<HashMap<String, String>> getDecisionsInXML(double fetchCount) throws IOException {
			
		decisionSet = new HashSet<HashMap<String, String>>();
		String oldTotal = readDecisionCount();	
		oldTotal = oldTotal.replaceAll("\n", "").replaceAll("\r", "");			
		int requiredCallsCount = 0;		
		String yesterday = fs.yesterday, today  = fs.today;
		//Start Test dates, uncomment to set test dates
		yesterday = "30-01-2013";
		//today = "18-12-2012";
		//End Test dates					 
		
		//CSV writer initialization here
		CSVWriter writer = new CSVWriter(new FileWriter(fs.csvDateFile), ';');
		//Write title labels in CSV file
		writer.writeNext(entries); 
		//Send request for XML
		DOMResponseParser drp = new DOMResponseParser();
		String request = "http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&type=27&count="+(int)fetchCount;
		System.out.println(request);
		System.out.println("Checking for a valid response...");
		String total = drp.checkXMLResponse(request);
		while(total==null) total = drp.checkXMLResponse(request);
		Integer fromParam = getFromParameter(oldTotal, total);	    
		Double reqLong = new Double(fromParam / fetchCount) ;
		requiredCallsCount = (int)Math.ceil(reqLong);
		System.out.println("have to catch decisions " + fromParam + " with old total " + oldTotal + " and new total " + total);
		if(fromParam<10)
		 	fromParam=0;
		System.out.println(reqLong + "    " + requiredCallsCount);
		Integer remainingLast = fromParam % (int)fetchCount;		    
		int i = 1;
		int countParameter = (int) fetchCount;
		Integer fromParameter = Integer.parseInt(oldTotal);
		//drp.getXMLresponse(request);		
		while(i<=requiredCallsCount){
	    	System.out.println("Call " + i);	    		    	
	    	String responseCall = "";	    	
	    	fromParameter = Integer.parseInt(oldTotal) + ((i-1)*(int)fetchCount+1);
	    	if(i<requiredCallsCount){	    		
	    		responseCall = "http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&output=full&type=27&count="+(int)fetchCount+"&from="+fromParameter;
	    		//System.out.println(responseCall);
	    	}	    		    
	    	else if(i==requiredCallsCount){	    		
	    		responseCall = "http://opendata.diavgeia.gov.gr/api/decisions?datefrom="+yesterday+"&dateTo="+today+"&output=full&type=27&count="+remainingLast+"&from="+fromParameter;
	    		countParameter = (int) remainingLast;
	    		//System.out.println(responseCall);	    	
	    	}
	    	HashSet<HashMap<String,String>> set = drp.getXMLresponse(responseCall, countParameter);
	    	if(set==null) {
	    		System.out.println(" ") ;
	    		System.out.println("Something's wrong with the response. Fetching again...");
	    		continue;  //if null set is returned then do the iteration again
	    	}
	    	System.out.println("Set size is:" + set.size());
	    	/*if(set.size()==0) {
	    		System.out.println("");
	    		System.out.println("Zero? Was ist dass?");	    			    		
	    	}*/
	    	decisionSet.addAll(set);	
	    	System.out.println(fromParameter);
	    	i++;
		}
		HashSet<String> problematicDecisions = drp.getProblematicDecisions();
		Iterator it = problematicDecisions.iterator();
		while(it.hasNext()){
			System.out.println((String)it.next());
		}
		System.out.println(decisionSet.size());
		writeDecisionCount(Integer.toString(Integer.parseInt(oldTotal)+decisionSet.size()));
    	decisionList = new ArrayList<HashMap<String,String>>(decisionSet);
    	return decisionList;   
	    	    
	            
	}

	public ArrayList<HashMap<String,String>> getDecisionsFromDB(){
		
		String yesterday = fs.yesterdayYYMMDD, today  = fs.todayYYMMDD;
		//test dates
		//yesterday = "2013-01-03";
		//today = "2013-01-04";
		//test dates end
		DiavgeiaDBAccess dba = new DiavgeiaDBAccess(yesterday, today);
		ArrayList<HashMap<String,String>> decisionList = new ArrayList<HashMap<String,String>>();
		try {
			decisionList = dba.readDataBase();
			for(HashMap<String,String> decisionMap : decisionList){
				Set keySet = decisionMap.keySet();
				Iterator it = keySet.iterator();
				/*System.out.println("-----------------------------------------------------------");
				while(it.hasNext()){
					String field = (String)it.next();
					System.out.println(field + ": " + decisionMap.get(field));
				}
				System.out.println("-----------------------------------------------------------");*/
			}
			return decisionList;
		} catch (Exception e) {			
			e.printStackTrace();
			return null;
		}
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
}
