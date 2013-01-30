package diavgeia.java.daily;

import java.util.HashMap;

public class GreeklishFactory {
		
	HashMap<String, String> conversionMap;
	public GreeklishFactory(){
		
		conversionMap = new HashMap<String, String>();
		conversionMap.put("α", "a");
		conversionMap.put("β", "b");
		conversionMap.put("γ", "g");
		conversionMap.put("δ", "d");
		conversionMap.put("ε", "e");
		conversionMap.put("ζ", "z");
		conversionMap.put("η", "i");
		conversionMap.put("θ", "th");
		conversionMap.put("ι", "i");
		conversionMap.put("κ", "k");
		conversionMap.put("λ", "l");
		conversionMap.put("μ", "m");
		conversionMap.put("ν", "n");
		conversionMap.put("ξ", "ks");
		conversionMap.put("ο", "o");
		conversionMap.put("π", "p");
		conversionMap.put("ρ", "r");
		conversionMap.put("σ", "s");
		conversionMap.put("τ", "t");
		conversionMap.put("υ", "y");
		conversionMap.put("φ", "f");
		conversionMap.put("χ", "h");
		conversionMap.put("ψ", "ps");
		conversionMap.put("ω", "o");
		conversionMap.put("Α", "A");
		conversionMap.put("Β", "B");
		conversionMap.put("Γ", "G");
		conversionMap.put("Δ", "D");
		conversionMap.put("Ε", "E");
		conversionMap.put("Ζ", "Z");
		conversionMap.put("Η", "I");
		conversionMap.put("Θ", "TH");
		conversionMap.put("Ι", "I");
		conversionMap.put("Κ", "K");
		conversionMap.put("Λ", "L");
		conversionMap.put("Μ", "M");
		conversionMap.put("Ν", "N");
		conversionMap.put("Ξ", "KS");
		conversionMap.put("Ο", "O");
		conversionMap.put("Π", "P");
		conversionMap.put("Ρ", "R");
		conversionMap.put("Σ", "S");
		conversionMap.put("Τ", "T");
		conversionMap.put("Υ", "Y");
		conversionMap.put("Φ", "F");
		conversionMap.put("Χ", "H");
		conversionMap.put("Ψ", "PS");
		conversionMap.put("Ω", "O");
	}
	
	public String getChar(String charS){
		String str = charS;		
		str = conversionMap.get(charS);			
		if(str==null)
			return charS;
		else
			return str;
		
		
	}
	
	public String greeklishGenerator(String greek){
		
		GreeklishFactory gf = new GreeklishFactory();
		String returnS = "";
		for (int i = 0; i < greek.length(); i++){
		    char c = greek.charAt(i);		    
		    returnS += gf.getChar(Character.toString(c));
		}
		return returnS;
	}
			
	
	

}
