package stockmarket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stockmarket.exceptions.ERRRegex;

public class JSONParser extends Parser {

	@Override
	public void parseFile(Stock stock) throws ERRRegex {
		String textLine = "";
		try {
		BufferedReader br = new BufferedReader(new FileReader("./crawlerData/curl_data.json"));
		textLine = br.readLine();
		br.close();
		} catch (Exception e) {}
		
		String timestamps = "", data = "", open = "", close = "", high = "", low = "";
		Pattern pattern = Pattern.compile(".*\"timestamp\":\\[([^\\]]*)\\].*\"quote\":\\[\\{([^}]*).*");
		Matcher matcher = pattern.matcher(textLine);

		if(matcher.matches()) {
			timestamps = matcher.group(1) + ",";
			data = matcher.group(2);
		}
		else
			throw new ERRRegex();
		
		String name, values;

		for (int i = 0; i < 5; i++) {
			pattern = Pattern.compile(",*\"([^\"]*)\":\\[([^\\]]*)\\](.*)");
			matcher = pattern.matcher(data);
			if (matcher.matches()) {
				name = matcher.group(1);
				values = matcher.group(2);
				data = matcher.group(3);
				if (name.equals("open")) {
					open = values + ",";
				}
				else if (name.equals("close")) {
					close = values + ",";
				}
				else if (name.equals("high")) {
					high = values + ",";
				}
				else if (name.equals("low")) {
					low = values + ",";
				}
			}
			else
				throw new ERRRegex();
		}
		
		long timestamp = 0;
		double opend = 0, highd = 0, lowd = 0, closed = 0;
		String str;

		while (!timestamps.equals("")) {
			
			pattern = Pattern.compile("([^,]*),(.*)");
			matcher = pattern.matcher(timestamps);
			if (matcher.matches()) {
				str = matcher.group(1);
				if (str.equals("null")) continue;
				timestamp = Long.parseLong(str);
				timestamps = matcher.group(2);
			}

			pattern = Pattern.compile("([^,]*),(.*)");
			matcher = pattern.matcher(open);
			if (matcher.matches()) {
				str = matcher.group(1);
				if (str.equals("null")) continue;
				opend = Double.parseDouble(str);
				open = matcher.group(2);
			}

			pattern = Pattern.compile("([^,]*),(.*)");
			matcher = pattern.matcher(close);
			if (matcher.matches()) {
				str = matcher.group(1);
				if (str.equals("null")) continue;
				closed = Double.parseDouble(str);
				close = matcher.group(2);
			}

			pattern = Pattern.compile("([^,]*),(.*)");
			matcher = pattern.matcher(high);
			if (matcher.matches()) {
				str = matcher.group(1);
				if (str.equals("null")) continue;
				highd = Double.parseDouble(str);
				high = matcher.group(2);
			}

			pattern = Pattern.compile("([^,]*),(.*)");
			matcher = pattern.matcher(low);
			if (matcher.matches()) {
				str = matcher.group(1);
				if (str.equals("null")) continue;
				lowd = Double.parseDouble(str);
				low = matcher.group(2);
			}

			stock.getCandles().add(new Candle(timestamp, opend, closed, highd, lowd));
		}
	}
	
	@Override
	public double parseCurrentPrice() throws ERRRegex{
		String textLine = "", price = "";
		try {
		BufferedReader br = new BufferedReader(new FileReader("./crawlerData/curl_data.json"));
		textLine = br.readLine();
		br.close();
		} catch (Exception e) {}
		Pattern pattern = Pattern.compile(".*\"regularMarketPrice\":([^,]*).*");
		Matcher matcher = pattern.matcher(textLine);

		if (matcher.matches()) {
			price = matcher.group(1);
		}
		else
			throw new ERRRegex();

		return Double.parseDouble(price);
	}
	
}
