package stockmarket.exceptions;

@SuppressWarnings("serial")
public class ERRExistingUser extends Exception {

	@Override
	public String toString() {
		return "Error: Existing username";
	}
	
}
