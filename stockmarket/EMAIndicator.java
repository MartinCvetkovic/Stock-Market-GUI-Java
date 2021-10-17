package stockmarket;

import java.util.ArrayList;
import java.util.List;

import stockmarket.exceptions.ERRInvalidValue;

public class EMAIndicator extends Indicator {

	private double EMA(List<Candle> candles, int n) {
		double sum = 0;
		if(n == 1) return candles.get(n - 1).getClose();
		sum += candles.get(n - 1).getClose() * 2. / (n + 1.) + EMA(candles, n - 1) * (1. - 2. / (n + 1.));
		return sum;
	}
	
	@Override
	public void calculateIndicator(Stock stock) throws ERRInvalidValue {
		if (interval > stock.getCandles().size()) throw new ERRInvalidValue();
		int n = interval;
		double sum;
		List<Candle> candles = new ArrayList<>(), list = new ArrayList<>();
		List<Double> tempRes = new ArrayList<>();
		for (int i = stock.getCandles().size() - 1; i >= 0; i--) {
            list.add(stock.getCandles().get(i));
        }
		for (int i = 0; i < list.size(); i++) {
			if (i + n > list.size()) n--;
			for (int j = i; j < i + n; j++) {
				candles.add(list.get(j));
			}
			sum = EMA(candles, n);
			tempRes.add(sum);
			candles.clear();
		}
		for (int i = tempRes.size() - 1; i >= 0; i--) {
            result.add(tempRes.get(i));
        }
	}

}
