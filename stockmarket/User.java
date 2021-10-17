package stockmarket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import stockmarket.exceptions.ERRExistingUser;
import stockmarket.exceptions.ERRForbidenUsername;
import stockmarket.exceptions.ERRInvalidValue;
import stockmarket.exceptions.ERRNoMoney;
import stockmarket.exceptions.ERRNotExistingUser;
import stockmarket.exceptions.ERRRegex;

public class User {

	private String username, password;
	private double money;
	private List<Transaction> transactions;
	
	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public double getMoney() {
		return money;
	}

	public void setUsernameAndPassword(String username, String password) throws ERRForbidenUsername{
		
		boolean flag[] = {true};
		username.chars().forEach(c -> {
			if(!Character.isAlphabetic(c) && !Character.isDigit(c)) {
				flag[0] = false;
			}
		});
		password.chars().forEach(c -> {
			if(!Character.isAlphabetic(c) && !Character.isDigit(c)) {
				flag[0] = false;
			}
		});
		if(!flag[0] || username.length() == 0 || password.length() == 0) throw new ERRForbidenUsername();
		this.username = username;
		this.password = password;
	}
	
	public void setMoney(double money) {
		this.money = money;
	}

	public void register() throws ERRExistingUser {
		if(usernameExists()) throw new ERRExistingUser();
		
		new File("./userData/" + username).mkdirs();
		try {
			File userFile = new File("./userData/" + username + "/" + username + ".txt");
			userFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(userFile, true));
		    bw.append(username + " " + money + "\n");
		    bw.close();
		} catch (Exception e) {}
		
		File userData = new File("./userData/user_data.txt");
		try {
		      BufferedWriter bw = new BufferedWriter(new FileWriter(userData, true));
		      bw.append(username + " " + password + "\n");
		      bw.close();
	    } catch (Exception e) {
	      System.out.println("Error in register()");
	    }
		
	}
	
	/**
	 * Checks if username and password exist in user_data.txt
	 * @return boolean array where first value represents username and second one password
	 */
	private boolean[] exists() {
		boolean[] flag = {false, false};
		try {
			new File("./userData").mkdir();
			File userData = new File("./userData/user_data.txt");
			userData.createNewFile();
			
			BufferedReader br = new BufferedReader(new FileReader(userData));
			Stream<String> s = br.lines();
			
			s.forEach(l -> {
				Pattern p = Pattern.compile("^(.*) (.*)$");
				Matcher m = p.matcher(l);
				if(m.matches()) {
					String usrnm = m.group(1);
					String psswd = m.group(2);
					if(username.equals(usrnm)) {
						flag[0] = true;
						if(password.equals(psswd))
							flag[1] = true;
						return;
					}
				}
			});
			
			br.close();
		} catch (Exception e) {
			System.out.println("Error in exists()");
		}
		return flag;
	}
	
	public boolean usernameExists() {
		return exists()[0];
	}
	
	public boolean usernameAndPasswordExist() {
		boolean[] ar = exists();
		return ar[0] && ar[1];
	}

	public void login() throws ERRNotExistingUser {
		transactions = new ArrayList<Transaction>();
		if(!usernameAndPasswordExist()) throw new ERRNotExistingUser();
		
		try {
			File userFile = new File("./userData/" + username + "/" + username + ".txt");
			BufferedReader br = new BufferedReader(new FileReader(userFile));
			String[] lines = br.readLine().split(" ");
			money = Double.parseDouble(lines[1]);
			String line;
			while((line = br.readLine()) != null) {
				lines = line.split(" ");
				transactions.add(new Transaction(Integer.parseInt(lines[0]), lines[1],
						Double.parseDouble(lines[2]), Double.parseDouble(lines[3])));
			}
			br.close();
		} catch (Exception e) {
			System.out.println("Error in login()");
		}
	}

	public void logout() {
		if(!usernameAndPasswordExist()) return;
		try {
			File file = new File("./userData/" + username + "/" + username + ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(username + " " + money + "\n");
			transactions.forEach(tr -> {
				try {
					bw.append(tr.toString()).append("\n");
				} catch (IOException e) {}
			});
			bw.close();
		} catch (IOException e) {
			System.out.println("File reading error");
		}
		username = "";
	}
	
	public void buy(String symbol, double amount) throws ERRNoMoney, ERRRegex, ERRInvalidValue {
		double currCost;
		for (Transaction tr : transactions) {
			if (symbol.equals(tr.getSymbol())) {
				currCost = tr.getCurrentCost();
				if (money < amount * currCost) throw new ERRNoMoney();
				tr.buy(amount, currCost);
				money -= amount * currCost;
				return;
			}
		}
		currCost = new Transaction(1, symbol, 1, 0).getCurrentCost();
		if (money < amount * currCost) throw new ERRNoMoney();
		int id;
		if (transactions.size() == 0)
			id = 1;
		else
			id = transactions.get(transactions.size() - 1).getID() + 1;
		transactions.add(new Transaction(id, symbol, amount, currCost));
		money -= amount * currCost;
	}
	
	public void sell(int id, double amount) throws ERRInvalidValue, ERRRegex {
		for(Transaction tr: transactions) {
			if (id == tr.getID()) {
				tr.sell(amount);
				if(tr.getAmount() == 0)
					transactions.remove(tr);
				money += tr.getCurrentCost() * amount;
				return;
			}
		}
		throw new ERRInvalidValue();
	}
	
}
