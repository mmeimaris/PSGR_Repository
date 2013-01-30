package publicspending.java.daily;

public class PaymentAgent {
		
	private String paymentAgentName = null;
	private String englishName = null;
	private String greekShortName = null;
	private String engShortName = null;
	private String sum = null;
	private String color = null;
	
	public PaymentAgent(){
		
	}
		
	
	public PaymentAgent setPaymentAgentName(String paymentAgentName){
		this.paymentAgentName = paymentAgentName;
		return this;
	}
	
	public String getPaymentAgentName(){
		return paymentAgentName;
	}
	
	public PaymentAgent setEnglishName(String englishName){
		this.englishName = englishName;
		return this;
	}
	
	public String getEnglishName(){
		return englishName;
	}
	
	public PaymentAgent setGreekShortName(String greekShortName){
		this.greekShortName = greekShortName;
		return this;
	}
	
	public String getGreekShortName(){
		return greekShortName;
	}
	
	public PaymentAgent setEngShortName(String engShortName){
		this.engShortName = engShortName;
		return this;
	}
	
	public String getEngShortName(){
		return engShortName;
	}
	
	public PaymentAgent setSum(String sum){
		this.sum = sum;
		return this;
	}
	
	public String getSum(){
		return sum;
	}
	
	public PaymentAgent setColor(String color){
		this.color = color;
		return this;
	}
	
	public String getColor(){
		return color;
	}

	
	
}
