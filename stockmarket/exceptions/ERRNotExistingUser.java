package stockmarket.exceptions;

@SuppressWarnings("serial")
public class ERRNotExistingUser extends Exception {
	
	@Override
	public String toString() {
		return "Error: Wrong username or password";
	}
	
}
