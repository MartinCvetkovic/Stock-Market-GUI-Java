package stockmarket;

import java.util.ArrayList;
import java.util.List;

import stockmarket.exceptions.ERRInvalidValue;

public abstract class Indicator {

	protected List<Double> result = new ArrayList<>();
	protected int interval;
	
	public void setInterval(int interval) {
		this.interval = interval;
	}

	public List<Double> getResult() {
		return result;
	}

	public abstract void calculateIndicator(Stock stock) throws ERRInvalidValue;

};
