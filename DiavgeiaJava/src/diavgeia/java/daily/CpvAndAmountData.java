package diavgeia.java.daily;

public class CpvAndAmountData {
	
	private String cpvCode;
	private String amount;
	private String cpvSubject;
	private String shortName;
	private Integer count;

	public void setShortName(String shortName){
		this.shortName = shortName ;
	}
	
	public String getShortName(){
		return shortName;
	}
	
	public void setCpvCode(String cpvCode){
		this.cpvCode = cpvCode;
	}
	
	public String getCpvCode(){
		return cpvCode;
	}
	
	public void setAmount(String amount){
		this.amount = amount;
	}
	
	public String getAmount(){
		return amount;
	}
	
	public void setCpvSubject(String cpvSubject){
		this.cpvSubject = cpvSubject;
	}
	
	public String getCpvSubject(){
		return cpvSubject;
	}
	
	public void setCount(Integer count){
		this.count = count;
	}
	
	public Integer getCount(){
		return count;
	}
}
