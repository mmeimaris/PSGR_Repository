package diavgeia.java.daily;

import java.util.HashMap;

public class CPVColours {
	
	private static final HashMap<String, String> colours= new HashMap<String, String>();	
	
	public CPVColours(){
				
		colours.put("80000000-4", "#00FFFF");
		colours.put("92000000-1", "#66FFFF");
		colours.put("44000000-0", "#0000FF");
		colours.put("45000000-7", "#3333FF");		
		colours.put("51000000-9", "#3366FF");
		colours.put("71000000-8", "#6666FF");
		colours.put("42000000-6", "#9999FF");
		colours.put("43000000-3", "#0066FF");		
		colours.put("50000000-5", "#0099FF");
		colours.put("70000000-1", "#00CCFF");
		colours.put("90000000-7", "#FF00FF");
		colours.put("41000000-9", "#FF33FF");
		colours.put("60000000-8", "#808080");
		colours.put("63000000-9", "#CCCCCC");
		colours.put("55000000-0", "#999999");
		colours.put("34000000-7", "#666666");
		colours.put("65000000-3", "#333333");
		colours.put("75000000-6", "#000000");
		colours.put("35000000-4", "#9999CC");
		colours.put("30000000-9", "#008000");
		colours.put("22000000-0", "#66FF00");
		colours.put("31000000-6", "#99FF66");
		colours.put("38000000-5", "#00FF00");
		colours.put("39000000-2", "#66FF66");
		colours.put("03000000-1", "#800000");
		colours.put("77000000-0", "#CC3366");
		colours.put("16000000-5", "#CC3399");
		colours.put("15000000-8", "#660033");
		colours.put("73000000-2", "#000080");
		colours.put("72000000-5", "#3333FF");
		colours.put("48000000-8", "#3333CC");
		colours.put("24000000-4", "#808000");
		colours.put("09000000-3", "#00FF66");
		colours.put("14000000-1", "#00FF99");
		colours.put("76000000-3", "#66FFCC");
		colours.put("85000000-9", "#FF0000");
		colours.put("33000000-0", "#FF0099");
		colours.put("66000000-0", "#C0C0C0");
		colours.put("79000000-4", "#CCCC99");
		colours.put("98000000-3", "#FFFFCC");
		colours.put("37000000-8", "#008080");
		colours.put("18000000-9", "#FF9933");
		colours.put("19000000-6", "#FFCC99");
		colours.put("64000000-6", "#FFFF00");
		colours.put("32000000-3", "#CCCC00");
		//colours.put("undefined", "#696969");
		colours.put("undefined", "#F52887");		
					
	}
	
	public String getColourFromCPV(String cpvCode){
		String returnS = "";
		try{
			returnS =colours.get(cpvCode); 
		}
		catch(Exception e){}		
		return(returnS);
	}

}
