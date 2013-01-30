package publicspending.java.daily;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import publicspending.java.control.NameController;
import diavgeia.java.ontology.Ontology;

public class RoutineInvoker {

	/**
	 * @param args
	 */
	static String graphName = "http://publicspending.medialab.ntua.gr/Decisions";
	//static String graphName = "testing.graph";	
	static String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";	
	//static String connectionString = "jdbc:virtuoso://83.212.110.193:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
	static String dayGraphName = "diavgeia.DayGraph1";
	static String weekGraphName = "diavgeia.WeekGraph1";
	static String monthGraphName = "diavgeia.MonthGraph1";
	static String yearGraphName = "diavgeia.YearGraph1";
	static String overallGraphName = "diavgeia.OverallGraph1";
	static String binaryGraphName = "diavgeia.BinaryGraph1";
	static String analyticsGraphName = "http://publicspending.medialab.ntua.gr/Analytics";
	static String overallResource = "<"+Ontology.diavgeiaPrefix+"Overall>", falseGraphName = "fake.graph", cpvGraphName = "";
	
	static boolean testMode;
	//static boolean testMode = true;
	
	//public static void main(String[] args) {			
	public RoutineInvoker(String[] args, boolean testMode){
		this.testMode = testMode;	//set test mode (true or false)
		FileStructure fs = null;	//initialize FileStructure object
		HelperMethods hm = new HelperMethods();	//build a HelperMethods object (TODO make static calls to HM?)
		System.out.println("Creating folders and files...");	
		if(args.length>0){	//Check if there are any arguments
			if(!args[0].equals("")){ //first argument sets root folder (relative path)
				System.out.println("Root Folder is set at " + args[0]);
				fs = new FileStructure(args[0]); //initialize the FileStructure object with the given root folder
			}					
			if(args.length==2 && !args[1].equals("") && args[1]!=null){
				System.out.println("Graph is set at " + args[1]);
				graphName = args[1];
			}			
		}
		else
			fs = new FileStructure();
		if(!testMode){								
			
			System.out.println("Done.");
			DecisionGatherer decisionGatherer = new DecisionGatherer(fs);							
			try {
				//ArrayList<HashMap<String, String>> decisionsList = decisionGatherer.getDecisions(500.00);
				ArrayList<HashMap<String, String>> decisionsList = decisionGatherer.getDecisionsInXML(500.00);
				Iterator it = decisionsList.iterator();
				RDFModelWriter rdfModelWriter = new RDFModelWriter(fs);
				while(it.hasNext()){
					HashMap<String, String> decisionMap = (HashMap<String, String>) it.next();				
					rdfModelWriter.AddToModel(decisionMap);				
				}		
				
				System.out.println("array size is " + decisionsList.size());
				//System.out.println("Press Any Key To Continue...");				
		          //new java.util.Scanner(System.in).nextLine();
				if(decisionsList.size()>0){
					//Upload models to Virtuoso here							
					if(!rdfModelWriter.writeModels()){
						System.out.println("Press Any Key To Continue...");				
				          new java.util.Scanner(System.in).nextLine();
						System.out.println("Read file failed...");
						System.out.println("Trying to upload models directly...");
						rdfModelWriter.rdfModelDirectUploader(rdfModelWriter.model, rdfModelWriter.invalidModel);
						try{
							  // Create file 
							  FileWriter fstream = new FileWriter(fs.rootFaultyDatesFolder + "faultyDates.txt",true);
							  BufferedWriter out = new BufferedWriter(fstream);
							  out.write(fs.today);
							  //Close the output stream
							  out.close();
							  }catch (Exception e){
								  //Catch exception if any
								  System.err.println("Error: " + e.getMessage());
							  }
					}				
					else{
						
						System.out.println("Uploading models to virtuoso....");
						System.out.println("Press Any Key To Continue...");				
				          new java.util.Scanner(System.in).nextLine();
						rdfModelWriter.rdfModelUploader();
					}
					System.out.println("Done uploading.");				
					System.out.println("Getting dates...");
					ArrayList<String[]> dayList = hm.getDecisionDates(rdfModelWriter.model, rdfModelWriter.invalidModel);												
					//GSIS Calls Code Block
					System.out.println("Calling GSIS...");
					SOAPHandler gsisHandler = new SOAPHandler(fs);
					gsisHandler.soapHandler(0);
					SOAPHandler gsisHandler2 = new SOAPHandler(fs);
					gsisHandler2.soapHandler(1);
					//End of GSIS Calls Code Block
					System.out.println("Handling names...");
					NameController nc = new NameController();
					nc.uniqueNamesOnIteration();
					System.out.println("Invoking handlers...");
					StatisticsHandler sh = new StatisticsHandler(dayList);
					OutputHandler oh = new OutputHandler(fs);		
					System.out.println("Compressing output.");
					hm.zipFiles(fs.decisionCountFolder, fs.jsonDateFolder);
					System.out.println("Done.");
					System.out.println("Sending email.");
					MailSender ms = new MailSender(fs.decisionCountFolder + "dailyOutput.zip", "johnnyxidias@hotmail.com");
					try {ms.test();System.out.println("Done.");} catch (Exception e) {e.printStackTrace();}					
					try {
						hm.copyDirectory(new File(fs.jsonDateFolder), new File(fs.dataFolder));
					} catch (IOException e1) {						
						System.out.println("Couldn't copy files...");
						e1.printStackTrace();
					}
				}
				else if (decisionsList == null) {System.out.println("Error with decisions."); System.exit(0);}
				else{
					System.out.println("No models to write.");
					OutputHandler oh = new OutputHandler(fs);		
					System.out.println("Compressing output.");
					hm.zipFiles(fs.decisionCountFolder, fs.jsonDateFolder);
					System.out.println("Done.");
					System.out.println("Sending email.");
					MailSender ms = new MailSender(fs.decisionCountFolder + "dailyOutput.zip", "johnnyxidias@hotmail.com");
					try {ms.test();System.out.println("Done.");} catch (Exception e) {e.printStackTrace();}		
					//NameController nc = new NameController();
					//nc.uniqueNamesOnIteration();
				}
				
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println("Test Mode on...");	
			//StatisticsHandler sh = new StatisticsHandler(hm.getUniversalDecisionDates());
			//SOAPHandler gsisHandler = new SOAPHandler(fs);
			//OutputHandler oh = new OutputHandler(fs);
			SOAPHandler gsisHandler = new SOAPHandler(fs);
			gsisHandler.soapHandler(1);
			/*DecisionGatherer decisionGatherer = new DecisionGatherer(fs);
			try {
				decisionGatherer.getDecisionsInXML(500.00);
			} catch (IOException e) {
				
				e.printStackTrace();
			}*/
			/*MailSender ms = new MailSender(fs.decisionCountFolder + "dailyOutput.zip", "johnnyxidias@hotmail.com");
			try {ms.test();System.out.println("Done.");} catch (Exception e) {e.printStackTrace();}		*/
		}		
	}
	
}
