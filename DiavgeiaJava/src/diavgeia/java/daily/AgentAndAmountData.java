package diavgeia.java.daily;

public class AgentAndAmountData {
	
	private String payer;
	private String amount;
	private String gsisName;
	private String pName;
	private String afm;
	private Integer count;
	

	public void setPayer(String payer){
		this.payer = payer;
	}
	
	public String getPayer(){
		return payer;
	}
	
	public void setAmount(String amount){
		this.amount = amount;
	}
	
	public String getAmount(){
		return amount;
	}
	
	public void setGsisName(String gsisName){
		this.gsisName = gsisName;
	}
	
	public String getGsisName(){
		return gsisName;
	}
	
	public void setAfm(String afm){
		this.afm = afm;
	}
	
	public String getAfm(){
		return afm;
	}
	
	public void setCount(Integer count){
		this.count = count;
	}
	
	public Integer getCount(){
		return count;
	}
}
