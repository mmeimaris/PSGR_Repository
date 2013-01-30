package publicspending.java.control;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class EnglishNames {
	
	public EnglishNames(){
		
	}
	
	public void setEnglishNames(){
		String connectionString = "jdbc:virtuoso://192.168.151.5:1111/autoReconnect=true/charset=UTF-8/log_enable=2";
		System.out.println("Connected.");
		VirtGraph graph = new VirtGraph ("http://publicspending.medialab.ntua.gr/Decisions", connectionString, "marios", "dirtymarios");
		String prefixes = "PREFIX psgr:<http://publicspending.medialab.ntua.gr/ontology#> " +
				   "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				   "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
				   "PREFIX cpv: <http://www.e-nvision.org/ontologies/CPVOntology.owl#> " +
				   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";
		String query = prefixes + "SELECT ?agent where {" +
				   				  "?agent a psgr:PaymentAgent ." +
				   				  "OPTIONAL{?agent psgr:engName ?name}"+
				   				  "FITLER(!BOUND(?name))}";
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		ResultSet results = vqe.execSelect();
		Model tempModel = ModelFactory.createDefaultModel();
		int count = 0;
		while(results.hasNext()){			
			QuerySolution rs = results.nextSolution();
			RDFNode agent = rs.get("agent");
		}
		vqe.close();
		tempModel.close();
		graph.close();
	}
}
