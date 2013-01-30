package publicspending.java.daily;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import diavgeia.java.ontology.Ontology;

public class StatisticsHandler {
	
	
	Model model;
	VirtGraph graph;
	String dbUrl;	
	
	//Query String for calculating aggregate amounts payed by payer
	//Property used is psgr:totalPaymentsByPayer
	
		
	public StatisticsHandler(ArrayList<String[]> dates){
				
		//dbUrl = "jdbc:virtuoso://diavgeia.medialab.ntua.gr:1111/charset=UTF-8/log_enable=2";
		dbUrl = RoutineInvoker.connectionString;
		model = VirtModel.openDatabaseModel(null, RoutineInvoker.connectionString, "marios", "dirtymarios");
		graph = (VirtGraph) model.getGraph();
		//model = VirtModel.openDatabaseModel(RoutineInvoker.graphName, RoutineInvoker.connectionString, "marios", "dirtymarios");
		  //binaryRelationships();
		try{
			HashSet<String> days = getDays(dates);
			HashSet<String> weeks = getWeeks(dates);
			HashSet<String> months = getMonths(dates);
			HashSet<String> years = getYears(dates);
		
		
		System.out.println("Calculating Date Statistics...");
		
		
		handleCorrections();
		
		Model dayModel = VirtModel.openDatabaseModel(RoutineInvoker.dayGraphName, dbUrl, "marios", "dirtymarios");
		System.out.println("Fetching Types for Days.");
		typeDates(days, dayModel, "day", RoutineInvoker.dayGraphName);
		//topPaymentsForDecisionDates(dayModel, RoutineInvoker.dayGraphName, SPARQLQueries.daySelector, days, "days");
		dayModel.close();		
		totalAmountsForDecisionDates(RoutineInvoker.dayGraphName, SPARQLQueries.daySelector2);
		
		Model weekModel = VirtModel.openDatabaseModel(RoutineInvoker.weekGraphName, dbUrl, "marios", "dirtymarios");
		System.out.println("Fetching Types for Weeks.");
		typeDates(weeks, weekModel, "week", RoutineInvoker.weekGraphName);
		weekModel.close();
		totalAmountsForDecisionDates(RoutineInvoker.weekGraphName, SPARQLQueries.weekSelector2);
		
		Model monthModel = VirtModel.openDatabaseModel(RoutineInvoker.monthGraphName, dbUrl, "marios", "dirtymarios");
		System.out.println("Fetching Types for Months.");
		typeDates(months, monthModel, "month", RoutineInvoker.monthGraphName);
		monthModel.close();
		totalAmountsForDecisionDates(RoutineInvoker.monthGraphName, SPARQLQueries.monthSelector2);
		
		Model yearModel = VirtModel.openDatabaseModel(RoutineInvoker.yearGraphName, dbUrl, "marios", "dirtymarios");
		System.out.println("Fetching Types for Years.");
		typeDates(years, yearModel, "year", RoutineInvoker.yearGraphName);
		yearModel.close();
		totalAmountsForDecisionDates(RoutineInvoker.yearGraphName, SPARQLQueries.yearSelector2);
		//totalAmountsOverall();
		//binaryRelationships();		
		System.out.println("Done.");	
		}catch(Exception e){e.printStackTrace();}
		
		System.out.println("Calculating overall statistics...");
		totalAmountsOverall();
		
		//Do I need these?
		/*System.out.println("Calculating binary relationships...");
		binaryRelationships();*/
		
		System.out.println("Done.");		
		model.close();
		
	}
	/*
	 * Handles corrections of decisions based on the isCorrectionOfAda field of the CSV files.
	 */
	public void handleCorrections(){
		
		try{
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (SPARQLQueries.correctionsQuery(), graph);		
			vqe.execSelect();
			vqe.close();
		}catch(Exception e){e.printStackTrace();}		
	}
	
	public void typeDates(HashSet<String> dates, Model model, String timeframe, String timeframeGraphName){
		
		Iterator it = dates.iterator();		
		while(it.hasNext()){
			String date = (String)it.next();			
				if(timeframe.equals("day")){								
					model.createResource(Ontology.instancePrefix+"days/"+date).removeAll(RDF.type)
					.addProperty(RDF.type, Ontology.dayResource).addProperty(Ontology.date, date);
				}
				else if(timeframe.equals("week")){
					model.createResource(Ontology.instancePrefix+"weeks/"+date).removeAll(RDF.type)
					.addProperty(RDF.type, Ontology.weekResource).addProperty(Ontology.date, date);
				}
				else if(timeframe.equals("month")){				
					model.createResource(Ontology.instancePrefix+"months/"+date).removeAll(RDF.type)
					.addProperty(RDF.type, Ontology.monthResource).addProperty(Ontology.date, date);
				}
				else if(timeframe.equals("year")){
					model.createResource(Ontology.instancePrefix+"years/"+date).removeAll(RDF.type)
					.addProperty(RDF.type, Ontology.yearResource).addProperty(Ontology.date, date);
				}
			//}
		}
	}
	
	/*
	 * This method returns a hashset with the days whose stats need to be changed
	 * 
	 * @datesList An array list containing strings of the form "YYYY-MM-DD"
	 * @return A hashset containing strings of the form "YYYY-MM-DD" 
	 */
	public HashSet<String> getDays(ArrayList<String[]> datesList){
		
		HashSet<String> days = new HashSet<String>();
		Iterator datesIt = datesList.iterator();
		while(datesIt.hasNext()){
			String[] date = (String[]) datesIt.next();			
			String dayString = date[2];			
			days.add(dayString);
		}
		return days;
	}
	
	/*
	 * This method returns a hashset with the months whose stats need to be changed
	 * 
	 * @datesList An array list containing strings of the form "YYYY-MM-DD"
	 * @return A hashset containing strings of the form "YYYY-MM" 
	 */
	public HashSet<String> getMonths(ArrayList<String[]> datesList){
		
		HashSet<String> months = new HashSet<String>();
		Iterator datesIt = datesList.iterator();
		while(datesIt.hasNext()){
			String[] date = (String[]) datesIt.next();			
			String monthString = date[1];			
			months.add(monthString);
		}
		return months;
	}
	
	/*
	 * This method returns a hashset with the years whose stats need to be changed
	 * 
	 * @datesList An array list containing strings of the form "YYYY-MM-DD"
	 * @return A hashset containing strings of the form "YYYY" 
	 */
	public HashSet<String> getYears(ArrayList<String[]> datesList){
		
		HashSet<String> years = new HashSet<String>();
		Iterator datesIt = datesList.iterator();
		while(datesIt.hasNext()){
			String[] date = (String[]) datesIt.next();			
			String yearString = date[0];
			years.add(yearString);
		}
		return years;
	}
	
	public HashSet<String> getWeeks(ArrayList<String[]> datesList){
		
		HashSet<String> weeks = new HashSet<String>();
		Iterator datesIt = datesList.iterator();
		while(datesIt.hasNext()){
			String[] date = (String[]) datesIt.next();
			String weekString = date[0]+"-w-"+date[3];
			weeks.add(weekString);
		}
		return weeks;
	}
	
	
	/*
	 * This method runs the SPARQL query that calculates aggregate payment amounts for each distinct payer
	 * 
	 *  @return True if the query was executed successfully, False otherwise
	 */
	public boolean payerAggregates(){
							
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (SPARQLQueries.payerAggTest1, graph);
			vqe.execSelect();
			vqe.close();
			
			VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (SPARQLQueries.payerAggTest2, graph);
			vqe2.execSelect();
			vqe2.close();
			return true;				
	}
	
	public boolean payeeAggregates(){
				
			//VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (SPARQLQueries.payeeAggQuery, graph);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (SPARQLQueries.payeeAggTest1, graph);
		vqe.execSelect();
		vqe.close();
		
		VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (SPARQLQueries.payeeAggTest2, graph);
		vqe2.execSelect();
		vqe2.close();
		return true;			
	}
	
	public boolean binaryRelationships(){
		
		try{
			//VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (SPARQLQueries.binaryRelationships, graph);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (SPARQLQueries.binaryRelationshipsTest1, graph);
			//System.out.println(SPARQLQueries.binaryRelationshipsTest1);
			vqe.execSelect();
			vqe.close();
			VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (SPARQLQueries.binaryRelationshipsTest2, graph);
			//System.out.println(SPARQLQueries.binaryRelationshipsTest2);
			vqe2.execSelect();
			vqe2.close();
			
			return true;
		}
		catch(Exception e){return false;}
	}
	
	
	/*
	 * This method runs a SPARQL query to calculate total payment amounts (aggregate) for a given set of timeframe periods.
	 * 
	 *  @dateModel The model that needs to be updated
	 *  @graphName The name of the date graph (e.g. "diavgeia.DayGraph") to be changed
	 *  @selector The appropriate SPARQL date selector as define in the SPARQLQueries class
	 *  @datesSet A hashset (to ensure no duplicates exist) of the dates of interest
	 *  
	 *  @return True if successful, False otherwise
	 */
	public boolean totalAmountsForDecisionDates(String graphName, String selector){
		
		//Calculate totals for dates		
		//String totalAmountQuery = SPARQLQueries.totalPaymentAmountForDecisionDates2(graphName, selector);
		String totalAmountQuery = SPARQLQueries.totalTest1(graphName, selector);
		//System.out.println(totalAmountQuery);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (totalAmountQuery, graph);
		vqe.execSelect();
		vqe.close();
		
		totalAmountQuery = SPARQLQueries.totalTest2(graphName, selector);
		//System.out.println(totalAmountQuery);
		VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (totalAmountQuery, graph);
		vqe2.execSelect();
		vqe2.close();
		/*Iterator it= datesSet.iterator();
		while(it.hasNext()){					
			String dateString = (String) it.next();		
			String totalAmountQuery = SPARQLQueries.totalPaymentAmountForDecisionDates2(graphName, dateString, selector);
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (totalAmountQuery, graph);
			vqe.execSelect();
			vqe.close();
		}*/		
		return true;
	}
	
	public boolean totalAmountsOverall(){
										
		//String totalAmountQuery = SPARQLQueries.totalPaymentAmountOverall();
		String totalAmountQuery = SPARQLQueries.totalOverallTest1();
		//System.out.println(totalAmountQuery);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (totalAmountQuery, graph);
		vqe.execSelect();
		vqe.close();		
		
		totalAmountQuery = SPARQLQueries.totalOverallTest2();
		//System.out.println(totalAmountQuery);
		VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (totalAmountQuery, graph);
		vqe2.execSelect();
		vqe2.close();
		return true;
	}
	

	/*
	 * This method runs a SPARQL query to calculate top payments for a given set of timeframe periods.
	 * 	
	 *  @graphName The name of the date graph (e.g. "diavgeia.DayGraph") to be changed
	 *  @selector The appropriate SPARQL date selector as define in the SPARQLQueries class
	 *  @datesSet A hashset (to ensure no duplicates exist) of the dates of interest
	 *  
	 *  @return True if successful, False otherwise
	 */
	public boolean topPaymentsForDecisionDates(Model dateModel, String graphName, String selector, HashSet<String> datesSet, String instanceName){
		
		//Calculate top payments for days		
		Iterator it= datesSet.iterator();
		while(it.hasNext()){					
			String dateString = (String) it.next();			
			dateModel.createResource(Ontology.instancePrefix+instanceName+"/"+dateString).removeAll(Ontology.topPayment);
					  
			/*String topPaymentsQuery = SPARQLQueries.topPaymentsForDecisionDates(graphName, dateString, selector);			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (topPaymentsQuery, graph);
			vqe.execSelect();
			vqe.close();*/
		}		
		return true;
	}
	
	/*public boolean topPaymentsOverall(){
				
		String topPaymentsQuery = SPARQLQueries.topPaymentsOverall();			
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (topPaymentsQuery, graph);
		vqe.execSelect();
		vqe.close();			
		return true;
	}
	
	
	 * This method runs a SPARQL query to calculate top payers for a given set of timeframe periods.
	 * 
	 *  @dateModel The model that needs to be updated
	 *  @graphName The name of the date graph (e.g. "diavgeia.DayGraph") to be changed
	 *  @selector The appropriate SPARQL date selector as define in the SPARQLQueries class
	 *  @datesSet A hashset (to ensure no duplicates exist) of the dates of interest
	 *  
	 *  @return True if successful, False otherwise
	 
	public boolean topPayersForDecisionDates(String graphName, String selector, HashSet<String> datesSet){
		
		//Calculate top payments for days
		Iterator it= datesSet.iterator();
		while(it.hasNext()){					
			String dateString = (String) it.next();			
			String topPayersQuery = SPARQLQueries.topPayersForDecisionDates(graphName, dateString, selector);			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (topPayersQuery, graph);
			vqe.execSelect();
			vqe.close();
		}		
		return true;
	}
	
	public boolean topPayersOverall(){
													
		String topPayersQuery = SPARQLQueries.topPayersOverall();			
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (topPayersQuery, graph);
		vqe.execSelect();
		vqe.close();		
		return true;
	}
	
	
	 * This method runs a SPARQL query to calculate top payees for a given set of timeframe periods.
	 * 
	 *  @dateModel The model that needs to be updated
	 *  @graphName The name of the date graph (e.g. "diavgeia.DayGraph") to be changed
	 *  @selector The appropriate SPARQL date selector as define in the SPARQLQueries class
	 *  @datesSet A hashset (to ensure no duplicates exist) of the dates of interest
	 *  
	 *  @return True if successful, False otherwise
	 
	public boolean topPayeesForDecisionDates(String graphName, String selector, HashSet<String> datesSet){
		
		//Calculate top payments for days	
		Iterator it= datesSet.iterator();
		while(it.hasNext()){					
			String dateString = (String) it.next();			
			String topPayeesQuery = SPARQLQueries.topPayeesForDecisionDates(graphName, dateString, selector);			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (topPayeesQuery, graph);
			vqe.execSelect();
			vqe.close();
		}		
		return true;
	}
	
	public boolean topPayeesOverall(){
								
		String topPayeesQuery = SPARQLQueries.topPayeesOverall();			
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (topPayeesQuery, graph);
		vqe.execSelect();
		vqe.close();
		return true;
	}
	
	
	 * This method runs a SPARQL query to calculate top cpv codes based on largest sums for a given set of timeframe periods.
	 * 
	 *  @dateModel The model that needs to be updated
	 *  @graphName The name of the date graph (e.g. "diavgeia.DayGraph") to be changed
	 *  @selector The appropriate SPARQL date selector as define in the SPARQLQueries class
	 *  @datesSet A hashset (to ensure no duplicates exist) of the dates of interest
	 *  
	 *  @return True if successful, False otherwise
	 
	public boolean topCPVForDecisionDates(String graphName, String selector, HashSet<String> datesSet){
				
		Iterator it= datesSet.iterator();
		while(it.hasNext()){					
			String dateString = (String) it.next();			
			String topCPVQuery = SPARQLQueries.topCPVForDecisionDates(graphName, dateString, selector);			
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (topCPVQuery, graph);
			vqe.execSelect();
			vqe.close();
		}		
		return true;
	}
	
	public boolean topCPVOverall(){
														
		String topCPVQuery = SPARQLQueries.topCPVOverall();			
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (topCPVQuery, graph);
		vqe.execSelect();
		vqe.close();		
		return true;
	}*/

	

}