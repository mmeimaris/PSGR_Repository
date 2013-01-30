package diavgeia.java.daily;

import diavgeia.java.ontology.Ontology;

public class SPARQLQueries {
	
	public static final String prefixes = "PREFIX psgr:<http://publicspending.medialab.ntua.gr/ontology#> " +
								   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
								   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
								   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
								   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";	
	
	//public static final String ruleSet = "DEFINE input:inference <" + DailyRoutineHandler.ruleSet + "> ";
	public static final String ruleSet = "";
				
	
	public static final String dayQuery = "PREFIX psgr:<http://publicspending.medialab.ntua.gr/ontology#> " +
	  		  							  "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
	  		  							  "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
	  		  							  "SELECT DISTINCT (xsd:date(?date) as ?dt) " +
	  		  							  "WHERE{ " +
	  		  							  "?payment rdf:type psgr:Payment . " +
	  		  							  "?decision psgr:refersTo ?payment . " +
	  		  							  "?decision psgr:date ?date . " +
	  		  							  "FILTER(?date != \"Null\")} ";
	
	public static final String dayQueryUniversal = ruleSet + prefixes +
				  								   "SELECT DISTINCT (xsd:date(?date) as ?dt) FROM <"+DailyRoutineHandler.graphName+"> " +
				  								   "WHERE{ " +
				  								   "?payment rdf:type psgr:Payment . " +
				  								   "?decision psgr:refersTo ?payment . " +
				  								   "?decision psgr:date ?date . " +
				  								   "FILTER(?date != \"Null\")} ";
	
	public static final String dayQueryUniversal2 = prefixes + "SELECT DISTINCT ?day FROM <" + DailyRoutineHandler.graphName+"> WHERE {" +
				  								   "?d a psgr:Day . ?d psgr:date ?day .}";
			
	
	public static final String payerAggQuery  =  prefixes +
												"DELETE FROM <" + DailyRoutineHandler.graphName + "> " +
												"{?payer psgr:aggregatePaymentAmount ?x} " + 
												"WHERE { " + 
												"?payer psgr:aggregatePaymentAmount ?x . ?payer rdf:type psgr:Payer . }" +
												"INSERT INTO <" + DailyRoutineHandler.graphName + "> " +
												"{?payer psgr:aggregatePaymentAmount ?total } " +
												"WHERE { { " +
												"SELECT DISTINCT ?payer (sum(xsd:decimal(?x)) as ?total) FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
												"?payer rdf:type psgr:Payer ." +
												"?payment rdf:type psgr:Payment . " +
												"?payment psgr:paymentAmount  ?x. " +
												"?payment psgr:payer ?payer . } ORDER BY DESC(?total) LIMIT 1 }}" ;
	
	public static final String payerAggTest1 = ruleSet + prefixes +
											   "DELETE FROM <" + DailyRoutineHandler.graphName + "> " +
											   "{?payer psgr:aggregatePaymentAmount ?x} " + 
											   "WHERE { " + 
											   "GRAPH ?g2 {?payer psgr:aggregatePaymentAmount ?x . ?payer rdf:type psgr:Payer . }}";
	
	public static final String payerAggTest2 = ruleSet + prefixes +
											   "INSERT INTO <" + DailyRoutineHandler.graphName + "> " +
											   "{?payer psgr:aggregatePaymentAmount ?total } " +
											   "WHERE { { " +
											   "SELECT DISTINCT ?payer (sum(xsd:decimal(?x)) as ?total) FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
											   "?payer rdf:type psgr:Payer ." +
											   //"?payment rdf:type psgr:Payment . " +
											   "?payment psgr:paymentAmount  ?x. " +
											   "?payment psgr:payer ?payer . }}}" ;

	public static final String payeeAggTest1 = ruleSet + prefixes +
			   "DELETE FROM <" + DailyRoutineHandler.graphName + "> " +
			   "{?payee psgr:aggregatePaymentAmount ?x} " + 
			   "WHERE { " + 
			   "GRAPH ?g3 {?payee psgr:aggregatePaymentAmount ?x . ?payee rdf:type psgr:Payee . }}";

	public static final String payeeAggTest2 = ruleSet + prefixes +
			   "INSERT INTO <" + DailyRoutineHandler.graphName + "> " +
			   "{?payee psgr:aggregatePaymentAmount ?total } " +
			   "WHERE { { " +
			   "SELECT DISTINCT ?payee (sum(xsd:decimal(?x)) as ?total) FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
			   "?payee rdf:type psgr:Payee ." +
			   //"?payment rdf:type psgr:Payment . " +
			   "?payment psgr:paymentAmount  ?x. " +
			   "?payment psgr:payee ?payee . }}}" ;

	
	
	public static final String payeeAggQuery  =  prefixes +
												 "DELETE FROM <" + DailyRoutineHandler.graphName + "> " +
												 "{?payee psgr:aggregatePaymentAmount ?x} " + 
												 "WHERE { " + 
												 "?payee psgr:aggregatePaymentAmount ?x . ?payee rdf:type psgr:Payee . }" +
												 "INSERT INTO <" + DailyRoutineHandler.graphName + "> " +
												 "{?payee psgr:aggregatePaymentAmount ?total } " +
												 "WHERE { { " +
												 "SELECT DISTINCT ?payee (sum(xsd:decimal(?x)) as ?total) FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
												 "?payee rdf:type psgr:Payee ." +
												 "?payment rdf:type psgr:Payment . " +
												 "?payment psgr:paymentAmount  ?x. " +
												 "?payment psgr:payee ?payee . } ORDER BY DESC(?total) LIMIT 1 }}" ;
			

	public static final String daySelector = "?decision psgr:day ?timeframe . " +
											 "?timeframe psgr:date \"";
	
	public static final String daySelector2 = "?decision psgr:day ?timeframe . " +
			 								 "?timeframe psgr:date ?dt .";
	
	public static final String weekSelector = "?decision psgr:week ?timeframe . " +
											  "?timeframe psgr:date \"";
	public static final String weekSelector2 = "?decision psgr:week ?timeframe . " +
			  								   "?timeframe psgr:date ?dt .";

	public static final String monthSelector = "?decision psgr:month ?timeframe . " +
			 								   "?timeframe psgr:date \"";
	public static final String monthSelector2 = "?decision psgr:month ?timeframe . " +
			   							  	    "?timeframe psgr:date ?dt .";

	public static final String yearSelector = "?decision psgr:year ?timeframe . " +
			  								  "?timeframe psgr:date \"";
	public static final String yearSelector2 = "?decision psgr:year ?timeframe . " +
			  							      "?timeframe psgr:date ?dt .";
	

	
	
	/*public static String totalPaymentAmountForDecisionDates(String timeframeGraph, String dateString, String selector){
	
		String str1 = ruleSet + prefixes + 
				   "DELETE FROM <" + timeframeGraph + "> " +
				   "{?tf psgr:aggregatePaymentAmount ?ta } " +
				   "WHERE {?tf psgr:aggregatePaymentAmount ?ta . ?tf psgr:date \"" + dateString + "\" } " + 
				   "INSERT INTO <" + timeframeGraph + "> " +
		   		   "{?timeframe psgr:aggregatePaymentAmount ?totalAmount } " + 
		   		   "WHERE { { " + 
		   		   "SELECT ?timeframe (sum(xsd:decimal(?am)) as ?totalAmount) " + 
		   		   "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
		   		   "?payment rdf:type psgr:Payment ." + 
		   		   "?payment psgr:isReferencedBy ?decision ." + 
		   		   "?payment psgr:paymentAmount ?am . " + selector + dateString + "\" .}}}";
		return str1;	
	}*/
	
	public static String totalPaymentAmountForDecisionDates2(String timeframeGraph, String selector){
		
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
				   "DELETE FROM <" + timeframeGraph + "> " +					
				   "{?tf psgr:aggregatePaymentAmount ?ta } " +
				   "WHERE {GRAPH ?g {?tf psgr:aggregatePaymentAmount ?ta . ?tf rdf:type " + timeframe +  
				   " }} " + 
				   "INSERT INTO <" + timeframeGraph + "> " +		   		   
				   "{?timeframe psgr:aggregatePaymentAmount ?totalAmount } " + 
		   		   "WHERE { { " + 
		   		   "SELECT distinct ?timeframe (sum(xsd:decimal(?am)) as ?totalAmount) " + 
		   		   "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
		   		   "?payment rdf:type psgr:Payment ." + 
		   		   "?payment psgr:isReferencedBy ?decision ." + 
		   		   "?payment psgr:paymentAmount ?am . " + selector + " }}}";
		return str1;	
	}
	
	
	public static String totalTest1(String timeframeGraph, String selector){
		
		
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
				   "DELETE FROM <" + timeframeGraph + "> " +					
				   "{?tf psgr:aggregatePaymentAmount ?ta } " +
				   "WHERE {GRAPH ?g {?tf psgr:aggregatePaymentAmount ?ta . ?tf rdf:type " + timeframe + " }}" ;
				   
		return str1;	
	}
	
	public static String totalTest2(String timeframeGraph, String selector){
	
		String str1 = ruleSet + prefixes +
				   "INSERT INTO <" + timeframeGraph + "> " +		   		   
				   "{?timeframe psgr:aggregatePaymentAmount ?totalAmount } " + 
		   		   "WHERE { { " + 
		   		   "SELECT distinct ?timeframe (sum(xsd:decimal(?am)) as ?totalAmount) " + 
		   		   //"FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
		   		   //"FROM <diavgeia.AllDecisions> WHERE {" +
		   		   "FROM <" + DailyRoutineHandler.graphName + "> FROM <" + DailyRoutineHandler.falseGraphName + "> WHERE {" +
		   		   "?payment rdf:type psgr:Payment ." + 
		   		   "?decision psgr:refersTo ?payment ." + 
		   		   "?payment psgr:paymentAmount ?am . " + selector + " }}}";
		return str1;
	
	}
	
	
	
	

	/*public static String totalPaymentAmountOverall(){
		
		String str1 = ruleSet + prefixes + 
				   "DELETE FROM <" + DailyRoutineHandler.overallGraphName + "> " +
				   "{" + DailyRoutineHandler.overallResource +" psgr:aggregatePaymentAmount ?ta } " +
				   "WHERE {" + DailyRoutineHandler.overallResource + " psgr:aggregatePaymentAmount ?ta} " + 
				   "INSERT INTO <" + DailyRoutineHandler.overallGraphName + "> " +
		   		   "{"+ DailyRoutineHandler.overallResource +" psgr:aggregatePaymentAmount ?totalAmount } " + 
		   		   "WHERE { { " + 
		   		   "SELECT (sum(xsd:decimal(?am)) as ?totalAmount) " + 
		   		   "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
		   		   "?payment rdf:type psgr:Payment ." +	   		   
		   		   "?payment psgr:paymentAmount ?am . } ORDER BY DESC(?totalAmount) LIMIT 1}}";
		
		return str1;}*/
	
	
	
	public static String totalOverallTest1(){
					
		String str1 = ruleSet + prefixes + 					
				   "DELETE FROM <" + DailyRoutineHandler.overallGraphName + "> " +					
				   "{" + DailyRoutineHandler.overallResource + " psgr:aggregatePaymentAmount ?ta } " +
				   "WHERE {GRAPH ?g {" + DailyRoutineHandler.overallResource + " psgr:aggregatePaymentAmount ?ta . }}" ;				   
		return str1;	
	}
	
	public static String totalOverallTest2(){
	
		String str1 = ruleSet + prefixes +
				   "INSERT INTO <" + DailyRoutineHandler.overallGraphName + "> " +		   		   
				   "{" + DailyRoutineHandler.overallResource + " psgr:aggregatePaymentAmount ?totalAmount } " + 
				   "WHERE { { " + 
		   		   "SELECT (sum(xsd:decimal(?am)) as ?totalAmount) " + 
		   		   "FROM <" + DailyRoutineHandler.graphName + "> FROM <" + DailyRoutineHandler.falseGraphName + "> WHERE {" +
		   		   "?payment rdf:type psgr:Payment ." +	   		   
		   		   "?payment psgr:paymentAmount ?am . } ORDER BY DESC(?totalAmount) LIMIT 1}}";
		return str1;
	
	}
	

	public static String topPaymentsForDecisionDates(String timeframeGraph, String dateString, String selector){
	
		String str1 = ruleSet + prefixes +
					  "DELETE FROM <" + timeframeGraph + "> " +	
					  "{?tf psgr:topPayment ?payment }" + 
					  "WHERE {?tf psgr:topPayment ?payment . ?tf psgr:date \"" + dateString + "\" }" +  
					  "INSERT INTO <" + timeframeGraph + "> " +
					  "{?timeframe psgr:topPayment ?payment} " + 
					  "WHERE { {" +
					  "SELECT DISTINCT ?timeframe ?payment (xsd:decimal(?am) as ?amount) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
					  "?payment rdf:type psgr:Payment . " +
					  "?payment psgr:paymentAmount ?am . " +
					  "?payment psgr:isReferencedBy ?decision . " + selector + dateString + "\" .} ORDER BY DESC(?amount) LIMIT 10 }}" ;
					  
		return str1;
	}
	
	/*public static String topPaymentsDates(String timeframeGraph, String dateString, String selector){
		
		String str1 = ruleSet + prefixes +					    
					  "INSERT INTO <" + timeframeGraph + "> " +
					  "{?timeframe psgr:topPayment ?payment} " + 
					  "WHERE { {" +
					  "SELECT DISTINCT ?timeframe ?payment (xsd:decimal(?am) as ?amount) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
					  "?payment rdf:type psgr:Payment . " +
					  "?payment psgr:paymentAmount ?am . " +
					  "?payment psgr:isReferencedBy ?decision . " + selector + dateString + "\" .} ORDER BY DESC(?amount) LIMIT 10 }}" ;
					  
		return str1;
	}		

	
	public static String topPaymentsOverall(){
		
		String str1 = ruleSet + prefixes +
					  "DELETE FROM <" + DailyRoutineHandler.overallGraphName + "> " +	
					  "{" + DailyRoutineHandler.overallResource + " psgr:topPayment ?payment }" + 
					  "WHERE {" + DailyRoutineHandler.overallResource +  " psgr:topPayment ?payment }" +  
					  "INSERT INTO <" + DailyRoutineHandler.overallGraphName + "> " +
					  "{" + DailyRoutineHandler.overallResource + " psgr:topPayment ?payment} " + 
					  "WHERE { {" +
					  "SELECT distinct ?payment (xsd:decimal(?am) as ?amount) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
					  "?payment rdf:type psgr:Payment . " +
					  "?payment psgr:paymentAmount ?am . " +
					  "} ORDER BY DESC(?amount) LIMIT 10 }}" ;
					  
		return str1;
	}

	public static String topPayersForDecisionDates(String timeframeGraph, String dateString, String selector){
		
		String str1 = ruleSet + prefixes +
					  "DELETE FROM <" + timeframeGraph + "> " +	
					  "{?tf psgr:topPayer ?payer . ?payer psgr:isTopPayerOf ?tf .}" + 
					  "WHERE {?tf psgr:topPayer ?payer . ?tf psgr:date \"" + dateString + "\" }" +  
					  "INSERT INTO <" + timeframeGraph + "> " +
					  "{?timeframe psgr:topPayer ?payer ." +
					  "?payer psgr:isTopPayerOf ?timeframe .}" + 
					  "WHERE { {" +
					  "SELECT DISTINCT ?payer ?timeframe (sum(xsd:decimal(?am)) as ?sum)" +
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
					  "?payment rdf:type psgr:Payment . " +
					  "?payment psgr:paymentAmount ?am . " +
					  "?payment psgr:payer ?payer ." +
					  "?payment psgr:isReferencedBy ?decision . " + selector + dateString + "\" .} ORDER BY DESC(?sum) LIMIT 10 }}" ;
					  
		return str1;
	}
	
	public static String topPayersOverall(){
		
		String str1 = ruleSet + prefixes +
					  "DELETE FROM <" + DailyRoutineHandler.overallGraphName + "> " +	
					  "{"+ DailyRoutineHandler.overallResource +" psgr:topPayer ?payer . ?payer psgr:isTopPayerOf " + DailyRoutineHandler.overallResource + ".}" + 
					  "WHERE {"+DailyRoutineHandler.overallResource+" psgr:topPayer ?payer . }" +  
					  "INSERT INTO <" + DailyRoutineHandler.overallGraphName + "> " +
					  "{"+DailyRoutineHandler.overallResource+" psgr:topPayer ?payer ." +
					  "?payer psgr:isTopPayerOf "+DailyRoutineHandler.overallResource+  " .}" + 
					  "WHERE { {" +
					  "SELECT distinct ?payer (sum(xsd:decimal(?am)) as ?sum)" +
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
					  "?payment rdf:type psgr:Payment . " +
					  "?payment psgr:paymentAmount ?am . " +
					  "?payment psgr:payer ?payer ." +
					  "} ORDER BY DESC(?sum) LIMIT 10 }}" ;
					  
		return str1;
	}

public static String topPayeesForDecisionDates(String timeframeGraph, String dateString, String selector){
	
	String str1 = ruleSet + prefixes +
				  "DELETE FROM <" + timeframeGraph + "> " +	
				  "{?tf psgr:topPayee ?payee . ?tf psgr:isTopPayeeOf ?payee .}" + 
				  "WHERE {?tf psgr:topPayee ?payee . ?tf psgr:date \"" + dateString + "\" }" +  
				  "INSERT INTO <" + timeframeGraph + "> " +
				  "{?timeframe psgr:topPayee ?payee . " +
				  "?payee psgr:isTopPayeeOf ?timeframe .}" +
				  "WHERE { {" +
				  "SELECT DISTINCT ?payee ?timeframe (sum(xsd:decimal(?am)) as ?sum)" +
				  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
				  "?payment rdf:type psgr:Payment . " +
				  "?payment psgr:paymentAmount ?am . " +
				  "?payment psgr:payee ?payee ." +
				  "?payment psgr:isReferencedBy ?decision . " + selector + dateString + "\" .} ORDER BY DESC(?sum) LIMIT 10 }}" ;
				  
	return str1;
}

	public static String topPayeesOverall(){
	
		String str1 = ruleSet + prefixes +
					  "DELETE FROM <" + DailyRoutineHandler.overallGraphName + "> " +	
					  "{"+ DailyRoutineHandler.overallResource +" psgr:topPayee ?payee . ?payee psgr:isTopPayeeOf " + DailyRoutineHandler.overallResource + ".}" + 
					  "WHERE {"+DailyRoutineHandler.overallResource+" psgr:topPayee ?payee . }" +  
					  "INSERT INTO <" + DailyRoutineHandler.overallGraphName + "> " +
					  "{"+DailyRoutineHandler.overallResource+" psgr:topPayee ?payee ." +
					  "?payee psgr:isTopPayeeOf "+DailyRoutineHandler.overallResource+  " .}" + 
					  "WHERE { {" +
					  "SELECT distinct ?payee (sum(xsd:decimal(?am)) as ?sum)" +
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
					  "?payment rdf:type psgr:Payment . " +
					  "?payment psgr:paymentAmount ?am . " +
					  "?payment psgr:payee ?payee ." +
					  "} ORDER BY DESC(?sum) LIMIT 10 }}" ;
					  
		return str1;
	}

	public static String topCPVForDecisionDates(String timeframeGraph, String dateString, String selector){
		
		String str1 = ruleSet + prefixes +
					  "DELETE FROM <" + timeframeGraph + "> " +
					  "{?tf psgr:topCpv ?c . ?c psgr:isTopCpvOf ?tf .} " + 
					  "WHERE {?tf psgr:topCpv ?c . ?tf psgr:date \"" + dateString + "\" } " +
					  "INSERT INTO <" + timeframeGraph + "> "+
					  "{?timeframe psgr:topCpv ?cpv . " +
					  "?cpv psgr:isTopCpvOf ?timeframe .}" + 				  
					  "WHERE { {" +
					  "SELECT DISTINCT ?cpv ?timeframe (sum(xsd:decimal(?am)) as ?sum) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
					  "?payment rdf:type psgr:Payment . " +
					  "?payment psgr:cpv ?cpv . " +
					  "?payment psgr:paymentAmount ?am ." + 
					  "?payment psgr:isReferencedBy ?decision . " + selector + dateString + "\" . } ORDER BY DESC(?sum) LIMIT 10 }}" ;
		
		return str1;
	}
	
	public static String topCPVOverall(){
		
		String str1 = ruleSet + prefixes +
					  "DELETE FROM <" +DailyRoutineHandler.overallGraphName + "> " +
					  "{"+DailyRoutineHandler.overallResource+ " psgr:topCpv ?c . ?c psgr:isTopCpvOf "+ DailyRoutineHandler.overallResource + " .} " + 
					  "WHERE {"+DailyRoutineHandler.overallResource+ " psgr:topCpv ?c .} " +
					  "INSERT INTO <" + DailyRoutineHandler.overallGraphName + "> "+
					  "{"+DailyRoutineHandler.overallResource+ " psgr:topCpv ?cpv . " +
					  "?cpv psgr:isTopCpvOf "+DailyRoutineHandler.overallResource+" .}" + 				  
					  "WHERE { {" +
					  "SELECT DISTINCT ?cpv (sum(xsd:decimal(?am)) as ?sum) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE {" +
					  "?payment rdf:type psgr:Payment . " +
					  "?payment psgr:cpv ?cpv . " +
					  "?payment psgr:paymentAmount ?am ." + 
					  " } ORDER BY DESC(?sum) LIMIT 10}}" ;
		
		return str1;
	}*/

	
	
	
	//Description methods start here
	
public static String topPaymentsDescription(String dateString, String selector, String timeframe){
		
		
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			str1 +=		  "SELECT DISTINCT ?topPayment (xsd:decimal(?am) as ?amount)" + 
						  "FROM <" + DailyRoutineHandler.graphName + "> " + 						  
						  //"FROM <" + DailyRoutineHandler.cpvGraphName + "> " +
						  "WHERE { " + 
						  "?decision psgr:refersTo ?topPayment ." + selector + dateString + "\" . " + 		
						  //"?topPayment rdf:type psgr:Payment . " + 						  
						  "?topPayment psgr:paymentAmount ?am . " + 
						  "?topPayment psgr:validCpv \"true\" ." + 
						  //"?topPayment psgr:cpv ?cpv . " + 						  		  					  
						  //"?cpv psgr:cpvGreekSubject ?subject . " +
						  "} ORDER BY DESC(?amount) LIMIT 10"; 
					  
		else
			str1 +=	  "SELECT DISTINCT ?topPayment (xsd:decimal(?am) as ?amount)" + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 					  
					  //"FROM <" + DailyRoutineHandler.cpvGraphName + "> " +
					  "WHERE { " + 
					  //"?topPayment rdf:type psgr:Payment . " + 					  
					  "?topPayment psgr:paymentAmount ?am . " +
					  "?topPayment psgr:validCpv \"true\" ." + 
					  //"?topPayment psgr:cpv ?cpv . " + 					   				  					 
					  //"?cpv psgr:cpvGreekSubject ?subject . " +
					  "} ORDER BY DESC(?amount) LIMIT 10";
		return str1;					  
	}
	
	public static String topCpvDescription(String dateString, String selector, String timeframe, String limit){ //works for CPVDivision
		
		//The update must include a language selector...
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			str1 +=   "SELECT DISTINCT ?cpvDiv (sum(xsd:decimal(?am)) as ?sumString) (count(?payment) as ?count) " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  "FROM <" + DailyRoutineHandler.falseGraphName + "> " +
					  		//"FROM <" + DailyRoutineHandler.cpvGraphName +">" +					  
					  //"FROM <" + DailyRoutineHandler.cpvGraphName + "> " + 
					  "WHERE { " + 				
					  "?decision psgr:refersTo ?payment." + selector + dateString + "\" . " +					  
					  "?payment psgr:paymentAmount ?am . " + 
					  "?payment psgr:cpv ?cpv . " + 			
					  "?cpv cpv:hasSuperCPVCode ?cpvDiv ." +					  
					  "?cpvDiv a cpv:CPVDivision ." +
					  //"?cpv psgr:cpvCode ?cpvCode . " +
					  //"?cpv psgr:cpvGreekSubject ?cpvSubject ." +
					  " } ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT " + limit;
		else
			str1 +=	  "SELECT DISTINCT ?cpvDiv (sum(xsd:decimal(?am)) as ?sumString) (count(distinct ?payment) as ?count) " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  "FROM <" + DailyRoutineHandler.falseGraphName + "> " +
					  		//"FROM <" + DailyRoutineHandler.cpvGraphName +">" +					  
					  //"FROM <" + DailyRoutineHandler.cpvGraphName + "> " + 
					  "WHERE { " + 					 					  
					  "?payment psgr:paymentAmount ?am . " + 
					  "?payment psgr:cpv ?cpv . " + 	
					  "?cpv cpv:hasSuperCPVCode ?cpvDiv ." +
					  "?cpvDiv a cpv:CPVDivision ." +
					  //"?cpv psgr:cpvCode ?cpvCode . " +
					  //"?cpv psgr:cpvGreekSubject ?cpvSubject ." +
					  " } ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT " + limit ;
					  
		return str1;
					  
	}
	
	public static String cpvDivisionQuery(){
		String str1 = prefixes + 
					  "SELECT ?cpvDiv ?cpvSub ?cpvEngSub FROM <" + DailyRoutineHandler.graphName +"> WHERE {?cpvDiv a cpv:CPVDivision . ?cpvDiv psgr:cpvGreekSubject ?cpvSub. ?cpvDiv psgr:cpvEnglishSubject ?cpvEngSub.} ORDER BY ASC(?cpvDiv)";
		return str1;
	}
	
	public static String cpvQuery(String resource){
				
		String str1 = ruleSet + prefixes + 
					  "SELECT ?cpvCode ?cpvSubject ?cpvEngSubject ?shortName ?engShortName " +
					  //"FROM <" + DailyRoutineHandler.cpvGraphName +"> " +
					  		" FROM <" + DailyRoutineHandler.graphName + "> WHERE {" + 
					  "<" + resource + "> psgr:cpvCode ?cpvCode . " + 
					  "<" + resource + "> psgr:greekShortName ?shortName ." +
					  "<" + resource + "> psgr:engShortName ?engShortName ." +
					  "<" + resource + "> psgr:cpvEnglishSubject ?cpvEngSubject ." +
					  "<" + resource + "> psgr:cpvGreekSubject ?cpvSubject . }";					
		return str1;
	}
	
	public static String cpvSecondaryQuery(String resource){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?cpvCode ?cpvSubject " +
					  //"FROM <" + DailyRoutineHandler.cpvGraphName +"> " +
					  		"FROM <" +DailyRoutineHandler.graphName +"> " +
					  "WHERE {" + 
					  "<" + resource + "> psgr:cpv ?cpv . " +
					  "?cpv psgr:cpvCode ?cpvCode ." +
					  "?cpv psgr:cpvGreekSubject ?cpvSubject . }";
		return str1;
	}
	
	public static String cpvLevelQuery(String resource){ //stub - add more levels
		
		String str1 = ruleSet + prefixes + 
				  "SELECT ?cpvCode ?cpvSubject ?cpvDivision FROM <" + DailyRoutineHandler.cpvGraphName +"> WHERE {" + 				  
				  "<" + resource + "> cpv:hasSuperCPVCode ?cpvDivision ." + 
				  "?cpvDivision a cpv:CPVDivision ." +
				  "?cpvDivision psgr:cpvCode ?cpvCode . " +
				  "?cpvDivision psgr:cpvGreekSubject ?cpvSubject . }";
		return str1;
	}
	
	public static String topPayersDescription(String dateString, String selector, String timeframe, String limit){
		
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			
			str1 +=   "SELECT DISTINCT ?payer (sum(xsd:decimal(?am)) as ?sumString) (avg(xsd:decimal(?am)) as ?avgString) " +
					  " (count(distinct ?payment) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  "FROM <" + DailyRoutineHandler.falseGraphName + "> " + 
					  "WHERE { " + 					
					   selector + dateString + "\" ." +
					  "?decision psgr:refersTo ?payment  ." + 
					  "?payment psgr:payer ?payer ." +			
					  "?payer psgr:validAfm \"true\" . " +
					  "?payment psgr:paymentAmount ?am ." +
					  " } ORDER BY DESC(?sumString) LIMIT " + limit; 		
		else
			str1 +=   "SELECT DISTINCT ?payer (sum(xsd:decimal(?am)) as ?sumString) (avg(xsd:decimal(?am)) as ?avgString)" +
					  " (count(?payment) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  "FROM <" + DailyRoutineHandler.falseGraphName + "> " + 
					  "WHERE { " + 					  
					  "?payment psgr:payer ?payer ." +		
					  "?payer psgr:validAfm \"true\" . " +
					  "?payment psgr:paymentAmount ?am ." +					  
					  "} ORDER BY DESC(?sumString) LIMIT " + limit; 					  					  
		
		return str1;
	}
	
	public static String topPayersDescriptionBubbles(String dateString, String selector, String timeframe, String limit){
		
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			
			str1 +=   "SELECT DISTINCT ?payer (sum(xsd:decimal(?am)) as ?sumString) (avg(xsd:decimal(?am)) as ?avgString) " +
					  " (count(distinct ?payment) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  //"FROM <" + DailyRoutineHandler.binaryGraphName + "> " + 	
					  "WHERE { " + 					
					   selector + dateString + "\" ." +
					  "?decision psgr:refersTo ?payment  ." + 
					  "?payment psgr:payer ?payer ." +					
					  "?payer psgr:validAfm \"true\" ." + 
					  //"?bRel psgr:binaryPayer ?payer ." + 
					  "?payment psgr:paymentAmount ?am ." +
					  //"OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  //"?paymentAgent psgr:gsisName ?gsisName . }" + 
					  //"?payer psgr:paymentAgentName ?pName . }" +
					  " } ORDER BY DESC(?sumString) LIMIT " + limit; 		
		else
			str1 +=   "SELECT DISTINCT ?payer (sum(xsd:decimal(?am)) as ?sumString) (avg(xsd:decimal(?am)) as ?avgString)" +
					  " (count(?payment) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  //"FROM <" + DailyRoutineHandler.binaryGraphName + "> " +
					  "WHERE { " + 					  
					  "?payment psgr:payer ?payer ." +
					  //"?bRel psgr:binaryPayer ?payer ." + 
					  "?payer psgr:validAfm \"true\" ." +
					  "?payment psgr:paymentAmount ?am ." +
					  //"OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  //"?paymentAgent psgr:gsisName ?gsisName . }" +
					  "} ORDER BY DESC(?sumString) LIMIT " + limit; 					  					  
		
		return str1;
	}
		
	
	public static String topCpvForPayer(String payer){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?subject FROM <" + DailyRoutineHandler.graphName + "> FROM <" + DailyRoutineHandler.cpvGraphName + "> WHERE {" + 
					  "?payment psgr:payer <" + payer + "> ." +
					  "?payment psgr:paymentAmount ?am . " + 
					  "?payment psgr:cpv ?cpv . " +
					  "?cpv psgr:cpvGreekSubject ?subject .} " + 
					  "ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1";
		
		return str1;
	}
	
	public static String mostFrequentPayee(String payer){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?payee from <" + DailyRoutineHandler.graphName + "> WHERE {" + 
					  "?payment psgr:payer <" + payer + "> ." + 
					  "?payment psgr:payee ?payee . }" +
					  "ORDER BY DESC(count(distinct ?payment)) LIMIT 1";
		return str1;
					  
	}
	
	public static String biggestAmountPayee(String payer){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?payee from <" + DailyRoutineHandler.graphName + "> WHERE {" + 
					  "?payment psgr:payer <" + payer + "> ." + 
					  "?payment psgr:payee ?payee . " +
					  "?payment psgr:paymentAmount ?am .}" +
					  "ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1";
		return str1;
					  
	}
	
	public static String topCpvForPayee(String payee){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?subject FROM <" + DailyRoutineHandler.graphName + "> WHERE {" + 
					  "?payment psgr:payee <" + payee + "> ." +
					  "?payment psgr:paymentAmount ?am . " + 
					  "?payment psgr:cpv ?cpv . " +
					  "?cpv psgr:cpvGreekSubject ?subject .} " + 
					  "ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1";
		
		return str1;
	}
	
	public static String topCpvForPayerPayee(String payer, String payee){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?cpvCode ?cpvCodeDiv ?subject ?engSubject ?shortName ?engShortName " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  		//"FROM <" + DailyRoutineHandler.cpvGraphName + "> " +
					  //"FROM <" + DailyRoutineHandler.nicknameGraphName +"> " +
					  "WHERE {" + 
					  "?payment psgr:payee <" + payee + "> ." +
					  "?payment psgr:payer <" + payer + "> ." +
					  "?payment psgr:paymentAmount ?am ." + 
					  "?payment psgr:cpv ?cpv ." +
					  "OPTIONAL{?cpv cpv:hasSuperCPVCode ?cpvDiv ." + 
					  "?cpvDiv a cpv:CPVDivision ." + 
					  "?cpvDiv psgr:cpvCode ?cpvCodeDiv .}" +
					  //"?cpvDiv psgr:cpvGreekSubject ?subject ." +
					  //"?cpvDiv psgr:cpvEnglishSubject ?engSubject ." +
					  //"?cpvDiv psgr:greekShortName ?shortName ." + 
					  //"?cpvDiv psgr:engShortName ?engShortName ." +
					  
					  "?cpv psgr:cpvCode ?cpvCode ." +
					  "} ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1 ";
		return str1;
					  
	}
	
	public static String topCpvForPayeePayer(String payee, String payer){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?cpvCode ?cpvCodeDiv ?subject ?engSubject ?shortName ?engShortName " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  		//"FROM <" + DailyRoutineHandler.cpvGraphName + "> " +
					  //"FROM <" + DailyRoutineHandler.nicknameGraphName +"> " +
					  "WHERE {" + 
					  "?payment psgr:payee <" + payee + "> ." +
					  "?payment psgr:payer <" + payer + "> ." +
					  "?payment psgr:paymentAmount ?am ." + 
					  "?payment psgr:cpv ?cpv ." +
					  "OPTIONAL{?cpv cpv:hasSuperCPVCode ?cpvDiv ." + 
					  "?cpvDiv a cpv:CPVDivision ." + 
					  "?cpvDiv psgr:cpvCode ?cpvCodeDiv ." +
					  "?cpvDiv psgr:cpvGreekSubject ?subject ." +
					  "?cpvDiv psgr:cpvEnglishSubject ?engSubject ." +
					  "?cpvDiv psgr:greekShortName ?shortName ." + 
					  "?cpvDiv psgr:engShortName ?engShortName .}" +
					  
					  "?cpv psgr:cpvCode ?cpvCode ." +
					  "} ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1 ";
		return str1;
					  
	}
	
	
	public static String topPayeesForPayer(String payer, String limit){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?payee (xsd:decimal(?am) as ?amount) FROM <" + DailyRoutineHandler.binaryGraphName + "> " +
					  		"FROM <"+DailyRoutineHandler.graphName + "> WHERE {" + 
					  "?bRel psgr:binaryPayer <" + payer + "> ." + 
					  "?bRel psgr:binaryPayee ?payee ." + 
					  "?bRel psgr:aggregatePaymentAmount ?am .} ORDER BY DESC(?amount) LIMIT " + limit;
		return str1;
	}
	
	public static String topPayersForPayee(String payee, String limit){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?payer (xsd:decimal(?am) as ?amount) FROM <" + DailyRoutineHandler.binaryGraphName + "> " +
					  		"FROM <"+DailyRoutineHandler.graphName + "> WHERE {" + 
					  "?bRel psgr:binaryPayer ?payer ." + 
					  "?bRel psgr:binaryPayee <"+payee+"> ." + 
					  "?bRel psgr:aggregatePaymentAmount ?am .} ORDER BY DESC(?amount) LIMIT " + limit;
		return str1;
	}
	
	public static String totalAmountForPayer(String payer){
		String str1 = ruleSet + prefixes + 
				  	  "SELECT (sum(xsd:decimal(?am)) as ?sum) FROM <" + DailyRoutineHandler.graphName + "> WHERE {" + 
				  	  "?payment psgr:payer <" + payer + "> ." + 
				  	  "?payment psgr:paymentAmount ?am . }";
		return str1;
	}
	
	public static String totalAmountForPayee(String payee){
		String str1 = ruleSet + prefixes + 
				  	  "SELECT (sum(xsd:decimal(?am)) as ?sum) FROM <" + DailyRoutineHandler.graphName + "> WHERE {" + 
				  	  "?payment psgr:payee <" + payee + "> ." + 
				  	  "?payment psgr:paymentAmount ?am . }";
		return str1;
	}
	
	public static String mostFrequentPayer(String payee){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?payer from <" + DailyRoutineHandler.graphName + "> WHERE {" + 
					  "?payment psgr:payee <" + payee + "> ." + 
					  "?payment psgr:payer ?payer . }" +
					  "ORDER BY DESC(count(distinct ?payment)) LIMIT 1";
		return str1;
					  
	}
	
	public static String biggestAmountPayer(String payee){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?payer from <" + DailyRoutineHandler.graphName + "> WHERE {" + 
					  "?payment psgr:payee <" + payee + "> ." + 
					  "?payment psgr:payer ?payer . " +
					  "?payment psgr:paymentAmount ?am .}" +
					  "ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 1";
		return str1;
					  
	}
	
	public static String topPayeesDescription(String dateString, String selector, String timeframe, String limit){
		
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			
			str1 +=   "SELECT DISTINCT ?payee (sum(xsd:decimal(?am)) as ?sumString) (avg(xsd:decimal(?am)) as ?avgString) " +
					  " (count(distinct ?payment) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  "FROM <" + DailyRoutineHandler.falseGraphName + "> " + 
					  "WHERE { " + 					
					  selector + dateString + "\" ." +
					  "?decision psgr:refersTo ?payment  ." + 
					  "?payment psgr:payee ?payee ." +			
					  "?payee psgr:validAfm \"true\" . " + 
					  "?payment psgr:paymentAmount ?am ." +
					  //"OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  //"?paymentAgent psgr:gsisName ?gsisName . }" + 
					  //"?payer psgr:paymentAgentName ?pName . }" +
					  " } ORDER BY DESC(?sumString) LIMIT " + limit; 		
		else
			str1 +=   "SELECT DISTINCT ?payee (sum(xsd:decimal(?am)) as ?sumString) (avg(xsd:decimal(?am)) as ?avgString)" +
					  " (count(?payment) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 	
					  "FROM <" + DailyRoutineHandler.falseGraphName + "> " + 
					  "WHERE { " + 					  
					  "?payment psgr:payee ?payee ." +				
					  "?payee psgr:validAfm \"true\" . " + 
					  "?payment psgr:paymentAmount ?am ." +
					  //"OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  //"?paymentAgent psgr:gsisName ?gsisName . }" +
					  "} ORDER BY DESC(?sumString) LIMIT " + limit; 					  					  
		
		return str1;
	
	}
	
	public static String topPayeesDescriptionBubbles(String dateString, String selector, String timeframe, String limit){
		
		String str1 = ruleSet+prefixes;
		if(!timeframe.equals("overall"))
			
			str1 +=   "SELECT DISTINCT ?payee (sum(xsd:decimal(?am)) as ?sumString) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  //"FROM <" + DailyRoutineHandler.binaryGraphName + "> " + 		
					  "WHERE { " + 					
					  selector + dateString + "\" ." +
					  "?decision psgr:refersTo ?payment  ." + 
					  "?payment psgr:payee ?payee ." +					  
					  "?payment psgr:paymentAmount ?am ." +
					  "?payee psgr:validAfm \"true\" ." + 
					  //"?bRel psgr:binaryPayee ?payee ." + 
					  //"OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  //"?paymentAgent psgr:gsisName ?gsisName . }" + 
					  //"?payer psgr:paymentAgentName ?pName . }" +
					  " } ORDER BY DESC(?sumString) LIMIT " + limit; 		
		else
			str1 +=   "SELECT DISTINCT ?payee (sum(xsd:decimal(?am)) as ?sumString) "+
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 	
					  //"FROM <" + DailyRoutineHandler.binaryGraphName + "> " + 		
					  "WHERE { " + 					  
					  "?payment psgr:payee ?payee ." +					  
					  "?payment psgr:paymentAmount ?am ." +
					  "?payee psgr:validAfm \"true\" ." +
					  //"?bRel psgr:binaryPayee ?payee ." + 
					  //"OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  //"?paymentAgent psgr:gsisName ?gsisName . }" +
					  "} ORDER BY DESC(?sumString) LIMIT " + limit; 					  					  
		
		return str1;
	
	}
	
	public static String topBinaryDescription(String dateString, String selector, String timeframe){
		
		String str1 = ruleSet + prefixes;
		if(!timeframe.equals("overall"))
			str1 +=   "SELECT ?bRel (sum(xsd:decimal(?amount)) as ?sumString) ?payer ?payee " +
					  " (count(distinct ?payment) as ?count) " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 
					  "FROM <" + DailyRoutineHandler.binaryGraphName + "> " + 
					  "WHERE { " + 					  
					  "?bRel rdf:type psgr:BinaryRelationship . " +  
					  "?bRel psgr:binaryPayer ?payer . " + 
					  "?bRel psgr:binaryPayee ?payee . " +					  
					  "?payment psgr:payer ?payer . " +  
					  "?payment psgr:payee ?payee . " + 	
					  "?payment psgr:isReferencedBy ?decision ." + selector + dateString + "\" . " +
					  //"?payer psgr:afm ?payerAfm . " +
					  //"?payee psgr:afm ?payeeAfm . " + 
					  "?payment psgr:paymentAmount ?amount . " +
					  //"OPTIONAL{?p owl:sameAs ?payer . ?p psgr:gsisName ?payerName .} " + 
					  //"OPTIONAL{?pe owl:sameAs ?payee . ?pe psgr:gsisName ?payeeName .} " +					  
					  "}ORDER BY DESC(?sum) LIMIT 10 "; 			
		else
			str1 +=   "SELECT ?bRel (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?payerAfm ?payeeAfm ?payerName ?payeeName " +
					  " (xsd:string(count(distinct ?payment)) as ?count) " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 
					  "FROM <" + DailyRoutineHandler.binaryGraphName + "> " + 
					  "WHERE { " + 
					  "?bRel rdf:type psgr:BinaryRelationship . " +  
					  "?bRel psgr:binaryPayer ?payer . " + 
					  "?bRel psgr:binaryPayee ?payee . " + 
					  "?payment psgr:payer ?payer . " +  
					  "?payment psgr:payee ?payee . " + 		
					  "?payer psgr:afm ?payerAfm . " +
					  "?payee psgr:afm ?payeeAfm . " +
					  "OPTIONAL{?p owl:sameAs ?payer . ?p psgr:gsisName ?payerName .} " + 
					  "OPTIONAL{?pe owl:sameAs ?payee . ?pe psgr:gsisName ?payeeName .} " +
					  "?payment psgr:paymentAmount ?amount . " +					  
					  "}ORDER BY DESC(sum(xsd:decimal(?amount))) LIMIT 10 "; 			
			
		return str1;
	}
	
	/*public static String topPaymentsDescription(String timeframeGraph, String dateString, String selector){
		
		String str1 = ruleSet+prefixes+
					  "SELECT DISTINCT ?topPayment ?subject (xsd:decimal(?am) as ?amount) ?am " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 
					  "FROM <" + timeframeGraph + "> " +
					  "FROM <" + DailyRoutineHandler.cpvGraphName + "> " +
					  "WHERE { " + 
					  "?topPayment rdf:type psgr:Payment . " + 
					  "?timeframe psgr:topPayment ?topPayment . " + 
					  "?topPayment psgr:paymentAmount ?am . " + 
					  "?topPayment psgr:cpv ?cpv . " + 
					  "?topPayment psgr:isReferencedBy ?decision ." + selector + dateString + "\" . " + 				  
					  //"?cpv psgr:cpvCode ?cpvCode . " + 
					  "?cpv psgr:cpvGreekSubject ?subject . } ORDER BY DESC(?amount)"; 
					  
		return str1;
					  
	}
	
	
	
	public static String topPaymentsOverallDescription(){
		
		String str1 = ruleSet+prefixes+
					  "SELECT DISTINCT ?topPayment ?subject (xsd:decimal(?am) as ?amount) ?am " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 
					  "FROM <" + DailyRoutineHandler.overallGraphName + "> " +
					  "FROM <" + DailyRoutineHandler.cpvGraphName + "> " +
					  "WHERE { " + 
					  "?topPayment rdf:type psgr:Payment . " + 
					  "?timeframe psgr:topPayment ?topPayment . " + 
					  "?topPayment psgr:paymentAmount ?am . " + 
					  "?topPayment psgr:cpv ?cpv . " + 					   				 
					  //"?cpv psgr:cpvCode ?cpvCode . " + 
					  "?cpv psgr:cpvGreekSubject ?subject . } ORDER BY DESC(?amount)"; 
					  
		return str1;
					  
	}

	public static String topCpvDescription(String timeframeGraph, String dateString, String selector){
			
			//The update must include a language selector...
			String str1 = ruleSet + prefixes + 
						  "SELECT DISTINCT ?cpvCode ?cpvSubject (xsd:string(sum(xsd:decimal(?am))) as ?sumString) (xsd:string(count(distinct ?payment)) as ?count) " + 
						  "FROM <" + DailyRoutineHandler.graphName + "> " +
						  //"FROM <" + timeframeGraph + "> " +
						  "FROM <" + DailyRoutineHandler.cpvGraphName + "> " + 
						  "WHERE { " + 
						  //"?timeframe psgr:topCpv ?cpv ." + 
						  "?cpv psgr:cpvCode ?cpvCode . " +
						  "?cpv psgr:cpvGreekSubject ?cpvSubject ." +
						  "?payment psgr:paymentAmount ?am . " + 
						  "?payment psgr:cpv ?cpv . " + 
						  "?payment psgr:isReferencedBy ?decision ." + selector + dateString + "\" . " + 
						  " } ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 10";
						  
			return str1;
						  
	}
	
	public static String topCpvOverallDescription(){
		
		//The update must include a language selector...
		String str1 = ruleSet + prefixes + 
					  "SELECT DISTINCT ?cpvCode ?cpvSubject (xsd:string(sum(xsd:decimal(?am))) as ?sumString) (xsd:string(count(distinct ?payment)) as ?count) " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  //"FROM <" + DailyRoutineHandler.overallGraphName + "> " +
					  "FROM <" + DailyRoutineHandler.cpvGraphName + "> " + 
					  "WHERE { " + 
					  //"?timeframe psgr:topCpv ?cpv ." + 
					  "?cpv psgr:cpvCode ?cpvCode . " +
					  "?cpv psgr:cpvGreekSubject ?cpvSubject ." +
					  "?payment psgr:paymentAmount ?am . " + 
					  "?payment psgr:cpv ?cpv . " + 					  
					  " } ORDER BY DESC(sum(xsd:decimal(?am))) LIMIT 10";
					  
		return str1;
					  
}
	
	public static String topPayersDescription(String timeframeGraph, String dateString, String selector){
		
		String str1 = ruleSet + prefixes +
					  "SELECT DISTINCT ?afm ?payer ?gsisName (xsd:string(sum(xsd:decimal(?am))) as ?sumString) " +
					  " (xsd:string((count(distinct ?payment))) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 
					  //"FROM <" + timeframeGraph + "> " +
					  "WHERE { " + 
					  //"?timeframe psgr:topPayer ?payer ." + 
					  "?payment psgr:payer ?payer ." +
					  "?payer psgr:afm ?afm ." + 
					  "?payment psgr:paymentAmount ?am ." +
					  "OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  "?paymentAgent psgr:gsisName ?gsisName . }" + 
					  //"?payer psgr:paymentAgentName ?pName . }" +
					  "?payment psgr:isReferencedBy ?decision ." + selector + dateString + "\" . } ORDER BY DESC((sum(xsd:decimal(?am)))) LIMIT 10"; 					  
		
		return str1;
	}
	
	public static String topPayersOverallDescription(){
		
		String str1 = ruleSet + prefixes +
					  "SELECT DISTINCT ?afm ?payer ?gsisName (xsd:string(sum(xsd:decimal(?am))) as ?sumString) " +
					  " (xsd:string((count(distinct ?payment))) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 
					  //"FROM <" + DailyRoutineHandler.overallGraphName + "> " +
					  "WHERE { " + 
					  //"?timeframe psgr:topPayer ?payer ." + 
					  "?payment psgr:payer ?payer ." +
					  "?payer psgr:afm ?afm ." + 
					  "?payment psgr:paymentAmount ?am ." +
					  "OPTIONAL{?paymentAgent owl:sameAs ?payer ." +
					  "?paymentAgent psgr:gsisName ?gsisName . }} ORDER BY DESC((sum(xsd:decimal(?am)))) LIMIT 10"; 
					  //"?payer psgr:paymentAgentName ?pName . }" +
					   					  
		
		return str1;
	}
	
	public static String topPayeesDescription(String timeframeGraph, String dateString, String selector){
		
		String str1 = ruleSet + prefixes +
					  "SELECT DISTINCT ?afm ?payee ?gsisName (xsd:string(sum(xsd:decimal(?am))) as ?sumString) " +
					  " (xsd:string((count(distinct ?payment))) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 
					  //"FROM <" + timeframeGraph + "> " +
					  "WHERE { " + 
					  //"?timeframe psgr:topPayee ?payee ." + 
					  "?payment psgr:payee ?payee ." + 
					  "?payment psgr:paymentAmount ?am ." +
					  "?payee psgr:afm ?afm . " +
					  "OPTIONAL{?paymentAgent owl:sameAs ?payee ." +
					  "?paymentAgent psgr:gsisName ?gsisName . }" + 
					  //"?payee psgr:paymentAgentName ?pName . " +
					  "?payment psgr:isReferencedBy ?decision ." + selector + dateString + "\" . } ORDER BY DESC((sum(xsd:decimal(?am)))) LIMIT 10"; 					  
		
		return str1;
	}
	
	public static String topPayeesOverallDescription(){
		
		String str1 = ruleSet + prefixes +
					  "SELECT DISTINCT ?afm ?payee ?gsisName (xsd:string(sum(xsd:decimal(?am))) as ?sumString) " +
					  " (xsd:string((count(distinct ?payment))) as ?count) " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " + 
					  //"FROM <" + DailyRoutineHandler.overallGraphName + "> " +
					  "WHERE { " + 
					  //"?timeframe psgr:topPayee ?payee ." + 
					  "?payment psgr:payee ?payee ." + 
					  "?payment psgr:paymentAmount ?am ." +
					  "?payee psgr:afm ?afm . " +
					  "OPTIONAL{?paymentAgent owl:sameAs ?payee ." +
					  "?paymentAgent psgr:gsisName ?gsisName . }} ORDER BY DESC((sum(xsd:decimal(?am)))) LIMIT 10"; 
					  //"?payee psgr:paymentAgentName ?pName . " +
					   					  
		
		return str1;
	}*/
	
	/*public static String nameQuery(String afm){
		
		String str1 = ruleSet + prefixes +
					  "SELECT ?name FROM <" + DailyRoutineHandler.graphName + "> WHERE { " +
					  "?payer psgr:afm \"" + afm + "\" . " +
					  "?payer psgr:paymentAgentName ?name . }";
		return str1;
	}*/
	
	public static String validityQuery(){
		String str1 = prefixes + "INSERT INTO <" + DailyRoutineHandler.graphName + "> " +
							     "{?agent psgr:validAfm \"true\"} WHERE {" +
							     "?agent a psgr:PaymentAgent . " +
							     "OPTIONAL{?agent psgr:validAfm ?val .} " +
							     "FILTER!(BOUND(?val))}";
		return str1;
	}

	public static String nameQuery(String resource){
		
		String str1 = ruleSet + prefixes +
					  "SELECT ?name ?engName ?gsisName ?shortName ?engShortName " +
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  "FROM <" + DailyRoutineHandler.falseGraphName + "> " +					  		
   			  		  "WHERE { " +
					  "<"+resource+"> psgr:paymentAgentName ?name ." +
   			  		  "<" + resource + "> psgr:afm ?afm ." + 
					  "?p psgr:afm ?afm ." +
					  "OPTIONAL{?p psgr:greekShortName ?shortName .}" +
					  "OPTIONAL{?p psgr:engShortName ?engShortName .}" +
					  "OPTIONAL{?p psgr:engName ?engName .}" +
					  //"OPTIONAL{?paymentAgent owl:sameAs <"+resource+"> . ?paymentAgent psgr:gsisName ?gsisName .}}ORDER BY DESC(?shortName) LIMIT 1";
					  "OPTIONAL{?p psgr:gsisName ?gsisName .}}ORDER BY DESC(?shortName) LIMIT 1";
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
					  "SELECT distinct (xsd:decimal(?am) as ?aggregateString) ?date ?dat " + 
					  "FROM <" + timeframeGraph + "> " +
					  //"FROM <" + DailyRoutineHandler.graphName + "> " +
					  //"FROM <diavgeia.AllDecisions> " +
					  "FROM <"+ DailyRoutineHandler.graphName + "> " +
					  "FROM <"+ DailyRoutineHandler.falseGraphName + "> " + 
					  "WHERE { " +
					  "?timeframe rdf:type " + timeframe +
					  "?timeframe psgr:date ?date . " +
					  "FILTER(?date!=\"Null\" && ?date!=\"Null-w-Null\" && ?date!=\"Null-w-\") " +
					  "?timeframe psgr:aggregatePaymentAmount ?am . " +					  
					  "} ORDER BY DESC(?aggregateString) LIMIT 1000 ";
		
		return str1;
	}
	
	/*public static String aggregateAmountTimeplotPerPayerDescription(String timeframeGraph){
		
		String timeframeProperty = "";
		if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
			timeframeProperty = "d:day ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
			timeframeProperty = "d:week ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
			timeframeProperty = "d:Month ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
			timeframeProperty = "d:Year ?timeframe .";
		
		String str1 = ruleSet + prefixes +
					 "SELECT ?gsisName ?payer (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?date " +
					 "FROM <" + DailyRoutineHandler.overallGraphName + "> FROM <" + timeframeGraph + "> FROM <" + DailyRoutineHandler.graphName + "> WHERE { " + 
					 "?payer psgr:isTopPayerOf psgr:Overall . " + //na to valw na einai isTopPayerOf psgr:Overall ?
					 "?timeframe psgr:date ?date . " + 
					 "?payment psgr:payer ?payer . " + 
					 "OPTIONAL{?paymentAgent owl:sameAs ?payer . ?paymentAgent psgr:gsisName ?gsisName .}" + 
					 "?payment psgr:isReferencedBy ?decision . " +					 
					 "?decision " + timeframeProperty + 
					 "?payment psgr:paymentAmount ?amount . }" ;
					 
		return str1;
		
	}*/
	
	/*public static String timeplotPerPayerDescription(String timeframeGraph, String afm){
		
		String timeframeProperty = "";
		if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
			timeframeProperty = "d:day ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
			timeframeProperty = "d:week ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
			timeframeProperty = "d:month ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
			timeframeProperty = "d:year ?timeframe .";
		
		String str1 = ruleSet + prefixes +
					  "SELECT ?gsisName (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?date " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE { " + 
					  "?payer psgr:afm \""+afm+"\" ." + 
					  "?payment psgr:payer ?payer . " + 
					  "?payment psgr:paymentAmount ?amount ." +
					  "?decision psgr:refersTo ?payment . " +
					  "?decision " + timeframeProperty + 
					  "?timeframe psgr:date ?date . " +
					  "FILTER(?date!=\"Null\" && ?date!=\"Null-w-Null\" && ?date!=\"Null-w-\") " +
					  "OPTIONAL{?paymentAgent owl:sameAs ?payer . ?paymentAgent psgr:gsisName ?gsisName .} }" ;
					  
		return str1;
					  
	}*/
	public static String timeplotPerPayerDescription(String timeframeGraph, String payer){
		
		String timeframeProperty = "";
		if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
			timeframeProperty = "d:day ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
			timeframeProperty = "d:week ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
			timeframeProperty = "d:month ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
			timeframeProperty = "d:year ?timeframe .";
				
		String str1 = ruleSet + prefixes +
					  "SELECT (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?date " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> " +
					  " WHERE { " + 				  
					  "?payment psgr:payer <"+payer+"> . " + 				  
					  "?payment psgr:paymentAmount ?amount ." +
					  "?decision psgr:refersTo ?payment . " +
					  "?decision " + timeframeProperty + 
					  "?timeframe psgr:date ?date . " +
					  "FILTER(?date!=\"Null\" && ?date!=\"Null-w-Null\" && ?date!=\"Null-w-\") " +
					  "}" ;
					  
		return str1;
					  
	}
	/*public static String aggregateAmountTimeplotPerPayeeDescription(String timeframeGraph){
		
		String timeframeProperty = "";
		if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
			timeframeProperty = "d:day ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
			timeframeProperty = "d:week ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
			timeframeProperty = "d:month ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
			timeframeProperty = "d:year ?timeframe .";
		
		String str1 = ruleSet + prefixes +
					 "SELECT ?gsisName ?payee (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?date " +
					 "FROM <" + DailyRoutineHandler.overallGraphName + "> FROM <" + timeframeGraph + "> FROM <" + DailyRoutineHandler.graphName + "> WHERE { " + 
					 "?payer psgr:isTopPayeeOf psgr:Overall . " + 
					 "?timeframe psgr:date ?date . " + 
					 "?payment psgr:payee ?payee . " + 
					 "OPTIONAL{?paymentAgent owl:sameAs ?payee . ?paymentAgent psgr:gsisName ?gsisName .}" +
					 "?payment psgr:isReferencedBy ?decision . " +
					 "?decision " + timeframeProperty + 
					 "?payment psgr:paymentAmount ?amount . }" ;
					 
		return str1;
		
	}*/
	
	/*public static String timeplotPerPayeeDescription(String timeframeGraph, String afm){
		
		String timeframeProperty = "";
		if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
			timeframeProperty = "d:day ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
			timeframeProperty = "d:week ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
			timeframeProperty = "d:month ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
			timeframeProperty = "d:year ?timeframe .";
		
		String str1 = ruleSet + prefixes +
					  "SELECT ?gsisName (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?date " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> WHERE { " + 
					  "?payee psgr:afm \""+afm+"\" ." + 
					  "?payment psgr:payee ?payee . " + 
					  "?payment psgr:paymentAmount ?amount ." +
					  "?decision psgr:refersTo ?payment . " +
					  "?decision " + timeframeProperty + 
					  "?timeframe psgr:date ?date . " +
					  "FILTER(?date!=\"Null\" && ?date!=\"Null-w-Null\" && ?date!=\"Null-w-\") " +
					  "OPTIONAL{?paymentAgent owl:sameAs ?payee . ?paymentAgent psgr:gsisName ?gsisName .} }" ;
					  
		return str1;
					  
	}
	*/
public static String timeplotPerCpvDescription(String timeframeGraph, String cpvCode){
		
		String timeframeProperty = "";
		if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
			timeframeProperty = "d:day ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
			timeframeProperty = "d:week ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
			timeframeProperty = "d:month ?timeframe .";
		else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
			timeframeProperty = "d:year ?timeframe .";
		
		String str1 = ruleSet + prefixes +
					  "SELECT ?cpvCode ?cpvSubject (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?date " + 
					  "FROM <" + DailyRoutineHandler.graphName + "> FROM <"+DailyRoutineHandler.cpvGraphName+"> WHERE { " + 
					  "?cpv psgr:cpvCode \""+cpvCode+"\" ." + 
					  "?payment psgr:cpv ?cpv . " + 
					  "?payment psgr:paymentAmount ?amount ." +
					  "?decision psgr:refersTo ?payment . " +
					  "?decision " + timeframeProperty + 
					  "?timeframe psgr:date ?date . " +
					  "FILTER(?date!=\"Null\" && ?date!=\"Null-w-Null\" && ?date!=\"Null-w-\") " +
					  "?cpv psgr:cpvGreekSubject ?cpvSubject . }" ;
					  
		return str1;
					  
	}

public static String timeplotPerCpvDivDescription(String timeframeGraph, String cpvDiv, String lang){
	
	String timeframeProperty = "", langSelector = "";
	if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
		timeframeProperty = "d:day ?timeframe .";
	else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
		timeframeProperty = "d:week ?timeframe .";
	else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
		timeframeProperty = "d:month ?timeframe .";
	else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
		timeframeProperty = "d:year ?timeframe .";
	
	if(lang.equals("gr"))
		langSelector = "d:cpvGreekSubject ?cpvSubject .";
	else if(lang.equals("eng"))
		langSelector = "d:cpvEnglishSubject ?cpvSubject .";
	
	String str1 = ruleSet + prefixes +
				  "SELECT <"+cpvDiv+"> ?cpvSubject ?shortName (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?date " + 
				  "FROM <" + DailyRoutineHandler.graphName + "> " +
				  		//"FROM <"+DailyRoutineHandler.cpvGraphName+"> " +
				  //"FROM <"+DailyRoutineHandler.nicknameGraphName+"> 
				  		" WHERE { " + 				  
				  "{?payment psgr:cpv ?cpv . " +
				  "<"+cpvDiv+"> cpv:hasSubCPVCode ?cpv} UNION {?payment psgr:cpv <"+cpvDiv+"> .}" +
				  "?payment psgr:paymentAmount ?amount ." +
				  "?decision psgr:refersTo ?payment . " +
				  "?decision " + timeframeProperty + 
				  "?timeframe psgr:date ?date . " +
				  "FILTER(?date!=\"Null\" && ?date!=\"Null-w-Null\" && ?date!=\"Null-w-\") " +
				  "<"+cpvDiv+"> "+langSelector+" " +
				  "<"+cpvDiv+"> psgr:greekShortName ?shortName .}" ;
				  
	return str1;
				  
}

public static String timeplotPerPayeeDescription(String timeframeGraph, String payee){
	
	String timeframeProperty = "";
	if(timeframeGraph.equals(DailyRoutineHandler.dayGraphName))
		timeframeProperty = "d:day ?timeframe .";
	else if(timeframeGraph.equals(DailyRoutineHandler.weekGraphName))
		timeframeProperty = "d:week ?timeframe .";
	else if(timeframeGraph.equals(DailyRoutineHandler.monthGraphName))
		timeframeProperty = "d:month ?timeframe .";
	else if(timeframeGraph.equals(DailyRoutineHandler.yearGraphName))
		timeframeProperty = "d:year ?timeframe .";
			
	String str1 = ruleSet + prefixes +
				  "SELECT (xsd:string(sum(xsd:decimal(?amount))) as ?sumString) ?date " + 
				  "FROM <" + DailyRoutineHandler.graphName + "> " +
				  " WHERE { " + 				  
				  "?payment psgr:payee <"+payee+"> . " + 				  
				  "?payment psgr:paymentAmount ?amount ." +
				  "?decision psgr:refersTo ?payment . " +
				  "?decision " + timeframeProperty + 
				  "?timeframe psgr:date ?date . " +
				  "FILTER(?date!=\"Null\" && ?date!=\"Null-w-Null\" && ?date!=\"Null-w-\") " +
				  "}" ;
				  
	return str1;
				  
}
	
	public static String binaryRelationships = ruleSet + prefixes + 
					  						   "DELETE FROM <" + DailyRoutineHandler.binaryGraphName + "> " + 
					  						   "{?bRel psgr:aggregatePaymentAmount ?amount .} WHERE " + 
					  						   "{ GRAPH ?g1 {?bRel psgr:rdfType psgr:BinaryRelationship . " +
					  						   "?bRel psgr:aggregatePaymentAmount ?amount . }} " +
					  						   "INSERT INTO <" + DailyRoutineHandler.binaryGraphName + "> " +
					  						   "{?bRel psgr:aggregatePaymentAmount ?sum .} WHERE {{ " + 
					  						   "SELECT distinct ?bRel (sum(xsd:decimal(?amount)) as ?sum) " + 
					  						 "FROM " + DailyRoutineHandler.graphName + " WHERE { " +
					  						   "?bRel rdf:type psgr:BinaryRelationship . " +
					  						   "?bRel psgr:binaryPayer ?payer . " +
					  						   "?bRel psgr:binaryPayee ?payee . " +
					  						   "?payment psgr:payer ?payer . " +
					  						   "?payment psgr:payee ?payee . " +
					  						   "?payment psgr:paymentAmount ?amount . } ORDER BY DESC(?sum) LIMIT 1 }} " ;
		
			
	


	public static String binaryRelationshipsTest1 = ruleSet + prefixes + 
			  "DELETE FROM <" + DailyRoutineHandler.binaryGraphName + "> " + 
			  "{?bRel psgr:aggregatePaymentAmount ?amount .} WHERE " + 
			  "{ GRAPH ?g1 {?bRel psgr:aggregatePaymentAmount ?amount . }} " ;

	public static String binaryRelationshipsTest2 = ruleSet + prefixes + 
			  "INSERT INTO <" + DailyRoutineHandler.binaryGraphName + "> " +
			  "{?bRel psgr:aggregatePaymentAmount ?sum .} WHERE {{ " + 
			  "SELECT distinct ?bRel (sum(xsd:decimal(?amount)) as ?sum) " +
			  //"SELECT distinct ?bRel (sum(?amount) as ?sum) " + 
			  "FROM <" + DailyRoutineHandler.graphName + "> WHERE { " +
			  "?bRel rdf:type psgr:BinaryRelationship . " +
			  "?bRel psgr:binaryPayer ?payer . " +
			  "?bRel psgr:binaryPayee ?payee . " +
			  "?payment psgr:payer ?payer . " +
			  "?payment psgr:payee ?payee . " +
			  "?payment psgr:paymentAmount ?amount . }}} " ;


	public static String binaryOverallDescription(){
		
		String str1 = ruleSet + prefixes + 
					  "SELECT ?bRel ?payer ?payee ?agg FROM <"+DailyRoutineHandler.binaryGraphName+"> FROM <"+DailyRoutineHandler.graphName+"> WHERE {"+
					  "?bRel psgr:aggregatePaymentAmount ?agg ." +
					  "?bRel psgr:binaryPayer ?payer . ?bRel psgr:binaryPayee ?payee ." + 
					 "} ORDER BY DESC(xsd:decimal(?agg)) LIMIT 10";
		return str1;
	}

	public static String aggregateAmountCounterDescription(String timeframe){
		
		String str1 = ruleSet + prefixes ;
		if(timeframe.equals("overall"))
			str1 += 	  "SELECT (xsd:string(?a) as ?agg) FROM <"+DailyRoutineHandler.overallGraphName+"> " + 
						  "WHERE {" + DailyRoutineHandler.overallResource + " psgr:aggregatePaymentAmount ?a .}" ;
		
						  
		return str1;
	}
	
	
	public static String correctionsQuery(){
		
		String str1 =  prefixes +					   
					   "DELETE FROM <" + DailyRoutineHandler.graphName + "> " +
					   "{?wrongDecision ?p ?o . ?wrongPayment ?p2 ?o2 . } WHERE {" +					   					  
					   "?correction psgr:correctionOfAda ?wrongDecision . " +					   
					   "?wrongDecision psgr:refersTo ?wrongPayment . " +
					   "?wrongDecision ?p ?o . " +
					   "?wrongPayment ?p2 ?o2 . } ";

		return str1;
	}
	
	public static String shortNames(String resource){
		
		String str1 = prefixes + 
					  "SELECT ?shortName FROM <" + DailyRoutineHandler.nicknameGraphName + "> WHERE{" +
					  "<" + resource + "> psgr:greekShortName ?shortName .} LIMIT 1";
		return str1;
	}
	
}

	


