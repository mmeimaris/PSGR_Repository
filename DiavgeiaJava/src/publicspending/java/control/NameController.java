package publicspending.java.control;

import java.io.FileOutputStream;

import publicspending.java.daily.GreeklishFactory;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtModel;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

import diavgeia.java.ontology.Ontology;

public class NameController {

	GreeklishFactory gf = new GreeklishFactory();
	String prefixes = "PREFIX psgr:<http://publicspending.medialab.ntua.gr/ontology#> " +
			   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
			   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
			   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";		
	
	public NameController(){
				
	}
	
	public void setEnglishNames(){
		
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		System.out.println("Connected to Virtuoso.");
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		String query = "SELECT ?agent ?validName WHERE {?agent psgr:validName ?validName}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model tempModel = ModelFactory.createDefaultModel();
		while(results.hasNext()){
			QuerySolution rs = results.nextSolution();
			RDFNode agent = rs.get("agent");
			Literal name = rs.getLiteral("validName");
			String engName = gf.greeklishGenerator(name.toString());
			String searchEngName = gf.greeklishGeneratorSearch(name.toString());
			tempModel.createResource(agent.toString()).addProperty(Ontology.validEngName, engName)
													  .addProperty(Ontology.searchEngName, searchEngName);
		}vqe.close();
		try {
			FileOutputStream fos2 = new FileOutputStream("C:/Users/marios/Desktop/englishNames.rdf");
			tempModel.write(fos2, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/englishNames.rdf");
		}catch(Exception e){e.printStackTrace();}
		tempModel.close();
		graph.close();
		
	}
	
	/*
	 * This method should be called at the end of the routine, after the SOAP calls.
	 */
	public void uniqueNamesOnIteration(){
		
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		System.out.println("Connected to Virtuoso.");
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		String gsisQuery = prefixes + "SELECT ?agent ?someName where {" +    //This will handle agents with valid gsis names.
 				  "?agent a psgr:PaymentAgent ." + //It will query for these agents and assign validName properties
 				  "?agent psgr:gsisName ?someName . " +
 				  "OPTIONAL{?agent psgr:validName ?name}"+ //Based on their gsis name.
 				  "FILTER(?someName!=\"Null\")" +
 				  "FILTER(!BOUND(?name))}";
		//System.out.println(query1);
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (gsisQuery, graph);
		ResultSet results = vqe.execSelect();
		Model tempModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){			
			QuerySolution rs = results.nextSolution();
			RDFNode agent = rs.get("agent");			
			String gsisNameS = rs.getLiteral("someName").getString();
			String searchEngName = gf.greeklishGeneratorSearch(gsisNameS.toString());
			Resource agentRes = tempModel.createResource(agent.toString());
			agentRes.addProperty(Ontology.validName, gsisNameS).addProperty(Ontology.validEngName, gf.greeklishGenerator(gsisNameS)).addProperty(Ontology.searchEngName, searchEngName);
			agentRes.addProperty(RDFS.label, tempModel.createLiteral(gsisNameS, "el"))
					.addProperty(RDFS.label, tempModel.createLiteral(gf.greeklishGenerator(gsisNameS), "en"))
					.addProperty(Ontology.searchEngName, searchEngName);
			count++;
		}
		vqe.close();		
		Model remoteModel = new VirtModel(graph);
    	remoteModel.add(tempModel);    	
    	//remoteModel.close();
    	tempModel.close();
    	
    	//Non-gsis agents here
    	String nonGsisQuery = prefixes + "SELECT distinct ?agent where {" +    //This will handle agents with no valid gsis names.
							  "?agent psgr:validAfm \"true\" ." + //It will query for these agents and assign validName properties
							  "?agent psgr:paymentAgentName ?someName . " +
							  "?agent psgr:gsisName ?name ."+ 
							  "OPTIONAL{?agent psgr:validName ?vName}" + //Based on their first agent name.
							  "FILTER(!BOUND(?vName))" +
							  "FILTER(?name=\"Null\")}";				
		VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (nonGsisQuery, graph);
		ResultSet results2 = vqe2.execSelect();
		Model tempModel2 = ModelFactory.createDefaultModel();
		int count2 = 0;
		while(results2.hasNext()){			
			QuerySolution rs2 = results2.nextSolution();
			RDFNode agent = rs2.get("agent");
			String agentQuery = prefixes + "SELECT ?name WHERE {<" + agent.toString().replace(" ", "") + "> psgr:paymentAgentName ?name .} LIMIT 1";
			VirtuosoQueryExecution vqe3 = VirtuosoQueryExecutionFactory.create (agentQuery, graph);			
			ResultSet results3 = vqe3.execSelect();
			String name = null;
			while(results3.hasNext()){			
				QuerySolution rs3 = results3.nextSolution();
				name = rs3.getLiteral("name").getString();
			}
			vqe3.close();
		try{
			Resource agentRes = tempModel2.createResource(agent.toString());
				agentRes.addProperty(Ontology.validName, name).addProperty(Ontology.validEngName, gf.greeklishGenerator(name));
			count2++;
		}catch(Exception e){System.out.println(agent.toString());}
					
		}
		vqe2.close();
		Model remoteModel2 = new VirtModel(graph);
    	remoteModel2.add(tempModel2);    	
    	remoteModel2.close();
    	tempModel2.close();
	}
	
	/*
	 * This is to be run once for all agents
	 */
	public void staticUniqueNames(){
		
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		System.out.println("Connected to Virtuoso.");
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		
		/*String query1 = prefixes + "SELECT ?agent ?someName where {" +    //This will handle agents with valid gsis names.
				   				  "?agent a psgr:PaymentAgent ." + //It will query for these agents and assign validName properties
				   				  "?agent psgr:gsisName ?someName . " +
				   				  "OPTIONAL{?agent psgr:validName ?name}"+ //Based on their gsis name.
				   				  "FILTER(?someName!=\"Null\")" +
				   				  "FILTER(!BOUND(?name))}";		
		//ArrayList<String>
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query1, graph);
		ResultSet results = vqe.execSelect();
		Model tempModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){			
			QuerySolution rs = results.nextSolution();
			RDFNode agent = rs.get("agent");
			//Literal gsisName = rs.getLiteral("someName");
			String gsisNameS = rs.getLiteral("someName").getString();
			Resource agentRes = tempModel.createResource(agent.toString());
			agentRes.addProperty(Ontology.validName, gsisNameS)
			  											.addProperty(Ontology.validEngName, gf.greeklishGenerator(gsisNameS));
			count++;
		}
		vqe.close();*/
		
		System.out.println("Starting second query...");
		String query1 = prefixes + "SELECT distinct ?agent where {" +    //This will handle agents with valid gsis names.
			 				  "?agent psgr:validAfm \"true\" ." + //It will query for these agents and assign validName properties
			 				  "?agent psgr:paymentAgentName ?someName . " +
			 				  "?agent psgr:gsisName ?name ."+ 
			 				  "OPTIONAL{?agent psgr:validName ?vName}" + //Based on their gsis name.
			 				  "FILTER(!BOUND(?vName))" +
			 				  "FILTER(?name=\"Null\")}";
		System.out.println(query1);
		//ArrayList<String>
		VirtuosoQueryExecution vqe2 = VirtuosoQueryExecutionFactory.create (query1, graph);
		ResultSet results2 = vqe2.execSelect();
		Model tempModel2 = ModelFactory.createDefaultModel();
		int count2 = 0;
		while(results2.hasNext()){			
			QuerySolution rs2 = results2.nextSolution();
			RDFNode agent = rs2.get("agent");
			String agentQuery = prefixes + "SELECT ?name WHERE {<" + agent.toString().replace(" ", "") + "> psgr:paymentAgentName ?name .} LIMIT 1";
			VirtuosoQueryExecution vqe3 = VirtuosoQueryExecutionFactory.create (agentQuery, graph);			
			ResultSet results3 = vqe3.execSelect();
			String name = null;
			while(results3.hasNext()){			
				QuerySolution rs3 = results3.nextSolution();
				name = rs3.getLiteral("name").getString();
			}
			vqe3.close();
			try{
				Resource agentRes = tempModel2.createResource(agent.toString());
					agentRes.addProperty(Ontology.validName, name).addProperty(Ontology.validEngName, gf.greeklishGenerator(name));
				count2++;
			}catch(Exception e){System.out.println(agent.toString());}
						
		}
		vqe2.close();
		System.out.println("Did this for " + count2 + " second class agents.");
		
		try {
			//FileOutputStream fos2 = new FileOutputStream("C:/Users/marios/Desktop/validNames1.rdf");
			//tempModel.write(fos2, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/validNames1.rdf");
			FileOutputStream fos2 = new FileOutputStream("C:/Users/marios/Desktop/validNames2.rdf");
			tempModel2.write(fos2, "RDF/XML-ABBREV", "C:/Users/marios/Desktop/validNames2.rdf");
		}catch(Exception e){e.printStackTrace();}
		tempModel2.close();
		graph.close();
	}
}
