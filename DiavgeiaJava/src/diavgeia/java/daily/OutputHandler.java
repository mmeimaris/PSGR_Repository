package diavgeia.java.daily;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

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
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import diavgeia.java.ontology.Ontology;

public class OutputHandler {

	VirtGraph graph;	
	ArrayList<String> lastDays, lastWeeks, lastMonths, lastYears;
	int limit = DailyRoutineHandler.outputDateLimit;
	static Integer todayDay, oldestDay,
				   todayWeek, oldestWeek,
				   todayMonth, oldestMonth,
				   todayYear, oldestYear;
	static String todayString, topAgentsString ;	
	HashMap<String, String> shortNamesMap = new HashMap<String, String>();		
	String periodToDate = "", rootFolder;	
	ArrayList<String[]> payersList = new ArrayList<String[]>();
	ArrayList<String[]> payeesList = new ArrayList<String[]>();	
	Color[] color = {Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, 
			Color.PINK, Color.YELLOW, Color.RED, Color.CYAN, Color.LIGHT_GRAY, 
			Color.BLACK, Color.DARK_GRAY};	
	NicknameHandler nicknames = new NicknameHandler();
	static GreeklishFactory greeklish = new GreeklishFactory();
	
	public OutputHandler(){
		
		Model model = VirtModel.openDatabaseModel(null, DailyRoutineHandler.connectionString, "marios", "dirtymarios");
		graph = (VirtGraph) model.getGraph();			
				
		SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		//String dayString = dayFormat.format(new Date());
		String dayString = "2012-06-05";
		String monthString = dayString.substring(0,7);				
		String yearString = dayString.substring(0, 4);		
		String weekString = yearString+"-w-"+DailyRoutineHandler.getWeek(dayString);	
		/*dayString = "2012-03-10";
		weekString = "2012-w-10";
		monthString = "2012-03";
		yearString = "2012";*/
		
		String today = dayFormat.format(new Date());		
		todayDay = Integer.parseInt(today.replace("-", ""));
		String todayW = DailyRoutineHandler.getWeek(today);		
		todayMonth = Integer.parseInt(today.substring(0,7).replace("-", ""));
		todayYear = Integer.parseInt(today.substring(0,4).replace("-", ""));
		todayWeek = Integer.parseInt(todayYear+todayW);
		
		oldestDay = 20101001;
		oldestWeek = 201044;
		oldestMonth = 201010;
		oldestYear = 2010;
		SimpleDateFormat dayFormat2 = new SimpleDateFormat("dd/MM/yyyy");
		periodToDate = "01/10/2010 - " + dayFormat2.format(new Date());
		System.out.println("Period to date: " + periodToDate);
		
		String[][] parameters = new String[][] {{dayString, DailyRoutineHandler.dayGraphName, SPARQLQueries.daySelector, "day" }, 
												{weekString, DailyRoutineHandler.weekGraphName , SPARQLQueries.weekSelector, "week"}, 
												{monthString, DailyRoutineHandler.monthGraphName , SPARQLQueries.monthSelector, "month"},
												{yearString, DailyRoutineHandler.yearGraphName , SPARQLQueries.yearSelector, "year"}};
		//String[][] parameters2 = new String[][] {{weekString, DailyRoutineHandler.weekGraphName , SPARQLQueries.weekSelector, "week"}};
		String[][] parameters2 = new String[][] {{weekString, DailyRoutineHandler.weekGraphName , SPARQLQueries.weekSelector, "week"}};
		
		//Date date = new Date(2012-1900, 05, 22);
		int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
		Date todayDate = new Date();
		Date date = new Date(todayDate.getTime() - MILLIS_IN_DAY);
		//Date date = new Date(2012-1900, 8, 14);
		System.out.println(date.toGMTString());
		//Date date = new Date();
		lastDays = getLastIPeriods(date, "day", limit);
		System.out.println(Arrays.toString(lastDays.toArray()));
		lastWeeks = getLastIPeriods(date, "week", limit);
		lastMonths = getLastIPeriods(date, "month", limit);	
		lastYears = getLastIPeriods(date, "year", limit);
		
		rootFolder = createFolderStructure();	
		//topPayerBubblesJSON();
		//aggregateAmountPerCpvJSON(parameters2); //exw valei parameters2
		//topPayeeProfile();
		//topPayerProfile();
		//mainBubble();
		System.out.println(new Date());
		System.out.println("1");
		topPaymentsTimeframeJSON();
		System.out.println(new Date());
		System.out.println("2");
		topPaymentsOverallJSON();
		System.out.println(new Date());
		System.out.println("3");
		topCPVTimeframeJSON();
		System.out.println(new Date());
		System.out.println("4");
		topCPVOverallJSON();
		System.out.println(new Date());
		System.out.println("5");
		topPayersTimeframeJSON();
		System.out.println(new Date());
		System.out.println("6");
		topPayersOverallJSON();
		topPayerBubblesJSON();
		System.out.println(new Date());
		System.out.println("7");
		topPayeesTimeframeJSON();
		System.out.println(new Date());
		System.out.println("8");
		topPayeesOverallJSON();
		topPayeeBubblesJSON();
		System.out.println("9");
		aggregateAmountCounterJSON();
		System.out.println("10");
		aggregateAmountPerTimeframeJSON(parameters2);
		System.out.println("11");
		aggregateAmountPerCpvJSON(parameters2); //exw valei parameters2

		writeBubbleNames();
		//handleNicknamesSpreadsheet();		
	}
	
	public void writeBubbleNames(){
		if(!payersList.isEmpty() && !payeesList.isEmpty())
		try{
			  // Create file 
			  JsonWriter jw = new JsonWriter();
			  FileWriter fstream = new FileWriter(rootFolder + "/bubbles/titles.json");
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(jw.jsonPayersListWriter(payersList) + jw.jsonPayeesListWriter(payeesList));
			  //Close the output stream
			  out.close();
			  }
		catch (Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
			  }	
	}
	
	public void handleNicknamesSpreadsheet(){
		Iterator it = shortNamesMap.keySet().iterator();		
		if(!shortNamesMap.isEmpty())
			nicknames.deletePrevious();
		while(it.hasNext()){
			String key = (String) it.next();				
			nicknames.addToSpreadsheet(new String[] {key, shortNamesMap.get(key)});
		}
	}
	
	public String createFolderStructure(){
		
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
		String rootFolder = DailyRoutineHandler.jsonFolder + dayFormat.format(new Date()); 
		new File(rootFolder + "/toppayments").mkdirs();
		new File(rootFolder + "/toppayers").mkdirs();
		new File(rootFolder + "/toppayees").mkdirs();
		new File(rootFolder + "/topcpv").mkdirs();
		new File(rootFolder + "/45cpv").mkdirs();
		new File(rootFolder + "/paymentstimeplot").mkdirs();
		new File(rootFolder + "/counter").mkdirs();
		new File(rootFolder + "/bubbles").mkdirs();
		new File(rootFolder + "/tables").mkdirs();
		new File(rootFolder + "/payertabs").mkdirs();
		new File(rootFolder + "/payeetabs").mkdirs();
		return rootFolder;
	}
	
	public void aggregateAmountCounterJSON(){
		
		Long todayAmount = new Long(0), aggregate = new Long(0);
		String query = SPARQLQueries.ruleSet + SPARQLQueries.prefixes + 
					   "SELECT ?am FROM <" + DailyRoutineHandler.dayGraphName + "> WHERE {" +
					   "?day a psgr:Day . ?day psgr:date \"" + lastDays.get(0) + "\" ." +
					   "?day psgr:aggregatePaymentAmount ?am .}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();			
		while (results.hasNext()) {
		 	   QuerySolution rs = results.nextSolution();
		 	   Literal amount = rs.getLiteral("am");
		 	   todayAmount = amount.getLong();
		 	 } 	   
		vqe.close();
		String query2 = SPARQLQueries.ruleSet + SPARQLQueries.prefixes + 
				   "SELECT ?am FROM <" + DailyRoutineHandler.overallGraphName + "> WHERE {" +				   
				   "?day psgr:aggregatePaymentAmount ?am .} LIMIT 1";
		VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (query2, graph);
		ResultSet results2 = vqe2.execSelect();			
		while (results2.hasNext()) {
			QuerySolution rs2 = results2.nextSolution();
	 	   	Literal amount2 = rs2.getLiteral("am");
	 	   	aggregate = amount2.getLong();
	 	 } 	   
		vqe2.close();
		Long initial = (aggregate-todayAmount);
		try{
			  // Create file 			  
			  FileWriter fstream = new FileWriter(rootFolder + "/counter/counter.txt");
			  BufferedWriter out = new BufferedWriter(fstream);
			  
			  out.write("initial: " +  initial.toString() + "\n" + " final: " + aggregate.toString());
			  //Close the output stream
			  out.close();
			  }catch (Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
			  }	
		
}

	
	public void topPaymentsTimeframeJSON(){
		
		ArrayList<String> dates = new ArrayList<String>();
		String timeframe = "" , selector = "", title = "", engTitle = "";		
		for(int i=0;i<4;i++){			
			title = StaticStrings.paymentTitles[i];
			engTitle = StaticStrings.engPaymentTitles[i];
			
			if(i==0){
				timeframe = "day"; 
				selector = SPARQLQueries.daySelector;
				dates = lastDays;
				
			}
			else if(i==1){
				timeframe = "week";
				selector = SPARQLQueries.weekSelector;
				dates = lastWeeks;				
			}
			else if(i==2){
				timeframe = "month";
				selector = SPARQLQueries.monthSelector;
				dates = lastMonths;				
			}
			else if(i==3){
				timeframe = "year";
				selector = SPARQLQueries.yearSelector;
				dates = lastYears;				
			}
			String[] dateForWriter = {"", ""};
			Iterator datesIt = dates.iterator();			
			ArrayList<String[]> writerList = new ArrayList<String[]>();
			while(datesIt.hasNext()){
				String dateString = (String) datesIt.next();					
				dateForWriter = getTimeInterval(dateString, timeframe);				
				String query = SPARQLQueries.topPaymentsDescription(dateString, selector , timeframe);	
				//System.out.println(query);
				VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
				ResultSet results = vqe.execSelect();
				Integer count = 1;						
				while (results.hasNext()) {					   
				 	   QuerySolution rs = results.nextSolution();
				 	   RDFNode amount = rs.get("amount");				 	  
				 	   RDFNode payment = rs.get("topPayment");				 	   
				 	   String cpvQuery = SPARQLQueries.cpvSecondaryQuery(payment.toString());		 				 	    
			 		   VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
			 		   ResultSet cpvResults = vqeCpv.execSelect();
			 		   while(cpvResults.hasNext()){
			 		   		QuerySolution cpvRs = cpvResults.next();
			 		   		RDFNode cpvCode = cpvRs.get("cpvCode");
			 		   		RDFNode cpvSubject = cpvRs.get("cpvSubject");		 		   					 		   
			 		   		String[] writerArray = {amount.toString().substring(0,amount.toString().indexOf('^')), cpvSubject.toString(), cpvSubject.toString(), cpvSubject.toString(), cpvSubject.toString()};
			 		   		writerList.add(writerArray);
			 		   	}
			 		   vqeCpv.close();
				 	   count++;
				 	 } 	   
				vqe.close();
			}			
			 try{
				  // Create file 
				  JsonWriter jw = new JsonWriter();
				  FileWriter fstream = new FileWriter(rootFolder + "/toppayments/topPayments"+timeframe+".json");
				  BufferedWriter out = new BufferedWriter(fstream);
				  out.write(jw.jsonBarWriter(writerList, "topPayments"+timeframe, dateForWriter, title, engTitle));
				  //Close the output stream
				  out.close();
			 }
			 catch (Exception e){//Catch exception if any
					  System.err.println("Error: " + e.getMessage());
			}										
		}
				
	}
	
	public void topPaymentsOverallJSON(){
										
		String query = SPARQLQueries.topPaymentsDescription("", "", "overall");			
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();		
		Integer count = 1;
		ArrayList<String[]> writerList = new ArrayList<String[]>();
		while (results.hasNext()) {
		 	   QuerySolution rs = results.nextSolution();
		 	   RDFNode amount = rs.get("amount");
		 	   //RDFNode cpv = rs.get("subject");
		 	   RDFNode payment = rs.get("topPayment");		 	   
		 	   String cpvQuery = SPARQLQueries.cpvSecondaryQuery(payment.toString());		 		   	
	 		   VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
	 		   ResultSet cpvResults = vqeCpv.execSelect();
	 		   while(cpvResults.hasNext()){
	 		   		QuerySolution cpvRs = cpvResults.next();
	 		   		RDFNode cpvCode = cpvRs.get("cpvCode");
	 		   		RDFNode cpvSubject = cpvRs.get("cpvSubject");		 		   			 		   
		 		   	String[] writerArray = {amount.toString().substring(0,amount.toString().indexOf('^')), cpvSubject.toString(),cpvSubject.toString(),cpvSubject.toString(),cpvSubject.toString()};
	 		   		writerList.add(writerArray);
	 		   	}
	 		   vqeCpv.close();
		 	   count++;

		 	 } 	   
		vqe.close();		
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(rootFolder + "/toppayments/topPaymentsOverall.json");
			  BufferedWriter out = new BufferedWriter(fstream);
			  JsonWriter jw = new JsonWriter();
			  out.write(jw.jsonBarWriter(writerList, "topPaymentsOverall", new String[] {"Συνολικά", "Overall"}, StaticStrings.paymentTitles[4], StaticStrings.engPaymentTitles[4]));
			  //Close the output stream
			  out.close();
			  }catch (Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
			  }			
	}
	
	
	public void topCPVTimeframeJSON(){
		
		ArrayList<String> dates = new ArrayList<String>();
		String timeframe = "" , selector = "", graphS = "", totalQuerySelector = "", title = "", engTitle="";		
		for(int i=0;i<4;i++){			
			title = StaticStrings.cpvTitles[i];
			engTitle = StaticStrings.engCpvTitles[i];			
			if(i==0){
				timeframe = "day"; 
				selector = SPARQLQueries.daySelector;
				dates = lastDays;
				graphS = DailyRoutineHandler.dayGraphName;
				totalQuerySelector = "days";				
			}
			else if(i==1){
				timeframe = "week";
				selector = SPARQLQueries.weekSelector;
				dates = lastWeeks;
				graphS = DailyRoutineHandler.weekGraphName;
				totalQuerySelector = "weeks";
			}
			else if(i==2){
				timeframe = "month";
				selector = SPARQLQueries.monthSelector;
				dates = lastMonths;
				graphS = DailyRoutineHandler.monthGraphName;
				totalQuerySelector = "months";
			}
			else if(i==3){
				timeframe = "year";
				selector = SPARQLQueries.yearSelector;
				dates = lastYears;
				graphS = DailyRoutineHandler.yearGraphName;
				totalQuerySelector = "years";
			}
			
			String[] dateForWriter = {"", ""};
			Double sumD = new Double(0);
			Iterator datesIt = dates.iterator();			
			ArrayList<String[]> writerList = new ArrayList<String[]>();
			while(datesIt.hasNext()){
				String dateString = (String) datesIt.next();
				dateForWriter = getTimeInterval(dateString, timeframe);				
				String query = SPARQLQueries.topCpvDescription(dateString, selector , timeframe, "10");				
				//System.out.println(query);
				VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
				ResultSet results = vqe.execSelect();
				Integer count = 1;						
				while (results.hasNext()) {
			 	   	QuerySolution rs = results.nextSolution();
			 	   	RDFNode cpv = rs.get("cpvDiv");			 	   	
			 	   	Literal sum = rs.getLiteral("sumString");
			 	   	sumD += sum.getDouble();
			 	   	RDFNode paymentCount = rs.get("count");
			 	   	String cpvQuery = SPARQLQueries.cpvQuery(cpv.toString());			 	    
		 		   	VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
		 		   	ResultSet cpvResults = vqeCpv.execSelect();
		 		   	while(cpvResults.hasNext()){
		 		   		QuerySolution cpvRs = cpvResults.next();
		 		   		RDFNode cpvCode = cpvRs.get("cpvCode");
		 		   		Literal cpvSubject = cpvRs.getLiteral("cpvSubject");
		 		   		Literal cpvEngSubject = cpvRs.getLiteral("cpvEngSubject");	
		 		   		Literal cpvShort = cpvRs.getLiteral("shortName");
		 		   		Literal cpvEngShort = cpvRs.getLiteral("engShortName");		 		   		
		 		   		String[] writerArray = {sum.toString().substring(0,sum.toString().indexOf('^')), cpvSubject.getString(), cpvShort.getString(), cpvEngSubject.getString(), cpvEngShort.getString()};
		 		   		writerList.add(writerArray);
		 		   	}
		 		   	vqeCpv.close();
			 	   	count++;
			 	} 	   
				vqe.close();
				String restQuery = SPARQLQueries.prefixes + "SELECT (sum(xsd:decimal(?am)) as ?sum) " +
						"FROM <" +DailyRoutineHandler.graphName + "> " +
						"FROM <" +DailyRoutineHandler.falseGraphName + "> " +
						"WHERE {" + 
						"?payment psgr:paymentAmount ?am ." + 
						"?decision psgr:refersTo ?payment . " + selector +  dateString + "\" . }" ;
				//System.out.println(restQuery);
				VirtuosoQueryExecution vqeRest = VirtuosoQueryExecutionFactory.create (restQuery, graph);
				ResultSet resultsRest = vqeRest.execSelect();
				Double rest = new Double(0);
				while (resultsRest.hasNext()) {
			 	   	QuerySolution rsRest = resultsRest.nextSolution();
			 	   	Literal restSum = rsRest.getLiteral("sum");
			 	    rest = restSum.getDouble();
				}
				vqeRest.close();
				Double restDouble = Math.abs(rest-sumD);				
				String[] writerArray = {restDouble.toString(), "ΥΠΟΛΟΙΠΑ", "Λοιπά", "Rest", "Rest"};
				writerList.add(writerArray);				
			}			
			
			try{
				  // Create file 
				  FileWriter fstreamBar = new FileWriter(rootFolder + "/topcpv/topCPV"+timeframe+"Bar.json");				  
				  BufferedWriter outBar = new BufferedWriter(fstreamBar);				  
				  JsonWriter jw = new JsonWriter();
				  outBar.write(jw.jsonBarWriter(writerList, "topCPV"+timeframe, dateForWriter, title, engTitle));
				  outBar.close();				 
			}
			catch (Exception e){//Catch exception if any
					  System.err.println("Error: " + e.getMessage());
		    }					
		}				
	}


	public void topCPVOverallJSON(){
			
		String query = SPARQLQueries.topCpvDescription("", "", "overall", "10");		
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();		
		Integer count = 1;
		ArrayList<String[]> writerList = new ArrayList<String[]>();
		Double sumD = new Double(0);
		while (results.hasNext()) {
		 	   QuerySolution rs = results.nextSolution();
		 	   //RDFNode cpvCode = rs.get("cpvCode");
		 	   //RDFNode cpvSubject = rs.get("cpvSubject");
		 	   RDFNode cpv = rs.get("cpvDiv");
		 	   Literal sum = rs.getLiteral("sumString");
		 	   sumD += sum.getDouble();
		 	   RDFNode paymentCount = rs.get("count");
		 	   String cpvQuery = SPARQLQueries.cpvQuery(cpv.toString());		 		   	
	 		   VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
	 		   ResultSet cpvResults = vqeCpv.execSelect();
	 		   while(cpvResults.hasNext()){
	 		   		QuerySolution cpvRs = cpvResults.next();
	 		   		RDFNode cpvCode = cpvRs.get("cpvCode");
	 		   		Literal cpvSubject = cpvRs.getLiteral("cpvSubject");
	 		   		Literal cpvEngSubject = cpvRs.getLiteral("cpvEngSubject");
	 		   		Literal cpvShort = cpvRs.getLiteral("shortName");
	 		   		Literal cpvEngShort = cpvRs.getLiteral("engShortName");	 		   
		 		   	String[] writerArray = {sum.toString().substring(0,sum.toString().indexOf('^')), cpvSubject.getString(), cpvShort.getString(), cpvEngSubject.getString(), cpvEngShort.getString()};
	 		   		writerList.add(writerArray);
	 		   	}
	 		   	vqeCpv.close();
		 	   	count++;
		 	 } 	  
		String restQuery = SPARQLQueries.prefixes + "SELECT (sum(xsd:decimal(?am)) as ?sum) " +
				"FROM <" +DailyRoutineHandler.graphName + "> " +
				"FROM <" +DailyRoutineHandler.falseGraphName + "> " +
				"WHERE {" + 
				"?payment psgr:paymentAmount ?am ." + 
				 "}" ;
		VirtuosoQueryExecution vqeRest = VirtuosoQueryExecutionFactory.create (restQuery, graph);
		ResultSet resultsRest = vqeRest.execSelect();
		Double rest = new Double(0);
		while (resultsRest.hasNext()) {
	 	   	QuerySolution rsRest = resultsRest.nextSolution();
	 	   	Literal restSum = rsRest.getLiteral("sum");
	 	    rest = restSum.getDouble();
		}
		vqeRest.close();
		Double restDouble = Math.abs(rest-sumD);
		String[] writerArray = {restDouble.toString(), "ΥΠΟΛΟΙΠΑ", "Λοιπά", "REST", "Rest"};
		writerList.add(writerArray);
		vqe.close();		
		try{
			  // Create file 
			  FileWriter fstreamBar = new FileWriter(rootFolder + "/topcpv/topCPVOverallBar.json");			  
			  BufferedWriter outBar = new BufferedWriter(fstreamBar);			 
			  JsonWriter jw = new JsonWriter();
			  outBar.write(jw.jsonBarWriter(writerList, "topPaymentsOverallBar", new String[] {"Συνολικά", "Overall"}, StaticStrings.cpvTitles[4], StaticStrings.engCpvTitles[4]));
			  //Close the output stream
			  outBar.close();			  
			  }catch (Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
			  }					
	}
	
	
	public void topPayersTimeframeJSON(){
		
		ArrayList<String> dates = new ArrayList<String>();
		String timeframe = "" , selector = "", title = "", engTitle="";		
		for(int i=0;i<4;i++){			
			title = StaticStrings.payerTitles[i];
			engTitle = StaticStrings.engPayerTitles[i];
			
			if(i==0){
				timeframe = "day"; 
				selector = SPARQLQueries.daySelector;
				dates = lastDays;
			}
			else if(i==1){
				timeframe = "week";
				selector = SPARQLQueries.weekSelector;
				dates = lastWeeks;
			}
			else if(i==2){
				timeframe = "month";
				selector = SPARQLQueries.monthSelector;
				dates = lastMonths;
			}
			else if(i==3){
				timeframe = "year";
				selector = SPARQLQueries.yearSelector;
				dates = lastYears;
			}
			String[] dateForWriter = {"", ""};
			Iterator datesIt = dates.iterator();			
			ArrayList<String[]> writerList = new ArrayList<String[]>();			
			while(datesIt.hasNext()){
				String dateString = (String) datesIt.next();					
				dateForWriter = getTimeInterval(dateString, timeframe);				
				String query = SPARQLQueries.topPayersDescription(dateString, selector , timeframe, "10");		
				//System.out.println(query);
				VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
				ResultSet results = vqe.execSelect();
				Integer count = 1;		
				while (results.hasNext()) {
				 	   QuerySolution rs = results.nextSolution();				 	   
				 	   RDFNode payer = rs.get("payer"), 
				 			   sum = rs.get("sumString"), 
				 	   		   avg = rs.get("avgString"), 
				 	   	       paymentCount = rs.get("count");				 	   	
				 	   String[] names = getAgentNames(payer.toString());
				 	   String nameS = names[0], engNameS = names[2], shortNameS = names[1], engShortNameS = names[3];
				 	   String[] writerArray = {sum.toString().substring(0,sum.toString().indexOf('^')), nameS, shortNameS, engNameS, engShortNameS};
				 	   writerList.add(writerArray);
				 	   count++;
				 	 } 	   
				vqe.close();				
			}			
			try{
				  // Create file 
				  FileWriter fstream = new FileWriter(rootFolder + "/toppayers/topPayers"+timeframe+".json");
				  BufferedWriter out = new BufferedWriter(fstream);
				  JsonWriter jw = new JsonWriter();
				  out.write(jw.jsonBarWriter(writerList, "topPayers"+timeframe, dateForWriter, title, engTitle));
				  //Close the output stream
				  out.close();
				  }
			catch (Exception e){//Catch exception if any
					  System.err.println("Error: " + e.getMessage());
				  }												
		}			
	}
	
	
	
	public void topPayersOverallJSON(){
		
		//Prepei na ektelestei gia diaforetika timeframes me katallhlo format!!!						
		String query = SPARQLQueries.topPayersDescription("", "", "overall", "10");		 		
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();		
		ArrayList<String[]> writerList = new ArrayList<String[]>();		
		Integer count = 1;				
		while (results.hasNext()) {			   
		 	   QuerySolution rs = results.nextSolution();		 	   
		 	   RDFNode payer = rs.get("payer");		 	   
		 	   Literal sum = rs.getLiteral("sumString");
		 	   String[] names = getAgentNames(payer.toString()); 
		 	   String nameS = names[0], 
		 		    	   engNameS = names[2], 
		 		    	   cpvS = "", 
		 		    	   topPayeeS="", 
		 		    	   freqPayeeS="", 
		 		    	   shortNameS = names[1], 
		 		    	   engShortNameS = names[3];	   	
		 	   String[] writerArray = {sum.toString().substring(0,sum.toString().indexOf('^')), nameS, shortNameS, engNameS, engShortNameS, cpvS, topPayeeS, freqPayeeS };
		 	   writerList.add(writerArray);		 	   
		 	   count++;
		 	 } 	   
		vqe.close();		
		try{
			  // Create file 
			  FileWriter fstream = new FileWriter(rootFolder + "/toppayers/topPayersOverall.json");
			  BufferedWriter out = new BufferedWriter(fstream);
			  JsonWriter jw = new JsonWriter();
			  out.write(jw.jsonBarWriter(writerList, "topPayersOverall", new String[] {"Συνολικά", "Overall"}, StaticStrings.payerTitles[4], StaticStrings.engPayerTitles[4]));
			  //Close the output stream
			  topAgentsString = jw.jsonTopPayersTableWriter(writerList);
			  out.close();
			  }
		catch (Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
		}							
	}	
		
		
		public void topPayerBubblesJSON(){
			
			String query = SPARQLQueries.topPayersDescriptionBubbles("", "", "overall", "20");		 			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();			
			ArrayList<String[]> writerList = new ArrayList<String[]>();		
			Integer count = 1;
			while (results.hasNext()) {
				   ArrayList<String[]> bubbleWriterList = new ArrayList<String[]>();
			 	   QuerySolution rs = results.nextSolution();		 	   
			 	   RDFNode payer = rs.get("payer");		 			 	   
			 	   Literal sum = rs.getLiteral("sumString");
			 	   String[] names= getAgentNames(payer.toString());
			 	   String nameS = names[0], 
			 				  engNameS = names[2], 
			 				  cpvS = "", 
			 				  topPayeeS="", 
			 				  freqPayeeS="", 
			 				  shortNameS = names[1], 
			 				  engShortNameS = names[3];
			 	   payersList.add(names);		
			 	   String totalAmountQuery = SPARQLQueries.totalAmountForPayer(payer.toString());
			 	   long totalSum = 0;			 		   		 		   
			 	   VirtuosoQueryExecution vqeTotalAmount = VirtuosoQueryExecutionFactory.create (totalAmountQuery, graph);
			 	   ResultSet totalAmountResults = vqeTotalAmount.execSelect();
			 	   while(totalAmountResults.hasNext()){
						QuerySolution totalAmountRs = totalAmountResults.nextSolution();
						Literal totalAmount = totalAmountRs.getLiteral("sum");
						totalSum = totalAmount.getLong();
			 	   }
			 	   vqeTotalAmount.close();
			 	   String topPayeesForPayer = SPARQLQueries.topPayeesForPayer(payer.toString(), "9");	
			 	   //System.out.println(topPayeesForPayer);
			 	   VirtuosoQueryExecution vqeTopPayees = VirtuosoQueryExecutionFactory.create (topPayeesForPayer, graph);
			 	   ResultSet topPayeeResults = vqeTopPayees.execSelect();
			 	   int payeeCount = 0;
			 	   long payeeSum = 0;
			 	   HashMap<String, String[]> cpvMap = new HashMap<String, String[]>();
			 	   int flag = 0;
			 	   while(topPayeeResults.hasNext()){
			 		   flag =1 ;
			 		   QuerySolution topPayeeRs = topPayeeResults.next();								
			 		   RDFNode topPayee = topPayeeRs.get("payee");								
			 		   Literal payeeAmount = topPayeeRs.getLiteral("amount");
			 		   payeeSum += payeeAmount.getLong();
			 		   String[] payeeNames = getAgentNames(topPayee.toString());
			 		   String shortNamePayee = payeeNames[1], 
			 				  engShortNamePayee = payeeNames[3], 
			 				  engNamePayee = payeeNames[2], 
			 				  namePayee = payeeNames[0];			 		   						 		   
			 		   String topCpvForPayee = SPARQLQueries.topCpvForPayerPayee(payer.toString(), topPayee.toString().replace(" ", ""));
			 		   /*System.out.println("<" + topPayee.toString()+">");
			 		   System.out.println(topCpvForPayee);*/
			 		   VirtuosoQueryExecution vqeTopCpvForPayee = VirtuosoQueryExecutionFactory.create(topCpvForPayee, graph);							
			 		   ResultSet topCpvResults = vqeTopCpvForPayee.execSelect();			
			 		   String cgs = "", ces = "", csn = "", cesn = "", cpvCode = "", cpvCodeDiv = "";							
			 		   while(topCpvResults.hasNext()){
								QuerySolution topCpvRs = topCpvResults.nextSolution();											
								try{
									Literal cpvCodeDivLiteral = topCpvRs.getLiteral("cpvCodeDiv");
									cpvCodeDiv = cpvCodeDivLiteral.getString();
								}
								catch(Exception e){}
								Literal cpvCodeLiteral = topCpvRs.getLiteral("cpvCode");
								cpvCode = cpvCodeLiteral.getString();																						
							}
							CPVColours cpvColours = new CPVColours();	
							String colour = cpvColours.getColourFromCPV(cpvCodeDiv);
							VirtuosoQueryExecution vqeCpvDesc = VirtuosoQueryExecutionFactory.create(SPARQLQueries.cpvQuery(Ontology.instancePrefix + "cpvCodes/" + cpvCodeDiv), graph);						
							ResultSet cpvDescRes = vqeCpvDesc.execSelect();								
							while(cpvDescRes.hasNext()){
								QuerySolution cpvRs = cpvDescRes.nextSolution();
								try{
									Literal cpvGreekSubject = cpvRs.getLiteral("cpvSubject");
									cgs = cpvGreekSubject.getString();
									Literal cpvEngSubject = cpvRs.getLiteral("cpvEngSubject");
									ces = cpvEngSubject.getString();
								}catch(Exception e){}
							}
							vqeCpvDesc.close();
							if(colour==null){
								colour = cpvColours.getColourFromCPV(cpvCode);
								VirtuosoQueryExecution vqeCpvDesc2 = VirtuosoQueryExecutionFactory.create(SPARQLQueries.cpvQuery(Ontology.instancePrefix + "cpvCodes/" + cpvCode), graph);						
								ResultSet cpvDescRes2 = vqeCpvDesc2.execSelect();								
								while(cpvDescRes2.hasNext()){ //GAMW TO DIA
									QuerySolution cpvRs2 = cpvDescRes2.nextSolution();
									try{
										Literal cpvGreekSubject = cpvRs2.getLiteral("cpvSubject");
										cgs = cpvGreekSubject.getString();
										Literal cpvEngSubject = cpvRs2.getLiteral("cpvEngSubject");
										ces = cpvEngSubject.getString();
									}catch(Exception e){}
								}
								vqeCpvDesc2.close();					
							}
							cpvMap.put(colour, new String[] {cgs, ces, colour});							
							String[] bubbleWriterArray = {namePayee, payeeAmount.getString(), cgs, ces, csn, cesn, colour ,shortNamePayee, engNamePayee, engShortNamePayee};
							bubbleWriterList.add(bubbleWriterArray);	
							vqeTopCpvForPayee.close();
							payeeCount++;
						}
						vqeTopPayees.close();
						if(flag==0){
							String topPayeesForPayer2 = SPARQLQueries.prefixes + "SELECT distinct ?payee (sum(xsd:decimal(?am)) as ?amount) " + 
									"FROM <" + DailyRoutineHandler.graphName + "> WHERE {" + 
									"?payment psgr:payer <" + payer.toString() + "> ; psgr:payee ?payee ; psgr:paymentAmount ?am .} LIMIT 9";				 		    
				 		    VirtuosoQueryExecution vqeTopPayees2 = VirtuosoQueryExecutionFactory.create (topPayeesForPayer2, graph);
							ResultSet topPayeeResults2 = vqeTopPayees2.execSelect();
							while(topPayeeResults2.hasNext()){								
								QuerySolution topPayeeRs = topPayeeResults2.next();								
								RDFNode topPayee = topPayeeRs.get("payee");									
								Literal payeeAmount = topPayeeRs.getLiteral("amount");
								payeeSum += payeeAmount.getLong();													 		   
					 		   	String[] payeeNames = getAgentNames(topPayee.toString());
					 		   	String shortNamePayee = payeeNames[1], 
					 				  engShortNamePayee = payeeNames[3], 
					 				  engNamePayee = payeeNames[2], 
					 				  namePayee = payeeNames[0];							 		   
								String topCpvForPayee = SPARQLQueries.topCpvForPayerPayee(payer.toString(), topPayee.toString());							
								VirtuosoQueryExecution vqeTopCpvForPayee = VirtuosoQueryExecutionFactory.create(topCpvForPayee, graph);							
								ResultSet topCpvResults = vqeTopCpvForPayee.execSelect();			
								String cgs = "", ces = "", csn = "", cesn = "", cpvCode = "", cpvCodeDiv = "";							
								while(topCpvResults.hasNext()){
									QuerySolution topCpvRs = topCpvResults.nextSolution();
									try{
										Literal cpvCodeDivLiteral = topCpvRs.getLiteral("cpvCodeDiv");
										cpvCodeDiv = cpvCodeDivLiteral.getString();
									}catch(Exception e){}
									Literal cpvCodeLiteral = topCpvRs.getLiteral("cpvCode");
									cpvCode = cpvCodeLiteral.getString();
								}
								CPVColours cpvColours = new CPVColours();	
								String colour = cpvColours.getColourFromCPV(cpvCodeDiv);
								VirtuosoQueryExecution vqeCpvDesc = VirtuosoQueryExecutionFactory.create(SPARQLQueries.cpvQuery(Ontology.instancePrefix + "cpvCodes/" + cpvCodeDiv), graph);						
								ResultSet cpvDescRes = vqeCpvDesc.execSelect();								
								while(cpvDescRes.hasNext()){
									QuerySolution cpvRs = cpvDescRes.nextSolution();
									try{
										Literal cpvGreekSubject = cpvRs.getLiteral("cpvSubject");
										cgs = cpvGreekSubject.getString();
										Literal cpvEngSubject = cpvRs.getLiteral("cpvEngSubject");
										ces = cpvEngSubject.getString();
									}catch(Exception e){}
								}
								vqeCpvDesc.close();
								if(colour==null){
									colour = cpvColours.getColourFromCPV(cpvCode);
									VirtuosoQueryExecution vqeCpvDesc2 = VirtuosoQueryExecutionFactory.create(SPARQLQueries.cpvQuery(Ontology.instancePrefix + "cpvCodes/" + cpvCode), graph);						
									ResultSet cpvDescRes2 = vqeCpvDesc2.execSelect();								
									while(cpvDescRes2.hasNext()){ //GAMW TO DIA
										QuerySolution cpvRs2 = cpvDescRes2.nextSolution();
										try{
											Literal cpvGreekSubject = cpvRs2.getLiteral("cpvSubject");
											cgs = cpvGreekSubject.getString();
											Literal cpvEngSubject = cpvRs2.getLiteral("cpvEngSubject");
											ces = cpvEngSubject.getString();
										}catch(Exception e){}
									}
									vqeCpvDesc2.close();					
								}
								cpvMap.put(colour, new String[] {cgs, ces, colour});								
								String[] bubbleWriterArray = {namePayee, payeeAmount.getString(), cgs, ces, csn, cesn, colour ,shortNamePayee, engNamePayee, engShortNamePayee};
								bubbleWriterList.add(bubbleWriterArray);	
								vqeTopCpvForPayee.close();
								payeeCount++;
							}
							vqeTopPayees.close();
						}
						String[] bubbleWriterArray = {"ΛΟΙΠΟΙ", Long.toString(totalSum-payeeSum),"", "", 
								 "", "", toHexString(color[9]), "ΛΟΙΠΟΙ", "Rest", "Rest"};
						bubbleWriterList.add(bubbleWriterArray);		
						try{
							  // Create file 							
							  FileWriter fstream = new FileWriter(rootFolder + "/bubbles/payerBubble"+count+".json");
							  FileWriter fstream2 = new FileWriter(rootFolder + "/bubbles/payerBubbleLegend"+count+".json");
							  BufferedWriter out = new BufferedWriter(fstream);
							  BufferedWriter out2 = new BufferedWriter(fstream2);
							  JsonWriter jw = new JsonWriter();
							  out.write(jw.jsonBubbleWriter(bubbleWriterList, nameS, Long.toString(totalSum), "Οι 10 μεγαλύτεροι ανάδοχοι για τον φορέα ", "Top 10 payees for payer ", shortNameS, engNameS, engShortNameS));
							  out2.write(jw.jsonCpvLegendWriter(cpvMap));
							  //Close the output stream
							  out.close();
							  out2.close();
							  }catch (Exception e){//Catch exception if any								  
								  System.err.println("Error: " + e.getMessage());
								  e.printStackTrace();
							  }		
						if(count<11){
						String[] writerArray = {sum.toString().substring(0,sum.toString().indexOf('^')), nameS, shortNameS, engNameS, engShortNameS, cpvS, topPayeeS, freqPayeeS };
		 		   		writerList.add(writerArray);}
			 	   count++;
			 	 } 	   
			vqe.close();							
		}	
		
		public void topPayeesTimeframeJSON(){
									
			ArrayList<String> dates = new ArrayList<String>();
			String timeframe = "" , selector = "", title = "", engTitle="";			
			for(int i=0;i<4;i++){
				
				title = StaticStrings.payeeTitles[i];
				engTitle = StaticStrings.engPayeeTitles[i];
				
				if(i==0){
					timeframe = "day"; 
					selector = SPARQLQueries.daySelector;
					dates = lastDays;
				}
				else if(i==1){
					timeframe = "week";
					selector = SPARQLQueries.weekSelector;
					dates = lastWeeks;
				}
				else if(i==2){
					timeframe = "month";
					selector = SPARQLQueries.monthSelector;
					dates = lastMonths;
				}
				else if(i==3){
					timeframe = "year";
					selector = SPARQLQueries.yearSelector;
					dates = lastYears;
				}
				String[] dateForWriter = {"", ""};
				Iterator datesIt = dates.iterator();				
				ArrayList<String[]> writerList = new ArrayList<String[]>();
				while(datesIt.hasNext()){
					String dateString = (String) datesIt.next();								
					dateForWriter = getTimeInterval(dateString, timeframe);					
					String query = SPARQLQueries.topPayeesDescription(dateString, selector , timeframe, "10");					
					VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
					ResultSet results = vqe.execSelect();
					Integer count = 1;		
					while (results.hasNext()) {
						   QuerySolution rs = results.nextSolution();					 	   
					 	   RDFNode payee = rs.get("payee");
					 	   RDFNode sum = rs.get("sumString");
					 	   RDFNode avg = rs.get("avgString");
					 	   RDFNode paymentCount = rs.get("count");
					 	   String[] names = getAgentNames(payee.toString());
					 	   String nameS = names[0], 
					 			  engNameS = names[2], 
					 			  shortNameS = names[1], 
					 			  engShortNameS = names[3];
					 	   String[] writerArray = {sum.toString().substring(0,sum.toString().indexOf('^')), nameS, shortNameS, engNameS, engShortNameS};
					 	   writerList.add(writerArray);
					 	   count++;
					}														 	 
					vqe.close();
				}					
				try{
					  // Create file 
					  FileWriter fstream = new FileWriter(rootFolder + "/toppayees/topPayees"+timeframe+".json");
					  BufferedWriter out = new BufferedWriter(fstream);
					  JsonWriter jw = new JsonWriter();
					  out.write(jw.jsonBarWriter(writerList, "topPayees"+timeframe, dateForWriter, title, engTitle));
					  //Close the output stream
					  out.close();
					  }catch (Exception e){//Catch exception if any
						  System.err.println("Error: " + e.getMessage());
					  }							
			}
					
		}		
		
		
		public void topPayeesOverallJSON(){
			
			//Prepei na ektelestei gia diaforetika timeframes me katallhlo format!!!								
			String query = SPARQLQueries.topPayeesDescription("", "", "overall", "10");
			//System.out.println(query);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();		
			ArrayList<String[]> writerList = new ArrayList<String[]>();
			Integer count = 1;			
			String cpvS = "";
			String topPayerS="";
			String freqPayerS="";					
			while (results.hasNext()) {				
				QuerySolution rs = results.nextSolution();
				RDFNode payee = rs.get("payee");
				RDFNode sum = rs.get("sumString");
				RDFNode paymentCount = rs.get("count");			 	 		 	   		 	  
				String[] names = getAgentNames(payee.toString());
				String nameS = names[0], 
					   engNameS = names[2], 
					   shortNameS = names[1], 
					   engShortNameS = names[3];
				String[] writerArray = {sum.toString().substring(0,sum.toString().indexOf('^')), nameS, shortNameS, engNameS, engShortNameS, cpvS, topPayerS, freqPayerS};
				//System.out.println(Arrays.toString(writerArray));
				writerList.add(writerArray);						
				count++;		
			} 	   
			vqe.close();			
			try{
				  // Create file 
				  FileWriter fstream = new FileWriter(rootFolder + "/toppayees/topPayeesOverall.json");
				  BufferedWriter out = new BufferedWriter(fstream);
				  JsonWriter jw = new JsonWriter();
				  out.write(jw.jsonBarWriter(writerList, "topPayeesOverall", new String[] {"Συνολικά", "Overall"}, StaticStrings.payeeTitles[4], StaticStrings.engPayeeTitles[4]));
				  FileWriter fstream2 = new FileWriter(rootFolder + "/tables/overall.json");
				  BufferedWriter out2 = new BufferedWriter(fstream2);
				  topAgentsString +=",\n" + jw.jsonTopPayeesTableWriter(writerList) + "}";				  				  
				  out2.write(topAgentsString);
				  //Close the output stream
				  out.close();
				  out2.close();
				  }catch (Exception e){//Catch exception if any
					  System.err.println("Error: " + e.getMessage());
				  }						
		}
		
		public void topPayeeProfile(){
			
			String query = SPARQLQueries.topPayeesDescription("", "", "overall", "10");	
			//query = SPARQLQueries.prefixes + "SELECT (<http://publicspending.medialab.ntua.gr/paymentAgents/094121006> as ?payee) (sum(xsd:decimal(?am)) as ?sumString) (count(?payment) as ?count) FROM <http://publicspending.medialab.ntua.gr/AllDecisions> WHERE {" +
			//		 "?payment psgr:payee <http://publicspending.medialab.ntua.gr/paymentAgents/094121006> . ?payment psgr:paymentAmount ?am .}"; 
			//System.out.println(query);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();			
			int count = 0;
			/*String[] companyList = {"http://publicspending.medialab.ntua.gr/paymentAgents/094012188", "http://publicspending.medialab.ntua.gr/paymentAgents/094449128", 
									"http://publicspending.medialab.ntua.gr/paymentAgents/097820321", "http://publicspending.medialab.ntua.gr/paymentAgents/094280318"
			};	*/		
			while(results.hasNext()){
				String writeable = "{";
				QuerySolution rs = results.nextSolution();
				count++;
				RDFNode payee = rs.get("payee");
				writeable += "\"payee\": [{";
			 	Literal sum = rs.getLiteral("sumString");
			 	Literal paymentCount = rs.getLiteral("count");
			 	String[] names = getAgentNames(payee.toString());
			 	String shortNameS = names[1], 
			 		   engShortNameS = names[3], 
			 		   nameS = names[0], 
			 		   engNameS = names[2];			 	
	 		   	writeable += "\"name\": \"" + nameS + "\", \"shortName\":\"" + shortNameS + "\", \"nameeng\":\"" + engNameS + "\", \"shortnameeng\":\"" + engShortNameS +"\"}],\n ";
	 		   	writeable += "\"amount\":[{\"num\": \"€" + decorateAmount(sum.getString()) + "\"}], \n";
	 		   	writeable += "\"count\": [{\"pos\": " + count + ", \"numberofpayments\": " + paymentCount.getString() + "}],";
				String gsisQuery = SPARQLQueries.prefixes + "PREFIX dd:<http://diavgeia.medialab.ntua.gr/ontology#> " + 
			 		   					   "SELECT ?startDate ?postalCity ?postalStreetNumber ?postalZipCode ?postalStreetName ?phoneNumber ?cpaGr ?cpaEn ?cpaCode ?stopDate FROM " + 
			 		   					   "<" +DailyRoutineHandler.graphName + "> WHERE {" + 
			 		   					   "<"+payee.toString()+"> psgr:postalStreetNumber ?postalStreetNumber ;"  + 
			 		   					   " psgr:postalZipCode ?postalZipCode ; psgr:phoneNumber ?phoneNumber ; " + 
			 		   					   "d:postalStreetName ?postalStreetName ; psgr:registrationDate ?startDate ; psgr:postalCity ?postalCity ; " + 
			 		   					   "d:cpaCode ?cpaCode ; psgr:cpaGreekSubject ?cpaGr ; psgr:stopDate ?stopDate . OPTIONAL{?cpa psgr:cpaCode ?cpaCode . ?cpa psgr:cpaEnglishSubject ?cpaEn .}}";
				//System.out.println(gsisQuery);
				VirtuosoQueryExecution vqeGsis = VirtuosoQueryExecutionFactory.create (gsisQuery, graph);
				ResultSet resultsGsis = vqeGsis.execSelect();
				while(resultsGsis.hasNext()){
					QuerySolution rsGsis = resultsGsis.nextSolution();						
					Literal startDate = rsGsis.getLiteral("startDate");
					Literal stopDate = rsGsis.getLiteral("stopDate");
					Literal zipCode = rsGsis.getLiteral("postalZipCode");
					Literal streetNumber = rsGsis.getLiteral("postalStreetNumber");
					Literal streetName = rsGsis.getLiteral("postalStreetName");
					Literal city = rsGsis.getLiteral("postalCity");
					Literal cpaCode = rsGsis.getLiteral("cpaCode");
					Literal cpaGr = rsGsis.getLiteral("cpaGr");
					String cpaEnS = "";
					try{Literal cpaEn = rsGsis.getLiteral("cpaEn"); cpaEnS = cpaEn.getString();}catch(Exception e){}
					Literal phoneNumber = rsGsis.getLiteral("phoneNumber");
					String active = "<img src='images/off.png' height='32' width='32' />";
					String stopDateS = stopDate.getString();
					if(stopDateS.equals("Null")){						
						active = "<img src='images/on.png' height='32' width='32' />";
						stopDateS = "-";
					}
						String date = startDate.getString();
						if(date.length()>=10)
							date = date.substring(0,10);						
						writeable += "\"status\": [{\"active\":\"" + active + "\"}], \n \"start\":[{\"startdate\": \"" + date + "\", \"enddate\":\"" + stopDateS + "\"}], \n";
						writeable += "\"cpa\":[{\"cpagr\": \"" + cpaGr.getString() + " (" + cpaCode.getString() + ")\", \"cpaeng\": \"" + cpaEnS + " (" + cpaCode.getString() + ")\"}], \n ";
						writeable += "\"address\": [{\"street\": \"" + streetName.getString() + " " + streetNumber.getString() + "\", \"city\":\""+city.getString()+"\", \"pobox\":\"" + zipCode.getString() + "\", \"phone\":\"" + phoneNumber.getString() + "\"}], \n";
						writeable += "\"addresseng\": [{\"street\": \"" + greeklish.greeklishGenerator(streetName.getString()) + " " + streetNumber.getString() + "\", \"city\":\""+greeklish.greeklishGenerator(city.getString())+"\", \"pobox\":\"" + zipCode.getString() + "\", \"phone\":\"" + phoneNumber.getString() + "\"}], \n";
					}
					vqeGsis.close();					
					String totalQuery = SPARQLQueries.prefixes + 
										"SELECT (sum(xsd:decimal(?am)) as ?sum) FROM <" + DailyRoutineHandler.graphName + "> WHERE {" + 
										"?payment psgr:payee <" + payee.toString() + "> ; psgr:paymentAmount ?am .}";
					VirtuosoQueryExecution vqeTotal = VirtuosoQueryExecutionFactory.create (totalQuery, graph);
					ResultSet resultsTotal = vqeTotal.execSelect();
					Long total = new Long(0);
					while(resultsTotal.hasNext()){
						QuerySolution rsTotal = resultsTotal.nextSolution();
						total = rsTotal.getLiteral("sum").getLong();						
					}
					vqeTotal.close();					
					String cpvQuery = SPARQLQueries.prefixes + "SELECT distinct ?cpv ?cpvGr ?cpvEn (sum(xsd:decimal(?am)) as ?sum) FROM <"+DailyRoutineHandler.graphName+"> WHERE {" + 
															   "?payment psgr:payee <"+payee.toString()+"> ." +
															   "?payment psgr:paymentAmount ?am . " + 
															   "?payment psgr:cpv ?cpv ." + 
															   "?cpv psgr:cpvGreekSubject ?cpvGr ." +
															   "?cpv psgr:cpvEnglishSubject ?cpvEn .} ORDER BY DESC(?sum) LIMIT 100";
					VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
					ResultSet resultsCpv = vqeCpv.execSelect();
					writeable += "\"topcpv\": [";
					int cpvOrder = 1;
					while(resultsCpv.hasNext()){
						QuerySolution rsCpv = resultsCpv.nextSolution();
						Literal cpvGr = rsCpv.getLiteral("cpvGr");
						Literal cpvEn = rsCpv.getLiteral("cpvEn");
						Literal cpvSum = rsCpv.getLiteral("sum");
						Long percentage = cpvSum.getLong()*100/total;
						writeable += "{\"cpvcat\": \"" + cpvGr.getString() + "\", \"cpvcateng\": \"" + cpvEn.getString() + "\", \"amount\": \"€" + decorateAmount(cpvSum.getString()) + "\", \"percentage\": \"" + percentage.toString() + "%\", \"order\":"+cpvOrder+"}, \n";
						cpvOrder++;
					}
					vqeCpv.close();
					writeable = writeable.substring(0, writeable.length()-3) + "], \n";
					String payerQuery = SPARQLQueries.prefixes + 
							   "SELECT distinct ?payer (sum(xsd:decimal(?am)) as ?sum) FROM <"+DailyRoutineHandler.graphName+"> WHERE {" + 
							   "?payment psgr:payee <"+payee.toString()+"> ." +
							   "?payment psgr:paymentAmount ?am . " + 
							   "?payment psgr:payer ?payer . ?payer psgr:validAfm \"true\" .} ORDER BY DESC(?sum) LIMIT 100";
					VirtuosoQueryExecution vqePayers = VirtuosoQueryExecutionFactory.create (payerQuery, graph);
					ResultSet resultsPayers = vqePayers.execSelect();
					writeable += "\"toppayers\": [";
					int payersOrder = 1;
					while(resultsPayers.hasNext()){
						QuerySolution rsPayers = resultsPayers.nextSolution();
						RDFNode payer = rsPayers.get("payer");						
			 		   	String[] payerNames = getAgentNames(payer.toString());
			 		    String payerShortNameS = payerNames[1], 
			 		    	   payerEngShortNameS = payerNames[3], 
			 		    	   payerNameS = payerNames[0], 
			 		    	   payerEngNameS = payerNames[2];
			 		    Literal payerSum = rsPayers.getLiteral("sum");
						Long percentage = payerSum.getLong()*100/total;
						writeable += "{\"payer\": \"" + payerNameS + "\", \"payereng\": \"" + payerEngNameS + "\", \"amount\": \"€" + decorateAmount(payerSum.getString()) + "\", \"percentage\": \"" + percentage.toString() + "%\", \"order\":"+payersOrder+"}, \n";
						payersOrder++;
					}
					vqeCpv.close();
					writeable = writeable.substring(0, writeable.length()-3) + "], \n";					
					String paymentQuery = SPARQLQueries.prefixes + 
										  "SELECT ?payment ?url ?date ?cpvGr ?cpvEn (xsd:decimal(?am) as ?amount) ?payer ?val ?afm FROM <"+DailyRoutineHandler.graphName+"> WHERE {" + 
										  "?payment psgr:payee <"+payee.toString()+"> ." + 
										  "?payment psgr:payer ?payer . ?payer psgr:validAfm ?val . ?payer psgr:afm ?afm ." + 
										  "?payment psgr:paymentAmount ?am ." +
										  "?decision psgr:refersTo ?payment . " +
										  "?decision psgr:day ?day ." + 
										  "?day psgr:date ?date ." + 
										  "?payment psgr:cpv ?cpv ." + 
										  "?cpv psgr:cpvGreekSubject ?cpvGr ." +
										  "?cpv psgr:cpvEnglishSubject ?cpvEn ." + 
										  "?decision psgr:url ?url .} ORDER BY DESC(?amount) LIMIT 100";
					VirtuosoQueryExecution vqePayments = VirtuosoQueryExecutionFactory.create (paymentQuery, graph);
					ResultSet resultsPayments = vqePayments.execSelect();
					writeable += "\"overall\": [";
					int paymentsOrder = 1;
					while(resultsPayments.hasNext()){
						QuerySolution rsPayments = resultsPayments.nextSolution();
						RDFNode payer = rsPayments.get("payer");
						Literal validity = rsPayments.getLiteral("val");
						Literal afm = rsPayments.getLiteral("afm");
						String[] payerNames = null;
						if(validity.getString().equals("true")){
				 		    payerNames = getAgentNames(payer.toString());				 		    
						}
						else
						{
							payerNames = getAgentNamesByAfm(afm.getString());							
						}
						String payerShortNameS = payerNames[1], 
				 		    	   payerEngShortNameS = payerNames[3], 
				 		    	   payerNameS = payerNames[0], 
				 		    	   payerEngNameS = payerNames[2];
			 		   	Literal url = rsPayments.getLiteral("url");
			 		   	Literal amount = rsPayments.getLiteral("amount");
			 		   	Literal date = rsPayments.getLiteral("date");
			 		   	Literal cpvGr = rsPayments.getLiteral("cpvGr");
			 		    Literal cpvEn = rsPayments.getLiteral("cpvEn");
			 		   	writeable += "{\"date\": \"" + date.getString() + "\", \"payer\":\"" + payerNameS + "\", \"payereng\":\"" + payerEngNameS + "\", \"amount\": \"€" + decorateAmount(amount.getString())+"\", \"cpvcat\":\"" + cpvGr.getString() + "\", \"cpvcateng\":\"" + cpvEn.getString() + "\", \"url\": \"<a href=\\\"" + url.getString() + "\\\">"+url.getString()+"</a>\", \"order\":"+paymentsOrder+"},\n";
			 		   	paymentsOrder++;
								
					}
					vqePayments.close();
					writeable = writeable.substring(0, writeable.length()-2) + "] \n }";					
					String daQuery = SPARQLQueries.timeplotPerPayeeDescription(DailyRoutineHandler.weekGraphName, payee.toString());					
					VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create(daQuery, graph);
					ResultSet results2 = vqe2.execSelect();
					HashMap<String, String> datesAndAmounts = new HashMap<String, String>();
					ArrayList<String[]> writerList = new ArrayList<String[]>();
					while(results2.hasNext()){						
						QuerySolution rs2 = results2.next();
						Literal date = rs2.getLiteral("date");
						Literal aggAmount = rs2.getLiteral("sumString");
						datesAndAmounts.put(date.toString(), aggAmount.toString());								
					}			
					vqe2.close();
					Object[] objectArray = datesAndAmounts.keySet().toArray();								
					String timeframe = "week";
					String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);					
					ArrayList<String> sortedDatesUnclean = dateComparison2(stringArray, timeframe);
					ArrayList<String> sortedDates = new ArrayList<String>();
					for (String dt : sortedDatesUnclean){
						if(!dt.equals("0")){
							Integer datInt = Integer.parseInt(dt.replace("-", "").replace("w", ""));
							if(timeframe.equals("day")){
								if(datInt<=todayDay && datInt>=oldestDay)
									sortedDates.add(dt);
														
							}
							else if(timeframe.equals("week")){
								if(datInt<=todayWeek && datInt>=oldestWeek)
									sortedDates.add(dt);
							}
							else if(timeframe.equals("month")){
								if(datInt<=todayMonth && datInt>=oldestMonth)
									sortedDates.add(dt);
							}
							else if(timeframe.equals("year")){
								if(datInt<=todayYear && datInt>=oldestYear){
									sortedDates.add(dt);						
								}
							}
						}
					}								
					Integer length = sortedDates.size();
					Iterator it = sortedDates.iterator();					
					while(it.hasNext()){		
						String date = (String) it.next();
						//System.out.println(date);
						if(date!=null && datesAndAmounts.get(date)!=null){						
							String[] writerArray = {getMilliseconds(date, timeframe), datesAndAmounts.get(date) };				
							writerList.add(writerArray);
							length--;
						}
					}			
					try{
						  // Create file 
						  FileWriter fstream = new FileWriter(rootFolder + "/payeetabs/tplotPayee"+count+timeframe+".json");
						  BufferedWriter out = new BufferedWriter(fstream);
						  JsonWriter jw = new JsonWriter();
						  out.write(jw.jsonTplotWriter(writerList, "tplotPayees"+timeframe, "Η διαχρονική εξέλιξη των πληρωμών", "Time plot of contracts", periodToDate));
						  //Close the output stream
						  out.close();
						  }catch (Exception e){//Catch exception if any
							  System.err.println("Error: " + e.getMessage()); e.printStackTrace();
						  }
					try{
						  // Create file 
						  FileWriter fstream = new FileWriter(rootFolder + "/payeetabs/payeetab"+count+".json");
						  BufferedWriter out = new BufferedWriter(fstream);				  
						  out.write(writeable.replace("Null", "-"));
						  //Close the output stream
						  out.close();
						  }catch (Exception e){//Catch exception if any
							  System.err.println("Error: " + e.getMessage());
						  }
			}
						
		}
		
public void topPayerProfile(){
			
			String query = SPARQLQueries.topPayersDescription("", "", "overall", "10");	
			//query = SPARQLQueries.prefixes + "SELECT (<http://publicspending.medialab.ntua.gr/paymentAgents/094121006> as ?payee) (sum(xsd:decimal(?am)) as ?sumString) (count(?payment) as ?count) FROM <http://publicspending.medialab.ntua.gr/AllDecisions> WHERE {" +
			//		 "?payment psgr:payee <http://publicspending.medialab.ntua.gr/paymentAgents/094121006> . ?payment psgr:paymentAmount ?am .}"; 
			//System.out.println(query);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();			
			int count = 0;
			/*String[] companyList = {"http://publicspending.medialab.ntua.gr/paymentAgents/094012188", "http://publicspending.medialab.ntua.gr/paymentAgents/094449128", 
									"http://publicspending.medialab.ntua.gr/paymentAgents/097820321", "http://publicspending.medialab.ntua.gr/paymentAgents/094280318"
			};	*/		
			while(results.hasNext()){
				String writeable = "{";
				QuerySolution rs = results.nextSolution();
				count++;
				RDFNode payer = rs.get("payer");
				writeable += "\"payer\": [{";
			 	Literal sum = rs.getLiteral("sumString");
			 	Literal paymentCount = rs.getLiteral("count");
			 	String[] names = getAgentNames(payer.toString());
			 	String shortNameS = names[1], 
			 		   engShortNameS = names[3], 
			 		   nameS = names[0], 
			 		   engNameS = names[2];			 	
	 		   	writeable += "\"name\": \"" + nameS + "\", \"shortName\":\"" + shortNameS + "\", \"nameeng\":\"" + engNameS + "\", \"shortnameeng\":\"" + engShortNameS +"\"}],\n ";
	 		   	writeable += "\"amount\":[{\"num\": \"€" + decorateAmount(sum.getString()) + "\"}], \n";
	 		   	writeable += "\"count\": [{\"pos\": " + count + ", \"numberofpayments\": " + paymentCount.getString() + "}],";
				String gsisQuery = SPARQLQueries.prefixes + "PREFIX dd:<http://diavgeia.medialab.ntua.gr/ontology#> " + 
			 		   					   "SELECT ?startDate ?postalCity ?postalStreetNumber ?postalZipCode ?postalStreetName ?phoneNumber ?cpaGr ?cpaEn ?cpaCode ?stopDate" +
			 		   					   "?legalStatus ?branches ?fpFlag ?firmDesc FROM " + 
			 		   					   "<" +DailyRoutineHandler.graphName + "> WHERE {" + 
			 		   					   "<"+payer.toString()+"> psgr:postalStreetNumber ?postalStreetNumber ;"  + 
			 		   					   " psgr:postalZipCode ?postalZipCode ; psgr:phoneNumber ?phoneNumber ; " +
			 		   					   "d:legalStatus ?legalStatus ; psgr:countOfBranches ?branches ; psgr:fpFlag ?fpFlag ; psgr:firmDescription ?firmDesc ;" + 
			 		   					   "d:postalStreetName ?postalStreetName ; psgr:registrationDate ?startDate ; psgr:postalCity ?postalCity ; " + 
			 		   					   "d:cpaCode ?cpaCode ; psgr:cpaGreekSubject ?cpaGr ; psgr:stopDate ?stopDate . OPTIONAL{?cpa psgr:cpaCode ?cpaCode . ?cpa psgr:cpaEnglishSubject ?cpaEn .}}";
				//System.out.println(gsisQuery);
				VirtuosoQueryExecution vqeGsis = VirtuosoQueryExecutionFactory.create (gsisQuery, graph);
				ResultSet resultsGsis = vqeGsis.execSelect();
				while(resultsGsis.hasNext()){
					QuerySolution rsGsis = resultsGsis.nextSolution();						
					Literal startDate = rsGsis.getLiteral("startDate");
					Literal stopDate = rsGsis.getLiteral("stopDate");
					Literal zipCode = rsGsis.getLiteral("postalZipCode");
					Literal streetNumber = rsGsis.getLiteral("postalStreetNumber");
					Literal streetName = rsGsis.getLiteral("postalStreetName");
					Literal city = rsGsis.getLiteral("postalCity");
					Literal legalStatus = rsGsis.getLiteral("legalStatus");
					Literal branches = rsGsis.getLiteral("branches");
					Literal fpFlag = rsGsis.getLiteral("fpFlag");
					Literal firmDescription = rsGsis.getLiteral("firmDesc");
					Literal cpaCode = rsGsis.getLiteral("cpaCode");
					Literal cpaGr = rsGsis.getLiteral("cpaGr");
					String cpaEnS = "";
					try{
						Literal cpaEn = rsGsis.getLiteral("cpaEn"); cpaEnS = cpaEn.getString();
					}catch(Exception e){}
					Literal phoneNumber = rsGsis.getLiteral("phoneNumber");
					String active = "off";
					String stopDateS = stopDate.getString();
					if(stopDateS.equals("Null")){
						active = "on";
						stopDateS = "-";
					}
					String date = startDate.getString();
					if(date.length()>=10)
						date = date.substring(0,10);					
						writeable += "\"status\": [{\"active\":\"" + active + "\"}], \n \"start\":[{\"startdate\": \"" + date+ "\", \"enddate\":\"" + stopDateS + "\"}], \n";
						writeable += "\"cpa\":[{\"cpagr\": \"" + cpaGr.getString() + " (" + cpaCode.getString() + ")\", \"cpaeng\": \"" + cpaEnS + " (" + cpaCode.getString() + ")\"}], \n ";
						writeable += "\"address\": [{\"street\": \"" + streetName.getString() + " " + streetNumber.getString() + "\", \"city\":\""+city.getString()+"\", \"pobox\":\"" + zipCode.getString() + "\", \"phone\":\"" + phoneNumber.getString() + "\"}], \n";
						writeable += "\"addresseng\": [{\"street\": \"" + greeklish.greeklishGenerator(streetName.getString()) + " " + streetNumber.getString() + "\", \"city\":\""+greeklish.greeklishGenerator(city.getString())+"\", \"pobox\":\"" + zipCode.getString() + "\", \"phone\":\"" + phoneNumber.getString() + "\"}], \n";
					}
					vqeGsis.close();					
					String totalQuery = SPARQLQueries.prefixes + 
										"SELECT (sum(xsd:decimal(?am)) as ?sum) FROM <" + DailyRoutineHandler.graphName + "> WHERE {" + 
										"?payment psgr:payer <" + payer.toString() + "> ; psgr:paymentAmount ?am .}";
					VirtuosoQueryExecution vqeTotal = VirtuosoQueryExecutionFactory.create (totalQuery, graph);
					ResultSet resultsTotal = vqeTotal.execSelect();
					Long total = new Long(0);
					while(resultsTotal.hasNext()){
						QuerySolution rsTotal = resultsTotal.nextSolution();
						total = rsTotal.getLiteral("sum").getLong();						
					}
					vqeTotal.close();					
					String cpvQuery = SPARQLQueries.prefixes + "SELECT distinct ?cpv ?cpvGr ?cpvEn (sum(xsd:decimal(?am)) as ?sum) FROM <"+DailyRoutineHandler.graphName+"> WHERE {" + 
															   "?payment psgr:payer <"+payer.toString()+"> ." +
															   "?payment psgr:paymentAmount ?am . " + 
															   "?payment psgr:cpv ?cpv ." + 
															   "?cpv psgr:cpvGreekSubject ?cpvGr ." +
															   "?cpv psgr:cpvEnglishSubject ?cpvEn .} ORDER BY DESC(?sum) LIMIT 100";
					//System.out.println(cpvQuery);
					VirtuosoQueryExecution vqeCpv = VirtuosoQueryExecutionFactory.create (cpvQuery, graph);
					ResultSet resultsCpv = vqeCpv.execSelect();
					writeable += "\"topcpv\": [";
					int cpvOrder = 1;
					while(resultsCpv.hasNext()){
						QuerySolution rsCpv = resultsCpv.nextSolution();
						Literal cpvGr = rsCpv.getLiteral("cpvGr");
						Literal cpvEn = rsCpv.getLiteral("cpvEn");
						Literal cpvSum = rsCpv.getLiteral("sum");
						Long percentage = cpvSum.getLong()*100/total;
						writeable += "{\"cpvcat\": \"" + cpvGr.getString() + "\", \"cpvcateng\": \"" + cpvEn.getString() + "\", \"amount\": \"€" + decorateAmount(cpvSum.getString()) + "\", \"percentage\": \"" + percentage.toString() + "%\", \"order\":"+cpvOrder+"}, \n";
						cpvOrder++;
					}
					vqeCpv.close();
					writeable = writeable.substring(0, writeable.length()-3) + "], \n";
					String payerQuery = SPARQLQueries.prefixes + 
							   "SELECT distinct ?payee (sum(xsd:decimal(?am)) as ?sum) FROM <"+DailyRoutineHandler.graphName+"> WHERE {" + 
							   "?payment psgr:payer <"+payer.toString()+"> ." +
							   "?payment psgr:paymentAmount ?am . " + 							  
							   "?payment psgr:payee ?payee . ?payee psgr:validAfm \"true\".} ORDER BY DESC(?sum) LIMIT 100";
					//System.out.println(payerQuery);
					VirtuosoQueryExecution vqePayees = VirtuosoQueryExecutionFactory.create (payerQuery, graph);
					ResultSet resultsPayees = vqePayees.execSelect();
					writeable += "\"toppayees\": [";
					int payeesOrder = 1;
					while(resultsPayees.hasNext()){
						QuerySolution rsPayees = resultsPayees.nextSolution();
						RDFNode payee = rsPayees.get("payee");						
			 		   	String[] payeeNames = getAgentNames(payee.toString());
			 		    String payeeShortNameS = payeeNames[1], 
			 		    	   payeeEngShortNameS = payeeNames[3], 
			 		    	   payeeNameS = payeeNames[0], 
			 		    	   payeeEngNameS = payeeNames[2];
			 		    Literal payeeSum = rsPayees.getLiteral("sum");
						Long percentage = payeeSum.getLong()*100/total;
						writeable += "{\"payee\": \"" + payeeNameS + "\", \"payeeeng\": \"" + payeeEngNameS + "\", \"amount\": \"€" + decorateAmount(payeeSum.getString()) + "\", \"percentage\": \"" + percentage.toString() + "%\", \"order\":"+payeesOrder+"}, \n";
						payeesOrder++;
					}
					vqePayees.close();
					writeable = writeable.substring(0, writeable.length()-3) + "], \n";					
					String paymentQuery = SPARQLQueries.prefixes + 
										  "SELECT ?payment ?url ?date ?cpvGr ?cpvEn (xsd:decimal(?am) as ?amount) ?payee ?val ?afm FROM <"+DailyRoutineHandler.graphName+"> WHERE {" + 
										  "?payment psgr:payer <"+payer.toString()+"> ." + 
										  "?payment psgr:payee ?payee . ?payee psgr:validAfm ?val . ?payee psgr:afm ?afm . " + 
										  "?payment psgr:paymentAmount ?am ." +
										  "?decision psgr:refersTo ?payment . " +
										  "?decision psgr:day ?day ." + 
										  "?day psgr:date ?date ." + 
										  "?payment psgr:cpv ?cpv ." + 
										  "?cpv psgr:cpvGreekSubject ?cpvGr ." +
										  "?cpv psgr:cpvEnglishSubject ?cpvEn ." + 
										  "?decision psgr:url ?url .} ORDER BY DESC(?amount) LIMIT 100";
					//System.out.println(paymentQuery);
					VirtuosoQueryExecution vqePayments = VirtuosoQueryExecutionFactory.create (paymentQuery, graph);
					ResultSet resultsPayments = vqePayments.execSelect();
					writeable += "\"overall\": [";
					int paymentsOrder = 1;
					while(resultsPayments.hasNext()){
						QuerySolution rsPayments = resultsPayments.nextSolution();
						RDFNode payee = rsPayments.get("payee");
						Literal validity = rsPayments.getLiteral("val");
						Literal afm = rsPayments.getLiteral("afm");
						String[] payerNames = null;
						if(validity.getString().equals("true")){
				 		    payerNames = getAgentNames(payer.toString());				 		    
						}
						else
						{
							payerNames = getAgentNamesByAfm(afm.getString());							
						}			 		    
			 		    String payeeShortNameS = payerNames[1], 
			 		    	   payeeEngShortNameS = payerNames[3], 
			 		    	   payeeNameS = payerNames[0], 
			 		    	   payeeEngNameS = payerNames[2];
			 		   	Literal url = rsPayments.getLiteral("url");
			 		   	Literal amount = rsPayments.getLiteral("amount");
			 		   	Literal date = rsPayments.getLiteral("date");
			 		   	Literal cpvGr = rsPayments.getLiteral("cpvGr");
			 		    Literal cpvEn = rsPayments.getLiteral("cpvEn");
			 		   	writeable += "{\"date\": \"" + date.getString() + "\", \"payee\":\"" + payeeNameS + "\", \"payeeeng\":\"" + payeeEngNameS + "\", \"amount\": \"€" + decorateAmount(amount.getString())+"\", \"cpvcat\":\"" + cpvGr.getString() + "\", \"cpvcateng\":\"" + cpvEn.getString() + "\", \"url\": \"<a href=\\\"" + url.getString() + "\\\">"+url.getString()+"</a>\", \"order\":"+paymentsOrder+"},\n";
			 		   	paymentsOrder++;
								
					}
					vqePayments.close();
					writeable = writeable.substring(0, writeable.length()-2) + "] \n }";					
					String daQuery = SPARQLQueries.timeplotPerPayerDescription(DailyRoutineHandler.weekGraphName, payer.toString());					
					VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create(daQuery, graph);
					ResultSet results2 = vqe2.execSelect();
					HashMap<String, String> datesAndAmounts = new HashMap<String, String>();
					ArrayList<String[]> writerList = new ArrayList<String[]>();
					while(results2.hasNext()){						
						QuerySolution rs2 = results2.next();
						Literal date = rs2.getLiteral("date");
						Literal aggAmount = rs2.getLiteral("sumString");
						datesAndAmounts.put(date.toString(), aggAmount.toString());								
					}			
					vqe2.close();
					Object[] objectArray = datesAndAmounts.keySet().toArray();								
					String timeframe = "week";
					String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);					
					ArrayList<String> sortedDatesUnclean = dateComparison2(stringArray, timeframe);
					ArrayList<String> sortedDates = new ArrayList<String>();
					for (String dt : sortedDatesUnclean){
						if(!dt.equals("0")){
							Integer datInt = Integer.parseInt(dt.replace("-", "").replace("w", ""));
							if(timeframe.equals("day")){
								if(datInt<=todayDay && datInt>=oldestDay)
									sortedDates.add(dt);
														
							}
							else if(timeframe.equals("week")){
								if(datInt<=todayWeek && datInt>=oldestWeek)
									sortedDates.add(dt);
							}
							else if(timeframe.equals("month")){
								if(datInt<=todayMonth && datInt>=oldestMonth)
									sortedDates.add(dt);
							}
							else if(timeframe.equals("year")){
								if(datInt<=todayYear && datInt>=oldestYear){
									sortedDates.add(dt);						
								}
							}
						}
					}								
					Integer length = sortedDates.size();
					Iterator it = sortedDates.iterator();					
					while(it.hasNext()){		
						String date = (String) it.next();
						//System.out.println(date);
						if(date!=null && datesAndAmounts.get(date)!=null){						
							String[] writerArray = {getMilliseconds(date, timeframe), datesAndAmounts.get(date) };				
							writerList.add(writerArray);
							length--;
						}
					}			
					try{
						  // Create file 
						  FileWriter fstream = new FileWriter(rootFolder + "/payertabs/tplotPayer"+count+timeframe+".json");
						  BufferedWriter out = new BufferedWriter(fstream);
						  JsonWriter jw = new JsonWriter();
						  out.write(jw.jsonTplotWriter(writerList, "tplotPayers"+timeframe, "Η διαχρονική εξέλιξη των πληρωμών", "Time plot of contracts", periodToDate));
						  //Close the output stream
						  out.close();
						  }catch (Exception e){//Catch exception if any
							  System.err.println("Error: " + e.getMessage()); e.printStackTrace();
						  }
					try{
						  // Create file 
						  FileWriter fstream = new FileWriter(rootFolder + "/payertabs/payertab"+count+".json");
						  BufferedWriter out = new BufferedWriter(fstream);				  
						  out.write(writeable.replace("Null", "-"));
						  //Close the output stream
						  out.close();
						  }catch (Exception e){//Catch exception if any
							  System.err.println("Error: " + e.getMessage());
						  }
			}
						
		}
		
		public void topPayeeBubblesJSON(){
			
			String query = SPARQLQueries.topPayeesDescriptionBubbles("", "", "overall", "20");			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();			
			ArrayList<String[]> writerList = new ArrayList<String[]>();
			Integer count = 1;										
			while (results.hasNext()) {
   				   ArrayList<String[]> bubbleWriterList = new ArrayList<String[]>();
				   QuerySolution rs = results.nextSolution();			 	   
			 	   RDFNode payee = rs.get("payee");			 	   
			 	   RDFNode sum = rs.get("sumString");
			 	   RDFNode paymentCount = rs.get("count");			 		  		 	   		 	 
			 	   String nameQuery = SPARQLQueries.nameQuery(payee.toString());		 		   	
			 	   VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory.create (nameQuery, graph);
			 	   ResultSet nameResults = vqeNames.execSelect();
			 	   String[] names = getAgentNames(payee.toString());
			 	   String nameS = names[0], 
			 			  engNameS = names[2], 
			 			  shortNameS = names[1], 
			 			  engShortNameS = names[3];
			 	   payeesList.add(new String[] {nameS, shortNameS, engNameS, engShortNameS});
			 	   String totalAmountQuery = SPARQLQueries.totalAmountForPayee(payee.toString());
			 	   long totalSum = 0;			 		   	   
			 	   VirtuosoQueryExecution vqeTotalAmount = VirtuosoQueryExecutionFactory.create (totalAmountQuery, graph);
			 	   ResultSet totalAmountResults = vqeTotalAmount.execSelect();
			 	   while(totalAmountResults.hasNext()){
			 		   QuerySolution totalAmountRs = totalAmountResults.nextSolution();
			 		   Literal totalAmount = totalAmountRs.getLiteral("sum");
			 		   totalSum = totalAmount.getLong();
			 	   }
			 	   vqeTotalAmount.close();
			 	   String topPayersForPayee = SPARQLQueries.topPayersForPayee(payee.toString(), "9");			 		   
			 	   VirtuosoQueryExecution vqeTopPayers = VirtuosoQueryExecutionFactory.create (topPayersForPayee, graph);
			 	   ResultSet topPayerResults = vqeTopPayers.execSelect();
			 	   int payerCount = 0;
			 	   long payerSum = 0;
			 	   HashMap<String, String[]> cpvMap = new HashMap<String, String[]>();
			 	   int flag = 0;
			 	   while(topPayerResults.hasNext()){
			 		   flag = 1;
			 		   QuerySolution topPayerRs = topPayerResults.next();							
			 		   RDFNode topPayer = topPayerRs.get("payer");															
			 		   Literal payerAmount = topPayerRs.getLiteral("amount");
			 		   payerSum += payerAmount.getLong();							
			 		   String[] payerNames = getAgentNames(topPayer.toString());
			 		   String namePayer = payerNames[0],
			 				   shortNamePayer = payerNames[1], 
			 				   engNamePayer = payerNames[2], 
			 				   engShortNamePayer = payerNames[3];				 		   	
			 		   String topCpvForPayer = SPARQLQueries.topCpvForPayeePayer(payee.toString(), topPayer.toString());						
			 		   VirtuosoQueryExecution vqeTopCpvForPayer = VirtuosoQueryExecutionFactory.create(topCpvForPayer, graph);								
			 		   ResultSet topCpvResults = vqeTopCpvForPayer.execSelect();			
			 		   String cgs = "", ces = "", csn = "", cesn = "", cpvCode = "", cpvCodeDiv = "";
			 		   while(topCpvResults.hasNext()){
								QuerySolution topCpvRs = topCpvResults.nextSolution();													
								try{
									Literal cpvCodeDivLiteral = topCpvRs.getLiteral("cpvCodeDiv");
									cpvCodeDiv = cpvCodeDivLiteral.getString();
								}catch(Exception e){}
								Literal cpvCodeLiteral = topCpvRs.getLiteral("cpvCode");
								cpvCode = cpvCodeLiteral.getString();								
							}
							if(cpvCodeDiv.equals(""))
								cpvCodeDiv  = cpvCode ;
							VirtuosoQueryExecution vqeCpvDesc = VirtuosoQueryExecutionFactory.create(SPARQLQueries.cpvQuery(Ontology.instancePrefix + "cpvCodes/" + cpvCodeDiv), graph);						
							ResultSet cpvDescRes = vqeCpvDesc.execSelect();								
							while(cpvDescRes.hasNext()){
								QuerySolution cpvRs = cpvDescRes.nextSolution();
								try{
									Literal cpvGreekSubject = cpvRs.getLiteral("cpvSubject");
									cgs = cpvGreekSubject.getString();
									Literal cpvEngSubject = cpvRs.getLiteral("cpvEngSubject");
									ces = cpvEngSubject.getString();
								}catch(Exception e){}
							}
							vqeCpvDesc.close();
							CPVColours cpvColours = new CPVColours();	
							String colour = cpvColours.getColourFromCPV(cpvCodeDiv);
							//System.out.println(colour);
							if(colour==null){
								colour = cpvColours.getColourFromCPV(cpvCode);			
								//System.out.println("old code: " + cpvCodeDiv + " new color: " + colour);
							}
							//System.out.println("Color: " + cpvColours.getColourFromCPV(cpvCode));
							cpvMap.put(colour, new String[] {cgs, ces, colour});
							String[] bubbleWriterArray = {namePayer, payerAmount.getString(), cgs, ces, csn, cesn, colour ,shortNamePayer, engNamePayer, engShortNamePayer};
							bubbleWriterList.add(bubbleWriterArray);	
							vqeTopCpvForPayer.close();
							payerCount++;
						}
						vqeTopPayers.close();		
						if(flag==0){
							String topPayersForPayee2 = SPARQLQueries.prefixes + "SELECT distinct ?payer (sum(xsd:decimal(?am)) as ?amount) " + 
														"FROM <" + DailyRoutineHandler.graphName + "> WHERE {" + 
														"?payment psgr:payee <" + payee.toString() + "> ; psgr:payer ?payer ; psgr:paymentAmount ?am .} LIMIT 9";
				 		    
				 		    VirtuosoQueryExecution vqeTopPayers2 = VirtuosoQueryExecutionFactory.create (topPayersForPayee2, graph);
							ResultSet topPayerResults2 = vqeTopPayers2.execSelect();
							while(topPayerResults2.hasNext()){								
								QuerySolution topPayerRs = topPayerResults2.next();								
								RDFNode topPayer = topPayerRs.get("payer");									
								Literal payerAmount = topPayerRs.getLiteral("amount");
								payerSum += payerAmount.getLong();
								String nameQueryPayer = SPARQLQueries.nameQuery(topPayer.toString());		 		   	
					 		   	VirtuosoQueryExecution vqeNamesPayer = VirtuosoQueryExecutionFactory.create (nameQueryPayer, graph);
					 		    //System.out.println("payee Name query");
					 		   	String[] payerNames = getAgentNames(topPayer.toString());
					 		   	String shortNamePayer = payerNames[1], 
					 		   		   engShortNamePayer = payerNames[3], 
					 		   		   engNamePayer = payerNames[2], 
					 		   		   namePayer = payerNames[0];					 		   							 		   	
								String topCpvForPayer = SPARQLQueries.topCpvForPayeePayer(payee.toString(), topPayer.toString());								
								VirtuosoQueryExecution vqeTopCpvForPayer = VirtuosoQueryExecutionFactory.create(topCpvForPayer, graph);								
								ResultSet topCpvResults = vqeTopCpvForPayer.execSelect();			
								String cgs = "ΑΠΡΟΣΔΙΟΡΙΣΤΕΣ ΚΑΤΗΓΟΡΙΕΣ", ces = "UNDEFINED CATEGORIES", csn = "ΑΠΡΟΣΔΙΟΡΙΣΤΑ", cesn = "UNDEFINED", cpvCode = "undefined", cpvCodeDiv = "undefined";
								while(topCpvResults.hasNext()){
									QuerySolution topCpvRs = topCpvResults.nextSolution();
									try{
										Literal cpvCodeDivLiteral = topCpvRs.getLiteral("cpvCodeDiv");
										cpvCodeDiv = cpvCodeDivLiteral.getString();
									}catch(Exception e){}
									Literal cpvCodeLiteral = topCpvRs.getLiteral("cpvCode");
									cpvCode = cpvCodeLiteral.getString();
								}
								if(cpvCodeDiv.equals("undefined"))
									cpvCodeDiv = cpvCode ;
								VirtuosoQueryExecution vqeCpvDesc = VirtuosoQueryExecutionFactory.create(SPARQLQueries.cpvQuery(Ontology.instancePrefix + "cpvCodes/" + cpvCodeDiv), graph);						
								ResultSet cpvDescRes = vqeCpvDesc.execSelect();								
								while(cpvDescRes.hasNext()){
									QuerySolution cpvRs = cpvDescRes.nextSolution();
									try{
										Literal cpvGreekSubject = cpvRs.getLiteral("cpvSubject");
										cgs = cpvGreekSubject.getString();
										Literal cpvEngSubject = cpvRs.getLiteral("cpvEngSubject");
										ces = cpvEngSubject.getString();
									}catch(Exception e){}
								}
								vqeCpvDesc.close();
								CPVColours cpvColours = new CPVColours();	
								String colour = cpvColours.getColourFromCPV(cpvCodeDiv);								
								if(colour==null){
									colour = cpvColours.getColourFromCPV(cpvCode);												
								}								
								cpvMap.put(colour, new String[] {cgs, ces, colour});
								String[] bubbleWriterArray = {namePayer, payerAmount.getString(), cgs, ces, csn, cesn, colour ,shortNamePayer, engNamePayer, engShortNamePayer};
								bubbleWriterList.add(bubbleWriterArray);	
								vqeTopCpvForPayer.close();
								payerCount++;
							}
							vqeTopPayers.close();			
						}
						String[] bubbleWriterArray = {"ΛΟΙΠΟΙ", Long.toString(totalSum-payerSum),"", "", 
								 "", "", toHexString(color[9]), "ΛΟΙΠΟΙ", "Rest", "Rest"};
						bubbleWriterList.add(bubbleWriterArray);		
						try{
							  // Create file 						
							  FileWriter fstream = new FileWriter(rootFolder + "/bubbles/payeeBubble"+count+".json");
							  FileWriter fstream2 = new FileWriter(rootFolder + "/bubbles/payeeBubbleLegend"+count+".json");
							  BufferedWriter out = new BufferedWriter(fstream);
							  BufferedWriter out2 = new BufferedWriter(fstream2);
							  JsonWriter jw = new JsonWriter();
							  out.write(jw.jsonBubbleWriter(bubbleWriterList, nameS, Long.toString(totalSum), "Οι 10 μεγαλύτεροι φορείς για τον ανάδοχο ", "Top 10 payers for payee ", shortNameS, engNameS, engShortNameS));
							  out2.write(jw.jsonCpvLegendWriter(cpvMap));
							  //Close the output stream
							  out.close();
							  out2.close();
							  }catch (Exception e){//Catch exception if any
								  System.err.println("Error: " + e.getMessage());
								  e.printStackTrace();
							  }								
			 	   count++;
		
			} 	   
			vqe.close();			
		}
		
		//DO SOMETHING WITH BINARY RELATIONSHIPS
		
		
		public void topBinaryTimeframeJSON(){
			
			ArrayList<String> dates = new ArrayList<String>();
			String timeframe = "" , selector = "";		
			for(int i=3;i<4;i++){
				if(i==3){
					timeframe = "year";
					selector = SPARQLQueries.yearSelector;
					dates = lastYears;
				}
				
				Iterator datesIt = dates.iterator();
				Model jsonModel = ModelFactory.createDefaultModel();
				while(datesIt.hasNext()){
					String dateString = (String) datesIt.next();													
					String query = SPARQLQueries.topBinaryDescription(dateString, selector , timeframe);	
					VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
					ResultSet results = vqe.execSelect();
					Integer count = 1;		
					while (results.hasNext()) {
				 	   	QuerySolution rs = results.nextSolution();
				 	   	//RDFNode bRel = rs.get("bRel");
				 	   	RDFNode sum = rs.get("sumString");			 	   
				 	   	RDFNode payer = rs.get("payer");
				 	   	RDFNode payee = rs.get("payee");
				 	   	RDFNode paymentCount = rs.get("count");				 	   				 	   
				 	   	Resource topBinary = jsonModel.createResource("topBinary" + count + dateString + timeframe)			 			                 			 			                 
				 			                 .addProperty(ResourceFactory.createProperty("count"), count.toString())				 			                 
				 			                 .addProperty(ResourceFactory.createProperty("timeframe"), timeframe)
				 			                 .addProperty(ResourceFactory.createProperty("date"), dateString)
				 			                 .addProperty(ResourceFactory.createProperty("amount"), sum.toString().substring(0,sum.toString().indexOf('^')))
				 			                 .addProperty(ResourceFactory.createProperty("numberOfPayments"), paymentCount.toString().substring(0,paymentCount.toString().indexOf('^')));
				 			                 
				 	   	count++;				 	   
				 		   //topBinary.addProperty(ResourceFactory.createProperty("payerGsisName"), payerName.toString());
				 	   
				 	   				 		   	
				 		   	String nameQuery = SPARQLQueries.nameQuery(payer.toString());		 		   	
				 		   	VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory
				 				 								.create (nameQuery, graph);
				 		   	ResultSet nameResults = vqeNames.execSelect();
				 		   	while(nameResults.hasNext()){
				 		   		QuerySolution nameRs = nameResults.next();
				 		   		RDFNode name = nameRs.get("name");
				 		   		RDFNode gsisName = nameRs.get("gsisName");
					 		   	Literal shortName = nameRs.getLiteral("shortName");				 		   		
				 		   		String shortNameS = "";
				 		   		try{
				 		   			shortNameS = shortName.getString();
				 		   		}catch(Exception e){}
				 		   		if(gsisName!=null)
				 		   			topBinary.addProperty(ResourceFactory.createProperty("payerGsisName"), gsisName.toString());
				 		   		else
				 		   			topBinary.addProperty(ResourceFactory.createProperty("payerPName"), name.toString());
				 		   	}
				 		   	vqeNames.close();
				 	   			
				 		   nameQuery = SPARQLQueries.nameQuery(payee.toString());		 		   	
				 		   	VirtuosoQueryExecution vqeNames2 = VirtuosoQueryExecutionFactory
				 				 								.create (nameQuery, graph);
				 		   	ResultSet nameResults2 = vqeNames2.execSelect();
				 		   	while(nameResults2.hasNext()){
				 		   		QuerySolution nameRs = nameResults.next();
				 		   		RDFNode name = nameRs.get("name");
				 		   		RDFNode gsisName = nameRs.get("gsisName");
					 		   	Literal shortName = nameRs.getLiteral("shortName");				 		   		
				 		   		String shortNameS = "";
				 		   		try{
				 		   			shortNameS = shortName.getString();
				 		   		}catch(Exception e){}
				 		   		if(gsisName!=null)
				 		   			topBinary.addProperty(ResourceFactory.createProperty("payeeGsisName"), gsisName.toString());
				 		   		else
				 		   			topBinary.addProperty(ResourceFactory.createProperty("payeePName"), name.toString());
				 		   	}
				 		   	vqeNames.close();
				 	   
				 	} 	   
					vqe.close();
				}
						/*try{
						JSONWiter jr = new JSONWiter();
						FileOutputStream fos = new FileOutputStream(DailyRoutineHandler.jsonFolder+"topBinaryPer"+ timeframe +".json");
						jr.write(jsonModel, fos, null);				
					}
					catch(Exception e){e.printStackTrace();}	
					jsonModel.close();		*/
			}
					
			
		}
		
		
		
		public void topBinaryOverallJSON(){
			
			//Prepei na ektelestei gia diaforetika timeframes me katallhlo format!!!				
			//String query = SPARQLQueriesTest.topPayersDescription("", "", "overall");
			//String query = SPARQLQueries.topBinaryDescription("", "", "overall");
			String query = SPARQLQueries.binaryOverallDescription();
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();
			Model jsonModel = ModelFactory.createDefaultModel();
			Integer count = 1;
			while (results.hasNext()) {
		 	   	QuerySolution rs = results.nextSolution();
		 	   	//RDFNode bRel = rs.get("bRel");
		 	    RDFNode sum = rs.get("agg");			 	   
		 	   	RDFNode payer = rs.get("payer");
		 	   	RDFNode payee = rs.get("payee");
		 	   	//RDFNode paymentCount = rs.get("count");				 	   				 	   
		 	   	Resource topBinary = jsonModel.createResource("topBinary" + count)			 			                 			 			                 
		 			                 .addProperty(ResourceFactory.createProperty("count"), count.toString())				 			                 
		 			                 .addProperty(ResourceFactory.createProperty("timeframe"), "overall")		 			                 
		 			                 .addProperty(ResourceFactory.createProperty("amount"), sum.toString().substring(0,sum.toString().indexOf('^')));
		 			//                 .addProperty(ResourceFactory.createProperty("numberOfPayments"), paymentCount.toString().substring(0,paymentCount.toString().indexOf('^')));
		 			                 
		 	   	count++;				 	   
		 		   //topBinary.addProperty(ResourceFactory.createProperty("payerGsisName"), payerName.toString());
		 	   
		 	   				 		   	
		 		   	String nameQuery = SPARQLQueries.nameQuery(payer.toString());		 		   	
		 		   	VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory
		 				 								.create (nameQuery, graph);
		 		   	ResultSet nameResults = vqeNames.execSelect();
		 		   	while(nameResults.hasNext()){
		 		   		QuerySolution nameRs = nameResults.next();
		 		   		RDFNode name = nameRs.get("name");
		 		   		RDFNode gsisName = nameRs.get("gsisName");
			 		   	Literal shortName = nameRs.getLiteral("shortName");				 		   		
		 		   		String shortNameS = "";
		 		   		try{
		 		   			shortNameS = shortName.getString();
		 		   		}catch(Exception e){}
		 		   		if(gsisName!=null)
		 		   			topBinary.addProperty(ResourceFactory.createProperty("payerGsisName"), gsisName.toString());
		 		   		else
		 		   			topBinary.addProperty(ResourceFactory.createProperty("payerPName"), name.toString());
		 		   	}
		 		   	vqeNames.close();
		 	   			
		 		   nameQuery = SPARQLQueries.nameQuery(payee.toString());		 		   	
		 		   	VirtuosoQueryExecution vqeNames2 = VirtuosoQueryExecutionFactory
		 				 								.create (nameQuery, graph);
		 		   	ResultSet nameResults2 = vqeNames2.execSelect();
		 		   	while(nameResults2.hasNext()){
		 		   		QuerySolution nameRs = nameResults2.next();
		 		   		RDFNode name = nameRs.get("name");
		 		   		RDFNode gsisName = nameRs.get("gsisName");
			 		   	Literal shortName = nameRs.getLiteral("shortName");				 		   		
		 		   		String shortNameS = "";
		 		   		try{
		 		   			shortNameS = shortName.getString();
		 		   		}catch(Exception e){}
		 		   		if(gsisName!=null)
		 		   			topBinary.addProperty(ResourceFactory.createProperty("payeeGsisName"), gsisName.toString());
		 		   		else
		 		   			topBinary.addProperty(ResourceFactory.createProperty("payeePName"), name.toString());
		 		   	}
		 		   	vqeNames.close();		 	   		 	
		 	} 	   
			vqe.close();
			/*try{
				JSONWiter jr = new JSONWiter();
				FileOutputStream fos = new FileOutputStream(DailyRoutineHandler.jsonFolder+"topBinaryOverall.json");
				jr.write(jsonModel, fos, null);				
			}
			catch(Exception e){e.printStackTrace();}*/
			jsonModel.close();
		
		}	
		
	
	public void aggregateAmountPerTimeframeJSON(String[][] parameters) { //to String timeframe prepei na exei Day h Month h Year 
		
		String graphName="", timeframe="", title = "", engTitle="";		
		for(String[] tfParams : parameters){			
			graphName = tfParams[1];			
			timeframe = tfParams[3];
			if(timeframe.equals("day")){
				title = StaticStrings.tplotPaymentTitle[0];
				engTitle = StaticStrings.engTplotPaymentTitle[0];
				}			
			else if(timeframe.equals("week")){
				title = StaticStrings.tplotPaymentTitle[1];
				engTitle = StaticStrings.engTplotPaymentTitle[1];
			}
			else if(timeframe.equals("month")){
				title = StaticStrings.tplotPaymentTitle[2];
				engTitle = StaticStrings.engTplotPaymentTitle[2];
			}
			else if(timeframe.equals("year")){
				title = StaticStrings.tplotPaymentTitle[3];
				engTitle = StaticStrings.engTplotPaymentTitle[3];
			}
			String query = SPARQLQueries.aggregateAmountTimeplotDescription(graphName);			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();		
			HashMap<String, String> datesAndAmounts = new HashMap<String, String>();
			ArrayList<String[]> writerList = new ArrayList<String[]>();
			while(results.hasNext()){
				QuerySolution rs = results.next();
				RDFNode aggAmount = rs.get("aggregateString");
				RDFNode date = rs.get("date");						
				datesAndAmounts.put(date.toString(), aggAmount.toString());										
			}			
			//takes key set of hashmap and returns Object[]
			Object[] objectArray = datesAndAmounts.keySet().toArray();			
			//converts Object[] to String[]
			String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);
			//returns sorted String[]
			ArrayList<String> sortedDatesUnclean = dateComparison2(stringArray, timeframe);
			ArrayList<String> sortedDates = new ArrayList<String>();
			for (String dt : sortedDatesUnclean){
				if(!dt.equals("0")){
					Integer datInt = Integer.parseInt(dt.replace("-", "").replace("w", ""));
					if(timeframe.equals("day")){
						if(datInt<=todayDay && datInt>=oldestDay)
							sortedDates.add(dt);
												
					}
					else if(timeframe.equals("week")){
						if(datInt<=todayWeek && datInt>=oldestWeek)
							sortedDates.add(dt);
					}
					else if(timeframe.equals("month")){
						if(datInt<=todayMonth && datInt>=oldestMonth)
							sortedDates.add(dt);
					}
					else if(timeframe.equals("year")){
						if(datInt<=todayYear && datInt>=oldestYear){
							sortedDates.add(dt);						
						}
					}
				}
			}
			ArrayList<String> sortedDates2 = new ArrayList<String>();
			Integer length = sortedDates.size();
			Iterator it = sortedDates.iterator();			
			while(it.hasNext()){		
				String date = (String) it.next();				
				if(date!=null && datesAndAmounts.get(date)!=null){				
				String[] writerArray = {getMilliseconds(date, timeframe), datesAndAmounts.get(date).substring(0,datesAndAmounts.get(date).toString().indexOf('^')) };				
				writerList.add(writerArray);
				length--;}
			}			
			vqe.close();			
			try{
				  // Create file 
				  FileWriter fstream = new FileWriter(rootFolder + "/paymentstimeplot/tplotPayments"+timeframe+".json");
				  BufferedWriter out = new BufferedWriter(fstream);
				  JsonWriter jw = new JsonWriter();
				  out.write(jw.jsonTplotWriter(writerList, "tplotPayments"+timeframe, title, engTitle, periodToDate));
				  //Close the output stream
				  out.close();
				  }catch (Exception e){//Catch exception if any
					  System.err.println("Error: " + e.getMessage()); e.printStackTrace();
				  }						
		}		
	}
	
	public void aggregateAmountPerCpvJSON(String[][] parameters){
		
		String graphName="", timeframe="", title = "", engTitle="";		
		for(String[] tfParams : parameters){			
			graphName = tfParams[1];			
			timeframe = tfParams[3];
			if(timeframe.equals("day")){
				title = StaticStrings.tplotCPVTitle[0];
				engTitle = StaticStrings.engTplotCPVTitle[0];
			}
			else if(timeframe.equals("week")){
				title = StaticStrings.tplotCPVTitle[1];
				engTitle = StaticStrings.engTplotCPVTitle[1];
			}
			else if(timeframe.equals("month")){
				title = StaticStrings.tplotCPVTitle[2];
				engTitle = StaticStrings.engTplotCPVTitle[2];
			}
			else if(timeframe.equals("year")){
				title = StaticStrings.tplotCPVTitle[3];
				engTitle = StaticStrings.engTplotCPVTitle[3];
			}
			String topCpvQuery = SPARQLQueries.topCpvDescription("","", "overall", "10");
			topCpvQuery = topCpvQuery.substring(0, topCpvQuery.indexOf("LIMIT 10"));			
			String cpvDivQuery = SPARQLQueries.cpvDivisionQuery();		
			//System.out.println(cpvDivQuery);			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (cpvDivQuery, graph);
			ResultSet results = vqe.execSelect();					
			int count=1;
			while(results.hasNext()){				
				ArrayList<String[]> writerList = new ArrayList<String[]>();
				QuerySolution rs = results.next();				
				RDFNode cpvDiv = rs.get("cpvDiv");
				Literal cpvSub = rs.getLiteral("cpvSub");
				Literal cpvEngSub = rs.getLiteral("cpvEngSub");
				String daQuery = SPARQLQueries.timeplotPerCpvDivDescription(graphName, cpvDiv.toString(), "gr");
				//System.out.println(daQuery);
				VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create(daQuery, graph);
				ResultSet results2 = vqe2.execSelect();
				HashMap<String, CpvAndAmountData> cpvDatesAndAmounts = new HashMap<String, CpvAndAmountData>();	
				while(results2.hasNext()){
					CpvAndAmountData dataHolder = new CpvAndAmountData();
					dataHolder.setCpvCode(cpvDiv.toString().substring(cpvDiv.toString().lastIndexOf("/")+1));
					QuerySolution rs2 = results2.next();				
					try{
						Literal cpvSubject = rs2.getLiteral("cpvSubject");
						dataHolder.setCpvSubject(cpvSubject.getString());
					}
					catch(NullPointerException e){
						dataHolder.setCpvSubject("Null");
					}
					Literal cpvShort = rs2.getLiteral("shortName");
					dataHolder.setShortName(cpvShort.getString());
					Literal amount = rs2.getLiteral("sumString");				
					dataHolder.setAmount(amount.getString());
					RDFNode date = rs2.get("date");	
					if(!date.toString().equals("Null"))
						cpvDatesAndAmounts.put(date.toString(), dataHolder);
				}
				//takes key set of hashmap and returns Object[]
				Object[] objectArray = cpvDatesAndAmounts.keySet().toArray();
				//converts Object[] to String[]
				String[] stringArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);
				//returns sorted String[] 
				ArrayList<String> sortedDates = dateComparison2(stringArray, timeframe);		
				Integer length = sortedDates.size();
				Iterator it = sortedDates.iterator();
				while(it.hasNext()){				
					String date = (String) it.next();
					if(date!=null && cpvDatesAndAmounts.get(date)!=null && !date.equals("0")){
					CpvAndAmountData dh = cpvDatesAndAmounts.get(date);								
					String[] writerArray = {getMilliseconds(date, timeframe), cpvDatesAndAmounts.get(date).getAmount()};					
					writerList.add(writerArray);
					length--;}
				}
				vqe2.close();
				try{
					  // Create file 
					  FileWriter fstream = new FileWriter(rootFolder + "/45cpv/tplotCpv"+count+timeframe+".json");
					  BufferedWriter out = new BufferedWriter(fstream);
					  JsonWriter jw = new JsonWriter();
					  out.write(jw.jsonTplotCPVWriter(writerList, title + cpvSub.getString(), engTitle + cpvEngSub.getString(), periodToDate)); //exw valei agglika
					  //Close the output stream
					  out.close();
					  }catch (Exception e){//Catch exception if any
						  System.err.println("Error: " + e.getMessage()); e.printStackTrace();
					  }		
				count++;
			}
			vqe.close();						
		}		
	}
	
	
	public void mainBubble(){
		
		String query = SPARQLQueries.topPayersDescription("", "", "overall", "10");		 		
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();		
		ArrayList<String[]> writerList = new ArrayList<String[]>();		
		Integer count = 1;
		Long agg = new Long(0);
		HashMap<PaymentAgent, ArrayList<PaymentAgent>> agentMap = new HashMap<PaymentAgent, ArrayList<PaymentAgent>>();
		while (results.hasNext()) {			   
		 	   QuerySolution rs = results.nextSolution();		 	   
		 	   RDFNode payer = rs.get("payer");
		 	   //As valw to xrwma tou CPV edw
		 	   String cpvQueryForPayer = SPARQLQueries.prefixes + "SELECT ?cpv ?cpvCode ?cpvDivCode FROM <"+DailyRoutineHandler.graphName+"> WHERE {" +
		 			   					 "?payment psgr:payer <"+payer.toString()+"> ;d:paymentAmount ?am ; psgr:cpv ?cpv . ?cpv a psgr:CPV .?cpv psgr:cpvCode ?cpvCode . " +
		 			   					 		"OPTIONAL{" +
		 			   					 		"?cpv cpv:hasSuperCPVCode ?cpvDiv . " +
		 			   					 		"?cpvDiv a cpv:CPVDivision ." +
		 			   					 		" ?cpvDiv psgr:cpvCode ?cpvDivCode .}}ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1" ;
		 	  //System.out.println(cpvQueryForPayer);
		 	   VirtuosoQueryExecution vqeCpvForPayer = VirtuosoQueryExecutionFactory.create (cpvQueryForPayer, graph);
		 	   ResultSet cpvForPayerResults = vqeCpvForPayer.execSelect();
		 	   CPVColours cpvCol = new CPVColours();
		 	   String colourPayer = "";
		 	   while(cpvForPayerResults.hasNext()) {
		 		   QuerySolution rsCpvForPayer = cpvForPayerResults.nextSolution();
		 		   Literal cpvCode = rsCpvForPayer.getLiteral("cpvCode");
		 		   Literal cpvDivCode = rsCpvForPayer.getLiteral("cpvDivCode");
		 		   try{
		 			   colourPayer = cpvCol.getColourFromCPV(cpvDivCode.getString());
		 		   }catch(Exception e){}
		 		   if(colourPayer == null || colourPayer.equals("")){
		 			   try{
		 				   colourPayer = cpvCol.getColourFromCPV(cpvCode.getString());
		 			   }catch(Exception e){}
		 			   if(colourPayer == null || colourPayer.equals(""))
		 				   colourPayer = cpvCol.getColourFromCPV("undefined");
		 		   } 
		 	   }
		 	   vqeCpvForPayer.close();
		 	   //Telos xrwmatos CPV
		 	   Literal sum = rs.getLiteral("sumString");
		 	   agg += sum.getLong();
		 	   String[] names = getAgentNames(payer.toString()); 		 	   		 	   
		 	   PaymentAgent topPayer = new PaymentAgent();
		 	   topPayer.setPaymentAgentName(names[0])
		 	   		   .setEnglishName(names[2])
		 	   		   .setEngShortName(names[3])
		 	   		   .setGreekShortName(names[1])
		 	   		   .setSum(sum.getString())
		 	   		   .setColor(colourPayer);
		 	   
		 	   String topPayeesForPayerQuery = SPARQLQueries.prefixes + 
					   "SELECT distinct ?payee (sum(xsd:decimal(?am)) as ?sum) FROM <"+DailyRoutineHandler.graphName+"> WHERE {" + 
					   "?payment psgr:payer <"+payer.toString()+"> ." +
					   "?payment psgr:paymentAmount ?am . " + 							  
					   "?payment psgr:payee ?payee . ?payee psgr:validAfm \"true\".} ORDER BY DESC(?sum) LIMIT 10";		 	   
		 	   VirtuosoQueryExecution vqePayees = VirtuosoQueryExecutionFactory.create (topPayeesForPayerQuery, graph);
		 	   ResultSet resultsPayees = vqePayees.execSelect();
		 	   ArrayList<PaymentAgent> payeesList = new ArrayList<PaymentAgent>();
		 	   Long payeeAgg = new Long(0);
		 	   while(resultsPayees.hasNext()){
		 		    QuerySolution rsPayees = resultsPayees.nextSolution();
		 		    RDFNode payee = rsPayees.get("payee");					
		 		    Literal payeeSum = rsPayees.getLiteral("sum");
		 		    payeeAgg += payeeSum.getLong();
	 		   		String[] payeeNames = getAgentNames(payee.toString());	 		    
	 		   		PaymentAgent topPayeeForPayer = new PaymentAgent();
	 		   		topPayeeForPayer.setPaymentAgentName(payeeNames[0])
		 	   				   	.setEnglishName(payeeNames[2])
		 	   				   	.setEngShortName(payeeNames[3]).setGreekShortName(payeeNames[1]).setSum(payeeSum.getString());	 		   		
	 		   		
	 		   		
	 		   	   String topCpvForPayee = SPARQLQueries.topCpvForPayerPayee(payer.toString(), payee.toString());
	 		   	   //System.out.println(topCpvForPayee);
		 		   VirtuosoQueryExecution vqeTopCpvForPayee = VirtuosoQueryExecutionFactory.create(topCpvForPayee, graph);							
		 		   ResultSet topCpvResults = vqeTopCpvForPayee.execSelect();			
		 		   String cgs = "", ces = "", csn = "", cesn = "", cpvCode = "", cpvCodeDiv = "";							
		 		   while(topCpvResults.hasNext()){
							QuerySolution topCpvRs = topCpvResults.nextSolution();											
							try{
								Literal cpvCodeDivLiteral = topCpvRs.getLiteral("cpvCodeDiv");
								cpvCodeDiv = cpvCodeDivLiteral.getString();
							}
							catch(Exception e){}
							Literal cpvCodeLiteral = topCpvRs.getLiteral("cpvCode");
							cpvCode = cpvCodeLiteral.getString();																						
						}
						CPVColours cpvColours = new CPVColours();	
						String colour = cpvColours.getColourFromCPV(cpvCodeDiv);
						VirtuosoQueryExecution vqeCpvDesc = VirtuosoQueryExecutionFactory.create(SPARQLQueries.cpvQuery(Ontology.instancePrefix + "cpvCodes/" + cpvCodeDiv), graph);						
						ResultSet cpvDescRes = vqeCpvDesc.execSelect();								
						while(cpvDescRes.hasNext()){
							QuerySolution cpvRs = cpvDescRes.nextSolution();
							try{
								Literal cpvGreekSubject = cpvRs.getLiteral("cpvSubject");
								cgs = cpvGreekSubject.getString();
								Literal cpvEngSubject = cpvRs.getLiteral("cpvEngSubject");
								ces = cpvEngSubject.getString();
							}catch(Exception e){}
						}
						vqeCpvDesc.close();
						//System.out.println(colour);
						if(colour==null){
							colour = cpvColours.getColourFromCPV(cpvCode);
							//System.out.println("cpvcode:" + cpvCode + "  changed: " + colour);
							VirtuosoQueryExecution vqeCpvDesc2 = VirtuosoQueryExecutionFactory.create(SPARQLQueries.cpvQuery(Ontology.instancePrefix + "cpvCodes/" + cpvCode), graph);						
							ResultSet cpvDescRes2 = vqeCpvDesc2.execSelect();								
							while(cpvDescRes2.hasNext()){ //GAMW TO DIA
								QuerySolution cpvRs2 = cpvDescRes2.nextSolution();
								try{
									Literal cpvGreekSubject = cpvRs2.getLiteral("cpvSubject");
									cgs = cpvGreekSubject.getString();
									Literal cpvEngSubject = cpvRs2.getLiteral("cpvEngSubject");
									ces = cpvEngSubject.getString();
								}catch(Exception e){}
							}
							vqeCpvDesc2.close();					
						}
						if(colour==null)
							colour = cpvColours.getColourFromCPV("undefined");
						topPayeeForPayer.setColor(colour);
						payeesList.add(topPayeeForPayer);
	 		   		
	 		    								
		 	   }
		 	   vqePayees.close();
		 	   PaymentAgent fakeRest = new PaymentAgent();
		 	   Long restLong = new Long(sum.getLong()-payeeAgg);
		 	   fakeRest.setColor("#F52887").setEnglishName("Rest").setEngShortName("Rest").setPaymentAgentName("Υπόλοιποι").setGreekShortName("Υπόλοιποι").setSum(restLong.toString());
		 	   payeesList.add(fakeRest);
		 	   agentMap.put(topPayer, payeesList);
		 	   
		}
		vqe.close();
		try{
			  // Create file 
			  FileWriter fstreamBar = new FileWriter(rootFolder + "/bubbles/mainBubble.json");			  
			  BufferedWriter outBar = new BufferedWriter(fstreamBar);			 
			  JsonWriter jw = new JsonWriter(); 
			  outBar.write(jw.jsonMainBubbleWriter(agentMap, agg.toString())); 
			  //Close the output stream
			  outBar.close();			  
			  }catch (Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
			  }					
		
	}
	
	//HELPER METHODS
	/*
	 * This method is used to sort an array containing string dates of the same timeframe
	 * 
	 * @dates A string array containing the dates to be compared
	 * @return A string array with sorted dates
	 */
	public String[] dateComparison(String[] dates){
				
			int[] datesInt = new int[dates.length];
			int i = 0;
			int weekFlag = 0;
			for(String date : dates){										
				if(date.contains("w"))
					weekFlag = 1 ;								
				datesInt[i] = Integer.parseInt(date.replace("-", "").replace("w", ""));							
				i++;				
									
			}			
			Arrays.sort(datesInt);
			String[] datesStr = new String[dates.length];
			i = 0;
			for(int dateIntS : datesInt){				
				Integer integ = new Integer(dateIntS);
				String integStr = integ.toString();				
				//Case Year
				if(integStr.length()==4)
					datesStr[i] = integStr;					
				//Case Month or Week depending on weekFlag
				else if(integStr.length()==6 && weekFlag == 0)
					datesStr[i] = integStr.substring(0, 4).concat("-").concat(integStr.substring(4, 6));
				else if(integStr.length()==6 && weekFlag == 1)
					datesStr[i] = integStr.substring(0, 4).concat("-w-").concat(integStr.substring(4, 6));
				//Case Day
				else if(integStr.length()==8)
					datesStr[i] = integStr.substring(0, 4).concat("-").concat(integStr.substring(4, 6).concat("-").concat(integStr.substring(6, 8)));
				i++;
			}
			return(datesStr);
		}
			
	public static ArrayList<String> dateComparison2(String[] dates, String timeframe){
		
		int[] datesInt = new int[dates.length];
		int i = 0;
		int weekFlag = 0, oldest = -1, today = -1;
		String flag = "";
		if(timeframe.equals("day")){
			oldest = oldestDay;
			today = todayDay;
		}
		else if(timeframe.equals("week")){
			oldest = oldestWeek;
			today = todayWeek;
		}
		else if(timeframe.equals("month")){
			oldest = oldestMonth;
			today = todayMonth;
		}
		else if(timeframe.equals("year")){
			oldest = oldestYear;
			today = todayYear;
		}
		
		for(String date : dates){				

			if(date.contains("w"))
				weekFlag = 1 ;								
			datesInt[i] = Integer.parseInt(date.replace("-", "").replace("w", ""));							
			i++;				
								
		}			
		Arrays.sort(datesInt);
		for(int s=0;s<datesInt.length;s++){
			if(datesInt[s]<oldest || datesInt[s]>today)
				datesInt[s] = 0;
		}
		ArrayList<String> datesStr = new ArrayList<String>();
		i = 0;
		for(int dateIntS : datesInt){				
			Integer integ = new Integer(dateIntS);
			String integStr = integ.toString();				
			//Case Year
			if(integStr.length()==4)
				datesStr.add(integStr);					
			//Case Month or Week depending on weekFlag
			else if(integStr.length()==6 && weekFlag == 0)
				datesStr.add(integStr.substring(0, 4).concat("-").concat(integStr.substring(4, 6)));
			else if(integStr.length()==6 && weekFlag == 1)
				datesStr.add(integStr.substring(0, 4).concat("-w-").concat(integStr.substring(4, 6)));
			//Case Day
			else if(integStr.length()==8)
				datesStr.add(integStr.substring(0, 4).concat("-").concat(integStr.substring(4, 6).concat("-").concat(integStr.substring(6, 8))));
			i++;
		}
		return(datesStr);
	}
	
	
	public ArrayList<String> getLastIPeriods(Date date, String timeframe, int limit){
		
		ArrayList<String> lastIDates = new ArrayList<String>();
		if(timeframe.equals("day")){			
			Calendar cal = new GregorianCalendar();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			cal.setTime(date);
			lastIDates.add(df.format(cal.getTime()));
			for(int i=0;i<limit-1;i++){
				cal.add(Calendar.DAY_OF_MONTH, -1);				
				lastIDates.add(df.format(cal.getTime()));
			}
		}
		else if(timeframe.equals("month")){			
			Calendar cal = new GregorianCalendar();
			DateFormat df = new SimpleDateFormat("yyyy-MM");
			cal.setTime(date);
			lastIDates.add(df.format(cal.getTime()));
			for(int i=0;i<limit-1;i++){
				cal.add(Calendar.MONTH, -1);
				lastIDates.add(df.format(cal.getTime()));
			}
		}
		else if(timeframe.equals("year")){			
			Calendar cal = new GregorianCalendar();
			DateFormat df = new SimpleDateFormat("yyyy");
			cal.setTime(date);
			lastIDates.add(df.format(cal.getTime()));
			for(int i=0;i<limit-1;i++){
				cal.add(Calendar.YEAR, -1);
				lastIDates.add(df.format(cal.getTime()));
			}
		}
		else if(timeframe.equals("week")){
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateFormat df1 = new SimpleDateFormat("yyyy");
			String week = DailyRoutineHandler.getWeek(df.format(date));
			Integer weekInt = Integer.parseInt(week);
			if(weekInt>limit-1){				
				lastIDates.add(df1.format(date)+"-w-"+week);
				for(int i=0;i<limit-1;i++){
					weekInt-=1;
					String wStr;
					if(weekInt<limit-1)
						wStr = "0".concat(weekInt.toString());
					else
						wStr = weekInt.toString();
					lastIDates.add(df1.format(date) + "-w-" + wStr);					
				}					
			}
			else{
				//
				Calendar cal = new GregorianCalendar();
				cal.setTime(date);	
				String prevWeek = df.format(cal.getTime());
				String yearNumber = df1.format(cal.getTime());
				String prevWeekNumber = DailyRoutineHandler.getWeek(prevWeek);
				lastIDates.add((yearNumber+"-w-"+prevWeekNumber));
				for(int i=0;i<limit;i++){
					cal.add(Calendar.DAY_OF_YEAR, -7);
					prevWeek = df.format(cal.getTime());
					yearNumber = df1.format(cal.getTime());
					prevWeekNumber = DailyRoutineHandler.getWeek(prevWeek);
					lastIDates.add((yearNumber+"-w-"+prevWeekNumber));
				}			
			}
			
		}
		return lastIDates;
	}
	
	
	
public String getMilliseconds(String date, String timeframe){
		
		Long millis = new Long(0);
		if(timeframe.equals("day")){
			try{
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");			
				Date d = df.parse(date);
				millis = d.getTime();
			}catch(Exception e){}
		}
		else if(timeframe.equals("month")){
			try{
				DateFormat df = new SimpleDateFormat("yyyy-MM");			
				Date d = df.parse(date);
				millis = d.getTime();
			}catch(Exception e){}
		}
		else if(timeframe.equals("year")){
			try{
				DateFormat df = new SimpleDateFormat("yyyy");			
				Date d = df.parse(date);
				millis = d.getTime();
			}catch(Exception e){}
		}
		else if(timeframe.equals("week")){
			try{
				String year = date.substring(0,4);
				String week = date.substring(date.indexOf("w")+2);
				//System.out.println(year + " week " + week);
				Integer weekInt = Integer.parseInt(week);
				Integer dayInt = (weekInt-1)*7 + 1;
				Calendar calendar = Calendar.getInstance();				
				calendar.set(Calendar.DAY_OF_YEAR, dayInt);		
				calendar.set(Calendar.YEAR, Integer.parseInt(year));
				millis = calendar.getTimeInMillis();
				/*System.out.println("Day of year " + dayInt + " = "				
				+ calendar.getTime());*/
				/*DateFormat df = new SimpleDateFormat("yyyy-MM");			
				Date d = df.parse(date);
				millis = d.getTime();*/
			}catch(Exception e){}
		}
		return millis.toString();
	}

	public String getWeek(String day){
	
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try{
			Date date = df.parse(day);			
			Calendar now = Calendar.getInstance();					
			now.clear();
			now.setTime(date);			
			Integer d = now.get(Calendar.DAY_OF_YEAR)/7 + 1;			
			String dStr = d.toString();
			if(d<10) return "0".concat(dStr);
			else return dStr;
			
		}
		catch(Exception e){e.printStackTrace();}
		return "";
	
}
	
	public static String toHexString(Color c) {
		  StringBuilder sb = new StringBuilder('#');

		  if (c.getRed() < 16) sb.append('0');
		  sb.append(Integer.toHexString(c.getRed()));

		  if (c.getGreen() < 16) sb.append('0');
		  sb.append(Integer.toHexString(c.getGreen()));

		  if (c.getBlue() < 16) sb.append('0');
		  sb.append(Integer.toHexString(c.getBlue()));		  
		  return "#"+sb.toString();
		}
	
	public String[] getTimeInterval(String dateString, String timeframe){
		
			String[] interval = {"", ""};
			Locale GREEK_LOCALE = new Locale("el","GR");
			if(timeframe.equals("week")){
				SimpleDateFormat dayFormat2 = new SimpleDateFormat("dd/MM/yyyy");
				Date date = new Date(Long.valueOf(getMilliseconds(dateString, "week")));
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				cal.add(Calendar.DATE,7);
				Date date2 = cal.getTime();
				interval[0] = dayFormat2.format(date) + " - " + dayFormat2.format(date2);
				interval[1] = dayFormat2.format(date) + " - " + dayFormat2.format(date2);
			}
			else if(timeframe.equals("day")){
				SimpleDateFormat dayFormat2 = new SimpleDateFormat("dd/MM/yyyy");
				Date date = new Date(Long.valueOf(getMilliseconds(dateString, "day")));			
				interval[0] = dayFormat2.format(date);
				interval[1] = dayFormat2.format(date);
			}
			else if(timeframe.equals("month")){
				SimpleDateFormat dayFormat2 = new SimpleDateFormat("MMMM yyyy", GREEK_LOCALE);
				SimpleDateFormat dayFormat3 = new SimpleDateFormat("MMMM yyyy");
				Date date = new Date(Long.valueOf(getMilliseconds(dateString, "month")));			
				interval[0] = dayFormat2.format(date);
				interval[1] = dayFormat3.format(date);
			}
			else if(timeframe.equals("year")){
				SimpleDateFormat dayFormat2 = new SimpleDateFormat("yyyy");
				Date date = new Date(Long.valueOf(getMilliseconds(dateString, "year")));			
				interval[0] = dayFormat2.format(date);
				interval[1] = dayFormat2.format(date);
			}
			return interval;

}
	
	public String[] getAgentNames(String agent){
			
			String shortNameS = "", engShortNameS = "", nameS = "", engNameS = "";
			String nameQuery = SPARQLQueries.nameQuery(agent.toString().replace(" ", ""));		 			
		   	VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory.create (nameQuery, graph);
		   	//System.out.println(nameQuery);
		   	ResultSet nameResults = vqeNames.execSelect();	 		    
		   	while(nameResults.hasNext()){
		   		QuerySolution nameRs = nameResults.next();
		   		Literal gsisName = nameRs.getLiteral("gsisName"), 
		   				name = nameRs.getLiteral("name"), 
		   				shortName = nameRs.getLiteral("shortName"), 
		   				engName = nameRs.getLiteral("engName"), 
		   				engShortName = nameRs.getLiteral("engShortName");
		   		try{
		   			shortNameS = shortName.getString();
		   			engShortNameS = engShortName.getString();
		   			engNameS = engName.getString();
		   		}catch(Exception e){}
		   		
		   		if(gsisName!=null && !gsisName.equals("Null")){			 		   			
		   			nameS = gsisName.getString();
		   		}
		   		else{			 		   			
		   			nameS = name.getString();
		   		}				
		   		if(shortNameS.equals("")){
		   			shortNamesMap.put(agent.toString(), nameS);
		   			engNameS = greeklish.greeklishGenerator(nameS);
	 		   	if(nameS.length()>12)
 		   			shortNameS = nameS.substring(0,10) + "...";
 		   		else 
 		   			shortNameS = nameS;
		   		if(engNameS.length()>12)
		   			engShortNameS = engNameS.substring(0,10) + "...";
		   		else
		   			engShortNameS = engNameS;		 	
		   		}		   	
		   	}
		   	vqeNames.close();
		   	return new String[] {nameS.replace("\"", ""), shortNameS.replace("\"", ""), engNameS.replace("\"", ""), engShortNameS.replace("\"", "")};
}
	
	public String[] getAgentNamesByAfm(String afm){
		
		String nameS = "", shortNameS = "", engNameS = "", engShortNameS = "";
		String query = SPARQLQueries.prefixes + "SELECT ?name FROM <"+DailyRoutineHandler.graphName+"> WHERE {?agent psgr:afm \""+afm+"\" . ?agent psgr:paymentAgentName ?name .}LIMIT 1";			
		VirtuosoQueryExecution vqeNames = VirtuosoQueryExecutionFactory.create(query, graph);	   	
	   	ResultSet nameResults = vqeNames.execSelect();
	   	while(nameResults.hasNext()){	   		
	   		QuerySolution rs = nameResults.nextSolution();	   		
	   		nameS = rs.getLiteral("name").getString();	   		
	   		engNameS = greeklish.greeklishGenerator(nameS);
	   		if(nameS.length()>12){
		   		shortNameS = nameS.substring(0,10) + "...";		   		
	   		}
		   	else 
		   		shortNameS = nameS;
	   		if(engNameS.length()>12)
	   			engShortNameS = engNameS.substring(0,10) + "...";
	   		else
	   			engShortNameS = engNameS;		 		   			
	   	}
	   	vqeNames.close();	   	
	   	return new String[] {nameS.replace("\"", ""), shortNameS.replace("\"", ""), engNameS.replace("\"", ""), engShortNameS.replace("\"", "")};
	   	
	}
	
	static String decorateAmount(String amount){		
		String regex = "(\\d)(?=(\\d{3})+$)";
		String amountInt = amount;
		String amountDec = "";
		if(amount.contains(".")){
			amountInt = amount.substring(0, amount.indexOf("."));
			amountDec = amount.substring(amount.indexOf("."));
		}
		//return amountInt.replaceAll(regex, "$1,")+amountDec;
		return amountInt.replaceAll(regex, "$1,");    
	}
}
