package publicspending.java.daily;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DiavgeiaDBAccess {
	private Connection connect = null;
	  private Statement statement = null;
	  private PreparedStatement preparedStatement = null;
	  private ResultSet resultSet = null;
	  String yesterday, today;
	  
	  public DiavgeiaDBAccess(String yesterday, String today){
		  this.yesterday = yesterday;
		  this.today = today;
	  }
	  
	  public ArrayList<HashMap<String,String>> readDataBase() throws Exception {
	    try {
	      // This will load the MySQL driver, each DB has its own driver
	      Class.forName("com.mysql.jdbc.Driver");
	      // Setup the connection with the DB
	      connect = DriverManager
	          .getConnection("jdbc:mysql://dbo8.diavgeia.gov.gr/apofaseis?&useUnicode=true&characterEncoding=utf-8&user=opendata&password=opendata@diavgeia&zeroDateTimeBehavior=round");
	      //.getConnection("jdbc:mysql://dbo8.diavgeia.gov.gr/apofaseis?user=opendata&password=opendata@diavgeia");

	      // Statements allow to issue SQL queries to the database
	      statement = connect.createStatement();
	      // Result set get the result of the SQL query
	      String query = "SELECT * FROM apofaseis INNER JOIN apofaseis_dynamic_fields_values ON apofaseis.ada=apofaseis_dynamic_fields_values.ada WHERE eidos_apofasis=27 AND submission_timestamp>='"+yesterday+"' AND submission_timestamp<'"+today+"'";
	      System.out.println(query);
	      resultSet = statement
	          .executeQuery(query);
	      return writeResultSet(resultSet);

	      // PreparedStatements can use variables and are more efficient
	     /* preparedStatement = connect
	          .prepareStatement("insert into  FEEDBACK.COMMENTS values (default, ?, ?, ?, ? , ?, ?)");
	      // "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
	      // Parameters start with 1
	      preparedStatement.setString(1, "Test");
	      preparedStatement.setString(2, "TestEmail");
	      preparedStatement.setString(3, "TestWebpage");
	      preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
	      preparedStatement.setString(5, "TestSummary");
	      preparedStatement.setString(6, "TestComment");
	      preparedStatement.executeUpdate();*/

	     /* preparedStatement = connect
	          .prepareStatement("SELECT myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
	      resultSet = preparedStatement.executeQuery();
	      writeResultSet(resultSet);*/

	      // Remove again the insert comment
	     /* preparedStatement = connect
	      .prepareStatement("delete from FEEDBACK.COMMENTS where myuser= ? ; ");
	      preparedStatement.setString(1, "Test");
	      preparedStatement.executeUpdate();
	      
	      resultSet = statement
	      .executeQuery("select * from FEEDBACK.COMMENTS");
	      writeMetaData(resultSet);*/
	      
	    } catch (Exception e) { e.printStackTrace();
	      throw e;
	    } finally {
	      close();
	    }

	  }

	  private ArrayList<HashMap<String,String>> writeResultSet(ResultSet resultSet) throws SQLException {
	    // ResultSet is initially before the first data set
		  int columns = resultSet.getMetaData().getColumnCount();
		  ArrayList<HashMap<String,String>> decisionList = new ArrayList<HashMap<String, String>>();
		  StringBuilder message = new StringBuilder();		  
		  String previousAda = "";
		  Decision decision = null;
		  while (resultSet.next()) {			  
			  String ada= resultSet.getString("ada");
			  if(ada.equals(previousAda)){
				//Eimaste sthn idia apofash, se allo row  
				  String dynamic_field_value = resultSet.getString("field_value");
				  String dynamic_field_id = resultSet.getString("dynamic_field_ID");				  
				  if(dynamic_field_id.equals("12")) decision.setPayerName(dynamic_field_value);
				  else if(dynamic_field_id.equals("21")) decision.setPayeeAfm(dynamic_field_value);
				  else if(dynamic_field_id.equals("22")) decision.setPayeeName(dynamic_field_value);
				  else if(dynamic_field_id.equals("31")) decision.setDescription(dynamic_field_value);
				  else if(dynamic_field_id.equals("41")) decision.setAmount(dynamic_field_value);
				  else if(dynamic_field_id.equals("61")) decision.setCpv(dynamic_field_value);
				  else if(dynamic_field_id.equals("109")) decision.setKae(dynamic_field_value);
				  else if(dynamic_field_id.equals("110")) decision.setPaymentCategory(dynamic_field_value);
				  /*System.out.println("dynamic_field_value: " + dynamic_field_value);
				  System.out.println("dynamic_field_ID: " + dynamic_field_id);*/
				  
			  }
			  else{
				//Nea apofasi
				  if(decision!=null) decisionList.add(createHashMap(decision));
				  decision = new Decision();			  			      
			      String arithmos_protokolou = resultSet.getString("arithmos_protokolou");
			      String apofasi_date = resultSet.getString("apofasi_date");
			      String dynamic_field_value = resultSet.getString("field_value");			      
				  String dynamic_field_id = resultSet.getString("dynamic_field_ID");
			      String koinopoiiseis = resultSet.getString("koinopoiiseis");
			      String eidos_apofasis = resultSet.getString("eidos_apofasis");
			      String thematiki = resultSet.getString("thematiki");
			      String submission_timestamp = resultSet.getString("submission_timestamp");
			      String subject = resultSet.getString("thema");
			      String ypomonada = resultSet.getString("monada");
			      String monada = resultSet.getString("lastlevel");
			      String signer = resultSet.getString("telikos_ypografwn");
			      String correction = resultSet.getString("is_orthi_epanalipsi");
			      //String tags = resultSet.getString("tags");			      
			      String fekNumber = resultSet.getString("ET_FEK");
			      String fekIssue = resultSet.getString("ET_FEK_tefxos");
			      String fekYear = resultSet.getString("ET_FEK_etos");
			      String relatedAda = resultSet.getString("related_ADAs");
			      
			      if(dynamic_field_id.equals("11")) decision.setPayerAfm(dynamic_field_value);
			      decision.setAda(ada);
			      decision.setProtocolNumber(arithmos_protokolou);
			      decision.setDate(apofasi_date);
			      decision.setDecisionTypeId(eidos_apofasis);
			      decision.setThematiki(thematiki);
			      decision.setSubmissionTimestamp(submission_timestamp);
			      decision.setSubject(subject);
			      decision.setOrgId(monada);
			      decision.setOrgUnitId(ypomonada);
			      decision.setSignerId(signer);
			      decision.setCorrectionOfAda(correction);
			      decision.setFekIssue(fekIssue);
			      decision.setFekNumber(fekNumber);
			      decision.setFekYear(fekYear);
			      decision.setRelativeAda(relatedAda);
			      decision.setUrl("http://et.diavgeia.gov.gr/f/all/ada/"+ada);
			      decision.setDocumentUrl("http://static.diavgeia.gov.gr/doc/"+ada);
			      decision.setTagArray("");
			      //decision.setTagArray(tagArray);
			      /*System.out.println("ada: " + ada);
			      System.out.println("protocol: " + arithmos_protokolou);
			      System.out.println("apofasi_date: " + apofasi_date);
			      System.out.println("decision type id: " + eidos_apofasis);
			      System.out.println("thematiki: " + thematiki);
			      System.out.println("submission timestamp: " + submission_timestamp);
			      System.out.println("dynamic_field_value: " + dynamic_field_value);
			      System.out.println("subject : " + subject);
			      System.out.println("monada id: " + monada);
			      System.out.println("ypomonada id: " + ypomonada);
			      System.out.println("signer id:" + signer);
			      System.out.println("correction : " + correction);*/
			      previousAda = ada;
			  }
	    }
		  return decisionList;
	  }
	  
	  public HashMap<String, String> createHashMap(Decision decision){
			HashMap<String, String> decisionMap = new HashMap<String, String>();
			decisionMap.put("ada", decision.getAda());
	    	decisionMap.put("isCorrectionOfAda", decision.getCorrectionOfAda());
	    	decisionMap.put("relativeAda", decision.getRelativeAda());
	    	decisionMap.put("submissionTimestamp", decision.getSubmissionTimestamp());
	    	decisionMap.put("url", decision.getUrl());
	    	decisionMap.put("documentUrl", decision.getDocumentUrl());
	    	decisionMap.put("protocolNumber", decision.getProtocolNumber());
	    	decisionMap.put("decisionType", decision.getDecisionTypeId());
	    	decisionMap.put("subject", decision.getSubject());
	    	decisionMap.put("date", decision.getDate());
	    	decisionMap.put("organization", "");
	    	decisionMap.put("organizationUID", decision.getOrgId());
	    	decisionMap.put("organizationUnit", "");
	    	decisionMap.put("organizationUnitUID", decision.getOrgUnitId());
	    	decisionMap.put("FEK number", decision.getFekNumber());
	    	decisionMap.put("FEK issue", decision.getFekIssue());
	    	decisionMap.put("FEK year", decision.getFekYear());
	    	decisionMap.put("tagLabels", "");
	    	decisionMap.put("tagUIDs", "");
	    	decisionMap.put("ΕΠΩΝΥΜΙΑ ΦΟΡΕΑ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)", decision.getPayerName());
	    	decisionMap.put("ΑΦΜ ΦΟΡΕΑ", decision.getPayerAfm());
	    	decisionMap.put("ΕΠΩΝΥΜΙΑ ΑΝΑΔΟΧΟΥ (ΚΕΦΑΛΑΙΑ ΕΛΛΗΝΙΚΑ)", decision.getPayeeName());
	    	decisionMap.put("ΑΦΜ ΑΝΑΔΟΧΟΥ", decision.getPayeeAfm());
	    	decisionMap.put("hasVAT", "");
	    	decisionMap.put("isValidVAT", "");
	    	decisionMap.put("CPV Description", decision.getCpv());
	    	decisionMap.put(" CPV Number", "");
	    	decisionMap.put("ΣΧΕΤΙΚΟΣ ΑΡΙΘΜΟΣ ΚΑΕ", decision.getKae());
	    	decisionMap.put("ΚΑΤΗΓΟΡΙΑ ΔΑΠΑΝΗΣ", decision.getPaymentCategory());
	    	decisionMap.put("ΠΟΣΟ ΔΑΠΑΝΗΣ / ΣΥΝΑΛΛΑΓΗΣ (ΜΕ ΦΠΑ)", decision.getAmount());
	    	decisionMap.put("ΠΕΡΙΓΡΑΦΗ ΔΑΠΑΝΗΣ", decision.getDescription());
	    	decisionMap.put("signer", decision.getSignerId());
	    	decisionMap.put("tagArray", decision.getTagArray());
	    	return decisionMap;
		}
	  // You need to close the resultSet
	  //NO. YOU need to close YOUR resultSet.
	  private void close() {
	    try {
	      if (resultSet != null) {
	        resultSet.close();
	      }

	      if (statement != null) {
	        statement.close();
	      }

	      if (connect != null) {
	        connect.close();
	      }
	    } catch (Exception e) {

	    }
	  }
}
