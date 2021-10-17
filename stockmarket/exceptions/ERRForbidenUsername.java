package stockmarket.exceptions;

@SuppressWarnings("serial")
public class ERRForbidenUsername extends Exception {

	@Override
	public String toString() {
		return "Error: Only alphanumeric characters allowed";
	}
	
}
