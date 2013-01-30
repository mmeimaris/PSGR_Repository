package publicspending.java.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import publicspending.java.daily.GreeklishFactory;
import publicspending.java.daily.HelperMethods;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import diavgeia.java.ontology.Ontology;




public class Tests {
	
	public Tests(){

		/*String query = "SELECT DISTINCT ?week ?date where {?week a psgr:Week ; psgr:date ?date}";
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		System.out.println("Connected. Weeks.");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,graph);
		ResultSet results = vqe.execSelect();		
		Model toWrite = ModelFactory.createDefaultModel();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		while(results.hasNext()){
			QuerySolution rs = results.nextSolution();
			RDFNode weekR = rs.get("week");
			Literal date = rs.getLiteral("date");
			String week = date.getString();
			Calendar calendar = Calendar.getInstance();
			Long now = Long.parseLong(getMilliseconds(week, "week"));
	        calendar.setTimeInMillis(now);	 
	        String dateR = formatter.format(calendar.getTime());
	        System.out.println(now + " = " + dateR);
	        toWrite.createResource(weekR.toString()).addProperty(ResourceFactory.createProperty(Ontology.diavgeiaPrefix+"weekRepDate"), dateR);
		}vqe.close();
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/weekRepDates.rdf");						
			toWrite.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/weekRepDates.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		toWrite.close();
		graph.close();*/
	}
	
	public void kotsidas(){

		String dbtime;
		String dbUrl = "jdbc:mysql://dbo8.diavgeia.gov.gr:3306";
		String dbClass = "com.mysql.jdbc.Driver";
		String query = "Select * FROM apofaseis LIMIT 10";

		try {

		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection (dbUrl, "opendata", "opendata@diavgeia");
		Statement stmt = con.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery(query);

		while (rs.next()) {
		dbtime = rs.getString(1);
		System.out.println(dbtime);
		} //end while

		con.close();
		} //end try

		catch(ClassNotFoundException e) {
		e.printStackTrace();
		}

		catch(SQLException e) {
		e.printStackTrace();
		}
	}
	
	public String[] listFilesForFolder(final File folder) {
		String[] fileNames = new String[10];
		int i = -1;
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            System.out.println(fileEntry.getName());
	            i++;
	            fileNames[i] = fileEntry.getName();
	        }
	    }
	    return fileNames;
	}
	public void addMissingDates(){
		try{
			Date todayDate = new Date();		
			//Date date = new Date(2012-1900, 05, 22);
			int MILLIS_IN_DAY = 1000 * 60 * 60 * 24, flag =0 ;
			SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date today = new Date();
			Date refdate = new Date(2010-1900, 8, 30);
			ArrayList<String> dateList = new ArrayList<String>();
			while(flag==0){
				refdate = new Date(refdate.getTime()+MILLIS_IN_DAY);
				System.out.println(dayFormat.format(refdate));
				dateList.add(dayFormat.format(refdate));
				if(dayFormat.format(refdate).equals(dayFormat.format(todayDate))) flag = 1 ;
			}
			String folderPath = "C:/Users/Marios/Desktop/Diavgeia-Root/klonaras";
			File folder = new File(folderPath);
			String[] fileNames = listFilesForFolder(folder);
			
			CSVWriter writerAll = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/Diavgeia-Root/klonaras/new Overall.csv"), ';');
			String[] fileNames2 = new String[11];
			fileNames2[0] = "date";
			for(int i=0;i<11;i++){
				fileNames2[i+1] =  fileNames[i];
			}
			writerAll.writeNext(fileNames2);	
			ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
			HashMap<String, String[]> allDateMap = new HashMap<String, String[]>();
			int files = 0;
			for(String fileName : fileNames){
				//System.out.println(fileName);
				CSVReader reader = new CSVReader(new FileReader("C:/Users/Marios/Desktop/Diavgeia-Root/klonaras/"+fileName), ',');
				CSVWriter writer = new CSVWriter(new FileWriter("C:/Users/Marios/Desktop/Diavgeia-Root/klonaras/new "+fileName), ';');
				writer.writeNext(new String[]{"date", "sum"});
				String[] nextLine;  
				int count = 0;
				HashMap<String, String> dateMap = new HashMap<String, String>();
	    		try{
	    	 		while((nextLine = reader.readNext())!= null){    	 			
	    	 				if(count==0){count++;}	    	 			
	    	 				else{
	    	 					String dateRow = nextLine[0];
	    	 					dateMap.put(dateRow, nextLine[1]);	    	 					
	    	 					count++;	    	 					
	    	 				}
	    	 		}	    	 		
	    		}catch(Exception e){e.printStackTrace();}
	    		reader.close();
	    		Iterator it = dateList.iterator();
				while(it.hasNext()){
					String nextDate = (String)it.next();
					if(!dateMap.containsKey(nextDate)){
						writer.writeNext(new String[]{nextDate, "0"});
						dateMap.put(nextDate, "0");
					}
					else{
						writer.writeNext(new String[] {nextDate, dateMap.get(nextDate)});
					}
					
				}
				writer.close();
				mapList.add(dateMap);
				files++;
			}
			
			
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	public void readTenForce(){
		Model testModel = ModelFactory.createDefaultModel();
		//String loc = "C:/Users/Marios/Desktop/a-test-dataset.rdf";
		String loc = "C:/Users/Marios/Desktop/skata.rdf";
		try {						 				
			InputStream in = FileManager.get().open(loc);
			if (in == null) {
			     throw new IllegalArgumentException(
			                                  "File: " + "C:/Users/Marios/Desktop/a-test-dataset.rdf" + " not found");
			}
			
			//testModel.write(System.out);
			//JenaParameters.enableEagerLiteralValidation = true;			
			//JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = true;
			
			String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
			VirtGraph graph = new VirtGraph ("tenforce.test", connectionString, "marios", "dirtymarios");
			VirtModel model = new VirtModel(graph);
			//com.hp.hpl.jena.shared.impl.JenaParameters.enableEagerLiteralValidation = false;
			//com.hp.hpl.jena.shared.impl.JenaParameters.enableSilentAcceptanceOfUnknownDatatypes = true;			
			//testModel.read(in, null);
			//FileOutputStream fos2 = new FileOutputStream("C:/Users/Marios/Desktop/skata.rdf");
			//invalidModel.write(fos2, "RDF/XML-ABBREV", invalidRdfLocationVariable+folderDateString2+".rdf");
			//testModel.write(fos2, "RDF/XML-ABBREV", "C:/Users/Marios/Desktop/skata.rdf");
			/*RDFReader rr = model.getReader();			
			rr.setProperty("error-mode", "lax");
	        rr.setProperty("ERR_BAD_RDF_ATTRIBUTE", "EM_IGNORE");
			rr.setProperty("http://xml.org/sax/properties/lexical-handler", null);
	        rr.setProperty("http://xml.org/sax/features/validation", false);
	        rr.setProperty("http://apache.org/xml/features/validation/schema", false);
	        rr.setProperty("WARN_UNKNOWN_PARSETYPE", "EM_IGNORE");
	        rr.setProperty("ERR_SAX_ERROR", "EM_IGNORE");
	        rr.setProperty("ERR_SAX_FATAL_ERROR", "EM_IGNORE");
			rr.read(model, in, null);*/			
			//testModel.read(in,null);			
			//model.add(testModel);
			model.read(in, null);
			testModel.close();	
			model.close();
										
		}        
    	catch(Exception e){e.printStackTrace();}
				
	}
	
	public void dailyTimeplots(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		System.out.println("Connected. Timestamps.");
		String query = "SELECT DISTINCT ?timestamp WHERE {?decision psgr:submissionTimeStamp ?timestamp}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,graph);
		ResultSet results = vqe.execSelect();
		//Model model = ModelFactory.createDefaultModel();
		HashSet<String> dates = new HashSet<String>();
		int i = 0;
		while(results.hasNext()){			
			QuerySolution rs = results.next();
			Literal timestamp = rs.getLiteral("timestamp");
			dates.add(timestamp.getString().substring(0,10));					
		}vqe.close();
		System.out.println(dates.size());
		
	}
	
	public void setCpaResources(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		System.out.println("Connected. CPA Resources.");
		String query = "SELECT DISTINCT ?agent ?cpa WHERE {?agent a psgr:PaymentAgent " +
													       "; psgr:cpaCode ?cpaCode ." +
													       "?cpa a psgr:CPA ; psgr:cpaCode ?cpaCode .} ";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,graph);
		ResultSet results = vqe.execSelect();
		Model model = ModelFactory.createDefaultModel();
		int i = 0;
		while(results.hasNext()){			
			QuerySolution rs = results.next();
			RDFNode agent = rs.get("agent");
			RDFNode cpa = rs.get("cpa");
			model.createResource(agent.toString()).addProperty(ResourceFactory.createProperty(Ontology.diavgeiaPrefix+"cpa"), model.createResource(cpa.toString()));
			i++;
		}vqe.close();
		try{
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Diavgeia-Root/cpaResourceAgents.rdf");
			model.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Diavgeia-Root/cpaResourceAgents.rdf");
		}
		catch(Exception e){}
		System.out.println(i);
	}
	
	public void getDebts(){
		int count = 0;
		String[] fields;
		HelperMethods hm = new HelperMethods();
		Model model = ModelFactory.createDefaultModel();
		try{			
    		CSVReader reader = new CSVReader(new FileReader("C:/Users/Marios/Desktop/Diavgeia-Root/2012-12 companies.csv"), ',');    		
    		String[] nextLine;    	     	     		    	
    		try{
    	 		while((nextLine = reader.readNext())!= null){    	 			
    	 				if(count==0){
    	 					fields = new String[nextLine.length];
    	 					for(int i=0;i<nextLine.length;i++){    	 						
    	 						fields[i] = nextLine[i];        	 						
    	 					}    	 					
    	 					System.out.println(Arrays.toString(fields));    	 					
    	 				}
    	 				//else if(count<=4396){
    	 				else{
    	 					String afm = nextLine[0];
    	 					if(afm.length()==8)
    	 						afm = "0"+afm;   
    	 					//System.out.println(afm);
    	 					afm = afm.replace(" ", "");
    	 					Resource debtResource = model.createResource(Ontology.instancePrefix+"Debts/"+afm+"/2012-12/", ResourceFactory.createResource(Ontology.diavgeiaPrefix+"PublicDebt"));
    	 					debtResource.addProperty(ResourceFactory.createProperty(Ontology.diavgeiaPrefix+"debtAmount"), hm.decoratePaymentAmount(nextLine[7]));
    	 					debtResource.addProperty(Ontology.date, "2012-11-16");
    	 					model.createResource("http://publicspending.medialab.ntua.gr/paymentAgents/" + afm).addProperty(RDF.type, ResourceFactory.createResource("http://publicspending.medialab.ntua.gr/ontology#PaymentAgent"))
    	 					.addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#publicDebt"), debtResource); 
    	 					//System.out.println("count: " + count + " AFM: " + afm + " with total: " + decoratePaymentAmount(nextLine[1]));
    	 				}    	 				    	 			
    	 				count++;        	 				    	 					
    	 		}    	 		    	 		    	 		
    	 		reader.close();    	 		
    	 	}    		
    	 	catch(IOException e){
    	 		e.printStackTrace();    	 		    	 		
    	 	}    		
    		try{
    			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Diavgeia-Root/debtor_companies.rdf");
    			model.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Diavgeia-Root/debtor_companies.rdf");
    		}
    		catch(Exception e){}
    	  
    	}
    	catch(FileNotFoundException e){ 
    		e.printStackTrace();    		
    	}	
	}
	
	public void dateDebts(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		System.out.println("Connected. Old debts.");
		String query = "SELECT DISTINCT ?agent ?totalDebt WHERE {?agent psgr:totalDebt ?totalDebt} ";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,graph);
		ResultSet results = vqe.execSelect();
		Model debtModel = ModelFactory.createDefaultModel();
		while(results.hasNext()){			
			QuerySolution rs = results.next();
			RDFNode agent = rs.get("agent");
			Literal debt = rs.getLiteral("totalDebt");
			//System.out.println(agent.toString());
			String afm = agent.toString().substring(agent.toString().indexOf("paymentAgents/")+1);
			
			Resource debtResource = debtModel.createResource(Ontology.instancePrefix+"Debts/"+afm+"/2012-06/", ResourceFactory.createResource(Ontology.diavgeiaPrefix+"PublicDebt"));
			debtResource.addProperty(ResourceFactory.createProperty(Ontology.diavgeiaPrefix+"debtAmount"), debt.getString());
			debtResource.addProperty(Ontology.date, "2012-06-01");
			debtModel.createResource(agent.toString()).addProperty(ResourceFactory.createProperty(Ontology.diavgeiaPrefix+"publicDebt"), debtResource);
		}vqe.close();
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/Marios/Desktop/Diavgeia-Root/oldDebts.rdf");						
			debtModel.write(fos, "RDF/XML-ABBREV", "C:/Users/Marios/Desktop/Diavgeia-Root/oldDebts.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		debtModel.close();
	}
	
	public void labels(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		System.out.println("Connected. DOYs.");
		String query = "SELECT ?agent ?validName ?validEngName WHERE {?agent psgr:validName ?validName ; psgr:validEngName ?validEngName}OFFSET 150000 ";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,graph);
		ResultSet results = vqe.execSelect();
		Model doyModel = ModelFactory.createDefaultModel();
		while(results.hasNext()){			
			QuerySolution rs = results.next();
			Literal greek = rs.getLiteral("validName");
			Literal eng = rs.getLiteral("validEngName");	
			RDFNode agent = rs.get("agent");
			doyModel.createResource(agent.toString()).addProperty(RDFS.label, doyModel.createLiteral(greek.getString(), "el"))
				.addProperty(RDFS.label, doyModel.createLiteral(eng.getString(), "en"));
		}vqe.close();
		graph.close();
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/Marios/Desktop/Diavgeia-Root/labels.rdf");						
			doyModel.write(fos, "RDF/XML-ABBREV", "C:/Users/Marios/Desktop/Diavgeia-Root/labels.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		doyModel.close();
	}
	
	public void createDoyResources(){
		
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		System.out.println("Connected. DOYs.");
		String query = "SELECT DISTINCT ?doy ?doyName WHERE {?a psgr:doy ?doy ; psgr:doyName ?doyName filter(?doy!=\"Null\")}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,graph);
		ResultSet results = vqe.execSelect();
		Model doyModel = ModelFactory.createDefaultModel();
		while(results.hasNext()){			
			QuerySolution rs = results.next();
			Literal doy = rs.getLiteral("doy");
			Literal doyName = rs.getLiteral("doyName");
			System.out.println(doy + " --- " + doyName);
			doyModel.createResource(Ontology.instancePrefix + "doy/"+doy, ResourceFactory.createResource(Ontology.diavgeiaPrefix+"Doy")).addProperty(Ontology.doyName, doyName);
		}vqe.close();
		graph.close();
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/Marios/Desktop/Diavgeia-Root/doy.rdf");						
			doyModel.write(fos, "RDF/XML-ABBREV", "C:/Users/Marios/Desktop/Diavgeia-Root/doy.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		doyModel.close();
		
	}
	
	public void changeToTimestamp(){
		
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		System.out.println("Connected. Timestamps.");
		String query = "SELECT distinct ?d (xsd:date(?dat) as ?date) WHERE {?d psgr:submissionTimeStamp ?dat}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,graph);
		ResultSet results = vqe.execSelect();		
		Model toWrite = ModelFactory.createDefaultModel();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		HelperMethods hm = new HelperMethods();
		int count = 0;
		CSVWriter writer = null;
		try {
			writer = new CSVWriter(new FileWriter("C:/Users/marios/Desktop/new dates.csv"), ';');
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(results.hasNext()){
			QuerySolution rs = results.nextSolution();
			RDFNode decision = rs.get("d");
			Literal date = rs.getLiteral("date");
			String[] dateElements = hm.getDateElements(date.getString());
			writer.writeNext(new String[] {decision.toString(), date.getString(), dateElements[0],dateElements[1], dateElements[2], dateElements[3]});
			/*if (count==0) System.out.println(Arrays.toString(dateElements));
			Resource day = toWrite.createResource(Ontology.instancePrefix+"days/"+dateElements[2]);//.addProperty(RDF.type, Ontology.dayResource).addProperty(Ontology.date, dateElements[2]);
			Resource month = toWrite.createResource(Ontology.instancePrefix+"months/"+dateElements[1]);//.addProperty(RDF.type, Ontology.monthResource).addProperty(Ontology.date, dateElements[1]);
			Resource year = toWrite.createResource(Ontology.instancePrefix+"years/"+dateElements[0]);//.addProperty(RDF.type, Ontology.yearResource).addProperty(Ontology.date, dateElements[0]);		
			//Weeks need special treatment
			Resource week = toWrite.createResource(Ontology.instancePrefix+"weeks/"+dateElements[0]+"-w-"+dateElements[3]);//.addProperty(RDF.type, Ontology.weekResource).addProperty(Ontology.date, dateElements[0]+"-w-"+dateElements[3]);
			toWrite.createResource(decision.toString()).
				addProperty(Ontology.day,day).
				addProperty(Ontology.month, month).
				addProperty(Ontology.year, year).
				addProperty(Ontology.week, week);			*/
			count++;
		}vqe.close();
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/new dates.rdf");						
			toWrite.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/new dates.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		toWrite.close();*/
		graph.close();
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
	
	public void exportDump(){
		
		String query = "SELECT ?decision ?submission ?pdf ?url ?subject ?ada ?payment ?payer ?payee ?amount ?cpv ?weso WHERE {" +
					   "?decision psgr:submissionTimeStamp ?submission ; psgr:documentUrl ?pdf ; psgr:url ?url ;" +
					   "psgr:subject ?subject ; psgr:ada ?ada ; psgr:refersTo ?payment . ?payment psgr:payer ?payer ; psgr:payee ?payee ; psgr:paymentAmount ?amount ; psgr:cpv ?cpv ;"+
						  "psgr:validCpv \"true\" . ?payer psgr:validAfm \"true\" . ?payee psgr:validAfm \"true\". OPTIONAL{?cpv owl:sameAs ?weso} filter(xsd:date(?submission)>=xsd:date(\"2011-10-01\")) filter(xsd:date(?submission)<xsd:date(\"2012-11-15\"))}";
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		System.out.println("Connected. Attempting to extract dump.");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query,graph);
		ResultSet results = vqe.execSelect();
		int count = 0;
		Model toWrite = ModelFactory.createDefaultModel();
		while(results.hasNext()){
			QuerySolution rs = results.nextSolution();
			RDFNode decision = rs.get("decision");
			Literal subm = rs.getLiteral("submission");
			Literal pdf = rs.getLiteral("pdf");
			Literal url = rs.getLiteral("url");
			Literal ada = rs.getLiteral("ada");
			Literal subject = rs.getLiteral("subject");
			RDFNode payment = rs.get("payment");
			Resource payRes = toWrite.createResource(payment.toString(), Ontology.paymentResource);
			/*String payQ = "SELECT ?payer ?payee ?amount ?cpv ?cpvValidity ?weso WHERE {" +
						  "<"+payment.toString()+"> psgr:payer ?payer ; psgr:payee ?payee ; psgr:paymentAmount ?amount ; psgr:cpv ?cpv ;"+
						  "psgr:validCpv ?cpvValidity . OPTIONAL{?cpv owl:sameAs ?weso}}LIMIT 1";
			
			VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (payQ,graph);
			ResultSet results2 = vqe2.execSelect();
			while(results2.hasNext()){
				QuerySolution rs2 = results2.nextSolution();*/
				RDFNode payer = rs.get("payer");
				RDFNode payee = rs.get("payee");
				Literal amount = rs.getLiteral("amount");
				
				RDFNode cpv = rs.get("cpv");
				RDFNode weso = null;
				try{
					 weso = rs.get("weso");
				}catch(Exception e){}
				payRes.addProperty(Ontology.payer, payer.toString())
					  .addProperty(Ontology.payee, payee.toString())
					  .addProperty(Ontology.paymentAmount, amount.getString())
					  .addProperty(Ontology.cpv, cpv.toString())
					  ;
				if(weso!=null) {toWrite.createResource(cpv.toString(), ResourceFactory.createResource(Ontology.diavgeiaPrefix+"CPV")).addProperty(OWL.sameAs, weso.toString());}
			/*		
			}vqe2.close();	*/		
			Resource decRes = toWrite.createResource(decision.toString(), Ontology.decisionResource)
					                 .addProperty(Ontology.submissionTimeStamp, subm.getString())
					                 .addProperty(Ontology.documentUrl, pdf.getString())
					                 .addProperty(Ontology.url, url.getString())
					                 .addProperty(Ontology.ada, ada.getString())
					                 .addProperty(Ontology.subject, subject.getString())
					                 .addProperty(Ontology.refersTo, payRes);
		}vqe.close();
		System.out.println("Done creating model. Attempting to write...");
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/dump2010.rdf");						
			toWrite.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/dump2010.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		toWrite.close();
	}
	
	
	
	public void specialCPAEng(){
		String query = "select ?cpa ?two ?four ?six where { ?cpa a psgr:CPA ; psgr:cpaGreekSubject ?cpaS " +
				"; psgr:cpaTwoDigits ?two ; psgr:cpaFourDigits ?four ;d:cpaSixDigits ?six ." +
				"OPTIONAL{?cpa psgr:cpaEnglishSubject ?cpaEn} FILTER(!BOUND(?cpaEn))}";
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		//Model remoteModel = new VirtModel(graph); 
		System.out.println("Connected. CPA Eng Fixes.");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		int count = 0;
		while(results.hasNext()){
			QuerySolution rs = results.nextSolution();
			RDFNode cpa = rs.get("cpa");
			System.out.println(cpa.toString());
			count++;
		}vqe.close();
		System.out.println(count);
	}
	
	public void handleFaultyCPAs(){
		
		String query = "select ?p ?cpaCode ?cpaSub where  { "+
					   "?p psgr:cpaCode ?cpaCode ; " + 
					   "psgr:cpaGreekSubject ?cpaSub " +
					   "filter regex(?cpaCode, \"(^|\\\\s)[0-9]{5}(\\\\s|$)\")}";
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		Model remoteModel = new VirtModel(graph); 
		System.out.println("Connected. CPA Fixes.");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		int count = 0, count2=0;
		//HashMap<String, String> map = new HashMap<String, String>();
		//Model model =  ModelFactory.createDefaultModel();
		while(results.hasNext()){
			QuerySolution rs = results.nextSolution();
			RDFNode agent = rs.get("p");
			Literal cpaSub = rs.getLiteral("cpaSub");
			Pattern p = Pattern.compile("[0-9]{8}");
			Matcher m = p.matcher(cpaSub.getString());
			while(m.find()){				
				//map.put(p.toString(), m.group());
				//model.createResource(agent.toString()).addProperty(Ontology.cpaCode, m.group());
				remoteModel.createResource(agent.toString()).removeAll(Ontology.cpaCode).addProperty(Ontology.cpaCode, m.group());
				count2++;
				//System.out.println(m.group());
			}
			count ++;
		}vqe.close();
		System.out.println(count);
		System.out.println(count2);
		System.out.println(count-count2);
		/*try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/cpaFixes.rdf");						
			model.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/cpaFixes.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		model.close();*/
		System.out.println("Done");
		
			
	}
	
	public void klonaras(){
		try{
			System.out.println("Klonaras.");
			int count = 0, count2 = 0;			
			String csvLocation = "C:/Users/Marios/Downloads/all payments.csv";				
			CSVReader reader = new CSVReader(new FileReader(csvLocation), ',', '\"');
			//CSVReader reader = new CSVReader(new FileReader(csvLocation));
			String[] nextLine;
			HashMap<String, String> payersMap = new HashMap<String, String>();
			HashMap<String, String> payeesMap = new HashMap<String, String>();
			//try{
		 		while((nextLine = reader.readNext())!= null){
		 				if(count==0) count++;
		 				else{
			 				String payerURI = nextLine[1];
			 				String payerName = nextLine[2];
			 				payersMap.put(payerURI, payerName);
			 				String payeeURI = nextLine[3];
			 				String payeeName = nextLine[4];
			 				payeesMap.put(payeeURI, payeeName);
			 				count++;    
		 				}
		 		}    	 		    	 		    	 		
		 		reader.close();		 		
		 		System.out.println(count);
		 		CSVWriter writer = new CSVWriter(new FileWriter("C:/Users/marios/Downloads/payers.csv"), ';');
		 		writer.writeNext(new String[]{"PayerURI", "PayerName"});
		 		Set<String> payers = payersMap.keySet();
		 		for(String payer : payers){
		 			String[] pair = new String[] {payer, payersMap.get(payer)};
		 			writer.writeNext(pair);
		 		}
		 		writer.close();
		 		CSVWriter writer2 = new CSVWriter(new FileWriter("C:/Users/marios/Downloads/payees.csv"), ';');
		 		writer2.writeNext(new String[]{"PayeeURI", "PayeeName"});
		 		Set<String> payees = payeesMap.keySet();
		 		for(String payee : payees){
		 			String[] pair = new String[] {payee, payeesMap.get(payee)};
		 			writer2.writeNext(pair);
		 		}
		 		writer2.close();
		 	}
		 	catch(IOException e){
		 		e.printStackTrace();    	 			 		
		 	}    		
	}
	
	public void opencorporatesDemo(){
		String query = 
		"select ?agent ?name ?label ?labelGr ?street ?number ?pobox ?cpaGr ?cpaEn ?city ?phone ?stopDate ?afm from <http://publicspending.medialab.ntua.gr/Decisions> from <organizations.test> from <http://publicspending.medialab.ntua.gr/organizationsOntology> where" +
		"{?p psgr:payee ?agent ; psgr:paymentAmount ?am ." +
		"?agent psgr:gsisName ?name ; psgr:postalStreetName ?street ; psgr:cpaCode ?cpaCode ; psgr:postalStreetNumber ?number ; psgr:phoneNumber ?phone ;"+
		"d:stopDate ?stopDate ; psgr:postalCity ?city ; psgr:afm ?afm ; psgr:postalZipCode ?pobox ; psgr:legalStatus ?legal ."+
		"?agent rdf:type ?type . ?type rdfs:label ?label . ?type rdfs:label ?labelGr . ?cpa psgr:cpaCode ?cpaCode ; psgr:cpaGreekSubject ?cpaGr ; psgr:cpaEnglishSubject ?cpaEn ."+
		"filter(lang(?label)=\"en\") filter(lang(?labelGr)=\"el\") filter(?legal!=\"Null\")}"		+
		"order by desc(sum(xsd:decimal(?am))) offset 1000 LIMIT 2000";
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		System.out.println("Connected. OC Demo.");
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		//Model tempModel = ModelFactory.createDefaultModel();
		int count = 0;
		GreeklishFactory gr = new GreeklishFactory();
		String jurisdiction = "Greece";
		String source = "General Secretariat of Information Systems, http://www.gsis.gr";
		try {
			CSVWriter writer = new CSVWriter(new FileWriter("C:/Users/marios/Desktop/opencorporatesdemo.csv"), ';');
			String[] entries = "GreekName#GreekAddress#EnglishName#EnglishAddress#PhoneNumber#VATNumber#Status#GreekCompanyType#CompanyType#GreekActivity#EnglishActivity#Jurisdiction#RegistryPage#Source".split("#");        
		    writer.writeNext(entries);
		    			
		while(results.hasNext()){
			count++;
			QuerySolution rs = results.nextSolution();
			String name = rs.getLiteral("name").getString();
			String address = rs.getLiteral("street").getString() +" "+ 
				     rs.getLiteral("number").getString() +", " +  
				     rs.getLiteral("city").getString() + ", " + 
				     rs.getLiteral("pobox").getString();			
			String engname = gr.greeklishGenerator(name);
			String engaddress = gr.greeklishGenerator(address);
			String phoneNumber = rs.getLiteral("phone").getString();
			String afm = rs.getLiteral("afm").getString();
			String registryPage = rs.get("agent").toString();
			String greekCompanyType = rs.getLiteral("labelGr").getString();
			String companyType = rs.getLiteral("label").getString();
			String cpaGr = rs.getLiteral("cpaGr").getString();
			String cpaEn = rs.getLiteral("cpaEn").getString();
			String active = "Inactive" ; 
			if(rs.getLiteral("stopDate").getString().equals("Null")) active = "Active";
			String[] fields = {name, address + ", " +"ΕΛΛΑΔΑ", engname, engaddress + ", GREECE" , phoneNumber, afm, active, greekCompanyType, companyType, cpaGr, cpaEn, jurisdiction, registryPage, source};
			writer.writeNext(fields);
			
		}
		System.out.println(count);
		vqe.close();
		writer.close();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handleHellasStatLists(){		
		try{
			System.out.println("Hellas Stat.");
			int count = 0, count2 = 0;			
			String csvLocation = "C:/Users/marios/Desktop/Diavgeia/BR/afm2000.csv";				
			CSVReader reader = new CSVReader(new FileReader(csvLocation), ';', '\'');		
			String[] nextLine;
			HashSet<String> afmSet = new HashSet<String>();
			String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";		 				
			VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
			try{
		 		while((nextLine = reader.readNext())!= null){    	 			
		 				String afm = nextLine[0];
		 				String query = "SELECT ?agent where {?agent psgr:afm \"" + afm + "\"}"; 		 				
		 				VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		 				ResultSet results = vqe.execSelect();
		 				if(!results.hasNext()){		 					
		 					afmSet.add(nextLine[0]);
		 					count2++;
		 				}
		 				vqe.close();		 				
		 				count++;        	 				    	 					
		 		}    	 		    	 		    	 		
		 		reader.close();
		 		graph.close();
		 		System.out.println(count + "  " + count2);
		 		SOAPHandler sh = new SOAPHandler(afmSet);
		 	}
		 	catch(IOException e){
		 		e.printStackTrace();    	 			 		
		 	}    		
		 
		}
		catch(FileNotFoundException e){ 
			e.printStackTrace();		
		}
	}
	
	public void gsisBundling(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		System.out.println("Connected.");
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");			
		String prefixes = "PREFIX psgr:<http://publicspending.medialab.ntua.gr/ontology#> " +
		   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
		   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";
		String query = "SELECT ?legalStatus ?agent ?name ?cpaCode ?cpaSub ?doy " +
				"?doyName ?fax ?phone ?city ?street ?streetNum ?pobox " +
				"?regDate ?deactFlag ?fpFlag ?firmDesc ?branches ?stopDate " +
				"WHERE {" +
				"?agent a psgr:PaymentAgent ; psgr:validAfm \"true\" ; psgr:gsisName ?name  " +
				"; psgr:cpaCode ?cpaCode ; psgr:cpaGreekSubject ?cpaSub ; psgr:doy ?doy ; psgr:doyName ?doyName " +
				"; psgr:faxNumber ?fax ; psgr:phoneNumber ?phone ; psgr:postalCity ?city ; psgr:postalStreetName ?street ; psgr:postalStreetNumber ?streetNum" +
				"; psgr:postalZipCode ?pobox ; psgr:legalStatus ?legalStatus ; psgr:countOfBranches ?branches ; psgr:registrationDate ?regDate " +
				"; psgr:stopDate ?stopDate ; psgr:deactivationFlag ?deactFlag ; psgr:fpFlag ?fpFlag ; psgr:firmDescription ?firmDesc ."  +
				"FILTER(?name!=\"Null\")} LIMIT 1";		
	    
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model tempModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){			
			QuerySolution rsGsis = results.nextSolution();
			count++;
			RDFNode agent = rsGsis.get("agent");
			String afm = agent.toString().substring(agent.toString().length()-9);
			Literal startDate = rsGsis.getLiteral("regDate");
			Literal stopDate = rsGsis.getLiteral("stopDate");
			Literal zipCode = rsGsis.getLiteral("pobox");
			Literal streetNumber = rsGsis.getLiteral("streetNum");
			Literal streetName = rsGsis.getLiteral("street");
			Literal city = rsGsis.getLiteral("city");
			Literal cpaCode = rsGsis.getLiteral("cpaCode");
			Literal cpaGr = rsGsis.getLiteral("cpaSub");
			Literal firmDesc = rsGsis.getLiteral("firmDesc");
			Literal doy = rsGsis.getLiteral("doy");
			Literal doyName = rsGsis.getLiteral("doyName");
			Literal legalStatus = rsGsis.getLiteral("legalStatus");
			Literal fax = rsGsis.getLiteral("fax");
			Literal branches = rsGsis.getLiteral("branches");
			Literal fpFlag = rsGsis.getLiteral("fpFlag");
			Literal deactFlag = rsGsis.getLiteral("deactFlag");
			Literal name = rsGsis.getLiteral("name");
			Literal phone = rsGsis.getLiteral("phone");
			
			//System.out.println(afm);
			Resource gsisBundle = tempModel.createResource("http://publicspending.medialab.ntua.gr/resource/gsisBundles/"+afm).addProperty(RDF.type, ResourceFactory.createResource("http://publicspending.medialab.ntua.gr/ontology#GsisBundle"))
					.addProperty(Ontology.legalStatusDescription, legalStatus.getString())
					 .addProperty(Ontology.gsisName, name.getString())					 					 
					 .addProperty(Ontology.cpaCode, cpaCode.getString())
					 .addProperty(Ontology.cpaGreekSubject, cpaGr.getString())
					 .addProperty(Ontology.doy, doy.getString())
					 .addProperty(Ontology.faxNumber, fax.getString())
					 .addProperty(Ontology.phoneNumber, phone.getString())
					 .addProperty(Ontology.postalCity, city.getString())
					 .addProperty(Ontology.postalStreetName, streetName.getString())
					 .addProperty(Ontology.postalStreetNumber, streetNumber.getString())
					 .addProperty(Ontology.postalZipCode, zipCode.getString())
					 .addProperty(Ontology.registrationDate, startDate.getString())
					 .addProperty(Ontology.deactivationFlag, deactFlag.getString())
				     .addProperty(Ontology.fpFlag, fpFlag.getString())
				     .addProperty(Ontology.firmDescription, firmDesc.getString())
				     .addProperty(Ontology.countOfBranches, branches.getString())
				     .addProperty(Ontology.doyName, doyName.getString())
					 .addProperty(Ontology.stopDate, stopDate.getString());
			tempModel.createResource(agent.toString()).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#gsisBundle"), gsisBundle);
		}
		vqe.close();
		System.out.println(count);
		graph.close();		
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/gsisBundleTest.rdf");						
			tempModel.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/gsisBundleTest.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		tempModel.close();
	}
	
	

	public void nameHandling(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		System.out.println("Connected.");
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");			
		String prefixes = "PREFIX psgr:<http://publicspending.medialab.ntua.gr/ontology#> " +
		   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
		   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
		
		
		String query = prefixes + "SELECT ?agent ?gsisName where {" +
								  "?agent psgr:gsisName ?gsisName . filter(?gsisName!=\"Null\")" +
								  "OPTIONAL{?agent psgr:nameFlag ?flag} FILTER(!BOUND(?flag))" +
								  "} limit 10000";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();		
		int count = 0;
		Model model = new VirtModel(graph);
		while(results.hasNext()){
			count++;
			QuerySolution rs = results.nextSolution();			
			RDFNode agent = rs.get("agent");					
			//System.out.println(agent.toString());
			Literal name = rs.getLiteral("gsisName");
			//try{
			model.createResource(agent.asResource().removeAll(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName")).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName"), name.getString()).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#nameFlag"), "unique"));
			//}catch(Exception e){e.printStackTrace();System.out.println(agent.toString());}
			/*if(agent.toString().equals("http://publicspending.medialab.ntua.gr/resource/paymentAgents/118076847")){
				model.createResource(agent.asResource().removeAll(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName")).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName"), name.getString()));
				System.out.println("Removed...");
			}*/
		}
		vqe.close();
		System.out.println(count);
		model.close();
	}
	
	public void nameHandlingExisting(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		System.out.println("Connected.");
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");			
		String prefixes = "PREFIX psgr:<http://publicspending.medialab.ntua.gr/ontology#> " +
		   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
		   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
		String countQ = prefixes + "select ?agent ?gsisName ?count where{{select ?agent ?gsisName (count(?name) as ?count) where {" +
				   "?agent psgr:paymentAgentName ?name ." +
				   "?agent psgr:gsisName ?gsisName . " + 
				   "?agent psgr:nameFlag \"unique\" ." + 
				   "}}FILTER(?count>1)} ORDER BY DESC(?count)" ;
		VirtuosoQueryExecution vqeCount = VirtuosoQueryExecutionFactory.create (countQ, graph);
		ResultSet resultsCount = vqeCount.execSelect();		
		int i = 0;
		HashMap<RDFNode, Literal> agents = new HashMap<RDFNode, Literal>();
		while(resultsCount.hasNext()){			
			QuerySolution rsCount = resultsCount.nextSolution();			
			RDFNode agent = rsCount.get("agent");
			Literal gsisName = rsCount.getLiteral("gsisName");
			agents.put(agent, gsisName);
			i++;
		}
		vqeCount.close();
		System.out.println(i);
		int count = 0;
		Model model = new VirtModel(graph);
		/*Set<RDFNode> agentSet = agents.keySet();
		for(RDFNode agent : agentSet){*/
		Iterator it = agents.entrySet().iterator();
	    while (it.hasNext()) {
				count++;	
				Map.Entry pairs = (Map.Entry)it.next();
				//System.out.println(pairs.getKey().toString() + "  " + pairs.getValue().toString());
				RDFNode agent = (RDFNode) pairs.getKey();
				Literal name = (Literal) pairs.getValue();
				//if(agent.toString().equals("http://publicspending.medialab.ntua.gr/resource/paymentAgents/099396617")){
					model.createResource(agent.asResource().removeAll(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName")).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName"), name.getString()));
					//System.out.println("Check it mofo");
				//}
		}			
		System.out.println(count);				
		graph.close();
		/*String query = prefixes + "SELECT ?agent ?gsisName where {" +
								  "?agent psgr:gsisName ?gsisName . filter(?gsisName!=\"Null\")" +
								  "OPTIONAL{?agent psgr:nameFlag ?flag} FILTER(!BOUND(?flag))" +
								  "} limit 10000";
		
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();		
		int count = 0;
		Model model = new VirtModel(graph);
		while(results.hasNext()){
			count++;
			QuerySolution rs = results.nextSolution();			
			RDFNode agent = rs.get("agent");					
			//System.out.println(agent.toString());
			Literal name = rs.getLiteral("gsisName");
			//try{
			model.createResource(agent.asResource().removeAll(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName")).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName"), name.getString()).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#nameFlag"), "unique"));
			//}catch(Exception e){e.printStackTrace();System.out.println(agent.toString());}
			if(agent.toString().equals("http://publicspending.medialab.ntua.gr/resource/paymentAgents/118076847")){
				model.createResource(agent.asResource().removeAll(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName")).addProperty(ResourceFactory.createProperty("http://publicspending.medialab.ntua.gr/ontology#paymentAgentName"), name.getString()));
				System.out.println("Removed...");
			}
		}
		vqe.close();
		System.out.println(count);
		model.close();*/
	}
	
	public void copyCPACodes(){
		String connectionString = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		VirtGraph graph = new VirtGraph ("diavgeia.cpa", connectionString, "marios", "dirtymarios");			
		String prefixes = "PREFIX psgr:<http://diavgeia.medialab.ntua.gr/ontology#> " +
		   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
		   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
		String query = prefixes + "SELECT ?cpa ?p ?o from <diavgeia.cpa> where {?cpa a psgr:CPA. ?cpa ?p ?o .}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model cpaModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){
			count++;
			QuerySolution rs = results.nextSolution();
			RDFNode org = rs.get("cpa");
			RDFNode property = rs.get("p");
			RDFNode object = rs.get("o");
			Resource cpaRes = cpaModel.createResource(org.toString()); 
			if(property.toString().substring(property.toString().indexOf("#")+1).equals("type")){
				cpaRes.addProperty(RDF.type, ResourceFactory.createResource(object.toString()));
			}
			else{
				cpaRes.addProperty(ResourceFactory.createProperty(property.toString()), object.toString());
			}
		}
		System.out.println(count);		
		vqe.close();
		graph.close();
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Old Graphs/cpaCodes.rdf");						
			cpaModel.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Old Graphs/cpaCodes.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		cpaModel.close();
	}
	
	public void copyTags(){
		String connectionString = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		VirtGraph graph = new VirtGraph ("tags", connectionString, "marios", "dirtymarios");			
		String prefixes = "PREFIX psgr:<http://diavgeia.medialab.ntua.gr/ontology#> " +
		   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
		   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
		String query = prefixes + "SELECT ?tag ?p ?o from <tags> where {?tag a psgr:Tag. ?tag ?p ?o .}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model tagModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){
			count++;
			QuerySolution rs = results.nextSolution();
			RDFNode org = rs.get("tag");
			RDFNode property = rs.get("p");
			RDFNode object = rs.get("o");
			Resource tagRes = tagModel.createResource(org.toString()); 
			if(property.toString().substring(property.toString().indexOf("#")+1).equals("type")){
				tagRes.addProperty(RDF.type, ResourceFactory.createResource(object.toString()));
			}
			else{
				tagRes.addProperty(ResourceFactory.createProperty(property.toString()), object.toString());
			}
		}
		System.out.println(count);		
		vqe.close();
		graph.close();
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Old Graphs/tags.rdf");						
			tagModel.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Old Graphs/tags.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		tagModel.close();
	}
	
	public void copyOrganizationUnits(){
		String connectionString = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		VirtGraph graph = new VirtGraph ("units", connectionString, "marios", "dirtymarios");			
		String prefixes = "PREFIX psgr:<http://diavgeia.medialab.ntua.gr/ontology#> " +
		   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
		   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
		String query = prefixes + "SELECT ?unit ?p ?o from <units> where {?unit a psgr:OrganizationUnit . ?unit ?p ?o .}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model unitModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){
			count++;
			QuerySolution rs = results.nextSolution();
			RDFNode org = rs.get("unit");
			RDFNode property = rs.get("p");
			RDFNode object = rs.get("o");
			Resource unitRes = unitModel.createResource(org.toString()); 
			if(property.toString().substring(property.toString().indexOf("#")+1).equals("type")){
				unitRes.addProperty(RDF.type, ResourceFactory.createResource(object.toString()));
			}
			else if(property.toString().substring(property.toString().indexOf("#")+1).equals("partOfOrganization")){
				unitRes.addProperty(Ontology.partOfOrganization, ResourceFactory.createResource(object.toString()));
			}
			else{
				unitRes.addProperty(ResourceFactory.createProperty(property.toString()), object.toString());
			}
		}
		System.out.println(count);		
		vqe.close();
		graph.close();
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Old Graphs/units.rdf");						
			unitModel.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Old Graphs/units.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		unitModel.close();
	}
	
	
	public void copyOrganizations(){
		String connectionString = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		VirtGraph graph = new VirtGraph ("organizations", connectionString, "marios", "dirtymarios");			
		String prefixes = "PREFIX psgr:<http://diavgeia.medialab.ntua.gr/ontology#> " +
		   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
		   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
		String query = prefixes + "SELECT ?org ?p ?o from <organizations> where {?org a psgr:Organization . ?org ?p ?o .}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model orgModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){
			count++;
			QuerySolution rs = results.nextSolution();
			RDFNode org = rs.get("org");
			RDFNode property = rs.get("p");
			RDFNode object = rs.get("o");
			Resource orgRes = orgModel.createResource(org.toString()); 
			if(property.toString().substring(property.toString().indexOf("#")+1).equals("type")){
				orgRes.addProperty(RDF.type, ResourceFactory.createResource(object.toString()));
			}
			else{
				orgRes.addProperty(ResourceFactory.createProperty(property.toString()), object.toString());
			}
		}
		System.out.println(count);		
		vqe.close();
		graph.close();
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Old Graphs/organizations.rdf");						
			orgModel.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Old Graphs/organizations.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		orgModel.close();
	}
	
	public void copyGsisAgents(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		VirtGraph graph = new VirtGraph ("diavgeia.GsisInformation", connectionString, "marios", "dirtymarios");	
		Model model = new VirtModel(graph);		
		String prefixes = "PREFIX psgr:<http://diavgeia.medialab.ntua.gr/ontology#> " +
		   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
		   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
		   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
		   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
		String query = prefixes + "SELECT ?ag ?p ?o from <diavgeia.GsisInformation> where {?ag a psgr:PaymentAgent . ?ag ?p ?o .}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model agentModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){
			count++;
			QuerySolution rs = results.nextSolution();
			RDFNode agent = rs.get("ag");
			String agentUri = agent.toString();
			Resource agentRes = agentModel.createResource(agentUri.replace("#", "/").replaceAll("diavgeia", "publicspending"));
			RDFNode property = rs.get("p");
			RDFNode object = rs.get("o");
			if(property.toString().substring(property.toString().indexOf("#")+1).equals("type")){
				agentRes.addProperty(RDF.type, ResourceFactory.createResource(object.toString()));
			}
			else if(property.toString().substring(property.toString().indexOf("#")+1).equals("sameAs")){}
			
			else{
				agentRes.addProperty(ResourceFactory.createProperty(property.toString()), object.toString());
			}
		}
		vqe.close();
		System.out.println("Count = " + count);
		try{			
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Old Graphs/gsis1.rdf");						
			agentModel.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Old Graphs/gsis1.rdf");
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	public void copyCPA(){
		String connectionString = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		VirtGraph graph = new VirtGraph ("diavgeia.gsisAgents", connectionString, "marios", "dirtymarios");	
		Model model = new VirtModel(graph);		
		
		/*String prefixes = "PREFIX psgr:<http://diavgeia.medialab.ntua.gr/ontology#> " +
				   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
				   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
				   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
		String query = prefixes + "CONSTRUCT {?s ?p ?o} where {{{SELECT ?s ?p ?o from <diavgeia.cpv> where {?s ?p ?o .}}}}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		//ResultSet results = vqe.execSelect();
		Model model1 = vqe.execConstruct();
		vqe.close();*/
		Model model1 = ModelFactory.createDefaultModel();	
		model1.add(model);
		//model.write(System.out);
		try{
			System.out.println("Try 1");
			//FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Old Graphs/cpv.rdf");
			FileOutputStream fos = new FileOutputStream("C:/Users/marios/Desktop/Old Graphs/gsis.rdf");
			System.out.println("Try 2");
			//model1.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Old Graphs/cpv.rdf");
			model1.write(fos, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/Old Graphs/gsis.rdf");
		}
		catch(Exception e){e.printStackTrace();}
		model.close();
		model1.close();
	}
	
	 static public Integer checkAfm(String afm){
	    		    		    		    		    		    	    		    	   			    	
	    		Integer res = Integer.parseInt(Character.toString(afm.charAt(0)))*256 + Integer.parseInt(Character.toString(afm.charAt(1)))*128+Integer.parseInt(Character.toString(afm.charAt(2)))*64+Integer.parseInt(Character.toString(afm.charAt(3)))*32+Integer.parseInt(Character.toString(afm.charAt(4)))*16+Integer.parseInt(Character.toString(afm.charAt(5)))*8+Integer.parseInt(Character.toString(afm.charAt(6)))*4+Integer.parseInt(Character.toString(afm.charAt(7)))*2;
	    		//System.out.println(res);
	    		Integer div = res%11;
	    		//System.out.println(div%10);
	    		return div%10;
	    		//return(Double.parseDouble(Character.toString(afm.charAt(8)))==div%10);
	    		
	 }
	 
	
	

}
