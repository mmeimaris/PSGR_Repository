package diavgeia.java.daily;

public class CSVFieldMappings {
	
	private String[] fields;
	
    public CSVFieldMappings(String[] fields){    	
    	this.fields = fields;		 
	}
    
    public String getField(String field){
    	
    	if(field.equals("ada"))
    		return fields[0]; 
    	else if(field.equals("isCorrectionOfAda"))
    		return fields[1];
    	else if(field.equals("relativeAda"))
    		return fields[2];
    	else if(field.equals("submissionTimestamp"))
    		return fields[3];
    	else if(field.equals("url"))
    		return fields[4];
    	else if(field.equals("documentUrl"))
    		return fields[5];
    	else if(field.equals("protocolNumber"))
    		return fields[6];
    	else if(field.equals("decisionType"))
    		return fields[7];
    	else if(field.equals("subject"))
    		return fields[8];
    	else if(field.equals("date"))
    		return fields[9];
    	else if(field.equals("organization"))
    		return fields[10];
    	else if(field.equals("organizationUID"))
    		return fields[11];
    	else if(field.equals("organizationUnit"))
    		return fields[12];
    	else if(field.equals("organizationUnitUID"))
    		return fields[13];
    	else if(field.equals("FEK number"))
    		return fields[14];
    	else if(field.equals("FEK issue"))
    		return fields[15];
    	else if(field.equals("FEK year"))
    		return fields[16];
    	else if(field.equals("tagLabels"))
    		return fields[17];
    	else if(field.equals("tagUIDs"))
    		return fields[18];
    	else if(field.equals("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"))
    		return fields[19];
    	else if(field.equals("ΑΦΜ ΦΟΡΕΑ"))
    		return fields[20];
    	else if(field.equals("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)"))
    		return fields[21];
    	else if(field.equals("ΑΦΜ ΑΝΑΔΟΧΟΥ"))
    		return fields[22];
    	else if(field.equals("hasVAT"))
    		return fields[23];
    	else if(field.equals("isValidVAT"))
    		return fields[24];
    	else if(field.equals("CPV Description"))
    		return fields[25];
    	else if(field.equals(" CPV Number"))
    		return fields[26];
    	else if(field.equals("ΣΧΕΤΙΚΟΣ ΑΡΙΘΜΟΣ ΚΑΕ"))
    		return fields[27];
    	else if(field.equals("ΚΑΤΗΓΟΡΙΑ ΔΑΠΑΝΗΣ"))
    		return fields[28];
    	else if(field.equals("ΠΟΣΟ ΔΑΠΑΝΗΣ / ΣΥΝΑΛΛΑΓΗΣ (ΜΕ ΦΠΑ)"))
    		return fields[29];    	    	
    	return "no field returned";
    }
    
}  