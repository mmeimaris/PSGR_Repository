package diavgeia.java.daily;

import java.util.HashMap;

public class GSISNames {
	
	private HashMap<String, String> hashmap;

	public void setName(String afm, String name){
		hashmap.put(afm, name);
	}
	
	public HashMap<String, String> getNamesMap(){
		return hashmap;
	}
	
}
