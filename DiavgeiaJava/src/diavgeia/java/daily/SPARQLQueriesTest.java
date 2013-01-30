package diavgeia.java.daily;

public class SPARQLQueriesTest {

	public static final String prefixes = "PREFIX psgr:<http://publicspending.medialab.ntua.gr/ontology#> " +
			   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	

	public static final String ruleSet = "DEFINE input:inference <" + DailyRoutineHandler.ruleSet + "> ";

	
	public static String topPaymentsDescription(String dateString, String selector, String timeframe){
		
		
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			str1 +=		  "SELECT DISTINCT ?topPayment ?subject (xsd:decimal(?am) as ?amount) ?am " + 
						  "FROM <" + DailyRoutineHandler.graphName + "> " + 						  
						  "FROM <" + DailyRoutineHandler.cpvGraphName + "> " +
						  "WHERE { " + 
						  "?topPayment rdf:type psgr:Payment . " + 						  
						  "?topPayment psgr:paymentAmount ?am . " + 
						  "?topPayment psgr:cpv ?cpv . " + 
						  "?topPayment psgr:isReferencedBy ?decision ." + selector + dateString + "\" . " + 				  					  
						  "?cpv psgr:cpvGreekSubject ?subject . } ORDER BY DESC(?amount) LIMIT 10"; 
					  
		else
			str1 +=	  "SELECT DISTINCT ?topPayment ?subject (xsd:decimal(?am) as ?amount) ?am " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 					  
					  "FROM <" + DailyRoutineHandler.cpvGraphName + "> " +
					  "WHERE { " + 
					  "?topPayment rdf:type psgr:Payment . " + 					  
					  "?topPayment psgr:paymentAmount ?am . " + 
					  "?topPayment psgr:cpv ?cpv . " + 					   				  					 
					  "?cpv psgr:cpvGreekSubject ?subject . } ORDER BY DESC(?amount) LIMIT 10";
		return str1;					  
	}
	
	public static String topCpvDescription(String dateString, String selector, String timeframe){
		
		//The update must include a language selector...
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			str1 +=   "SELECT DISTINCT ?cpvCode ?cpvSubject (xsd:string(sum(xsd:decimal(?am))) as ?sumString) (xsd:string(count(distinct ?payment)) as ?count) " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " +					  
					  "FROM <" + DailyRoutineHandler.cpvGraphName + "> " + 
					  "WHERE { " + 					 
					  "?cpv psgr:cpvCode ?cpvCode . " +
					  "?cpv psgr:cpvGreekSubject ?cpvSubject ." +
					  "?payment psgr:paymentAmount ?am . " + 
					  "?payment psgr:cpv ?cpv . " + 
					  "?payment psgr:isReferencedBy ?decision ." + selector + dateString + "\" . " + 
					  " } ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 10";
		else
			str1 +=	  "SELECT DISTINCT ?cpvCode ?cpvSubject (xsd:string(sum(xsd:decimal(?am))) as ?sumString) (xsd:string(count(distinct ?payment)) as ?count) " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " +					  
					  "FROM <" + DailyRoutineHandler.cpvGraphName + "> " + 
					  "WHERE { " + 					 
					  "?cpv psgr:cpvCode ?cpvCode . " +
					  "?cpv psgr:cpvGreekSubject ?cpvSubject ." +
					  "?payment psgr:paymentAmount ?am . " + 
					  "?payment psgr:cpv ?cpv . " + 					  
					  " } ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 10";
					  
		return str1;
					  
	}
	
	public static String topPayersDescription(String dateString, String selector, String timeframe){
		
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			
			str1 +=   "SELECT DISTINCT ?payer (sum(xsd:decimal(?am)) as ?sumString) " +
					  " (count(distinct ?payment) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 					  
					  "WHERE { " + 					
					  "?payment psgr:isReferencedBy ?decision ." + selector + dateString + "\" ." +
					  "?payment psgr:payer ?payer ." +					  
					  "?payment psgr:paymentAmount ?am ." +
					  //"OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  //"?paymentAgent psgr:gsisName ?gsisName . }" + 
					  //"?payer psgr:paymentAgentName ?pName . }" +
					  " } ORDER BY DESC(?sumString) LIMIT 10"; 		
		else
			str1 +=   "SELECT DISTINCT ?payer (sum(xsd:decimal(?am)) as ?sumString) " +
					  " (count(?payment) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 					  
					  "WHERE { " + 					  
					  "?payment psgr:payer ?payer ." +					  
					  "?payment psgr:paymentAmount ?am ." +
					  //"OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  //"?paymentAgent psgr:gsisName ?gsisName . }" +
					  "} ORDER BY DESC(?sumString) LIMIT 10 "; 					  					  
		
		return str1;
	}
	
	public static String topPayeesDescription(String dateString, String selector, String timeframe){
		
		String str1 = ruleSet + prefixes;
		if(!timeframe.equals("overall"))
			
			str1 +=	  "SELECT DISTINCT ?afm ?payee ?gsisName (xsd:string(sum(xsd:decimal(?am))) as ?sumString) " +
					  " (xsd:string((count(distinct ?payment))) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 					  
					  "WHERE { " + 					  
					  "?payment psgr:payee ?payee ." + 
					  "?payment psgr:paymentAmount ?am ." +
					  "?payee psgr:afm ?afm . " +
					  "OPTIONAL{?paymentAgent owl:sameAs ?payee ." +
					  "?paymentAgent psgr:gsisName ?gsisName . }" + 					  
					  "?payment psgr:isReferencedBy ?decision ." + selector + dateString + "\" . } ORDER BY DESC((sum(xsd:decimal(?am)))) LIMIT 10 "; 					  
			
		else
			str1 +=	  "SELECT DISTINCT ?afm ?payee ?gsisName (xsd:string(sum(xsd:decimal(?am))) as ?sumString) " +
					  " (xsd:string((count(distinct ?payment))) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 					  
					  "WHERE { " + 
					  "?timeframe psgr:topPayee ?payee ." + 
					  "?payment psgr:payee ?payee ." + 
					  "?payment psgr:paymentAmount ?am ." +
					  "?payee psgr:afm ?afm . " +
					  "OPTIONAL{?paymentAgent owl:sameAs ?payee ." +
					  "?paymentAgent psgr:gsisName ?gsisName . }} ORDER BY DESC((sum(xsd:decimal(?am)))) LIMIT 10 ";  					  
		return str1;
	}
	
	
	public static String aggregateAmountTimeplotDescription(String timeframeGraph){
		
		String timeframe = "";
		if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
			timeframe = "d:Day .";
		else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
			timeframe = "d:Week .";
		else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
			timeframe = "d:Month .";
		else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
			timeframe = "d:Year .";
		
		String str1 = ruleSet + prefixes +
					  "SELECT distinct (xsd:string(xsd:decimal(?am)) as ?aggregateString) ?date " + 
					  "FROM <" + timeframeGraph + "> " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  "WHERE { " +
					  "?timeframe rdf:type " + timeframe +
					  "?timeframe psgr:date ?date . " +
					  "?timeframe psgr:aggregatePaymentAmount ?am . " +
					  "} ";
		
		return str1;
	}
		
	
}
