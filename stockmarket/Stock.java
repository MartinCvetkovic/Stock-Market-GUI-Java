package stockmarket;

import java.util.ArrayList;
import java.util.List;

public class Stock {

	@SuppressWarnings("unused")
	private String name;
	@SuppressWarnings("unused")
	private long startPeriod, endPeriod;
	private List<Candle> candles = new ArrayList<>();

	public Stock(String name, long startPeriod, long endPeriod) {
		this.name = name;
		this.startPeriod = startPeriod;
		this.endPeriod = endPeriod;
	}
	
	public List<Candle> getCandles() {
		return candles;
	}
	
}
