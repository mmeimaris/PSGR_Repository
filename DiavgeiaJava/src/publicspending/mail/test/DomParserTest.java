package publicspending.mail.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import publicspending.java.control.DOMHelper;

public class DomParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
			DOMHelper dmh = new DOMHelper();
			//File fXmlFile = new File("c:/Users/Marios/Desktop/Diavgeia-Root/decisions.xml");
			try {
				String request = "http://opendata.diavgeia.gov.gr/api/decisions?datefrom=10-12-2012&dateTo=14-12-2012&type=27&count=500&from=1";
				System.out.println(request);
				String response = httpGet(request);
				Document doc = parseXMLResponse(response);
			 
			    // Get the document's root XML node
			    NodeList root = doc.getChildNodes();
			    Node rootNode = dmh.getNode("ns2:findDecisionsResponse", root);
			    NodeList children = rootNode.getChildNodes();
			    Node decisions = dmh.getNode("ns2:decisions", children);
			    Node queryInfo = dmh.getNode("queryInfo", children);
			    NodeList queryInfoNodes = queryInfo.getChildNodes();
			    String count = dmh.getNodeValue("count", queryInfoNodes);
			    String from = dmh.getNodeValue("from", queryInfoNodes);
			    String order = dmh.getNodeValue("order", queryInfoNodes);
			    String total = dmh.getNodeValue("total", queryInfoNodes);
			    System.out.println("-----------------------");
			    System.out.println("Clarity API call info: ");
			    System.out.println("Count: " + count);
			    System.out.println("From: " + from);
			    System.out.println("Order: " + order);
			    System.out.println("Total: " + total);
			    System.out.println("-----------------------");
			   
			    /*Node comp = dmh.getNode("Company", root);
			    Node exec = dmh.getNode("Executive", comp.getChildNodes() );
			    String execType = dmh.getNodeAttr("type", exec);
			 
			    // Load the executive's data from the XML
			    NodeList nodes = exec.getChildNodes();
			    String lastName = dmh.getNodeValue("LastName", nodes);
			    String firstName = dmh.getNodeValue("FirstName", nodes);
			    String street = dmh.getNodeValue("street", nodes);
			    String city = dmh.getNodeValue("city", nodes);
			    String state = dmh.getNodeValue("state", nodes);
			    String zip = dmh.getNodeValue("zip", nodes);
			 
			    System.out.println("Executive Information:");
			    System.out.println("Type: " + execType);
			    System.out.println(lastName + ", " + firstName);
			    System.out.println(street);
			    System.out.println(city + ", " + state + " " + zip);*/
			}
			catch ( Exception e ) {
			    e.printStackTrace();
			}

	}
	
	public static Document parseXMLResponse(String response) throws Exception {
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(new ByteArrayInputStream(response.getBytes("utf-8"))));
	    Document doc = parser.getDocument();
	    return doc;
	}
	
	public static String httpGet(String urlStr) throws IOException {
		  URL url = new URL(urlStr);
		  HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		  if (conn.getResponseCode() != 200) {
		    throw new IOException(conn.getResponseMessage());
		  }
		  	/*int errorCounter = 0;
			  while(conn.getResponseCode() !=200){
				  if(errorCounter==0){
					  System.out.println("Getting error from remote server... Trying again.");
					  errorCounter++;
				  }			  
			  }*/
		  

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
	
}
