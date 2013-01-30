package diavgeia.java.daily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import diavgeia.java.daily.PaymentAgent;

public class JsonWriter {
	
	
	/*
	 * JSON serializer for bar charts.
	 * 
	 */		
	public String jsonBarWriter(ArrayList<String[]> writerList, String tag, String[] date, String title, String engTitle){
		
		String writable = "[{ \n \"name\":\"" + "EUR, €" + "\", " + "\n \"title\":\"" + title + "\"," 
						   + "\n \"titleeng\":\"" + engTitle + "\","
						   + "\n \"date\":\"" + date[0] + "\","
						   + "\n \"dateeng\":\"" + date[1] + "\","
						   +"\n" + "\"categories\":";
		//String writable = "[{ \n \"name\":\"" + date + "\", " + "\n" + "\"categories\":";
		String y = "", category = "[", engCategory = "[", data = "[";		
		for(String[] writeArray : writerList){
			y += "{\"y\":" + writeArray[0] + "},";
			category += "\"" + writeArray[2] + "\",";
			engCategory +="\"" + writeArray[4] + "\",";
			data += "{\"name\": \"" + writeArray[1] + "\", \"nameeng\": \"" + writeArray[3] + "\", \"y\": " + writeArray[0] + "}," ;
		}
		writable += category.substring(0,category.length()-1) + "],\n"+"\"categorieseng\": " + engCategory.substring(0,engCategory.length()-1) + "],\n" + "\"data\": " + data.substring(0, data.length()-1) + "]\n}]";
		return writable;
	}
	
	public String jsonPieWriter(ArrayList<String[]> writerList, String tag, String date, String title, String engTitle){
				
		String writable = "[{ \n \"name\":\"" + "EUR, €" + "\", " + "\n \"title\":\"" + title + "\"," + "\n \"engTitle\":\"" + engTitle + "\"," + "\n" + "\"data\":";
		String data = "[";
		for(String[] writeArray : writerList){
			data += "{\"name\": \"" + writeArray[1] + "\", \"y\": " + writeArray[0] + "}," ;
		}
		writable += data.substring(0, data.length()-1) + "]\n}]";
		return writable;
		
	}
	
	public String jsonTplotWriter(ArrayList<String[]> writerList, String tag, String title, String engTitle, String period){
		
		//String writable = "[{\n \"name\": \"" + tag + "\", \n \"data\": \n [";
		String writable = "[{\n \"name\": \"" + "EUR, €" + "\"," + "\n \"title\":\"" + title +"\", \n \"date\": \""+period+ "\"," + "\n \"titleeng\":\"" + engTitle + "\"," + "\n \"data\": \n [";
		String data = "";
		for(String[] writeArray : writerList){
			data += "{\"x\":" + writeArray[0] + ", \"y\":" + writeArray[1] + "},";
		}		
		writable += data.substring(0, data.length()-1) + "]\n}]";
		return writable;
	}
	
	public String jsonTplotCPVWriter(ArrayList<String[]> writerList, String title, String engTitle, String period){
		
		//String writable = "[{\n \"name\": \"" + tag + "\", \n \"data\": \n [";
		String writable = "[{\n \"name\": \"" + "EUR, €" + "\", "+ "\n \"title\":\"" + title +"\", \n \"date\": \""+period+ "\","+ "\n \"titleeng\":\"" + engTitle + "\"," + "\n \"data\": \n [";
		String data = "";
		for(String[] writeArray : writerList){
			data += "{\"x\":" + writeArray[0] + ", \"y\":" + writeArray[1] + "},";			
		}				
		//System.out.println(data.length());
		writable += data.substring(0, data.length()-1) + "]\n}]"; 
		return writable;
	}
	
	public String jsonBubbleWriter(ArrayList<String[]> writerList, String payerName, String totalAmount,  String title, String engTitle, String shortName, String engName, String engShortName){
		
		String writable = "var json = { \n name: '" + payerName + "', label: '" + shortName + "', \n amount:" + totalAmount + ", \n " + 
						  "children: [";
		String data = "";
		for(String[] writeArray : writerList){
			data += "{ name: '" + writeArray[0] + "', label: '" + writeArray[7] + "', amount: " + writeArray[1] + ", color:'"+writeArray[6] +"', cpv:'"+writeArray[2]+"'},\n" ;
		}
		writable += data.substring(0, data.length()-2) + "]\n};";
		String writable2 = "var jsoneng = { \n name: '" + engName + "', label: '" + engShortName + "', \n amount:" + totalAmount + ", \n " + 
				  "children: [";
		String data2 = "";
		for(String[] writeArray : writerList){
			data2 += "{ name: '" + writeArray[8] + "', label: '" + writeArray[9] + "', amount: " + writeArray[1] + ", color:'"+writeArray[6] +"', cpv:'"+writeArray[3]+"'},\n" ;
		}
		writable2 += data2.substring(0, data2.length()-2) + "]\n};";
		return writable + "\n" +  writable2;
	}
	
	public String jsonCpvLegendWriter(HashMap<String, String[]> writerList){
		
		String writable = "[";
		String data = "";
		Set<String> keySet = writerList.keySet();
		for(String cpvCode : keySet){
			String[] writeArray = writerList.get(cpvCode);
			data += "{\"cpv\": \"" + writeArray[0] + "\", \"cpveng\":\""+writeArray[1]+"\",  \"color\":\"" + writeArray[2] + "\"},";
		}
		writable += data.substring(0, data.length()-1) + "]";
		return writable;
	}
	
	public String jsonPayersListWriter(ArrayList<String[]> list){
		
		String writable = "";
		Iterator it = list.iterator();
		int i = 0;
		while(it.hasNext()){
			i++;
			String[] names = (String[]) it.next();
			String name = names[0];
			String shortName = names[1];
			String engName = names[2];
			String engShortName = names[3];
			writable += "var payer"+i+"=\"" + name + "\";\n";
			writable += "var payer"+i+"short=\"" + shortName + "\";\n";
			writable += "var payereng"+i+"=\"" + engName + "\";\n";
			writable += "var payereng"+i+"short=\"" + engShortName + "\";\n";
		}
		return writable;
	}
	
public String jsonPayeesListWriter(ArrayList<String[]> list){
		
		String writable = "";
		Iterator it = list.iterator();
		int i = 0;
		while(it.hasNext()){
			i++;
			String[] names = (String[]) it.next();
			String name = names[0];
			String shortName = names[1];
			String engName = names[2];
			String engShortName = names[3];
			writable += "var payee"+i+"=\"" + name + "\";\n";
			writable += "var payee"+i+"short=\"" + shortName + "\";\n";
			writable += "var payeeeng"+i+"=\"" + engName + "\";\n";
			writable += "var payeeeng"+i+"short=\"" + engShortName + "\";\n";
		}
		return writable;
	}

	public String jsonTopPayersTableWriter(ArrayList<String[]> list){
		String writable = "{ \"toppayers\": [\n";
		Iterator it = list.iterator();
		int i = 0;
		while(it.hasNext()){
			i++;
			String[] names = (String[]) it.next();
			writable += "{\"payer\": \"<a href=\\\"tab-payer"+i+".php\\\">"+names[1]+"</a>\", \"payereng\" : \"<a href=\\\"tab-payer"+i+".php\\\">"+names[3]+"</a>\", \"order\":"+i+", \"amount\": \"€"+decorateAmount(names[0])+"\"},\n";
		}
		writable = writable.substring(0,writable.lastIndexOf(",")) + "]";
		return writable;
	}
	public String jsonTopPayeesTableWriter(ArrayList<String[]> list){
		String writable = " \"toppayees\": [\n";
		Iterator it = list.iterator();
		int i = 0;
		while(it.hasNext()){
			i++;
			String[] names = (String[]) it.next();
			writable += "{\"payee\": \"<a href=\\\"tab-payee"+i+".php\\\">"+names[1]+"</a>\", \"payeeeng\" : \"<a href=\\\"tab-payee"+i+".php\\\">"+names[3]+"</a>\", \"order\":"+i+", \"amount\": \"€"+decorateAmount(names[0])+"\"},\n";
		}
		writable = writable.substring(0,writable.lastIndexOf(",")) + "]";
		return writable;
	}
	
	public String jsonMainBubbleWriter(HashMap<PaymentAgent, ArrayList<PaymentAgent>> agentMap, String sum){
				
		String writable = "var json = {\nname:'Σύνολο 10 πρώτων φορέων', label:'Σύνολο', amount:" + sum + ", children: [";
		String writableEng = "var json = {\nname:'Total amount for top 10 payers', label:'Total', amount:" + sum + ", children: [";
		Set topPayers = agentMap.keySet();
		Iterator topPayerIt = topPayers.iterator();
		while(topPayerIt.hasNext()){
			PaymentAgent topPayer = (PaymentAgent) topPayerIt.next();
			ArrayList<PaymentAgent> payeesList = agentMap.get(topPayer);			
			writable += "{name: '" + topPayer.getPaymentAgentName() + "', " +
						"label: '" + topPayer.getGreekShortName() + "', " +
						"color: '" + topPayer.getColor() + "', " +
						"amount: " + topPayer.getSum() + ", " +
						"children: [";
			writableEng += "{name: '" + topPayer.getEnglishName() + "', " +
							"label: '" + topPayer.getEngShortName() + "', " +
							"color: '" + topPayer.getColor() + "', " +
							"amount: " + topPayer.getSum() + ", " +
							"children: [";
			Iterator payeesIt = payeesList.iterator();
			while(payeesIt.hasNext()){
				PaymentAgent payee = (PaymentAgent) payeesIt.next();
				writable += "{name: '" + payee.getPaymentAgentName() + "', " +
							"label: '" + payee.getGreekShortName() + "', " +
							"color: '" + payee.getColor() + "', " +
							"amount: " + payee.getSum() + "},\n";
				writableEng += "{name: '" + payee.getEnglishName() + "', " +
								"label: '" + payee.getEngShortName() + "', " +
								"color: '" + payee.getColor() + "', " +
								"amount: " + payee.getSum() + "},\n";
			}
			writable = writable.substring(0, writable.length()-2) + "]},\n";	
			writableEng = writableEng.substring(0, writableEng.length()-2) + "]},\n";
		}
		writable = writable.substring(0, writable.length()-2) + "]}";
		writableEng = writableEng.substring(0, writableEng.length()-2) + "]}";
		
		
		return writable + "\n" + writableEng;
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
