package stockmarket;

import java.util.ArrayList;
import java.util.List;

import stockmarket.exceptions.ERRInvalidValue;

public class MAIndicator extends Indicator {

	@Override
	public void calculateIndicator(Stock stock) throws ERRInvalidValue {
		if (interval > stock.getCandles().size())
			throw new ERRInvalidValue();
		int n = interval;
		List<Candle> list = new ArrayList<>();
		//List<Double> resList = new ArrayList<>();
		for (int i = stock.getCandles().size() - 1; i >= 0; i--) {
            list.add(stock.getCandles().get(i));
        }
		for (int i = 0; i < list.size(); i++) {
			double sum = 0;
			if (i + n > stock.getCandles().size()) n--;
			for (int j = i; j < i + n; j++) {
				sum += stock.getCandles().get(j).getClose();
			}
			sum /= n;
			result.add(sum);
		}
		/*for (int i = resList.size() - 1; i >= 0; i--) {
            result.add(resList.get(i));
        }*/
	}

}
