package diavgeia.java.daily;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class NicknameHandler {

	public NicknameHandler(){
				
		
	}

	
public void invalidPayeeForTop20Payers(){
		
		
		try{
				SpreadsheetService service = new SpreadsheetService("MySpreadsheetIntegration-v3");		
				service.setUserCredentials("m.meimaris@gmail.com", "aliceson");			    			   
			    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");			    
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();			   
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
			    System.out.println(spreadsheet.getTitle().getPlainText());
			    
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);			    
			     

			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();			    
			    
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);			    
			    int iter = 0;							    
			    for (ListEntry row : listFeed.getEntries()) {			      
			      int i = 0;			      			      
			      String agentUri = "", shortName = "", engName = "", engShortName = "", category = "", fullName = "";
			      int flag = 1;			      
			      ListEntry row2 = new ListEntry();	
			      for (String tag : row.getCustomElements().getTags()) {	
			    	  if(i==0 && row.getCustomElements().getValue(tag)!=null){			    		  
			    		  agentUri = row.getCustomElements().getValue(tag).replace(" ", "");
			    		  String agentName = getAgentNames(agentUri);
			    		  //System.out.println("uri: " + agentUri);
			    		  iter++;
			    		  System.out.println(iter);
			    		  String query = "SELECT ?payment ?url ?cpv ?cpvSubject ?name ?afm (xsd:decimal(?am) as ?amount) from <" + DailyRoutineHandler.graphName + "> WHERE {" +
			    				  		 "?payment psgr:payer <" + agentUri + "> ; psgr:payee ?payee ; psgr:cpv ?cpv ; " +
			    				  		 		" psgr:paymentAmount ?am . ?decision psgr:refersTo ?payment ; psgr:url ?url .?payee psgr:validAfm \"false\" . ?payee psgr:afm ?afm. ?payee psgr:paymentAgentName ?name ." +
			    				  		 		"OPTIONAL{?cpv psgr:cpvGreekSubject ?cpvSubject .}} ORDER BY DESC(?amount) LIMIT 200";
			    		  System.out.println(query);
			    		  Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");		
			    		  VirtGraph graph = (VirtGraph) model.getGraph();
			    		  VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			    		  ResultSet results = vqe.execSelect();			    		  			    		 			    		  
			    		  while(results.hasNext()){			 			    			  	
			    			  	WorksheetEntry sheet = worksheets.get(iter);			    			    
			    			    sheet.setTitle(new PlainTextConstruct(agentName));			    			    
			    			  	URL listFeedUrl2 = sheet.getListFeedUrl();			    
			    				QuerySolution rs = results.nextSolution();
			    				RDFNode payment = rs.get("payment");
			    				RDFNode cpv = rs.get("cpv");
			    				Literal amount= rs.getLiteral("amount");
			    				Literal url = rs.getLiteral("url");
			    				Literal afm = rs.getLiteral("afm");
			    				Literal name = rs.getLiteral("name");
			    				Literal cpvSub = rs.getLiteral("cpvSubject");
			    				String cpvS = "";
			    				try{
			    					cpvS = cpvSub.getString();			    					
			    				}catch(Exception e){cpvS = cpv.toString().substring(cpv.toString().indexOf("#")+1);}
			    				//System.out.println(cpvS);
			    				//System.out.println(name);
//			    				RDFNode payee = rs.get("payee");			    				
//			    				String payeeName = getAgentNamesByAfm(afm.getString());
			    				//System.out.println("name: " + payeeName);
			    				row2.getCustomElements().setValueLocal("PaymentURI", payment.toString());
			    				row2.getCustomElements().setValueLocal("PaymentURL", url.getString());
			    				row2.getCustomElements().setValueLocal("Payee", name.getString());
			    				row2.getCustomElements().setValueLocal("Afm", afm.getString());			    				
			    				row2.getCustomElements().setValueLocal("Cpv", cpvS);
			    				//row2.getCustomElements().setValueLocal("CpvSubject", cpvSub);
			    				row2.getCustomElements().setValueLocal("Amount", amount.getString());			    				
			    				row2 = service.insert(listFeedUrl2, row2);			    				
			    		  }			    		  			    		  
			    		  vqe.close();			    		  
			    		  //System.out.println("---------------------");
			    		  //System.out.println(agentUri);
			    		  
			    	  }
			    	  i++;			    	  
			      }
			      //row2.update();
			      
			    }
			  
			}
			catch(Exception e){e.printStackTrace();}
		
	}

	
	public void fixInvalidCpvPayments(){
		
		
		try{
				SpreadsheetService service = new SpreadsheetService("MySpreadsheetIntegration-v3");		
				service.setUserCredentials("m.meimaris@gmail.com", "aliceson");			    			   
			    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");			    
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();			   
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
			    System.out.println(spreadsheet.getTitle().getPlainText());
			    
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    for(int i=0;i<20;i++){
			    	WorksheetEntry worksheet = worksheets.get(i+1);
			    	URL listFeedUrl = worksheet.getListFeedUrl();				    
				    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);				    
				    for (ListEntry row : listFeed.getEntries()) {
				    	int ii = 0;
					      String paymentUri = "";					      
					      //ListEntry row2 = new ListEntry();	
					      for (String tag : row.getCustomElements().getTags()) {	
					    	  if(ii==0 && row.getCustomElements().getValue(tag)!=null){			    		  
					    		  paymentUri = row.getCustomElements().getValue(tag).replace(" ", "");
					    		  System.out.println(paymentUri);
					    		  String query = "SELECT ?name ?gsisName from <" + DailyRoutineHandler.graphName + "> WHERE {" +
					    				  		 "<" + paymentUri + "> psgr:payee ?payee . ?payee psgr:paymentAgentName ?name . OPTIONAL{?payee psgr:gsisName ?gsisName}}LIMIT 1";
					    		  //System.out.println(query);
					    		  Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");		
					    		  VirtGraph graph = (VirtGraph) model.getGraph();
					    		  VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
					    		  ResultSet results = vqe.execSelect();			    
					    		  String nameS = "";
					    		  while(results.hasNext()){			    			  				    			  	
					    				QuerySolution rs = results.nextSolution();
					    				Literal name = rs.getLiteral("name");
					    				Literal gsisName = rs.getLiteral("gsisName");
					    				try{
					    					nameS = gsisName.getString();
					    				}catch(Exception e){nameS = name.getString();}
					    				//System.out.println(nameS);
					    				row.getCustomElements().setValueLocal("Payee", nameS);
					    				
					    		  }
					    		  			    		  
					    		  vqe.close();			    		  
					    		  //System.out.println("---------------------");
					    		  //System.out.println(agentUri);
					    	  }		
					    	  ii++;
					      }
					      row.update();
					      
					    }
				    
			    }
			    
			     

			 // Fetch the list feed of the worksheet.
			    			    
			    											    
			    			      
			      			  
			}
			catch(Exception e){e.printStackTrace();}
		
	}

	

public void invalidCpvForTop20Payers(){
		
		
		try{
				SpreadsheetService service = new SpreadsheetService("MySpreadsheetIntegration-v3");		
				service.setUserCredentials("m.meimaris@gmail.com", "aliceson");			    			   
			    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");			    
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();			   
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
			    System.out.println(spreadsheet.getTitle().getPlainText());
			    
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);			    
			     

			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();			    
			    
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);			    
			    int iter = 0;							    
			    for (ListEntry row : listFeed.getEntries()) {			      
			      int i = 0;			      			      
			      String agentUri = "", shortName = "", engName = "", engShortName = "", category = "", fullName = "";
			      int flag = 1;			      
			      ListEntry row2 = new ListEntry();	
			      for (String tag : row.getCustomElements().getTags()) {	
			    	  if(i==0 && row.getCustomElements().getValue(tag)!=null){			    		  
			    		  agentUri = row.getCustomElements().getValue(tag).replace(" ", "");
			    		  String agentName = getAgentNames(agentUri);
			    		  //System.out.println("uri: " + agentUri);
			    		  iter++;
			    		  System.out.println(iter);
			    		  String query = "SELECT ?payment ?url ?payee ?afm ?name ?cpv (xsd:decimal(?am) as ?amount) from <" + DailyRoutineHandler.graphName + "> WHERE {" +
			    				  		 "?payment psgr:payer <" + agentUri + "> ; psgr:payee ?payee ; psgr:validCpv \"false\"; psgr:cpv ?cpv ; " +
			    				  		 		" psgr:paymentAmount ?am . ?decision psgr:refersTo ?payment ; psgr:url ?url . ?payee psgr:afm ?afm. ?payee psgr:paymentAgentName ?name } ORDER BY DESC(?amount) LIMIT 200";
			    		  //System.out.println(query);
			    		  Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");		
			    		  VirtGraph graph = (VirtGraph) model.getGraph();
			    		  VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			    		  ResultSet results = vqe.execSelect();			    		  			    		 			    		  
			    		  while(results.hasNext()){			 			    			  	
			    			  	WorksheetEntry sheet = worksheets.get(iter);
			    			    //WorksheetEntry sheet = new WorksheetEntry();
			    			    sheet.setTitle(new PlainTextConstruct(agentName));
			    			  	URL listFeedUrl2 = sheet.getListFeedUrl();
			    			  	ListFeed listFeed2 = service.getFeed(listFeedUrl2, ListFeed.class);			    			  	
			    			  	//ListFeed listFeed2 = service.getFeed(listFeedUrl2, ListFeed.class);
			    				QuerySolution rs = results.nextSolution();
			    				RDFNode payment = rs.get("payment");
			    				Literal cpv = rs.getLiteral("cpv");
			    				Literal amount= rs.getLiteral("amount");
			    				Literal url = rs.getLiteral("url");
			    				Literal afm = rs.getLiteral("afm");
			    				Literal name = rs.getLiteral("name");
			    				System.out.println(name);
//			    				RDFNode payee = rs.get("payee");			    				
//			    				String payeeName = getAgentNamesByAfm(afm.getString());
			    				//System.out.println("name: " + payeeName);
			    				//row2.getCustomElements().setValueLocal("PaymentURI", payment.toString());
			    				//row2.getCustomElements().setValueLocal("PaymentURL", url.getString());
			    				row2.getCustomElements().setValueLocal("Payee", name.getString());
			    				row2.getCustomElements().setValueLocal("Αφμ", afm.getString());
			    				//row2.getCustomElements().setValueLocal("Cpv", cpv.getString());
			    				//row2.getCustomElements().setValueLocal("Amount", amount.getString());			    				
			    				row2 = service.insert(listFeedUrl2, row2);
			    				
			    		  }			    		  			    		  
			    		  vqe.close();			    		  
			    		  //System.out.println("---------------------");
			    		  //System.out.println(agentUri);
			    		  
			    	  }
			    	  i++;			    	  
			      }
			      //row2.update();
			      
			    }
			  
			}
			catch(Exception e){e.printStackTrace();}
		
	}

	
	public void koinopraksies(){
		
		
		try{
				SpreadsheetService service = new SpreadsheetService("MySpreadsheetIntegration-v3");		
				service.setUserCredentials("m.meimaris@gmail.com", "aliceson");			    			   
			    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");			    
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();			   
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
			    System.out.println(spreadsheet.getTitle().getPlainText());
			    
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);
			     

			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();
			    
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);			    
			    											    
			    for (ListEntry row : listFeed.getEntries()) {			      
			      int i = 0;			      			      
			      String agentUri = "", shortName = "", engName = "", engShortName = "", category = "", fullName = "";
			      int flag = 1;	
			      ListEntry row2 = new ListEntry();	
			      for (String tag : row.getCustomElements().getTags()) {	
			    	  if(i==0 && row.getCustomElements().getValue(tag)!=null){			    		  
			    		  agentUri = row.getCustomElements().getValue(tag).replace(" ", "");
			    		  String query = "SELECT distinct ?payer (sum(xsd:decimal(?am)) as ?sum) from <" + DailyRoutineHandler.graphName + "> WHERE {" +
			    				  		 "?payment psgr:payee <" + agentUri + "> ; psgr:payer ?payer . ?payer psgr:validAfm \"true\" . " +
			    				  		 		"?payment psgr:paymentAmount ?am . } ORDER BY DESC(?sum) LIMIT 3";
			    		  //System.out.println(query);
			    		  Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");		
			    		  VirtGraph graph = (VirtGraph) model.getGraph();
			    		  VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			    		  ResultSet results = vqe.execSelect();			    		  			    		 
			    		  int iter = 1;
			    		  while(results.hasNext()){			    			  				    			  	
			    				QuerySolution rs = results.nextSolution();
			    				RDFNode payer = rs.get("payer");
			    				Literal sum = rs.getLiteral("sum");			    				
			    				String cpvQuery = "SELECT ?cpv ?cpvSub FROM <" +DailyRoutineHandler.graphName + "> WHERE {" +
		    				  		    "?payment psgr:payee <" + agentUri +"> ; psgr:payer <" + payer.toString() + "> ; psgr:paymentAmount ?am ; psgr:cpv ?cpv . OPTIONAL{?cpv psgr:cpvGreekSubject ?cpvSub}} ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1";			    			
			    				VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
			    				ResultSet resultsCpv = vqeCpv.execSelect();
			    				String cpvS = "";
			    				while(resultsCpv.hasNext()){			    			  				    			  	
				    				QuerySolution rsCpv = resultsCpv.nextSolution();
				    				Literal cpvSub = rsCpv.getLiteral("cpvSub");
				    				RDFNode cpv = rsCpv.get("cpv");
				    				cpvS = cpv.toString();
				    				try{
				    					cpvS = cpvSub.getString();
				    				}catch(Exception e){}
			    				}
			    				vqeCpv.close();			    				
			    				row.getCustomElements().setValueLocal("Payer"+iter+"URI", payer.toString());
			    				row.getCustomElements().setValueLocal("Sum"+iter, sum.getString());
			    				row.getCustomElements().setValueLocal("Payer"+iter+"Name", getAgentNames(payer.toString()));
			    				row.getCustomElements().setValueLocal("Cpv"+iter, cpvS);
			    				iter++;
			    		  }
			    		  			    		  
			    		  vqe.close();			    		  
			    		  //System.out.println("---------------------");
			    		  //System.out.println(agentUri);
			    	  }
			    	  i++;
			      }
			      row.update();
			      
			    }
			  
			}
			catch(Exception e){e.printStackTrace();}
		
	}
	
	
	public void aeitei(){
		try{
			DocsService client = new DocsService("yourCo-yourAppName-v1");	
			SpreadsheetService service = new SpreadsheetService("MySpreadsheetIntegration-v3");		
				service.setUserCredentials("m.meimaris@gmail.com", "ointzasetsi.1");			    			   
			    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			    
			    SpreadsheetQuery query1 = new SpreadsheetQuery(SPREADSHEET_FEED_URL);
			    query1.setSortMode("title");
			    query1.setTitleQuery("AEI/TEI tests");
			    query1.setTitleExact(true);
			    query1.setMaxResults(10);			    
			    SpreadsheetFeed feed = service.getFeed(query1,SpreadsheetFeed.class);			    
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();			   
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
			    System.out.println(spreadsheet.getTitle().getPlainText());			    			    			    			   
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);
			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();			    
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);			    
			    											    
			    for (ListEntry row : listFeed.getEntries()) {			      
			      int i = 0;			      			      
			      String agentUri = "", shortName = "", engName = "", engShortName = "", category = "", fullName = "", agentName="", firmDesc="";
			      int flag = 1;	
			      ListEntry row2 = new ListEntry();	
			      for (String tag : row.getCustomElements().getTags()) {	
			    	  if(i==0 && row.getCustomElements().getValue(tag)!=null){			    		  
			    		  agentUri = row.getCustomElements().getValue(tag).replace(" ", "");
			    		  String query = "SELECT distinct ?payment (xsd:decimal(?am) as ?amount) ?url from <" + DailyRoutineHandler.graphName + "> WHERE {" +
			    				  		 "?payment psgr:payee <" + agentUri + "> ; psgr:payer ?payer . ?payer psgr:greekShortName \"ΑΠΘ\" . " +
			    				  		 		"?payment psgr:paymentAmount ?am . ?decision psgr:refersTo ?payment . ?decision psgr:url ?url .} ORDER BY DESC(?amount) LIMIT 3";
			    		  //System.out.println(query);
			    		  Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");		
			    		  VirtGraph graph = (VirtGraph) model.getGraph();
			    		  /*String nameQuery = "SELECT ?name ?desc ?gName from <" + DailyRoutineHandler.graphName + "> WHERE {" + 
								   "<" + agentUri + "> psgr:paymentAgentName ?name . " +
								   "OPTIONAL{<"+agentUri+"> psgr:gsisName ?gName .}" +
								   		"OPTIONAL{<"+agentUri+"> psgr:firmDescription ?desc .}} LIMIT 1";
			    		  VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory.create (nameQuery, graph);
			    		  ResultSet resultsNames = vqeNames.execSelect();			    		  			    		 			    		  
			    		  while(resultsNames.hasNext()){			    			  				    			  	
			    				QuerySolution rsNames = resultsNames.nextSolution();
			    				Literal name = rsNames.getLiteral("name");
			    				Literal gName = rsNames.getLiteral("gName");
			    				Literal firmDescL = rsNames.getLiteral("desc");
			    				agentName = name.getString();
			    				try{
			    					agentName = gName.getString();			    					
			    					firmDesc = firmDescL.getString();
			    				}catch(Exception e){agentName = name.getString();}
			    				if(agentName.contains("Null"))
			    					agentName = name.getString();
			    		  }
			    		  vqeNames.close();
			    		  row.getCustomElements().setValueLocal("Name", agentName);
			    		  row.getCustomElements().setValueLocal("FirmDescription", firmDesc);*/
			    		  VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			    		  ResultSet results = vqe.execSelect();			    		  			    		 
			    		  int iter = 1;
			    		  while(results.hasNext()){			    			  				    			  	
			    				QuerySolution rs = results.nextSolution();
			    				RDFNode payment = rs.get("payment");			    							    			
			    				Literal amount = rs.getLiteral("amount");
			    				Literal url = rs.getLiteral("url");
			    				String cpvQuery = "SELECT ?cpv ?cpvSub FROM <" +DailyRoutineHandler.graphName + "> WHERE {" +
		    				  		    "<"+payment.toString()+"> psgr:cpv ?cpv . OPTIONAL{?cpv psgr:cpvGreekSubject ?cpvSub}}LIMIT 1";			    			
			    				VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
			    				ResultSet resultsCpv = vqeCpv.execSelect();
			    				String cpvS = "";
			    				while(resultsCpv.hasNext()){			    			  				    			  	
				    				QuerySolution rsCpv = resultsCpv.nextSolution();
				    				Literal cpvSub = rsCpv.getLiteral("cpvSub");
				    				RDFNode cpv = rsCpv.get("cpv");
				    				cpvS = cpv.toString();
				    				try{
				    					cpvS = cpvSub.getString();
				    				}catch(Exception e){}
			    				}
			    				vqeCpv.close();			    				
			    				//row.getCustomElements().setValueLocal("Payment"+iter+"URL", payment.toString());
			    				row.getCustomElements().setValueLocal("Payment"+iter+"URL", url.getString());
			    				row.getCustomElements().setValueLocal("Sum"+iter, amount.getString());
			    				//row.getCustomElements().setValueLocal("Payment"+iter+"Name", getAgentNames(payer.toString()));
			    				row.getCustomElements().setValueLocal("Cpv"+iter, cpvS);
			    				iter++;
			    		  }
			    		  			    		  
			    		  vqe.close();			    		  
			    		  //System.out.println("---------------------");
			    		  //System.out.println(agentUri);
			    	  }
			    	  i++;
			      }
			      row.update();
			      
			    }
			  
			}
			catch(Exception e){e.printStackTrace();}
	}
	
	public void fysikaProswpa(){
		
			
		try{
			DocsService client = new DocsService("yourCo-yourAppName-v1");	
			SpreadsheetService service = new SpreadsheetService("MySpreadsheetIntegration-v3");		
				service.setUserCredentials("m.meimaris@gmail.com", "aliceson");			    			   
			    URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");			    			    
			    //WorksheetFeed worksheetFeed = client.getFeed(query1, WorksheetFeed.class);
			    //SpreadsheetFeed feed = service.getFeed(query1,SpreadsheetFeed.class);
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();			   
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
			    System.out.println(spreadsheet.getTitle().getPlainText());
			    			    			    			   
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);
			     

			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();
			    
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);			    
			    											    
			    for (ListEntry row : listFeed.getEntries()) {			      
			      int i = 0;			      			      
			      String agentUri = "", shortName = "", engName = "", engShortName = "", category = "", fullName = "", agentName="", firmDesc="";
			      int flag = 1;	
			      ListEntry row2 = new ListEntry();	
			      for (String tag : row.getCustomElements().getTags()) {	
			    	  if(i==0 && row.getCustomElements().getValue(tag)!=null){			    		  
			    		  agentUri = row.getCustomElements().getValue(tag).replace(" ", "");
			    		  String query = "SELECT distinct ?payer (sum(xsd:decimal(?am)) as ?sum) from <" + DailyRoutineHandler.graphName + "> WHERE {" +
			    				  		 "?payment psgr:payee <" + agentUri + "> ; psgr:payer ?payer . ?payer psgr:validAfm \"true\" . " +
			    				  		 		"?payment psgr:paymentAmount ?am . } ORDER BY DESC(?sum) LIMIT 3";
			    		  //System.out.println(query);
			    		  Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");		
			    		  VirtGraph graph = (VirtGraph) model.getGraph();
			    		  String nameQuery = "SELECT ?name ?desc ?gName from <" + DailyRoutineHandler.graphName + "> WHERE {" + 
								   "<" + agentUri + "> psgr:paymentAgentName ?name . " +
								   "OPTIONAL{<"+agentUri+"> psgr:gsisName ?gName .}" +
								   		"OPTIONAL{<"+agentUri+"> psgr:firmDescription ?desc .}} LIMIT 1";
			    		  VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory.create (nameQuery, graph);
			    		  ResultSet resultsNames = vqeNames.execSelect();			    		  			    		 			    		  
			    		  while(resultsNames.hasNext()){			    			  				    			  	
			    				QuerySolution rsNames = resultsNames.nextSolution();
			    				Literal name = rsNames.getLiteral("name");
			    				Literal gName = rsNames.getLiteral("gName");
			    				Literal firmDescL = rsNames.getLiteral("desc");
			    				agentName = name.getString();
			    				try{
			    					agentName = gName.getString();			    					
			    					firmDesc = firmDescL.getString();
			    				}catch(Exception e){agentName = name.getString();}
			    				if(agentName.contains("Null"))
			    					agentName = name.getString();
			    		  }
			    		  vqeNames.close();
			    		  row.getCustomElements().setValueLocal("Name", agentName);
			    		  row.getCustomElements().setValueLocal("FirmDescription", firmDesc);
			    		  VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			    		  ResultSet results = vqe.execSelect();			    		  			    		 
			    		  int iter = 1;
			    		  while(results.hasNext()){			    			  				    			  	
			    				QuerySolution rs = results.nextSolution();
			    				RDFNode payer = rs.get("payer");			    							    			
			    				Literal sum = rs.getLiteral("sum");			    				
			    				String cpvQuery = "SELECT ?cpv ?cpvSub FROM <" +DailyRoutineHandler.graphName + "> WHERE {" +
		    				  		    "?payment psgr:payee <" + agentUri +"> ; psgr:payer <" + payer.toString() + "> ; psgr:paymentAmount ?am ; psgr:cpv ?cpv . OPTIONAL{?cpv psgr:cpvGreekSubject ?cpvSub}} ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1";			    			
			    				VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
			    				ResultSet resultsCpv = vqeCpv.execSelect();
			    				String cpvS = "";
			    				while(resultsCpv.hasNext()){			    			  				    			  	
				    				QuerySolution rsCpv = resultsCpv.nextSolution();
				    				Literal cpvSub = rsCpv.getLiteral("cpvSub");
				    				RDFNode cpv = rsCpv.get("cpv");
				    				cpvS = cpv.toString();
				    				try{
				    					cpvS = cpvSub.getString();
				    				}catch(Exception e){}
			    				}
			    				vqeCpv.close();			    				
			    				row.getCustomElements().setValueLocal("Payer"+iter+"URI", payer.toString());
			    				row.getCustomElements().setValueLocal("Sum"+iter, sum.getString());
			    				row.getCustomElements().setValueLocal("Payer"+iter+"Name", getAgentNames(payer.toString()));
			    				row.getCustomElements().setValueLocal("Cpv"+iter, cpvS);
			    				iter++;
			    		  }
			    		  			    		  
			    		  vqe.close();			    		  
			    		  //System.out.println("---------------------");
			    		  //System.out.println(agentUri);
			    	  }
			    	  i++;
			      }
			      row.update();
			      
			    }
			  
			}
			catch(Exception e){e.printStackTrace();}
		
	}
	
	
	public void deletePrevious(){
		try{
			SpreadsheetService service =
			        new SpreadsheetService("MySpreadsheetIntegration-v1");		
			service.setUserCredentials("m.meimaris@gmail.com", "aliceson");
			    // TODO: Authorize the service object for a specific user (see other sections)

			    // Define the URL to request.  This should never change.
			    URL SPREADSHEET_FEED_URL = new URL(
			        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

			    // Make a request to the API and get all spreadsheets.
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
			        SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();

			    if (spreadsheets.size() == 0) {
			      // TODO: There were no spreadsheets, act accordingly.
			    }

			    // TODO: Choose a spreadsheet more intelligently based on your
			    // app's needs.
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);			 
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);
			    	   

			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();			    
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);			    
			    for (ListEntry row : listFeed.getEntries()) {			    	
			    	row.delete();			    	
			    }
			    
		}
		catch(Exception e){}		
	}
	
	public void addToSpreadsheet(String[] entry){
		
		try{
			SpreadsheetService service =
			        new SpreadsheetService("MySpreadsheetIntegration-v1");		
			service.setUserCredentials("m.meimaris@gmail.com", "aliceson");
			    // TODO: Authorize the service object for a specific user (see other sections)

			    // Define the URL to request.  This should never change.
			    URL SPREADSHEET_FEED_URL = new URL(
			        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

			    // Make a request to the API and get all spreadsheets.
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
			        SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();

			    if (spreadsheets.size() == 0) {
			      // TODO: There were no spreadsheets, act accordingly.
			    }

			    // TODO: Choose a spreadsheet more intelligently based on your
			    // app's needs.
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
			    //System.out.println(spreadsheet.getTitle().getPlainText());

			    // Get the first worksheet of the first spreadsheet.
			    // TODO: Choose a worksheet more intelligently based on your
			    // app's needs.
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);
			    	   

			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();			    
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
			    ListEntry row = new ListEntry();
			    row.getCustomElements().setValueLocal("URI", entry[0]);
			    row.getCustomElements().setValueLocal("GreekFull", entry[1]);
			    row = service.insert(listFeedUrl, row);
			    
		}
		catch(Exception e){}		
	}
	
	public void convertToRDF(){
		
		//Model model = VirtModel.openDatabaseModel("http://publicspending.medialab.ntua.gr/ValidDecisions", DailyRoutineHandler.connectionString, "marios", "dirtymarios");
		Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");
		//Model model = VirtModel.openDatabaseModel("testgoogledocs2", DailyRoutineHandler.connectionString, "marios", "dirtymarios");
		VirtGraph graph = (VirtGraph) model.getGraph();		
		try{
			SpreadsheetService service =
			        new SpreadsheetService("MySpreadsheetIntegration-v1");		
			service.setUserCredentials("m.meimaris@gmail.com", "aliceson");
			    // TODO: Authorize the service object for a specific user (see other sections)

			    // Define the URL to request.  This should never change.
			    URL SPREADSHEET_FEED_URL = new URL(
			        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

			    // Make a request to the API and get all spreadsheets.
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
			        SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();

			    if (spreadsheets.size() == 0) {
			      // TODO: There were no spreadsheets, act accordingly.
			    }

			    // TODO: Choose a spreadsheet more intelligently based on your
			    // app's needs.
			    SpreadsheetEntry spreadsheet = spreadsheets.get(2);
			    System.out.println(spreadsheet.getTitle().getPlainText());

			    // Get the first worksheet of the first spreadsheet.
			    // TODO: Choose a worksheet more intelligently based on your
			    // app's needs.
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);
			    WorksheetEntry worksheet2 = worksheets.get(1);			    

			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();
			    URL listFeedUrl2 = worksheet2.getListFeedUrl();
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
			    ListFeed listFeed2 = service.getFeed(listFeedUrl2, ListFeed.class);
			    								
			    // Iterate through each row, printing its cell values.
			    for (ListEntry row : listFeed.getEntries()) {
			      // Print the first column's cell value
			      //System.out.print(row.getTitle().getPlainText() + "\t");
			      // Iterate over the remaining columns, and print each cell value
			      int i = 0;			      			      
			      String agentUri = "", shortName = "", engName = "", engShortName = "", category = "", fullName = "";
			      int flag = 1;	
			      ListEntry row2 = new ListEntry();	
			      for (String tag : row.getCustomElements().getTags()) {	
			    	  //System.out.println(flag + " " + i);			    	  
			    	  //System.out.println(tag);
			    	  if(i==0 && row.getCustomElements().getValue(tag)!=null){			    		  
			    		  agentUri = row.getCustomElements().getValue(tag);
			    		  String existsQuery = SPARQLQueries.prefixes + "SELECT ?name FROM <" + DailyRoutineHandler.graphName + "> WHERE{" +			    		 
			    				  										"<"+agentUri+"> psgr:greekShortName ?name. }";			    		  
			    		  VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (existsQuery, graph);
			    		  ResultSet results = vqe.execSelect();			    		  			    		  			    		
			    		  while(results.hasNext()){
			    			  	flag = 0;
			    			  	//System.out.println("flag = 0");
			    				QuerySolution rs = results.nextSolution();
			    				Literal name = rs.getLiteral("name");				    							    			
			    		  }
			    		  vqe.close();
			    		  if(flag==1){
			    			  row2.getCustomElements().setValueLocal("URI", agentUri);			    			  
			    		  }
			    		  
			    		  //System.out.print("Greek Short Name: " + row.getCustomElements().getValue(tag) + "\t");
			    		  //String greekAbbrev = row.getCustomElements().getValue(tag);
			    	  }
			    	  if(flag==1){
			    		  //System.out.println("flag = 1 ");			    		  
			    		  if(i==1 && row.getCustomElements().getValue(tag)!=null){
				    		  fullName = row.getCustomElements().getValue(tag);				    		  
				    		  row2.getCustomElements().setValueLocal("GreekFull", fullName);				    		  
				    		  //System.out.print("Greek Short Name: " + row.getCustomElements().getValue(tag) + "\t");
				    		  //String greekAbbrev = row.getCustomElements().getValue(tag);
				    	  }
			    		  else if(i==2 && row.getCustomElements().getValue(tag)!=null){
				    		  shortName = row.getCustomElements().getValue(tag);				    		  
				    		  row2.getCustomElements().setValueLocal("GreekShort", shortName);				    		  
				    		  //System.out.print("Greek Short Name: " + row.getCustomElements().getValue(tag) + "\t");
				    		  //String greekAbbrev = row.getCustomElements().getValue(tag);
				    	  }
				    	  else if(i==3 && row.getCustomElements().getValue(tag)!=null){
				    		  category = row.getCustomElements().getValue(tag);
				    		  row2.getCustomElements().setValueLocal("Category", category);
				    		  //String category = row.getCustomElements().getValue(tag);
				    		  //System.out.print("Category: " + row.getCustomElements().getValue(tag)+ "\t");
				    	  }
				    	  else if(i==4 && row.getCustomElements().getValue(tag)!=null){
				    		  engName = row.getCustomElements().getValue(tag);
				    		  row2.getCustomElements().setValueLocal("EnglishFull", engName);
				    		  //System.out.print("English Name: " + row.getCustomElements().getValue(tag)+ "\t");
				    	  }
				    	  else if(i==5 && row.getCustomElements().getValue(tag)!=null){
				    		  //System.out.print("English Short Name: " + row.getCustomElements().getValue(tag)+ "\t");
				    		  engShortName = row.getCustomElements().getValue(tag);
				    		  row2.getCustomElements().setValueLocal("EnglishShort", engShortName);
				    	  }
				    	  
			    	  }
			    	  i++;			    	  
			    	  
			      }			      
			      if(!agentUri.equals("") && !shortName.equals("") && !engName.equals("") && !engShortName.equals("")){
			    	  //System.out.println(row2.getPlainTextContent().toString());
			    	  row2 = service.insert(listFeedUrl2, row2);
			    	  row.delete();
		    		  //System.out.println("hello!");
		    		  Resource agent = model.createResource(agentUri)
		    				  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "greekShortName"), shortName)
		    				  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "engShortName"), engShortName)
		    				  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "engName"), engName);
		    	  if(!category.equals(""))
		    		  agent.addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "category"), category);		    				  		  
		    	  }			      
			      /*System.out.println(agentUri);
		    	  System.out.println(shortName);
		    	  System.out.println(engName);
		    	  System.out.println(engShortName);*/
			      //System.out.println();
			    }
			   // model.createResource(payerUri).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "greekShortName"), greekAbbrev)
			//	  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "engShortName"), englishAbbrev)
			//	  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "engName"), englishFull)
			//	  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "category"), category);
			  model.close();			  
			}
			catch(Exception e){e.printStackTrace();}
	}
	
	public void convertToRDFLocally(){
				
		Model model = ModelFactory.createDefaultModel();					
		try{
			SpreadsheetService service =
			        new SpreadsheetService("MySpreadsheetIntegration-v1");		
			service.setUserCredentials("m.meimaris@gmail.com", "aliceson");
			    // TODO: Authorize the service object for a specific user (see other sections)

			    // Define the URL to request.  This should never change.
			    URL SPREADSHEET_FEED_URL = new URL(
			        "https://spreadsheets.google.com/feeds/spreadsheets/private/full");

			    // Make a request to the API and get all spreadsheets.
			    SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
			        SpreadsheetFeed.class);
			    List<SpreadsheetEntry> spreadsheets = feed.getEntries();

			    if (spreadsheets.size() == 0) {
			      // TODO: There were no spreadsheets, act accordingly.
			    }

			    // TODO: Choose a spreadsheet more intelligently based on your
			    // app's needs.
			    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
			    //System.out.println(spreadsheet.getTitle().getPlainText());

			    // Get the first worksheet of the first spreadsheet.
			    // TODO: Choose a worksheet more intelligently based on your
			    // app's needs.
			    WorksheetFeed worksheetFeed = service.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
			    List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
			    WorksheetEntry worksheet = worksheets.get(0);
			    WorksheetEntry worksheet2 = worksheets.get(1);			    

			 // Fetch the list feed of the worksheet.
			    URL listFeedUrl = worksheet.getListFeedUrl();
			    URL listFeedUrl2 = worksheet2.getListFeedUrl();
			    ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
			    ListFeed listFeed2 = service.getFeed(listFeedUrl2, ListFeed.class);
			    								
			    // Iterate through each row, printing its cell values.
			    for (ListEntry row : listFeed.getEntries()) {
			      // Print the first column's cell value
			      //System.out.print(row.getTitle().getPlainText() + "\t");
			      // Iterate over the remaining columns, and print each cell value
			      int i = 0;			      			      
			      String agentUri = "", shortName = "", engName = "", engShortName = "", category = "", fullName = "";
			      int flag = 1;	
			      //ListEntry row2 = new ListEntry();	
			      for (String tag : row.getCustomElements().getTags()) {	
			    	  //System.out.println(flag + " " + i);
			    	  if(i==0 && row.getCustomElements().getValue(tag)!=null){
			    		  agentUri = row.getCustomElements().getValue(tag);			    		  			    		  			    		  
			    	  }
			    	  if(flag==1){
			    		  //System.out.println("flag = 1 ");			    		  
			    		  if(i==1 && row.getCustomElements().getValue(tag)!=null){
				    		  fullName = row.getCustomElements().getValue(tag);				    		  
				    		  //row2.getCustomElements().setValueLocal("GreekFull", fullName);				    		  
				    		  //System.out.print("Greek Short Name: " + row.getCustomElements().getValue(tag) + "\t");
				    		  //String greekAbbrev = row.getCustomElements().getValue(tag);
				    	  }
			    		  else if(i==2 && row.getCustomElements().getValue(tag)!=null){
				    		  shortName = row.getCustomElements().getValue(tag);				    		  
				    		  //row2.getCustomElements().setValueLocal("GreekShort", shortName);				    		  
				    		  //System.out.print("Greek Short Name: " + row.getCustomElements().getValue(tag) + "\t");
				    		  //String greekAbbrev = row.getCustomElements().getValue(tag);
				    	  }
				    	  else if(i==3 && row.getCustomElements().getValue(tag)!=null){
				    		  category = row.getCustomElements().getValue(tag);
				    		  //row2.getCustomElements().setValueLocal("Category", category);
				    		  //String category = row.getCustomElements().getValue(tag);
				    		  //System.out.print("Category: " + row.getCustomElements().getValue(tag)+ "\t");
				    	  }
				    	  else if(i==4 && row.getCustomElements().getValue(tag)!=null){
				    		  engName = row.getCustomElements().getValue(tag);
				    		 //row2.getCustomElements().setValueLocal("EnglishFull", engName);
				    		  //System.out.print("English Name: " + row.getCustomElements().getValue(tag)+ "\t");
				    	  }
				    	  else if(i==5 && row.getCustomElements().getValue(tag)!=null){
				    		  //System.out.print("English Short Name: " + row.getCustomElements().getValue(tag)+ "\t");
				    		  engShortName = row.getCustomElements().getValue(tag);
				    		  //row2.getCustomElements().setValueLocal("EnglishShort", engShortName);
				    	  }
				    	  
			    	  }
			    	  i++;			    	  
			    	  
			      }			      
			      if(!agentUri.equals("") && !shortName.equals("") && !engName.equals("") && !engShortName.equals("")){
			    	  //System.out.println(row2.getPlainTextContent().toString());
			    	  //row2 = service.insert(listFeedUrl2, row2);
			    	  //row.delete();
		    		  //System.out.println("hello!");
		    		  Resource agent = model.createResource(agentUri)
		    				  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "greekShortName"), shortName)
		    				  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "engShortName"), engShortName)
		    				  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "engName"), engName);
		    	  if(!category.equals(""))
		    		  agent.addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "category"), category);		    				  		  
		    	  }			      
			      /*System.out.println(agentUri);
		    	  System.out.println(shortName);
		    	  System.out.println(engName);
		    	  System.out.println(engShortName);*/
			      //System.out.println();
			    }
			   // model.createResource(payerUri).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "greekShortName"), greekAbbrev)
			//	  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "engShortName"), englishAbbrev)
			//	  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "engName"), englishFull)
			//	  .addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#" + "category"), category);
			    
			    try {
					FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/nicknames-second.rdf");
					model.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/nicknames-second.rdf");
			    }
			    catch(Exception e){e.printStackTrace();}
			  model.close();			  
			}
			catch(Exception e){e.printStackTrace();}
	}
	
	public String getAgentNames(String agent){
		
		Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");		
		VirtGraph graph = (VirtGraph) model.getGraph();
		String nameS="";
		String nameQuery = SPARQLQueries.nameQuery(agent.toString());	
		//System.out.println(nameQuery);
	   	VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory.create (nameQuery, graph);	   	
	   	ResultSet nameResults = vqeNames.execSelect();	 		    
	   	while(nameResults.hasNext()){
	   		QuerySolution nameRs = nameResults.next();
	   		Literal gsisName = nameRs.getLiteral("gsisName"), 
	   				name = nameRs.getLiteral("name");	   		
	   		
	   		if(gsisName!=null && !gsisName.equals("Null")){			 		   			
	   			nameS = gsisName.getString();
	   		}
	   		else{			 		   			
	   			nameS = name.getString();
	   		}					   		
	   	}
	   	vqeNames.close();
	   	return nameS;
}
	
public String getAgentNamesByAfm(String afm){
		
		String nameS = "", shortNameS = "", engNameS = "", engShortNameS = "";
		Model model = VirtModel.openDatabaseModel(DailyRoutineHandler.graphName, DailyRoutineHandler.connectionString, "marios", "dirtymarios");		
		VirtGraph graph = (VirtGraph) model.getGraph();
		String query = SPARQLQueries.prefixes + "SELECT ?name FROM <"+DailyRoutineHandler.graphName+"> WHERE {?agent psgr:afm \""+afm+"\" . ?agent psgr:paymentAgentName ?name .}LIMIT 1";
		//System.out.println(query);
		VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory.create(query, graph);	   	
	   	ResultSet nameResults = vqeNames.execSelect();
	   	while(nameResults.hasNext()){	   		
	   		QuerySolution rs = nameResults.nextSolution();	   		
	   		nameS = rs.getLiteral("name").getString();	   			   			   				 		   		
	   	}
	   	vqeNames.close();	   	
	   	return nameS;
	   	
	}

}
