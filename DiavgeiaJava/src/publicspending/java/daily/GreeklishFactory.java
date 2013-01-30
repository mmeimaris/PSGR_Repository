package publicspending.java.daily;

import java.util.HashMap;
import java.util.HashSet;

public class GreeklishFactory {
		
	HashMap<String, String> conversionMapISO, conversionMapSearch;
	/*HashMap<String, String> conversionMapUNELOT;
	HashSet<String> vowels; */
	public GreeklishFactory(){
		
		initializeISO();
		initializeSearch();
		/*initializeUNELOT();
		vowels = new HashSet<String>();
		vowels.add("α");
		vowels.add("ε");
		vowels.add("η");
		vowels.add("ι");
		vowels.add("υ");
		vowels.add("ο");
		vowels.add("ω");*/
		
	}
	
	public String getChar(String charS){
		String str = charS;		
		str = conversionMapISO.get(charS);			
		if(str==null)
			return charS;
		else
			return str;
		
		
	}
	
	public String getCharSearch(String charS){
		String str = charS;		
		str = conversionMapSearch.get(charS);			
		if(str==null)
			return charS;
		else
			return str;
		
		
	}
	
	public String greeklishGenerator(String greek){
				
		String returnS = "";
		for (int i = 0; i < greek.length(); i++){
			String doub = null;
			if(i<greek.length()-1){
				doub = greek.substring(i,i+2);
				//System.out.println(doub);
				String doub2 = conversionMapISO.get(doub);
				if(doub2!=null){
					returnS += doub2;
					i++;
					continue;
				}					
			}
		    char c = greek.charAt(i);		    
		    returnS += getChar(Character.toString(c));
		}
		return returnS;
	}
	public String greeklishGeneratorSearch(String greek){
		
		String returnS = "";
		for (int i = 0; i < greek.length(); i++){
			String doub = null;
			if(i<greek.length()-1){
				doub = greek.substring(i,i+2);
				//System.out.println(doub);
				String doub2 = conversionMapSearch.get(doub);
				if(doub2!=null){
					returnS += doub2;
					i++;
					continue;
				}					
			}
		    char c = greek.charAt(i);		    
		    returnS += getCharSearch(Character.toString(c));
		}
		return returnS;
	}
			
	public void initializeISO(){
		conversionMapISO = new HashMap<String, String>();
		conversionMapISO.put("α", "a");
		conversionMapISO.put("β", "v");
		conversionMapISO.put("γ", "g");
		conversionMapISO.put("δ", "d");
		conversionMapISO.put("ε", "e");
		conversionMapISO.put("ζ", "z");
		conversionMapISO.put("η", "ī");
		conversionMapISO.put("θ", "th");
		conversionMapISO.put("ι", "i");
		conversionMapISO.put("κ", "k");
		conversionMapISO.put("λ", "l");
		conversionMapISO.put("μ", "m");
		conversionMapISO.put("ν", "n");
		conversionMapISO.put("ξ", "x");	
		conversionMapISO.put("ο", "o");
		conversionMapISO.put("π", "p");
		conversionMapISO.put("ρ", "r");
		conversionMapISO.put("σ", "s");
		conversionMapISO.put("τ", "t");
		conversionMapISO.put("υ", "y");
		conversionMapISO.put("φ", "f");
		conversionMapISO.put("χ", "ch");
		conversionMapISO.put("ψ", "ps");
		conversionMapISO.put("ω", "ō");			
		conversionMapISO.put("Α", "A");
		conversionMapISO.put("Β", "V");
		conversionMapISO.put("Γ", "G");
		conversionMapISO.put("Δ", "D");
		conversionMapISO.put("Ε", "E");
		conversionMapISO.put("Ζ", "Z");
		conversionMapISO.put("Η", "Ī");
		conversionMapISO.put("Θ", "TH");
		conversionMapISO.put("Ι", "I");
		conversionMapISO.put("Κ", "K");
		conversionMapISO.put("Λ", "L");
		conversionMapISO.put("Μ", "M");
		conversionMapISO.put("Ν", "N");
		conversionMapISO.put("Ξ", "X");
		conversionMapISO.put("Ο", "O");
		conversionMapISO.put("Π", "P");
		conversionMapISO.put("Ρ", "R");
		conversionMapISO.put("Σ", "S");
		conversionMapISO.put("Τ", "T");
		conversionMapISO.put("Υ", "Y");
		conversionMapISO.put("Φ", "F");
		conversionMapISO.put("Χ", "CH");
		conversionMapISO.put("Ψ", "PS");
		conversionMapISO.put("Ω", "Ō");
		conversionMapISO.put("αυ", "au");
		conversionMapISO.put("ΑΥ", "AU");
		conversionMapISO.put("ευ", "eu");
		conversionMapISO.put("ΕΥ", "EU");
		conversionMapISO.put("ου", "ou");
		conversionMapISO.put("ΟΥ", "OU");
	}
	
	public void initializeSearch(){
		conversionMapSearch = new HashMap<String, String>();
		conversionMapSearch.put("α", "a");
		conversionMapSearch.put("β", "v");
		conversionMapSearch.put("γ", "g");
		conversionMapSearch.put("δ", "d");
		conversionMapSearch.put("ε", "e");
		conversionMapSearch.put("ζ", "z");
		conversionMapSearch.put("η", "i");
		conversionMapSearch.put("θ", "th");
		conversionMapSearch.put("ι", "i");
		conversionMapSearch.put("κ", "k");
		conversionMapSearch.put("λ", "l");
		conversionMapSearch.put("μ", "m");
		conversionMapSearch.put("ν", "n");
		conversionMapSearch.put("ξ", "x");	
		conversionMapSearch.put("ο", "o");
		conversionMapSearch.put("π", "p");
		conversionMapSearch.put("ρ", "r");
		conversionMapSearch.put("σ", "s");
		conversionMapSearch.put("τ", "t");
		conversionMapSearch.put("υ", "y");
		conversionMapSearch.put("φ", "f");
		conversionMapSearch.put("χ", "ch");
		conversionMapSearch.put("ψ", "ps");
		conversionMapSearch.put("ω", "o");			
		conversionMapSearch.put("Α", "A");
		conversionMapSearch.put("Β", "V");
		conversionMapSearch.put("Γ", "G");
		conversionMapSearch.put("Δ", "D");
		conversionMapSearch.put("Ε", "E");
		conversionMapSearch.put("Ζ", "Z");
		conversionMapSearch.put("Η", "I");
		conversionMapSearch.put("Θ", "TH");
		conversionMapSearch.put("Ι", "I");
		conversionMapSearch.put("Κ", "K");
		conversionMapSearch.put("Λ", "L");
		conversionMapSearch.put("Μ", "M");
		conversionMapSearch.put("Ν", "N");
		conversionMapSearch.put("Ξ", "X");
		conversionMapSearch.put("Ο", "O");
		conversionMapSearch.put("Π", "P");
		conversionMapSearch.put("Ρ", "R");
		conversionMapSearch.put("Σ", "S");
		conversionMapSearch.put("Τ", "T");
		conversionMapSearch.put("Υ", "Y");
		conversionMapSearch.put("Φ", "F");
		conversionMapSearch.put("Χ", "CH");
		conversionMapSearch.put("Ψ", "PS");
		conversionMapSearch.put("Ω", "O");
		conversionMapSearch.put("αυ", "au");
		conversionMapSearch.put("ΑΥ", "AU");
		conversionMapSearch.put("ευ", "eu");
		conversionMapSearch.put("ΕΥ", "EU");
		conversionMapSearch.put("ου", "ou");
		conversionMapSearch.put("ΟΥ", "OU");
	}

}
