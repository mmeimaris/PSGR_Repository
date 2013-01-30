package publicspending.java.daily;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import diavgeia.java.daily.DailyRoutineHandler;

public class FileStructure {
	
	public String rootFolder;
	public String decisionCounter = rootFolder + "Files/decisionCount.txt";
	public String filesFolder = rootFolder + "Files/";
	public String rootCSVFolder = rootFolder + "CSV-Files/";
	public String rootRDFFolder = rootFolder + "RDF-Files/";
	public String rootJSONFolder = rootFolder + "JSONOutput/";
	public String rootFaultyDatesFolder = rootFolder + "FaultyDates/";
	public String yesterday = null, today = null, jsonDateFolder = null, rdfDateFolder = null, csvDateFolder = null,
			validRDFDateFolder = null, invalidRDFDateFolder = null, decisionCountFolder = null, dataFolder = null;
	public String csvDateFile = null, validRDFDateFile = null, invalidRDFDateFile = null, decisionCountFile = null,
			soapRequestLocation = null, soapResponseLocation = null, gsisLocation = null; 
			//= "C:/Users/marios/Desktop/soap.xml";
	
	public FileStructure(){	
		rootFolder = "C:/Users/marios/Desktop/Diavgeia-Root/";
		//rootFolder = "/root/routine/";
		new File(rootFolder).mkdirs();
		setDateStrings();
		setFolderStrings();
		initializeFolders();
	}
	
	public FileStructure(String rootFolder){
		this.rootFolder = rootFolder;
		new File(rootFolder).mkdirs();
		setDateStrings();
		setFolderStrings();
		initializeFolders();
	}

	private void setDateStrings(){
		int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
		Date todayDate = new Date();		
		Date dateYesterday = new Date(todayDate.getTime() - MILLIS_IN_DAY);
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
		yesterday = dayFormat.format(dateYesterday);
		today  = dayFormat.format(todayDate);
	}
	
	private void setFolderStrings(){
		decisionCounter = rootFolder + "Files/decisionCount.txt";
		dataFolder = rootFolder + "data/";
		filesFolder = rootFolder + "Files/";
		rootCSVFolder = rootFolder + "CSV-Files/";
		rootRDFFolder = rootFolder + "RDF-Files/";
		rootJSONFolder = rootFolder + "JSONOutput/";
		jsonDateFolder = rootJSONFolder + today + "/";
		rdfDateFolder = rootRDFFolder + today + "/";
		csvDateFolder = rootCSVFolder + today + "/";		
		validRDFDateFolder = rdfDateFolder + "Valid-RDF/";
		invalidRDFDateFolder = rdfDateFolder + "Invalid-RDF/";
		decisionCountFolder = filesFolder + today + "/";
		rootFaultyDatesFolder = rootFolder + "FaultyDates/";
	}
	
	private void initializeFolders(){
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy");
		new File(jsonDateFolder).mkdirs();
		new File(rdfDateFolder).mkdirs();
		new File(validRDFDateFolder).mkdirs();
/*		File f1=new File(validRDFDateFolder+today+".rdf");
		  if(!f1.exists()){
			  try {
				f1.createNewFile();
			  } 
			  catch (IOException e) {}
		  }
		validRDFDateFile = f1.getAbsolutePath();*/
		validRDFDateFile = validRDFDateFolder+today+".rdf";
		new File(invalidRDFDateFolder).mkdirs();
		/*File f2=new File(invalidRDFDateFolder+today+".rdf");
		  if(!f2.exists()){
			  try {
				f2.createNewFile();
			  } 
			  catch (IOException e) {}
		  }
		invalidRDFDateFile = f2.getAbsolutePath();*/
		invalidRDFDateFile = invalidRDFDateFolder+today+".rdf";
		new File(csvDateFolder).mkdirs();		
		new File(filesFolder).mkdirs();
		new File(jsonDateFolder + "/toppayments").mkdirs();
		new File(jsonDateFolder + "/toppayers").mkdirs();
		new File(jsonDateFolder + "/toppayees").mkdirs();
		new File(jsonDateFolder + "/topcpv").mkdirs();
		new File(jsonDateFolder + "/45cpv").mkdirs();
		new File(jsonDateFolder + "/paymentstimeplot").mkdirs();
		new File(jsonDateFolder + "/counter").mkdirs();
		new File(jsonDateFolder + "/bubbles").mkdirs();
		new File(jsonDateFolder + "/tables").mkdirs();
		new File(jsonDateFolder + "/payertabs").mkdirs();		
		new File(jsonDateFolder + "/payeetabs").mkdirs();		
		new File(decisionCountFolder).mkdirs();
		File f3=new File(decisionCountFolder+"decisionCount.txt");
		  if(!f3.exists()){
			  try {
				f3.createNewFile();
			  } 
			  catch (IOException e) {}
		  }
		decisionCountFile = f3.getAbsolutePath();	
		File f4=new File(decisionCountFolder+"soapRequest.xml");
		  if(!f4.exists()){
			  try {
				f4.createNewFile();
			  } 
			  catch (IOException e) {}
		  }
		soapRequestLocation = f4.getAbsolutePath();	
		File f5=new File(decisionCountFolder+"soapResponse.xml");
		  if(!f5.exists()){
			  try {
				f5.createNewFile();
			  } 
			  catch (IOException e) {}
		  }
		soapResponseLocation = f5.getAbsolutePath();	
		File f6=new File(decisionCountFolder+"gsis.rdf");
		  if(!f6.exists()){
			  try {
				f6.createNewFile();
			  } 
			  catch (IOException e) {}
		  }
		gsisLocation = f6.getAbsolutePath();
	}
	
	public void setCsvPath(){
		File f=new File(csvDateFolder+today+"tt"+System.currentTimeMillis()+".csv");
		  if(!f.exists()){
			  try {
				f.createNewFile();
			  } 
			  catch (IOException e) {}
		  }
		csvDateFile = f.getAbsolutePath();
	}
}
