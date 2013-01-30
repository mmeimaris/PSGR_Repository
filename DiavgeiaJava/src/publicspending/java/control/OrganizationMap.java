package publicspending.java.control;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Resource;

import diavgeia.java.ontology.LegalStatusOntology;

public class OrganizationMap {
	
	private static ArrayList<String> plcArray = new ArrayList<String>();
	
	
	private static ArrayList<String> ltdArray = new ArrayList<String>();
												   													
	
	private static ArrayList<String> lpArray = new ArrayList<String>();
	
	private static ArrayList<String> gpArray = new ArrayList<String>();
		
	private static ArrayList<String> publicArray = new ArrayList<String>();																										
	
	private static ArrayList<String> nonProfitArray = new ArrayList<String>();														   														  
	
	public OrganizationMap(){
		plcArray.add("ΑΕ");
		plcArray.add("ΑΕ ΜΟΝΟΠΡΟΣΩΠΗ");
		plcArray.add("ΑΕ ΟΤΑ");
		plcArray.add("ΕΝΤΟΛΕΑΣ ΕΕ ΜΗ ΦΠ ΑΕ");
		plcArray.add("ΑΕ ΕΠΙΧ ΠΡΟΓΡΑΜ ΚΠΣ (Ν.2860/2000)");
		plcArray.add("AN 89/67 ΗΜΕΔΑΠΗ ΑΕ");
		plcArray.add("ΑΕ ΜΗ ΚΕΡΔΟΣΚΟΠΙΚΗ");
		plcArray.add("ΑΕ ΟΑΕΔ (Ν.2956/2001)");
		plcArray.add("ΕΝΤΟΛΕΑΣ ΜΗ ΦΠ ΑΕ");
		ltdArray.add("ΕΠΕ");
		ltdArray.add("ΕΠΕ ΜΟΝΟΠΡΟΣΩΠΗ");
		ltdArray.add("ΙΜΕ ΕΠΕ");
		ltdArray.add("ΙΜΕ ΕΠΕ ΜΟΝΟΠΡΟΣΩΠΗ");
		ltdArray.add("ΕΝΤΟΛΕΑΣ ΕΕ ΜΗ ΦΠ ΕΠΕ");
		ltdArray.add("ΕΝΤΟΛΕΑΣ ΜΗ ΦΠ ΕΠΕ");
		ltdArray.add("ΑΝ 89/67 ΗΜΕΔΑΠΗ ΕΠΕ");
		lpArray.add("ΕΕ");
		gpArray.add("ΟΕ");
		publicArray.add("ΝΟΜΙΚΟ ΠΡΟΣΩΠΟ ΔΗΜΟΣΙΟΥ ΔΙΚΑΙΟΥ");
		publicArray.add("ΔΗΜΟΣΙΑ ΥΠΗΡΕΣΙΑ");
		nonProfitArray.add("ΑΕ ΜΗ ΚΕΡΔΟΣΚΟΠΙΚΗ");
		nonProfitArray.add("ΑΛΛΟ ΝΠΙΔ ΜΗ ΚΕΡΔΟΣΚΟΠΙΚΟ");
		nonProfitArray.add("ΑΣΤΙΚΗ ΜΗ ΚΕΡΔΟΣΚΟΠΙΚΗ ΕΤΑΙΡΙΑ");
		nonProfitArray.add("ΑΛΛΗ ΕΝΩΣΗ ΠΡΟΣΩΠΩΝ ΜΗ ΚΕΡΔΟΣΚΟΠΙΚΗ");
		nonProfitArray.add("ΑΣΤΙΚΟΣ ΣΥΝΕΤΑΙΡΙΣΜΟΣ ΜΗ ΚΕΡΔΟΣΚΟΠΙΚΟΣ");
		nonProfitArray.add("ΚΟΙΝΩΝΙΑ ΑΣΤΙΚΟΥ ΔΙΚΑΙΟΥ ΜΗ ΚΕΡΔΟΣΚΟΠ.");				   		   		   
		
		 		 													 
	}
	public static Resource entityCheckArrays(String type){
		if(plcArray.contains(type)) return LegalStatusOntology.plcEntity;
		else if(ltdArray.contains(type)) return LegalStatusOntology.ltdEntity;
		else if(lpArray.contains(type)) return LegalStatusOntology.limitedPartnershipEntity;
		else if(gpArray.contains(type)) return LegalStatusOntology.generalPartnershipEntity;
		else if(publicArray.contains(type)) return LegalStatusOntology.publicLegalEntity;
		else return LegalStatusOntology.otherLegalEntity;
	}
	
	public static String profitCheckArrays(String type){
		if(nonProfitArray.contains(type)) return "Not For Proft";
		else return "For Profit";
	}
	 
	


}
