package stockmarket;

public class Crawler {

	static {
		System.loadLibrary("libcurl-x64");
		System.loadLibrary("CRAWLER");
	}
	
	public native void collectData(String symbol, long start, long end);
	
}
