package stockmarket;

public class Candle {

	private double open, close, high, low;
	private long timestamp;

	public Candle(long t, double o, double c, double h, double l){
		open = o;
		close = c;
		high = h;
		low = l;
		timestamp = t;
	}

	public double getOpen() {
		return open;
	}
	
	public double getClose() {
		return close;
	}
	
	public double getHigh() {
		return high;
	}
	
	public double getLow() {
		return low;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "O:" + open + " C:" + close + " H:" + high + " L:" + low + " T:"	+ timestamp;
	}
	
	
	
}
