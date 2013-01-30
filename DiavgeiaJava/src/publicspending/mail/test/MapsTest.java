package publicspending.mail.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class MapsTest {
	
	public MapsTest(){
		
		
		
		//System.out.println(format(9222));
		int count=0;
		for(int i=15000;i<20000;i++){
		try {
			String request = "http://maps.google.com/maps/api/geocode/json?components=postal_code:"+i+"|country:GR&sensor=false";
			String response;
			response = httpGet(request);
			String jsonTxt = response;
			//System.out.println(request);
			/*JSONObject model = null;	
			JSONObject json = (JSONObject) JSONSerializer.toJSON( jsonTxt );
			model = json.getJSONObject("results");*/			
			try{int index1 = jsonTxt.indexOf("\"formatted_address\" : ");
			int index2 = jsonTxt.indexOf("geometry\" : ");			
			System.out.println(jsonTxt.substring(index1, index2-11));}catch(Exception e){count++;}			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		}
		System.out.println(count);
		//System.out.println(response);
	    
	}
	
	public static String httpGet(String urlStr) throws IOException {
		  URL url = new URL(urlStr);
		  HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }

		  // Buffer the result into a string
		  BufferedReader rd = new BufferedReader(
		      new InputStreamReader(conn.getInputStream()));
		  StringBuilder sb = new StringBuilder();
		  String line;
		  while ((line = rd.readLine()) != null) {
		    sb.append(line);
		  }
		  rd.close();

		  conn.disconnect();
		  return sb.toString();
		}

	public String format(Integer i){
		String ret = Integer.toString(i);
		System.out.println(i%10000);
		if(i<10){			
			ret = "0000" + ret;				
		}
		else if(i>=10 && i<100){			
			ret = "000" + ret;				
		}
		else if(i>=100 && i<1000){			
			ret = "00" + ret;				
		}
		else if(i>=1000 && i<100000){
			ret = "0" + ret;				
		}
		return ret;
	}
}
