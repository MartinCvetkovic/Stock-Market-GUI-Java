package stockmarket;

import stockmarket.exceptions.ERRRegex;

public abstract class Parser {

	public abstract void parseFile(Stock stock) throws ERRRegex;
	public abstract double parseCurrentPrice() throws ERRRegex;
	
}
