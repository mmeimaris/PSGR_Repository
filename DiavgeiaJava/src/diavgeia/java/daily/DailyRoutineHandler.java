package diavgeia.java.daily;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;

import diavgeia.java.ontology.Ontology;

/*
 * This class is used to perform the daily routine that includes reading the daily csv file, rdf-izing it and adding it the Virtuoso store,
 * fixing the names of the payers and the payees and calculating statistics.
 */
public class DailyRoutineHandler {
	
		
	static Model remoteModel, falseRemoteModel;
	static Model model, invalidModel;
	static VirtGraph graph, falseGraph;
	static String[] fields;		
	static String csvLocation = "C:/Users/marios/Desktop/CSV-Files/decisions_30006-59508.csv";
	static String csvLocationProb = "C:/Users/marios/Desktop/CSV-Files/problematic csv/decisions_531619-561129.csv";
	static String csvNameProb = "decisions_531619-561129";
	static String csvPath = "C:/Users/marios/Desktop/CSV-Files/";
	static String csvName = "decisions_30006-59508";
	//static String graphName = "diavgeia.ValidDecisions";
	//static String graphName = "http://publicspending.medialab.ntua.gr/ValidDecisions";
	static String graphName = "http://publicspending.medialab.ntua.gr/Decisions";
	//static String graphName = "diavgeia.TestDecisions";
	//static String falseGraphName = "diavgeia.InvalidDecisions";
	//static String falseGraphName = "http://publicspending.medialab.ntua.gr/InvalidDecisions";
	static String falseGraphName = "fake.graph";
	static String correctedDecisionsGraphName = "diavgeia.CorrectedDecisions";
	static String correctionsGraphName = "diavgeia.corrections";
	static String dayGraphName = "diavgeia.DayGraph1";
	static String weekGraphName = "diavgeia.WeekGraph1";
	static String monthGraphName = "diavgeia.MonthGraph1";
	static String yearGraphName = "diavgeia.YearGraph1";
	static String overallGraphName = "diavgeia.OverallGraph1";
	static String binaryGraphName = "diavgeia.BinaryGraph1";
	static String nicknameGraphName = "diavgeia.nicknames";
	static String gsisGraphName = "diavgeia.GsisInformation";
	static String overallResource = "<"+Ontology.diavgeiaPrefix+"Overall>";
	//static String cpvGraphName = "diavgeia.cpv";
	static String cpvGraphName = "";
	//static String connectionString = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
	static String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
	//static String graphName = "DecisionsTesting";
	//static String dayGraphName = "DayGraph";
	static String ruleSet = "diavgeiaRuleSet";
	static String filePath = "C:/Users/marios/Desktop/Diavgeia/";
	static String rdfLocation = filePath + "RDF Models/valid/uploadingModel3.rdf";
	static String rdfLocationVariable = filePath + "RDF Models/valid/uploadingModel";	
	static String invalidRdfLocation = filePath + "RDF Models/invalid/invalidModel.rdf"; 			
	static String invalidRdfLocationVariable = filePath + "RDF Models/invalid/invalidModel";
	static String jsonFolder = filePath + "JSONOutput/";
	static ArrayList<Integer> badHits = new ArrayList<Integer>();
	static int fails=0, count=0, outputDateLimit = 1;
	static boolean testMode = true;
	//static boolean testMode = false;
	static HashSet<String> payers = new HashSet<String>();
	static HashSet<String> payees = new HashSet<String>();
	static HashSet<String> agents = new HashSet<String>();

	
	public static void main(String[] args) {
		
		
		if(!testMode){
			System.out.print("Creating model...");
			model = ModelFactory.createDefaultModel();			
			invalidModel = ModelFactory.createDefaultModel();
			System.out.println("Done.");
			/*String url;
	    	if(args.length == 0)
	    	    url = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/charset=UTF-8/log_enable=2";
	    	else
	    	    url = args[0];*/
	
	    	//Get the Decisions Graph    
	    	
	    	
	    	
	    	/*System.out.println("Clearing Models...");
	    	VirtGraph dayGraph = new VirtGraph (dayGraphName, connectionString, "marios", "dirtymarios");
	    	dayGraph.clear();
	    	dayGraph.close();
	    	VirtGraph weekGraph = new VirtGraph (weekGraphName, connectionString, "marios", "dirtymarios");
	    	weekGraph.clear();
	    	weekGraph.close();
	    	VirtGraph monthGraph = new VirtGraph (monthGraphName, connectionString, "marios", "dirtymarios");
	    	monthGraph.clear();
	    	monthGraph.close();
	    	VirtGraph yearGraph = new VirtGraph (yearGraphName, connectionString, "marios", "dirtymarios");
	    	yearGraph.clear();
	    	yearGraph.close();
	    	VirtGraph overallGraph = new VirtGraph (overallGraphName, connectionString, "marios", "dirtymarios");
	    	overallGraph.clear();
	    	overallGraph.close();
	    	VirtGraph binaryGraph = new VirtGraph (binaryGraphName, connectionString, "marios", "dirtymarios");
	    	binaryGraph.clear(); //This is just for testing, it should be deleted when the system is online.
	    	binaryGraph.close();			
			graph = new VirtGraph (graphName, connectionString, "marios", "dirtymarios");
	    	graph.clear(); //This is just for testing, it should be deleted when the system is online.
	    	graph.close();	
	    	falseGraph = new VirtGraph (falseGraphName, connectionString, "marios", "dirtymarios");
			falseGraph.clear();
			falseGraph.close();
			System.out.println("Done.");	*/			    	
	    	System.out.println("Starting at " + new Date());
	    	//Here it is attempted to read the csv file and write it in the remote model, with a middle step of saving it as an rdf file.
	    	System.out.print("Reading csv file and populating model...");
	    	if(!handleCSVFile()){
	    		//In case of emergency break glass
	    		System.out.println("Could not Handle CSV file.");    		
	    		remoteModel.close();
	    		model.close();   
	    		invalidModel.close();
	    	}
	    	else{
	    		System.out.println("Done.");
	    		System.out.println("Starting at " + new Date());	    		
	    		System.out.print("Connecting to virtuoso...");
				graph = new VirtGraph (graphName, connectionString, "marios", "dirtymarios");	
		    	remoteModel = new VirtModel(graph);
		    	System.out.println("Done.");
		    	System.out.print("Uploading to virtuoso...");
		    	if(!readRdfInRemoteModel())
		 			System.out.println("Could not read rdf.");
	    		
	    		System.out.println("Done.");
	    		remoteModel.close();
	    		
	    		System.out.println(new Date());
	    		//Uploading false entries model for error checking.
	    		System.out.println("Uploading false entries model for error checking...");
	    		InputStream in = FileManager.get().open(invalidRdfLocation);
				if (in == null) {
				     throw new IllegalArgumentException(
				                                  "File: " + invalidRdfLocation + " not found");
				}
				//VirtGraph falseGraph = new VirtGraph (falseGraphName, connectionString, "marios", "dirtymarios");
				falseGraph = new VirtGraph (falseGraphName, connectionString, "marios", "dirtymarios");
				falseRemoteModel = new VirtModel(falseGraph);		
				falseRemoteModel.read(in,null);
	    		falseRemoteModel.close();
	    		System.out.println("Done.");
	    		ArrayList<String[]> dates = getDecisionDates();  
	    		model.close(); 	
	    		invalidModel.close();
	    	
	    		//Ask for names from GSIS
	    		System.out.print("Trying to get GSIS names...");
	    		System.out.println(new Date());
	    		SOAPHandler soapHandler = new SOAPHandler();    		
	    		System.out.println("Done.");
	    		//Create a statistics handler object
	    		
	    		System.out.println("Calculating statistics...");
	    		System.out.println(new Date());    		  	
	    		StatisticsHandler sh = new StatisticsHandler(dates);
	    		System.out.println("Done.");
	    		//System.out.println("Outputting JSON results...");
	    		//OutputHandler oh = new OutputHandler();	    		
	    		System.out.println("Exceptions: " + fails);
	    		System.out.println("Done at " + new Date());
	    	}
		}
		else{
			System.out.println("Test Mode On...");
			/*File dir = new File("C:/Users/marios/Desktop/Diavgeia/CSV-Files/all");
			String[] children = dir.list();
			if (children == null) {
			    // Either dir does not exist or is not a directory
				System.out.println("File does not exist.");
			} 
			else {    	
				new File("C:/Users/marios/Desktop/Diavgeia/Resource RDF2/").mkdirs();
		    	new File("C:/Users/marios/Desktop/Diavgeia/Resource RDF2/Valid RDF/").mkdirs();
		    	new File("C:/Users/marios/Desktop/Diavgeia/Resource RDF2/Invalid RDF/").mkdirs();
			    for (int i=0; i<children.length; i++) {
			    	    		    	    		    	
			        // Get filename of file or directory
			        String filename = children[i];
			        csvName = filename.substring(0, filename.length()-4);
			        csvLocation = "C:/Users/marios/Desktop/Diavgeia/CSV-Files/all/"+ csvName + ".csv";
			        System.out.print("Creating models...");			    				
			        model = ModelFactory.createDefaultModel();			
					invalidModel = ModelFactory.createDefaultModel();
					System.out.println("Done.");
					System.out.print("Reading csv file " + csvName + ".csv" + " and populating model...");
			        if(!handleCSVFile()){
			    		//In case of emergency break glass
			    		System.out.println("Could not Handle CSV file.");    		    		    		
			    		model.close();   
			    		invalidModel.close();
			    	}
			        else{
			    		System.out.println("Done. Number of entries: " + count);	    		
			    		try{
			    			rdfLocationVariable = "C:/Users/marios/Desktop/Diavgeia/Resource RDF2/Valid RDF/";
			    			System.out.println("Writing valid model to file " + rdfLocationVariable+csvName+".rdf" + " .....");
				    		FileOutputStream fos = new FileOutputStream(rdfLocationVariable+csvName+".rdf");
							model.write(fos, "RDF/XML-ABBREV", rdfLocationVariable+csvName+".rdf");							
							fos.close();
			    		}
			    		catch(Exception e){e.printStackTrace();}	    		
						//Write the invalid entries in an RDF file.
						try{
							invalidRdfLocationVariable = "C:/Users/marios/Desktop/Diavgeia/Resource RDF2/Invalid RDF/";
							System.out.println("Writing invalid model to file " + invalidRdfLocationVariable+csvName+".rdf" + " .....");
				    		FileOutputStream fos2 = new FileOutputStream(invalidRdfLocationVariable+csvName+".rdf");
							invalidModel.write(fos2, "RDF/XML-ABBREV", invalidRdfLocationVariable+csvName+".rdf");
							fos2.close();
						}
						catch(Exception e){e.printStackTrace();}
			    		//ArrayList<String[]> dates = getDecisionDates();  
			    		model.close(); 	
			    		invalidModel.close();
					
			    	}
			        
			    } 
			} */
		
			//handleCSVLocally();
			
			System.out.println("Getting dates...");
			ArrayList<String[]> dayList = getUniversalDecisionDates2();
			//ArrayList<String[]> dayList = getUniversalDecisionDates();
			System.out.println("Dates: " + dayList.size());
			System.out.println("Invoking handler...");
			StatisticsHandler sh = new StatisticsHandler(dayList);
			//OutputHandler oh = new OutputHandler();
			//NicknameHandler nh = new NicknameHandler();			
			//nh.aeitei();
			//nh.fysikaProswpa();
		    //nh.convertToRDF();
			//System.out.println(SPARQLQueries.correctionsQuery());
			//OutputHandler oh = new OutputHandler();
			//SOAPHandler soaph = new SOAPHandler();
			/*model = ModelFactory.createDefaultModel();
			InputStream in = FileManager.get().open(rdfLocation);
			if (in == null) {
			     throw new IllegalArgumentException(
			                                  "File: " + rdfLocation + " not found");
			}
			graph = new VirtGraph ("diavgeia.corrections", connectionString, "marios", "dirtymarios");	
	    	remoteModel = new VirtModel(graph);
			remoteModel.read(in,null);			
    		model.close();
    		remoteModel.close();*/
			//OutputHandler oh = new OutputHandler();			
			//StatisticsHandler sh = new StatisticsHandler(dates);
			//SOAPHandler soapHandler = new SOAPHandler();
			/*System.out.println("Test Mode On...");
			model = ModelFactory.createDefaultModel();
			invalidModel = ModelFactory.createDefaultModel();
			handleCSVFile();
			//model.close();			
			System.out.println("Done. Number of entries: " + count);	    		
    		try{//static String rdfLocationVariable = filePath + "RDF Models/valid/    			
    			String path = filePath + "RDF Models/Valid/problematic tests/" +csvNameProb+"model.rdf";
    			System.out.println("Writing valid model to file " + path + " .....");
	    		FileOutputStream fos = new FileOutputStream(path);
				model.write(fos, "RDF/XML-ABBREV", path);
				fos.close();
    		}
    		catch(Exception e){e.printStackTrace();}
    		try{
    			String path = filePath + "RDF Models/Invalid/problematic tests/" +csvNameProb+"invalidModel.rdf";
				System.out.println("Writing invalid model to file " + path + " .....");
	    		FileOutputStream fos2 = new FileOutputStream(path);
				invalidModel.write(fos2, "RDF/XML-ABBREV", path);
				fos2.close();
			}
			catch(Exception e){e.printStackTrace();}
    		model.close();
    		invalidModel.close();*/
			//Write the invalid entries in an RDF file.
			
			//File dir = new File("C:/Users/marios/Desktop/CSV-Files/");			
			
		}					
    }
          
	static void handleCSVLocally(){
		
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
		String folderDateString = dayFormat.format(new Date())+"/";		
		folderDateString = "35-08-2012/";
		File dir = new File("C:/Users/marios/Desktop/Diavgeia/CSV-Files/" + folderDateString);
		String[] children = dir.list();
		if (children == null) {
		    // Either dir does not exist or is not a directory
			System.out.println("File does not exist.");
		} 
		else {    	
			new File("C:/Users/marios/Desktop/Diavgeia/Test/"+folderDateString).mkdirs();
	    	new File("C:/Users/marios/Desktop/Diavgeia/Test/" +folderDateString + "Valid RDF/").mkdirs();
	    	new File("C:/Users/marios/Desktop/Diavgeia/Test/" + folderDateString + "Invalid RDF/").mkdirs();
		    for (int i=0; i<children.length; i++) {
		    	    		    	    		    	
		        // Get filename of file or directory
		        String filename = children[i];
		        csvName = filename.substring(0, filename.length()-4);
		        csvLocation = "C:/Users/marios/Desktop/Diavgeia/CSV-Files/"+folderDateString + csvName + ".csv";
		        System.out.print("Creating models...");			    				
		        model = ModelFactory.createDefaultModel();			
				invalidModel = ModelFactory.createDefaultModel();
				System.out.println("Done.");
				System.out.print("Reading csv file " + csvName + ".csv" + " and populating model...");
		        if(!handleCSVFile()){
		    		//In case of emergency break glass
		    		System.out.println("Could not Handle CSV file.");    		    		    		
		    		model.close();   
		    		invalidModel.close();
		    	}
		        else{
		    		System.out.println("Done. Number of entries: " + count);	    		
		    		try{
		    			rdfLocationVariable = "C:/Users/marios/Desktop/Diavgeia/Test/" + folderDateString + "Valid RDF/";
		    			System.out.println("Writing valid model to file " + rdfLocationVariable+csvName+".rdf" + " .....");
			    		FileOutputStream fos = new FileOutputStream(rdfLocationVariable+csvName+".rdf");
						model.write(fos, "RDF/XML-ABBREV", rdfLocationVariable+csvName+".rdf");
						/*graph = new VirtGraph (graphName, connectionString, "marios", "dirtymarios");
						Model remodel = new VirtModel(graph);						
						InputStream in = FileManager.get().open(rdfLocationVariable+csvName+".rdf");
						if (in == null) {
						     throw new IllegalArgumentException(
						                                  "File: " + rdfLocationVariable+csvName+".rdf" + " not found");
						}
						remodel.read(in,null);
						remodel.close();*/
						fos.close();
		    		}
		    		catch(Exception e){e.printStackTrace();}	    		
					//Write the invalid entries in an RDF file.
					try{
						invalidRdfLocationVariable = "C:/Users/marios/Desktop/Diavgeia/Test/" + folderDateString + "Invalid RDF/";
						//System.out.println("Writing invalid model to file " + invalidRdfLocationVariable+csvName+".rdf" + " .....");
			    		FileOutputStream fos2 = new FileOutputStream(invalidRdfLocationVariable+csvName+".rdf");
						invalidModel.write(fos2, "RDF/XML-ABBREV", invalidRdfLocationVariable+csvName+".rdf");
						fos2.close();
					}
					catch(Exception e){e.printStackTrace();}
		    		//ArrayList<String[]> dates = getDecisionDates();  
		    		model.close(); 	
		    		invalidModel.close();
				
		    	}
		        
		    } 
		} 
	}
	
	static boolean handleCSVFile(){
		
		try{
			count = 0;
    		//CSVReader reader = new CSVReader(new FileReader(csvLocation), ';', '⊆');
    		CSVReader reader = new CSVReader(new FileReader(csvLocation), ';', '\"');
    		//CSVReader reader = new CSVReader(new FileReader("C:/Users/marios/Desktop/CSV-Files/problematic csv/decisions_1180892-1205307.csv"), ';', '¬' );    //'⊆'
    		String[] nextLine;    	     	     		    	
    		try{
    	 		while((nextLine = reader.readNext())!= null){    	 			
    	 				if(count==0){
    	 					fields = new String[nextLine.length];
    	 					for(int i=0;i<nextLine.length;i++){    	 						
    	 						fields[i] = nextLine[i];        	 						
    	 					}
    	 					System.out.println();
    	 					System.out.println(Arrays.toString(fields));  
    	 					/*System.out.println("Press Any Key To Continue...");
    	 			          new java.util.Scanner(System.in).nextLine();*/
    	 				}
    	 				else{        	 					    	 					
    	 						addToModel(nextLine);
    	 				}    	 				    	 			
    	 				count++;        	 				    	 					
    	 		}    	 		    	 		    	 		
    	 		reader.close();
    	 		return true;
    	 	}
    	 	catch(IOException e){
    	 		e.printStackTrace();    	 		
    	 		return false;
    	 	}    		
    	 
    	}
    	catch(FileNotFoundException e){ 
    		e.printStackTrace();
    		return false;
    	}
				
	}
	
	
    /*
     * This method is used to read the newly created rdf file in to the remote model of the Virtuoso store
     * 
     * @return True if reading was successful, False otherwise
     */
	static boolean readRdfInRemoteModel(){
		
		try {
			FileOutputStream fos = new FileOutputStream(rdfLocation);
			model.write(fos, "RDF/XML-ABBREV", rdfLocation);  
			//Write the invalid entries in an RDF file.
			FileOutputStream fos2 = new FileOutputStream(invalidRdfLocation);
			invalidModel.write(fos2, "RDF/XML-ABBREV", invalidRdfLocation);			
			InputStream in = FileManager.get().open(rdfLocation);
			if (in == null) {
			     throw new IllegalArgumentException(
			                                  "File: " + rdfLocation + " not found");
			}
			remoteModel.read(in,null);
			return true;
		} 
        catch (FileNotFoundException e) {			
			e.printStackTrace();
			return false;
		}		
		
	}
	
	
	/*
	 * This method creates the model of the next line of the CSV file
	 * 
	 *  @nextLine The next line of the csv parser
	 *
	 */
    static void addToModel(String[] nextLine){
    	    	    	
    	HashMap<String, String> hashmap = new HashMap<String, String>();
    	//HashMap<Integer, String> hashmap2 = new HashMap<Integer, String>();
    	try{    		
    		for(int i=0; i<fields.length;i++){
    			String nextLineString = nextLine[i];    			
    			if(!Normalizer.isNormalized(nextLineString, Normalizer.Form.NFC)){
    				try{    					    			
    					nextLineString = Normalizer.normalize(nextLineString, Normalizer.Form.NFC);
    				}
    				catch(Exception e){
    					System.out.println("Couldn't normalize form.");
    					//throw(new Exception());
    				}
    			}
    			try{
    				//CSVFieldMappings fieldMap = new CSVFieldMappings(fields[i]);
    				if(nextLineString.equals("")){
    					hashmap.put(fields[i], "Null");
    					//hashmap2.put(i, "Null");
    				}
    				else{
    					//System.out.println(fields[i]);
    					String test = fields[i];    					
    					//System.out.println(test);
    					//System.out.println(nextLineString);
    					hashmap.put(test, nextLineString);
    					//System.out.println(hashmap.get(test) + "djgfhjkfdsg");
    					//hashmap2.put(i, nextLineString);
    					//System.out.println(hashmap.get(fields[i]));
    					
    				}
    			}
    			catch(ArrayIndexOutOfBoundsException e){
    				e.printStackTrace();
    			}    			
    		}    	    		
    		//System.out.println(hashmap.get(test) + "djgfhjkfdsg");
    		/*Set keySet = hashmap.keySet();
    		Iterator setIt = keySet.iterator();
    		while(setIt.hasNext()){
    			System.out.println((String)setIt.next());
    		}*/
    		//This is solely for adding corrections. . .
    		//addCorrections(hashmap, model);
    		
    		boolean[] checks = new boolean[4];
    		/*System.out.println("Press Any Key To Continue...");    		
    		//System.out.println(hashmap.get("subject"));
    		//System.out.println(hashmap.get(fields[20]));
	          new java.util.Scanner(System.in).nextLine();*/
    		checks[0] = checkAfm((String)hashmap.get("ΑΦΜ ΦΟΡΕΑ"), hashmap, "ΑΦΜ ΦΟΡΕΑ");
    		//checks[0] = checkAfm((String)hashmap2.get(20), hashmap, "ΑΦΜ ΦΟΡΕΑ");
    		checks[1] = checkAfm((String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ"), hashmap, "ΑΦΜ ΑΝΑΔΟΧΟΥ");
    		String cpvCode = (String)hashmap.get(" CPV Number") + " " + (String) hashmap.get("CPV Description");    		
    		//checks[2] = !cpvCode.equals("Null") && !cpvCode.equals("");
    		checks[2] = checkCpvString(cpvCode, hashmap);
    		String[] el = getDateElements((String)hashmap.get("date"));
    		checks[3] = (el[0]!="Null");
    		if(checks[0] && checks[1] && checks[2]){
    			addToCorrectModel(hashmap, model, checks);
    		}
    		else{
    			addToCorrectModel(hashmap, invalidModel, checks);
    		}
    	    
    	}
    	catch(Exception e){fails++;
    	e.printStackTrace();}    	
    }
        
    
    static public ArrayList<String> getCorrectives(String correctionOfAda){
    	
    	ArrayList<String> correctives = new ArrayList<String>();
    	StringTokenizer st = new StringTokenizer(correctionOfAda, ",");
    	while(st.hasMoreElements()){
    		correctives.add(st.nextToken().replace(" ", ""));
    	}
    	return correctives;
    }
    
    static public void addToCorrectModel(HashMap<String, String> hashmap, Model model, boolean[] checks){
    	
    	Resource decision = model.createResource(Ontology.instancePrefix + "decisions/" + (String)hashmap.get("ada"));
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
    	/*if(!correction.equals("Null")){
    		decision.addProperty(Ontology.correctionOfAda, model.createResource(Ontology.instancePrefix + "decisions/" + correction));
    	}*/
		String rel = (String)hashmap.get("relativeAda");
    	ArrayList<String> relatives = getCorrectives(rel);
    	for(String relative : relatives){
			if(!relative.equals("Null"))
				decision.addProperty(Ontology.relativeAda, model.createResource(Ontology.instancePrefix + "decisions/" + relative));
		}
    	/*if(!relative.equals("Null"))
    		decision.addProperty(Ontology.relativeAda, relative);*/
		
		
		if(!checks[0]){
			//payer = model.createResource(Ontology.instancePrefix + "payers/" + (String)hashmap.get("ΑΦΜ ΦΟΡΕΑ")+"File"+csvLocation+"Count"+count);
			payer = model.createResource(Ontology.instancePrefix + "paymentAgents/" + (String)hashmap.get("ΑΦΜ ΦΟΡΕΑ")+"File"+csvLocation+"Count"+count);
			payer.addProperty(Ontology.validAfm, "false");
		}
		else{
			payer = model.createResource(Ontology.instancePrefix + "paymentAgents/" + (String)hashmap.get("ΑΦΜ ΦΟΡΕΑ"));
			//agents.add(payer.toString());
			//payer = model.createResource(Ontology.instancePrefix + "payers#" + (String)hashmap.get("ΑΦΜ ΦΟΡΕΑ"));
		}
		if(!checks[1]){
			payee = model.createResource(Ontology.instancePrefix + "paymentAgents/" + (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ")+"File"+csvLocation+"Count"+count);
			//payee = model.createResource(Ontology.instancePrefix + "payees#" + (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ")+"File"+csvLocation+"Count"+count);
			payee.addProperty(Ontology.validAfm, "false");
		}
		else{
			payee = model.createResource(Ontology.instancePrefix + "paymentAgents/" + (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ"));
			//agents.add(payee.toString());
			//payee = model.createResource(Ontology.instancePrefix + "payees#" + (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ"));
		}
		
		//Tags
		String tagId = hashmap.get("tagUIDs");
		if(!tagId.equals("Null") && !tagId.equals(""))
			decision.addProperty(Ontology.tag, ResourceFactory.createResource(Ontology.instancePrefix+"tags/"+(String)hashmap.get("tagId")));
		
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
		String[] dateElements = getDateElements((String)hashmap.get("date"));	
		String[] timestampElements = getDateElements((String)hashmap.get("submissionTimestamp"));
		//decision.addProperty(Ontology.date, (String)hashmap.get("date"));
		Resource day, week, month, year;
		if(dateElements[2].equals("Null")){
			decision.addProperty(Ontology.date, model.createTypedLiteral(timestampElements[2], XSDDatatype.XSDdate));
			day = model.createResource(Ontology.instancePrefix+"days/"+timestampElements[2]).addProperty(RDF.type, Ontology.dayResource).addProperty(Ontology.date, timestampElements[2]);
			month = model.createResource(Ontology.instancePrefix+"months/"+timestampElements[1]).addProperty(RDF.type, Ontology.monthResource).addProperty(Ontology.date, timestampElements[1]);
			year = model.createResource(Ontology.instancePrefix+"years/"+timestampElements[0]).addProperty(RDF.type, Ontology.yearResource).addProperty(Ontology.date, timestampElements[0]);		
			//Weeks need special treatment
			week = model.createResource(Ontology.instancePrefix+"weeks/"+timestampElements[0]+"-w-"+timestampElements[3]).addProperty(RDF.type, Ontology.weekResource).addProperty(Ontology.date, timestampElements[0]+"-w-"+timestampElements[3]);
			decision.addProperty(Ontology.day,day).addProperty(Ontology.month, month).addProperty(Ontology.year, year).addProperty(Ontology.week, week);		
			//System.out.println((String)hashmap.get("date") +"  "+ (String)hashmap.get("submissionTimestamp"));
		}
		else{
			decision.addProperty(Ontology.date, model.createTypedLiteral(dateElements[2], XSDDatatype.XSDdate));
			day = model.createResource(Ontology.instancePrefix+"days/"+dateElements[2]).addProperty(RDF.type, Ontology.dayResource).addProperty(Ontology.date, dateElements[2]);
			month = model.createResource(Ontology.instancePrefix+"months/"+dateElements[1]).addProperty(RDF.type, Ontology.monthResource).addProperty(Ontology.date, dateElements[1]);
			year = model.createResource(Ontology.instancePrefix+"years/"+dateElements[0]).addProperty(RDF.type, Ontology.yearResource).addProperty(Ontology.date, dateElements[0]);		
			//Weeks need special treatment
			week = model.createResource(Ontology.instancePrefix+"weeks/"+dateElements[0]+"-w-"+dateElements[3]).addProperty(RDF.type, Ontology.weekResource).addProperty(Ontology.date, dateElements[0]+"-w-"+dateElements[3]);
			decision.addProperty(Ontology.day,day).addProperty(Ontology.month, month).addProperty(Ontology.year, year).addProperty(Ontology.week, week);
			//System.out.println((String)hashmap.get("date") +"  "+ (String)hashmap.get("submissionTimestamp"));
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
		payment.addProperty(Ontology.paymentAmountString, decoratePaymentAmount(amount));     		    	
		payment.addProperty(Ontology.paymentAmount, model.createTypedLiteral(decoratePaymentAmountDouble(amount)));
		payment.addProperty(Ontology.payer, payer);
		payment.addProperty(Ontology.payee, payee);
		payment.addProperty(Ontology.paymentCategory, (String)hashmap.get("ΚΑΤΗΓΟΡΙΑ ΔΑΠΑΝΗΣ"));
		payment.addProperty(Ontology.relativeKae, (String)hashmap.get("ΣΧΕΤΙΚΟΣ ΑΡΙΘΜΟΣ ΚΑΕ"));
		payment.addProperty(RDF.type, Ontology.paymentResource);
		
		//Other Properties
		payer.addProperty(Ontology.paymentAgentName, (String)hashmap.get("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
		payer.addProperty(Ontology.afm, (String)hashmap.get("ΑΦΜ ΦΟΡΕΑ"));
		payer.addProperty(RDF.type, Ontology.paymentAgentResource);
		//payer.addProperty(RDF.type, Ontology.payerResource);
		payee.addProperty(Ontology.paymentAgentName, (String)hashmap.get("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
		payee.addProperty(Ontology.afm, (String)hashmap.get("ΑΦΜ ΑΝΑΔΟΧΟΥ"));
		payee.addProperty(RDF.type, Ontology.paymentAgentResource);
		//payee.addProperty(RDF.type, Ontology.payeeResource);
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

  
    
    
    /*
     * This method returns an ArrayList containing the newly parsed decision days.
     * This is needed for the step of statistics,
     * @return ArrayList<String> containing decision days in the form "YYYY-MM-DD"
     */
    static public ArrayList<String[]> getDecisionDates(){
		
		//Returns the (distinct) days that were found to be associated with decisions on the particular graph.
		//Note: This should query the temporary decisions graph
		
		QueryExecution qe = QueryExecutionFactory.create (SPARQLQueries.dayQuery, model);
	 	ResultSet results = qe.execSelect();
	 	ArrayList<String[]> dayList = new ArrayList<String[]>();	 		 	
	 	while (results.hasNext()) {
	 	   QuerySolution rs = results.nextSolution();
	 	   RDFNode day = rs.get("dt");
	 	   dayList.add(new String[] {day.toString().substring(0, 4), day.toString().substring(0, 7), day.toString().substring(0, 10), getWeek(day.toString())});	     
	 	 } 	   
	 	 qe.close();
	 	 return dayList;
		
	}
    
    static public ArrayList<String[]> getUniversalDecisionDates(){
		
		//Returns the (distinct) days that were found to be associated with decisions on the particular graph.
		/*//Note: This should query the temporary decisions graph
    	InputStream in = FileManager.get().open(rdfLocation);
		if (in == null) {
		     throw new IllegalArgumentException(
		                                  "File: " + rdfLocation + " not found");
		}
		Model model = ModelFactory.createDefaultModel();
		model.read(in,null);*/
    	graph = new VirtGraph (graphName, connectionString, "marios", "dirtymarios");
    	String query = SPARQLQueries.dayQueryUniversal2;
    	//System.out.println(query);
    	VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
	 	ResultSet results = vqe.execSelect();
		//QueryExecution qe = QueryExecutionFactory.create (SPARQLQueries.dayQuery, model);
	 	//ResultSet results = qe.execSelect();
	 	ArrayList<String[]> dayList = new ArrayList<String[]>();	 		 	
	 	while (results.hasNext()) {
	 	   QuerySolution rs = results.nextSolution();
	 	   Literal day = rs.getLiteral("day");
	 	   String dayS = day.getString();
	 	   dayList.add(new String[] {dayS.substring(0, 4), dayS.substring(0, 7), dayS.substring(0, 10), getWeek(dayS)});	     
	 	 } 	   
	 	 //qe.close();
	 	//model.close();
	 	vqe.close();
	 	graph.close();
	 	 return dayList;
		
	}
    
static public ArrayList<String[]> getUniversalDecisionDates2(){
		
		//Returns the (distinct) days that were found to be associated with decisions on the particular graph.
		/*//Note: This should query the temporary decisions graph
    	InputStream in = FileManager.get().open(rdfLocation);
		if (in == null) {
		     throw new IllegalArgumentException(
		                                  "File: " + rdfLocation + " not found");
		}
		Model model = ModelFactory.createDefaultModel();
		model.read(in,null);*/
    	graph = new VirtGraph (graphName, connectionString, "marios", "dirtymarios");
    	String query = SPARQLQueries.dayQueryUniversal;
    	//System.out.println(query);
    	VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
	 	ResultSet results = vqe.execSelect();
		//QueryExecution qe = QueryExecutionFactory.create (SPARQLQueries.dayQuery, model);
	 	//ResultSet results = qe.execSelect();
	 	ArrayList<String[]> dayList = new ArrayList<String[]>();	 		 	
	 	while (results.hasNext()) {
	 	   QuerySolution rs = results.nextSolution();
	 	   Literal day = rs.getLiteral("dt");
	 	   String dayS = day.getString();
	 	   dayList.add(new String[] {dayS.substring(0, 4), dayS.substring(0, 7), dayS.substring(0, 10), getWeek(dayS)});	     
	 	 } 	   
	 	 //qe.close();
	 	//model.close();
	 	vqe.close();
	 	graph.close();
	 	 return dayList;
		
	}
    
    static public String getWeek(String day){
    	
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
		return "Null";
    	
    }
    
    static public String[] getDateElements(String date){
    	
    	try{
    	String[] elements = new String[4];
    	elements[0] = date.substring(0, 4); //Get Year
    	elements[1] = date.substring(0, 7); //Get Month
    	elements[2] = date.substring(0, 10); //Get Day
    	elements[3] = getWeek(date);
    	return elements;
    	}
    	catch(Exception e){    	
    		//e.printStackTrace();
    		//System.out.println("Problematic date: " + date);
    		String[] elements = new String[4];
    		elements[0] = "Null" ;
    		elements[1] = "Null" ;
    		elements[2] = "Null" ;
    		elements[3] = "Null" ;
    		/*DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    		String today = df.format(new Date());
    		String[] elements = new String[4];
        	elements[0] = today.substring(0, 4); //Get Year
        	elements[1] = today.substring(0, 7); //Get Month
        	elements[2] = today.substring(0, 10); //Get Day
        	elements[3] = getWeek(today);*/
    		return elements;
        	    		
    	}
    	
    }
    
    static public boolean checkAfm(String afm, HashMap<String, String> hashmap, String field){
    	
    	String originalAfm = afm;
    	if(afm.equals("Null") || afm.contains("&") || afm.contains(",")){	//Null or multiple payment agents -> return false
    		//System.out.println("False afm: " + afm);
    		return false;
    	}
    	afm = afm.replaceAll("[oOοΟ]", "0");
    	//afm = afm.replaceAll("[^0-9&,\" & \"]", "");
    	afm = afm.replaceAll("[^0-9]", "");
    	if(afm.length()==9){
    		//System.out.println(afm);
    		
    		try{
    		double res = Integer.parseInt(Character.toString(afm.charAt(0)))*256 + Integer.parseInt(Character.toString(afm.charAt(1)))*128+Integer.parseInt(Character.toString(afm.charAt(2)))*64+Integer.parseInt(Character.toString(afm.charAt(3)))*32+Integer.parseInt(Character.toString(afm.charAt(4)))*16+Integer.parseInt(Character.toString(afm.charAt(5)))*8+Integer.parseInt(Character.toString(afm.charAt(6)))*4+Integer.parseInt(Character.toString(afm.charAt(7)))*2;
    		double div = res%11;  
    		if(afm.equals("000000000"))
    			return false;
    		return(Double.parseDouble(Character.toString(afm.charAt(8)))==div%10);
    		}
    		catch(Exception e){
    			System.out.println("Afm Exception: " + afm);    			
    		}
    		return false;
    	}
    	else{    		
    		if(afm.length()==8){
    			try{
    				String afmNew = "0".concat(afm);
    	    		double res = Integer.parseInt(Character.toString(afmNew.charAt(0)))*256 + Integer.parseInt(Character.toString(afmNew.charAt(1)))*128+Integer.parseInt(Character.toString(afmNew.charAt(2)))*64+Integer.parseInt(Character.toString(afmNew.charAt(3)))*32+Integer.parseInt(Character.toString(afmNew.charAt(4)))*16+Integer.parseInt(Character.toString(afmNew.charAt(5)))*8+Integer.parseInt(Character.toString(afmNew.charAt(6)))*4+Integer.parseInt(Character.toString(afmNew.charAt(7)))*2;
    	    		double div = res%11;  
    	    		if(afmNew.equals("000000000"))
    	    			return false;
    	    		if(Double.parseDouble(Character.toString(afmNew.charAt(8)))==div%10){
    	    			hashmap.remove(field);
    	    			hashmap.put(field, afmNew);
    	    			/*System.out.println("CORRECTED AFM: " + afm + " to: " + afmNew);    	    			    	    			
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));*/
    	    			return true;
    	    		}
    	    	}
    			catch(Exception e){
	    			//System.out.println("Exception: " + afm);	    			
	    		}
    			try{
    				String afmNew = afm.concat("0");
    	    		double res = Integer.parseInt(Character.toString(afmNew.charAt(0)))*256 + Integer.parseInt(Character.toString(afmNew.charAt(1)))*128+Integer.parseInt(Character.toString(afmNew.charAt(2)))*64+Integer.parseInt(Character.toString(afmNew.charAt(3)))*32+Integer.parseInt(Character.toString(afmNew.charAt(4)))*16+Integer.parseInt(Character.toString(afmNew.charAt(5)))*8+Integer.parseInt(Character.toString(afmNew.charAt(6)))*4+Integer.parseInt(Character.toString(afmNew.charAt(7)))*2;
    	    		double div = res%11;  
    	    		if(afmNew.equals("000000000"))
    	    			return false;
    	    		if(Double.parseDouble(Character.toString(afmNew.charAt(8)))==div%10){
    	    			hashmap.remove(field);
    	    			hashmap.put(field, afmNew);
    	    			/*System.out.println("CORRECTED AFM: " + afm + " to: " + afmNew);
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));*/
    	    			return true;
    	    		}
    	    	}
    	    		catch(Exception e){
    	    			//System.out.println("Exception: " + afm);    	    			
    	    		}
    			
    			try{
    				String afmNew = "1".concat(afm);
    	    		double res = Integer.parseInt(Character.toString(afmNew.charAt(0)))*256 + Integer.parseInt(Character.toString(afmNew.charAt(1)))*128+Integer.parseInt(Character.toString(afmNew.charAt(2)))*64+Integer.parseInt(Character.toString(afmNew.charAt(3)))*32+Integer.parseInt(Character.toString(afmNew.charAt(4)))*16+Integer.parseInt(Character.toString(afmNew.charAt(5)))*8+Integer.parseInt(Character.toString(afmNew.charAt(6)))*4+Integer.parseInt(Character.toString(afmNew.charAt(7)))*2;
    	    		double div = res%11;  
    	    		if(afmNew.equals("111111111"))
    	    			return false;
    	    		if(Double.parseDouble(Character.toString(afmNew.charAt(8)))==div%10){
    	    			hashmap.remove(field);
    	    			hashmap.put(field, afmNew);
    	    			/*System.out.println("CORRECTED AFM: " + afm + " to: " + afmNew);    	    			    	    			
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));*/
    	    			return true;
    	    		}
    	    	}
    			catch(Exception e){
	    			//System.out.println("Exception: " + afm);	    			
	    		}
    			try{
    				String afmNew = afm.concat("1");
    	    		double res = Integer.parseInt(Character.toString(afmNew.charAt(0)))*256 + Integer.parseInt(Character.toString(afmNew.charAt(1)))*128+Integer.parseInt(Character.toString(afmNew.charAt(2)))*64+Integer.parseInt(Character.toString(afmNew.charAt(3)))*32+Integer.parseInt(Character.toString(afmNew.charAt(4)))*16+Integer.parseInt(Character.toString(afmNew.charAt(5)))*8+Integer.parseInt(Character.toString(afmNew.charAt(6)))*4+Integer.parseInt(Character.toString(afmNew.charAt(7)))*2;
    	    		double div = res%11;  
    	    		if(afmNew.equals("111111111"))
    	    			return false;
    	    		if(Double.parseDouble(Character.toString(afmNew.charAt(8)))==div%10){
    	    			hashmap.remove(field);
    	    			hashmap.put(field, afmNew);
    	    			/*System.out.println("CORRECTED AFM: " + afm + " to: " + afmNew);
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));*/
    	    			return true;
    	    		}
    	    	}
    	    		catch(Exception e){
    	    			//System.out.println("Exception: " + afm);    	    			
    	    		}
    		}
    		
    		if(afm.length()>9){
    			try{
    				String afmNew = afm.substring(1,10);
    	    		double res = Integer.parseInt(Character.toString(afmNew.charAt(0)))*256 + Integer.parseInt(Character.toString(afmNew.charAt(1)))*128+Integer.parseInt(Character.toString(afmNew.charAt(2)))*64+Integer.parseInt(Character.toString(afmNew.charAt(3)))*32+Integer.parseInt(Character.toString(afmNew.charAt(4)))*16+Integer.parseInt(Character.toString(afmNew.charAt(5)))*8+Integer.parseInt(Character.toString(afmNew.charAt(6)))*4+Integer.parseInt(Character.toString(afmNew.charAt(7)))*2;
    	    		double div = res%11;  
    	    		if(afmNew.equals("000000000"))
    	    			return false;
    	    		if(Double.parseDouble(Character.toString(afmNew.charAt(8)))==div%10){
    	    			hashmap.remove(field);
    	    			hashmap.put(field, afmNew);
    	    			/*System.out.println("CORRECTED AFM: " + afm + " to: " + afmNew);
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));*/
    	    			return true;
    	    		}
    	    	}
    	    		catch(Exception e){
    	    			//System.out.println("Exception: " + afm);    	    			
    	    		}
    			try{
    				String afmNew = afm.substring(0,9);
    	    		double res = Integer.parseInt(Character.toString(afmNew.charAt(0)))*256 + Integer.parseInt(Character.toString(afmNew.charAt(1)))*128+Integer.parseInt(Character.toString(afmNew.charAt(2)))*64+Integer.parseInt(Character.toString(afmNew.charAt(3)))*32+Integer.parseInt(Character.toString(afmNew.charAt(4)))*16+Integer.parseInt(Character.toString(afmNew.charAt(5)))*8+Integer.parseInt(Character.toString(afmNew.charAt(6)))*4+Integer.parseInt(Character.toString(afmNew.charAt(7)))*2;
    	    		double div = res%11;  
    	    		if(afmNew.equals("000000000"))
    	    			return false;
    	    		if(Double.parseDouble(Character.toString(afmNew.charAt(8)))==div%10){
    	    			hashmap.remove(field);
    	    			hashmap.put(field, afmNew);
    	    			/*System.out.println("CORRECTED AFM: " + afm + " to: " + afmNew);
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));
    	    			System.out.println(hashmap.get("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"));*/
    	    			return true;
    	    		}
    	    	}
    	    		catch(Exception e){
    	    			//System.out.println("Exception: " + afm);    	    			
    	    		}
    		    		
    		}
    		
    		//System.out.println("False afm: " + originalAfm);
    		return false;
    	}
		
    }
    
    static public boolean checkCpv(HashMap<String, String> hashmap){
    	
    	Pattern p = Pattern.compile("[0-9]{8}-[0-9]");
		Matcher m = p.matcher((String)hashmap.get(" CPV Number"));
		while(m.find()){
			hashmap.remove(" CPV Number");
			hashmap.put(" CPV Number", m.group());
			return true;
		}
		return false;
    }
    
    static public boolean checkCpvString(String cpvCode, HashMap<String, String> hashmap){
    	
    	//To String cpvCode periexei kai ta 2 pedia apo to CSV enwmena me white space    	
    	String cpvCodeP = cpvCode.replaceAll("[^0-9-]", "");        
    	Pattern p = Pattern.compile("[0-9]{8}-[0-9]");    
		Matcher m = p.matcher(cpvCodeP);
		while(m.find()){
			hashmap.remove(" CPV Number");
			hashmap.put(" CPV Number", m.group());
			return true;
		}		
		hashmap.remove(" CPV Number");
		hashmap.put(" CPV Number", cpvCode);
		return false;
    }
    
    static public boolean checkCpvDescription(HashMap<String, String> hashmap){
    	
    	Pattern p = Pattern.compile("[0-9]{8}-[0-9]");
		Matcher m = p.matcher((String)hashmap.get("CPV Description"));
		while(m.find()){
			hashmap.remove(" CPV Number");
			hashmap.put(" CPV Number", m.group());
			return true;
		}
		return false;
    }
    /*
     * This method creates a string representation of the payment amount that is processable by future sparql queries
     * 
     * @amount A String representation of the payment amount as it was parsed in the CSV file
     * @return A String representation of the payment amount that will be convertible to decimal by SPARQL
     * 
     */
    static String decoratePaymentAmount(String amount){
    	
		Double amountDouble;    		
		if(amount.equals("Null"))
			amount = "0";
		int divider = 100;				
		amount = amount.replaceAll("[^0-9\\.,]", "");			
		if(amount.contains(",") || amount.contains(".")){
			if(amount.charAt(amount.length()-2)=='.' || amount.charAt(amount.length()-2)==',')
				divider = 10;
			amount = amount.replace(".", "").replace(",", "");
			amountDouble = Double.valueOf(amount);    			
			amountDouble = amountDouble/divider;
		}
		else
			amountDouble = Double.valueOf(amount);
		return amountDouble.toString();
		
    }
    
static Double decoratePaymentAmountDouble(String amount){
    	
		Double amountDouble;    		
		if(amount.equals("Null"))
			amount = "0";
		int divider = 100;				
		amount = amount.replaceAll("[^0-9\\.,]", "");			
		if(amount.contains(",") || amount.contains(".")){
			if(amount.charAt(amount.length()-2)=='.' || amount.charAt(amount.length()-2)==',')
				divider = 10;
			amount = amount.replace(".", "").replace(",", "");
			amountDouble = Double.valueOf(amount);    			
			amountDouble = amountDouble/divider;
		}
		else
			amountDouble = Double.valueOf(amount);
		return amountDouble;
		
    }
    
}
