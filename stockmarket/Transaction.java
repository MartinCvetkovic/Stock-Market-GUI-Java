package stockmarket;

import java.sql.Timestamp;

import stockmarket.exceptions.ERRInvalidValue;
import stockmarket.exceptions.ERRRegex;

public class Transaction {

	private int ID;
	private String stockSymbol;
	private double amount, buyingCost, currentCost = 0;

	public Transaction(int i, String sym, double a, double buy) {
		ID = i;
		stockSymbol = sym;
		amount = a;
		buyingCost = buy;
	}

	public int getID() {
		return ID;
	}
	
	public String getSymbol() {
		return stockSymbol;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public double getBuyCost() {
		return buyingCost;
	}

	public double getCurrentCost() throws ERRRegex {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Crawler crawler = new Crawler();
		Parser parser = new JSONParser();
		crawler.collectData(stockSymbol, timestamp.getTime() / 1000 - 1, timestamp.getTime() / 1000);
		currentCost = parser.parseCurrentPrice();
		return currentCost;
	}

	public double absoluteDifference() {
		return currentCost - buyingCost;
	}
	public double relativeDifference() {
		return 100. * (currentCost - buyingCost) / buyingCost;
	}

	public void sell(double amount) throws ERRInvalidValue {
		if (getAmount() < amount || amount <= 0) throw new ERRInvalidValue();
		this.amount -= amount;
	}
	
	public void buy(double amount, double currCost) throws ERRInvalidValue {
		if (amount <= 0) throw new ERRInvalidValue();
		this.amount += amount;
		this.buyingCost = currCost;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ID).append(" ").append(stockSymbol).append(" ").append(amount).append(" ").append(buyingCost);
		return sb.toString();
	}
	
}
