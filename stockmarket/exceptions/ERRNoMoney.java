package stockmarket.exceptions;

@SuppressWarnings("serial")
public class ERRNoMoney extends Exception {

	@Override
	public String toString() {
		return "Error: Not enough resources";
	}
	
}
