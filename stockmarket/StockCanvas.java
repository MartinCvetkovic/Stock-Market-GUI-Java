package stockmarket;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import stockmarket.exceptions.ERRInvalidValue;

@SuppressWarnings("serial")
public class StockCanvas extends Canvas {

	private Stock stock;
	private int shownCandles, offset;
	private boolean ma, ema;
	private List<Double> maRes, emaRes;
	private double high, low;

	public StockCanvas() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()){
					case KeyEvent.VK_ADD:{
						if(shownCandles == 1) break;
						shownCandles--;
						StockCanvas.this.repaint();
						break;
					}
					case KeyEvent.VK_SUBTRACT:{
						if(stock == null || offset + shownCandles >= stock.getCandles().size()) break;
						shownCandles++;
						StockCanvas.this.repaint();
						break;
					}
					case KeyEvent.VK_LEFT:{
						if(offset == 0) break;
						offset--;
						StockCanvas.this.repaint();
						break;
					}
					case KeyEvent.VK_RIGHT:{
						if(stock == null || offset + shownCandles >= stock.getCandles().size()) break;
						offset++;
						StockCanvas.this.repaint();
						break;
					}
				}
			}
		});
	}

	public void setStock(Stock stock) {
		this.stock = stock;
		if(50 > stock.getCandles().size())
			shownCandles = stock.getCandles().size();
		else
			shownCandles = 50;
	}

	public void setMa(boolean ma, int interval) throws ERRInvalidValue {
		if(stock == null) return;
		this.ma = ma;
		Indicator i = new MAIndicator();
		i.setInterval(interval);
		i.calculateIndicator(stock);
		maRes = i.getResult();
	}

	public void setEma(boolean ema, int interval) throws ERRInvalidValue {
		if(stock == null) return;
		this.ema = ema;
		Indicator i = new EMAIndicator();
		i.setInterval(interval);
		i.calculateIndicator(stock);
		emaRes = i.getResult();
	}

	@Override
	public void paint(Graphics g) {
		if(stock == null) return;
		
		List<Candle> currentCandles = new ArrayList<>();
		for(int i = offset; i < shownCandles + offset; i++) {
			if(i >= stock.getCandles().size()) break;
			currentCandles.add(stock.getCandles().get(i));
		}
		
		double[] higha = {-1}, lowa = {Double.MAX_VALUE};
		currentCandles.forEach(c -> {
			if(c.getHigh() > higha[0])
				higha[0] = c.getHigh();
			if(c.getLow() < lowa[0])
				lowa[0] = c.getLow();
		});
		
		high = higha[0];
		low = lowa[0];
		int width = getWidth() / shownCandles, x = 0;
		for(Candle candle: currentCandles) {
			if(candle.getOpen() > candle.getClose()) {
				g.setColor(Color.RED);
				g.fillRect(x, getPosition(candle.getOpen(), high, low),
						width, getPosition(candle.getClose(), high, low) - getPosition(candle.getOpen(), high, low));
			}
			else {
				g.setColor(Color.GREEN);
				g.fillRect(x, getPosition(candle.getClose(), high, low),
						width, getPosition(candle.getOpen(), high, low) - getPosition(candle.getClose(), high, low));
			}
			
			g.drawLine(x + width / 2, getPosition(candle.getHigh(), high, low),
					x + width / 2, getPosition(candle.getLow(), high, low));
			x += width;
		}
		
		if(ma) {
			g.setColor(Color.BLUE);
			x = 0;
			double prev = maRes.get(0), curr;
			for(int i = 1; i < maRes.size(); i++) {
				curr = maRes.get(i);
				g.drawLine(x + width / 2, getPosition(prev, high, low), x + 3 * width / 2, getPosition(curr, high, low));
				x += width;
				prev = curr;
			}
		}
		
		if(ema) {
			g.setColor(Color.BLACK);
			x = 0;
			double prev = emaRes.get(0), curr;
			for(int i = 1; i < emaRes.size(); i++) {
				curr = emaRes.get(i);
				g.drawLine(x + width / 2, getPosition(prev, high, low), x + 3 * width / 2, getPosition(curr, high, low));
				x += width;
				prev = curr;
			}
		}
		
		
	}
	
	public int getShownCandles() {
		return shownCandles;
	}

	public int getOffset() {
		return offset;
	}

	private int getPosition(double parameter, double high, double low) {
		return getHeight() - (int)((parameter - low) / (high - low) * getHeight());
	}
	
	/*private double getParameter(int position, double high, double low) {
		return (high - low) * (getHeight() - position) / getHeight() + low;
	}*/
	
}
