package publicspending.java.daily;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

import diavgeia.java.daily.SPARQLQueries;

public class HelperMethods {

	
	public String[] getDateElements(String date){
    	
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
		return "Null";
    	
    }
	
	public  String decoratePaymentAmount(String amount){
    	
		Double amountDouble;    		
		if(amount.equals("Null") || amount.equals(""))
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
		else{
			//System.out.println("kjsdhgksjd" + amount);
			amountDouble = Double.valueOf(amount);
			
		}
		return amountDouble.toString();
		
    }
    
	public Double decoratePaymentAmountDouble(String amount){
    	
		Double amountDouble;    		
		if(amount.equals("Null") || amount.equals(""))
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
	
	public ArrayList<String[]> getDecisionDates(Model validModel, Model invalidModel){
		
		//Returns the (distinct) days that were found to be associated with decisions on the particular graph.
		//Note: This should query the temporary decisions graph
		
		QueryExecution qe = QueryExecutionFactory.create (SPARQLQueries.dayQuery, validModel.union(invalidModel));
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
	
public ArrayList<String[]> getUniversalDecisionDates(){
		
		//Returns the (distinct) days that were found to be associated with decisions on the particular graph.
		/*//Note: This should query the temporary decisions graph
    	InputStream in = FileManager.get().open(rdfLocation);
		if (in == null) {
		     throw new IllegalArgumentException(
		                                  "File: " + rdfLocation + " not found");
		}
		Model model = ModelFactory.createDefaultModel();
		model.read(in,null);*/
    	VirtGraph graph = new VirtGraph (RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");
    	String query = SPARQLQueries.dayQueryUniversal;
    	//System.out.println(query);
    	VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
	 	ResultSet results = vqe.execSelect();		
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
	
	public void zipFiles(String filesFolder, String jsonDateFolder){
    	try
    	 {
    		File inFolder=new File(jsonDateFolder);
    		File outFolder=new File(filesFolder+"dailyOutput.zip");
    	 	ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder)));
    	 	int len = inFolder.getAbsolutePath().lastIndexOf(File.separator);
			String baseName = inFolder.getAbsolutePath().substring(0,len+1);
			
			addFolderToZip(inFolder, out, baseName);
			
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	 }
    
    private static void addFolderToZip(File folder, ZipOutputStream zip, String baseName) throws IOException {
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				addFolderToZip(file, zip, baseName);
			} else {
				String name = file.getAbsolutePath().substring(baseName.length());
				ZipEntry zipEntry = new ZipEntry(name);
				zip.putNextEntry(zipEntry);
				IOUtils.copy(new FileInputStream(file), zip);
				zip.closeEntry();
			}
		}
	}
    
    public static String fixEncoding(String latin1) {
    	  try {
    	   byte[] bytes = latin1.getBytes("ISO-8859-1");
    	   if (!validUTF8(bytes))
    	    return latin1;   
    	   return new String(bytes, "UTF-8");  
    	  } catch (UnsupportedEncodingException e) {
    	   // Impossible, throw unchecked
    	   throw new IllegalStateException("No Latin1 or UTF-8: " + e.getMessage());
    	  }

    	 }

    	 public static boolean validUTF8(byte[] input) {
    	  int i = 0;
    	  // Check for BOM
    	  if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
    	    && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
    	   i = 3;
    	  }

    	  int end;
    	  for (int j = input.length; i < j; ++i) {
    	   int octet = input[i];
    	   if ((octet & 0x80) == 0) {
    	    continue; // ASCII
    	   }

    	   // Check for UTF-8 leading byte
    	   if ((octet & 0xE0) == 0xC0) {
    	    end = i + 1;
    	   } else if ((octet & 0xF0) == 0xE0) {
    	    end = i + 2;
    	   } else if ((octet & 0xF8) == 0xF0) {
    	    end = i + 3;
    	   } else {
    	    // Java only supports BMP so 3 is max
    	    return false;
    	   }

    	   while (i < end) {
    	    i++;
    	    octet = input[i];
    	    if ((octet & 0xC0) != 0x80) {
    	     // Not a valid trailing byte
    	     return false;
    	    }
    	   }
    	  }
    	  return true;
    	 }
    	 
    	 static public void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
    		    if (sourceLocation.isDirectory()) {
    		        if (!targetLocation.exists()) {
    		            targetLocation.mkdir();
    		        }

    		        String[] children = sourceLocation.list();
    		        for (int i=0; i<children.length; i++) {
    		            copyDirectory(new File(sourceLocation, children[i]),
    		                    new File(targetLocation, children[i]));
    		        }
    		    } else {

    		        InputStream in = new FileInputStream(sourceLocation);
    		        OutputStream out = new FileOutputStream(targetLocation);

    		        // Copy the bits from instream to outstream
    		        byte[] buf = new byte[1024];
    		        int len;
    		        while ((len = in.read(buf)) > 0) {
    		            out.write(buf, 0, len);
    		        }
    		        in.close();
    		        out.close();
    		    }
    		}
}
