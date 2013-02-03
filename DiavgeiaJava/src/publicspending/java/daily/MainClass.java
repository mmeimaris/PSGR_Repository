package publicspending.java.daily;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		//RoutineInvoker ri = new RoutineInvoker(args, false, "JSON");
		RoutineInvoker ri = new RoutineInvoker(args, false, "XML");
		//RoutineInvoker ri = new RoutineInvoker(args, false, "DB");
		//RoutineInvoker ri = new RoutineInvoker(args, true);		
	}

}