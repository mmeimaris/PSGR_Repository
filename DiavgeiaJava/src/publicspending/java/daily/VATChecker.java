package publicspending.java.daily;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VATChecker {

	
	public static boolean checkAfm(String afm, HashMap<String, String> hashmap, String field){
    	
    	String originalAfm = afm;
    	if(afm==null){
    		System.out.println("Yoko Chocko");
    	}
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
}
