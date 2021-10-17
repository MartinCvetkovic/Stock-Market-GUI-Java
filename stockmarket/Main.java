package stockmarket;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import stockmarket.exceptions.ERRExistingUser;
import stockmarket.exceptions.ERRForbidenUsername;
import stockmarket.exceptions.ERRInvalidValue;
import stockmarket.exceptions.ERRNoMoney;
import stockmarket.exceptions.ERRNotExistingUser;
import stockmarket.exceptions.ERRRegex;
import stockmarket.exceptions.ErrorDialog;

@SuppressWarnings("serial")
public class Main extends Frame {
	
	private class QuitDialog extends Dialog {
		
		public QuitDialog(Frame owner) {
			super(owner, "Quit", true);
			setBounds(owner.getX() + owner.getWidth() / 2, owner.getY() + owner.getHeight() / 2, 200, 100);
			Panel buttons = new Panel();
			Button yes = new Button("Yes"), no = new Button("No");
			yes.addActionListener(ae -> {
				Main.this.dispose();
				user.logout();
			});
			no.addActionListener((ae) -> {
				dispose();
			});
			buttons.add(yes);
			buttons.add(no);
			add(new Label("Are you sure you want to quit?", Label.CENTER), BorderLayout.NORTH);
			add(buttons, BorderLayout.SOUTH);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dispose();
				}
			});
			setVisible(true);
		}
	}
	
	private User user = new User();
	private CardLayout cardLayout = new CardLayout();
	private Crawler crawler = new Crawler();
	private Stock stock;
	private Parser parser = new JSONParser();
	private Panel portfolio = new Panel(new GridLayout(0, 1));
	private Label usernameLabel = new Label();
	private Label moneyLabel = new Label();
	private StockCanvas canvas = new StockCanvas();
	
	public Main() {
		setBounds(400, 100, 700, 500);
		setTitle("Stock market");
		setLayout(cardLayout);
		
		populate();
		populateChoiceStock();
		populateChoicePortfolio();
		//pack();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				new QuitDialog(Main.this);
			}
		});
		
		setVisible(true);
	}
	
	private void populate() {
		Panel loginPanel = new Panel(new GridLayout(0, 1));
		Panel mainPanel = new Panel(new GridLayout(0, 1));
		Label userLabel = new Label();
		
		Panel usernamePanel = new Panel();
		TextField username = new TextField(15);
		Panel passwordPanel = new Panel();
		TextField password = new TextField(15);
		password.setEchoChar('*');
		usernamePanel.add(new Label("Username:", Label.CENTER));
		usernamePanel.add(username);
		passwordPanel.add(new Label("Password:", Label.CENTER));
		passwordPanel.add(password);
		loginPanel.add(usernamePanel);
		loginPanel.add(passwordPanel);
		
		Panel buttonPanel = new Panel();
		Button login = new Button("Login");
		login.addActionListener(ae -> {

			try {
				user.setUsernameAndPassword(username.getText(), password.getText());
				user.login();
				cardLayout.next(this);
				userLabel.setText("Welcome " + user.getUsername());
				usernameLabel.setText(user.getUsername());
				moneyLabel.setText(Double.toString(user.getMoney()));
				usernameLabel.revalidate();
				moneyLabel.revalidate();
			} catch (ERRNotExistingUser | ERRForbidenUsername e) {
				new ErrorDialog(Main.this, e.toString());
			}
		});
		
		
		Button register = new Button("Register");
		register.addActionListener(ae -> {
			try {
				user.setUsernameAndPassword(username.getText(), password.getText());
				double[] money = {-1};
				class MoneyDialog extends Dialog {
					public MoneyDialog(Frame owner, double[] resources) {
						super(owner, "Input", true);
						setBounds(owner.getX() + owner.getWidth() / 2, owner.getY() + owner.getHeight() / 2, 200, 100);
						Panel southPanel = new Panel();
						TextField money = new TextField(10);
						Button confirm = new Button("Confirm");
						confirm.addActionListener(ae -> {
							try {
								resources[0] = Double.parseDouble(money.getText());
							} catch (Exception e) {}
							dispose();
						});
						
						southPanel.add(money);
						southPanel.add(confirm);
						add(new Label("Enter starting resources (USD)", Label.CENTER), BorderLayout.NORTH);
						add(southPanel, BorderLayout.SOUTH);
						addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent e) {
								resources[0] = -1;
								dispose();
							}
						});
						pack();
						setVisible(true);
					}
				}
				new MoneyDialog(Main.this, money);
				if(money[0] < 0) {
					new ErrorDialog(Main.this, "Error: Incorrect value");
					return;
				}
				user.setMoney(money[0]);
				user.register();
			} catch (ERRExistingUser | ERRForbidenUsername e) {
				new ErrorDialog(Main.this, e.toString());
			}
		});
		
		Button quit = new Button("Quit");
		quit.addActionListener(ae -> { new QuitDialog(Main.this); });
		
		buttonPanel.add(login);
		buttonPanel.add(register);
		buttonPanel.add(quit);
		loginPanel.add(buttonPanel);
		
		CheckboxGroup checkboxGroup = new CheckboxGroup();
		Checkbox showAction = new Checkbox("Show action timestamps", true, checkboxGroup);
		Checkbox showPortfolio = new Checkbox("Show user portfolio", false, checkboxGroup);
		Checkbox logout = new Checkbox("Logout", false, checkboxGroup);
		Button confirm = new Button("Confirm");
		confirm.addActionListener(ae -> {
			String choice = checkboxGroup.getSelectedCheckbox().getLabel();
			if(choice.equals("Show action timestamps")) {
				cardLayout.next(Main.this);
			}
			else if(choice.equals("Show user portfolio")) {
				cardLayout.next(Main.this);
				cardLayout.next(Main.this);
				writeToPortfolio();
			}
			else if(choice.equals("Logout")) {
				user.logout();
				cardLayout.next(Main.this);
				cardLayout.next(Main.this);
				cardLayout.next(Main.this);
				username.setText("");
				password.setText("");
			}
		});
		
		mainPanel.add(userLabel);
		mainPanel.add(showAction);
		mainPanel.add(showPortfolio);
		mainPanel.add(logout);
		mainPanel.add(confirm);
		
		
		add(loginPanel);
		add(mainPanel);
	}

	private void populateChoiceStock() {
		Panel labelPanel = new Panel();
		Label label = new Label("", Label.CENTER);
		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if(stock == null) return;
				List<Candle> currentCandles = new ArrayList<>();
				for(int i = canvas.getOffset(); i < canvas.getShownCandles() + canvas.getOffset(); i++) {
					if(i >= stock.getCandles().size()) break;
					currentCandles.add(stock.getCandles().get(i));
				}
				int width = canvas.getWidth() / canvas.getShownCandles();
				label.setText(currentCandles.get(e.getX() / width).toString());
				labelPanel.revalidate();
			}
		});
		Panel stocksPanel = new Panel(new BorderLayout());
		Panel indicatorPanel = new Panel(new BorderLayout());
		Panel dataPanel = new Panel(new GridLayout(0, 1));
		Panel namePanel = new Panel();
		TextField name = new TextField("aapl", 10);
		Panel startPanel = new Panel();
		TextField start = new TextField("1615679664", 10);
		Panel endPanel = new Panel();
		TextField end = new TextField("1625679664", 10);
		
		Button confirm = new Button("Confirm");
		confirm.addActionListener(ae -> {
			try {
				stock = new Stock(name.getText(), Long.parseLong(start.getText()), Long.parseLong(end.getText()));
			}
			catch(Exception e) {
				new ErrorDialog(Main.this, "Error: Incorrect values");
				return;
			}
			crawler.collectData(name.getText(), Long.parseLong(start.getText()), Long.parseLong(end.getText()));
			try {
				parser.parseFile(stock);
				canvas.setStock(stock);
				canvas.repaint();
			} catch (ERRRegex e) {
				new ErrorDialog(Main.this, e.toString());
			}
		});
		
		Button back = new Button("Back");
		back.addActionListener(ae -> {
			cardLayout.next(Main.this);
			cardLayout.next(Main.this);
			cardLayout.next(Main.this);
		});
		
		dataPanel.add(new Label("Stock symbol: ", Label.CENTER));
		namePanel.add(name);
		dataPanel.add(namePanel);
		
		dataPanel.add(new Label("Start timestamp: ", Label.CENTER));
		startPanel.add(start);
		dataPanel.add(startPanel);
		
		dataPanel.add(new Label("End timestamp: ", Label.CENTER));
		endPanel.add(end);
		dataPanel.add(endPanel);
		dataPanel.add(confirm);
		dataPanel.add(back);
		
		Panel intervalPanel = new Panel();
		Panel checkboxPanel = new Panel();
		TextField interval = new TextField(3);
		Checkbox ma = new Checkbox();
		Checkbox ema = new Checkbox();
		Button choose = new Button("Choose");
		
		choose.addActionListener(ae -> {
			try {
				canvas.setMa(ma.getState(), Integer.parseInt(interval.getText()));
				canvas.setEma(ema.getState(), Integer.parseInt(interval.getText()));
				canvas.repaint();
			}
			catch(ERRInvalidValue e) {
				new ErrorDialog(Main.this, e.toString());
			}
			catch(NumberFormatException e) {
				new ErrorDialog(Main.this, "Error: Incorrect value");
			}
		});
		
		indicatorPanel.setPreferredSize(new Dimension(170, 100));
		
		intervalPanel.add(new Label("Indicator interval:"));
		intervalPanel.add(interval);
		indicatorPanel.add(intervalPanel, BorderLayout.NORTH);
		checkboxPanel.add(new Label("MA:"));
		checkboxPanel.add(ma);
		checkboxPanel.add(new Label("EMA:"));
		checkboxPanel.add(ema);
		checkboxPanel.add(choose);
		indicatorPanel.add(checkboxPanel, BorderLayout.CENTER);
		
		labelPanel.add(label);
		
		stocksPanel.add(labelPanel, BorderLayout.NORTH);
		stocksPanel.add(indicatorPanel, BorderLayout.WEST);
		stocksPanel.add(dataPanel, BorderLayout.EAST);
		stocksPanel.add(canvas, BorderLayout.CENTER);
		add(stocksPanel);
	}
	
	private void populateChoicePortfolio() {
		Panel portfolioPanel = new Panel(new BorderLayout());
		// Data panel on the right
		Panel dataPanel = new Panel(new GridLayout(0, 1));
		Panel buyingSymbol = new Panel();
		Panel buyingAmount = new Panel();
		Panel sellingID = new Panel();
		Panel sellingAmount = new Panel();
		TextField symbolField = new TextField(10);
		TextField buyAmountField = new TextField(10);
		TextField idField = new TextField(10);
		TextField sellAmountField = new TextField(10);
		
		Button buy = new Button("Buy");
		buy.addActionListener(ae -> {
			try {
				user.buy(symbolField.getText(), Double.parseDouble(buyAmountField.getText()));
				moneyLabel.setText(Double.toString(user.getMoney()));
				moneyLabel.revalidate();
				writeToPortfolio();
			} catch (ERRNoMoney | ERRRegex | ERRInvalidValue e) {
				new ErrorDialog(Main.this, e.toString());
			} catch (NumberFormatException e) {
				new ErrorDialog(Main.this, "Error: Incorrect values");
			}
		});
		
		Button sell = new Button("Sell");
		sell.addActionListener(ae -> {
			try {
				user.sell(Integer.parseInt(idField.getText()), Double.parseDouble(sellAmountField.getText()));
				moneyLabel.setText(Double.toString(user.getMoney()));
				moneyLabel.revalidate();
				writeToPortfolio();
			} catch (ERRRegex | ERRInvalidValue e) {
				new ErrorDialog(Main.this, e.toString());
			} catch (NumberFormatException e) {
				new ErrorDialog(Main.this, "Error: Incorrect values");
			}
		});
		
		Button back = new Button("Back");
		back.addActionListener(ae -> {
			cardLayout.next(Main.this);
			cardLayout.next(Main.this);
		});
		
		buyingSymbol.add(new Label("Stock symbol:", Label.LEFT));
		buyingSymbol.add(symbolField);
		buyingAmount.add(new Label("Stock amount:", Label.LEFT));
		buyingAmount.add(buyAmountField);
		sellingID.add(new Label("Stock id:", Label.LEFT));
		sellingID.add(idField);
		sellingAmount.add(new Label("Stock amount:", Label.LEFT));
		sellingAmount.add(sellAmountField);
		
		dataPanel.add(new Label("Buy stocks", Label.CENTER));
		dataPanel.add(buyingSymbol);
		dataPanel.add(buyingAmount);
		dataPanel.add(buy);
		dataPanel.add(new Label("Sell stocks", Label.CENTER));
		dataPanel.add(sellingID);
		dataPanel.add(sellingAmount);
		dataPanel.add(sell);
		dataPanel.add(back);
		
		portfolioPanel.add(dataPanel, BorderLayout.EAST);
		// End of data panel
		// Upper panel
		Panel upperPanel = new Panel();
		upperPanel.add(new Label("Username: ", Label.LEFT));
		upperPanel.add(usernameLabel);
		upperPanel.add(new Label("USD: ", Label.LEFT));
		upperPanel.add(moneyLabel);
		portfolioPanel.add(upperPanel, BorderLayout.NORTH);
		// End of upper panel
		// Portfolio panel
		portfolioPanel.add(portfolio, BorderLayout.CENTER);
		// End of portfolio panel
		add(portfolioPanel);
	}
	
	private void writeToPortfolio() {
		Font font = new Font(Font.MONOSPACED, 0, 12);
		portfolio.removeAll();
		String string = String.format("%-10s%-15s%-20s%-20s%-20s%-20s%-20s", "ID", "Stock symbol",
				"Number of stocks", "Buying cost", "Current cost", "Absolute change", "Relative change");
		Label label = new Label(string, Label.LEFT);
		label.setFont(font);
		label.setForeground(Color.BLACK);
		portfolio.add(label);
		portfolio.revalidate();
		pack();
		user.getTransactions().forEach(tr -> {
			String s = "";
			try {
				s = String.format("%-10s%-15s%-20.4f%-20.4f%-20.4f%-20.4f%.4f%%", tr.getID(), tr.getSymbol(),
						tr.getAmount(), tr.getBuyCost(), tr.getCurrentCost(), tr.absoluteDifference(), tr.relativeDifference());
			} catch (ERRRegex e) {}
			Label l = new Label(s, Label.LEFT);
			l.setFont(font);
			if(tr.absoluteDifference() <= 0)
				l.setForeground(Color.RED);
			else
				l.setForeground(Color.GREEN);
			portfolio.add(l);
			portfolio.revalidate();
		});
		portfolio.revalidate();
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
}
