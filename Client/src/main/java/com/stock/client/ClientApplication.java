package com.stock.client;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.jakewharton.fliptables.FlipTable;
import com.stock.client.models.Balance;
import com.stock.client.models.Deposit;
import com.stock.client.models.ExecutedOrder;
import com.stock.client.models.Notification;
import com.stock.client.models.OrderDetails;
import com.stock.client.models.StockDetails;
import com.stock.client.models.StockHistory;
import com.stock.client.models.Unsubscribe;
import com.stock.client.models.UserLogin;
import com.stock.client.models.UserOrder;
import com.stock.client.models.UserRegistration;
import com.stock.client.models.UserStockInfo;
import com.stock.client.models.UserSubscription;
import com.stock.client.util.InputChecker;
import com.stock.client.util.PositiveDoubleChecker;
import com.stock.client.util.PositiveIntegerChecker;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

	@Autowired
	private DiscoveryClient disco;

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder b) {
		return b.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	public void say(String message) {
		System.out.println(message);
	}

	private Scanner sc = new Scanner(System.in);

	public String ask(String prompt) {
		System.out.print(prompt);
		while (sc.hasNextLine()) {
			String input = sc.nextLine();
			String stripped = input.strip();
			if (stripped.equals("")) {
				continue;
			} else {
				return stripped;
			}
		}
		return "";
	}

	// Validation of input
	public <T> T askWithChecker(String prompt, InputChecker<T> checker) {
		System.out.print(prompt);
		while (sc.hasNextLine()) {
			String input = sc.nextLine();
			String stripped = input.strip();
			if (stripped.equals("")) {
				System.out.print(prompt);
				continue;
			} else if (stripped.equalsIgnoreCase("Q")) {
				return checker.quit();
			} else if (!checker.valid(stripped)) {
				System.out.print(prompt);
				continue;
			} else {
				return checker.convertFromString(stripped);
			}
		}
		return checker.quit();
	}

	private String stockSvc = "";
	private String userSvc = "";
	private String username = "";
	Map<String, Notification> notifications = new HashMap<>();
	private final double STOCK_MIN = 0.0001;
	private final double STOCK_MAX = 100000.0;

	@Override
	public void run(String... args) throws Exception {

		// If stock or user instances are not running, then client application will quit
		List<ServiceInstance> stockSvcInstances = disco.getInstances("STOCK");
		if (stockSvcInstances.isEmpty()) {
			System.out.println("Cannot locate STOCK eureka service, quitting ...");
			System.exit(1);
		}
		stockSvc = stockSvcInstances.get(0).getUri().toString();

		List<ServiceInstance> userSvcInstances = disco.getInstances("USER");
		if (userSvcInstances.isEmpty()) {
			System.out.println("Cannot locate USER eureka service, quitting ...");
			System.exit(1);
		}

		userSvc = userSvcInstances.get(0).getUri().toString();


		say("Welcome to the Stock Trading application");
		String response = ask("Login or register [l/r]? ");
		
		// login
		if (response.equals("l")) {
			for (int i = 0; i < 3; i++) { // 3 invalid chances
				String user = ask("username: ");
				String pass = ask("password: ");
				UserLogin login = new UserLogin();
				login.setPassword(pass);
				login.setUsername(user);
				ResponseEntity<String> loginResponse = null;
				try {
					loginResponse = restTemplate.postForEntity(userSvc + "/user/login", login, String.class);
					say(loginResponse.getBody());
					username = user;
					break;
				} catch (HttpClientErrorException e) {
					say(e.getResponseBodyAsString() + "!!");

				}
			}
		}

		// registration
		else if (response.equals("r")) {
			String user = ask("Username: ");
			String pass = ask("Password: ");
			String fname = ask("First Name: ");
			String lname = ask("Last Name: ");

			UserRegistration reg = new UserRegistration();
			reg.setFirstName(fname);
			reg.setLastName(lname);
			reg.setPassword(pass);
			reg.setUsername(user);

			ResponseEntity<String> regResponse = null;
			try {
				regResponse = restTemplate.postForEntity(userSvc + "/user/register", reg, String.class);
				say(regResponse.getBody());
				username = user;
			} catch (HttpClientErrorException e) {
				say(e.getResponseBodyAsString() + "!!");
			}
		}

		else {
			say("You must choose between login / register");
			return;
		}

		while (true) {

			fetchAllStocks(); // Once logged in, we will fetch all stocks at a time
			fetchUserSubscriptions(); // To send notifications, we will get all subscribed stocks at a time

			say("\nPlease select any of the following (1-14): ");

			String[] headers = new String[] { "ID", "Option name" };
			String[][] options = new String[][] { { "1", "View all stocks" }, { "2", "View my stocks" },
					{ "3", "Sell my stock" }, { "4", "Buy a stock" }, { "5", "Subscribe a stock" },
					{ "6", "Unsubscribe a stock" }, { "7", "View subscribed stocks" }, { "8", "Stock history" },
					{ "9", "Check balance" }, { "10", "Deposit amount" }, { "11", "Net worth" }, { "12", "My past orders" },
					{ "13", "View notifications [" + notifications.size() + "]" }, { "14", "Quit" }

			};

			System.out.println(FlipTable.of(headers, options));
			int option = askWithChecker("Select option (Q to quit) : ", new PositiveIntegerChecker(1, 14)); // validate user input
																																																
			if (option == PositiveIntegerChecker.QUIT) {
				option = 14;
			}

			System.out.println("You have selected option : " + options[option - 1][1]);
			switch (option) {

			case 1:
				viewAllStocks();
				break;
			case 2:
				viewMyStocks();
				break;
			case 3:
				sellStock();
				break;
			case 4:
				buyStock();
				break;
			case 5:
				subscribeStock();
				break;
			case 6:
				unsubscribeStock();
				break;
			case 7:
				viewSubscribedStocks();
				break;
			case 8:
				viewStockHistory();
				break;
			case 9:
				checkBalance();
				break;
			case 10:
				deposit();
				break;
			case 11:
				netWorth();
				break;
			case 12:
				orderHistory();
				break;
			case 13:
				viewNotifications();
				break;
			case 14:
				quit(); // System exit
				break;
			default:
				break;
			}

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	// 1. View all stocks in a table
	Map<String, StockDetails> allStocksMap = new HashMap<>();

	// To fetch all stocks
	private StockDetails[] fetchAllStocks() {
		ResponseEntity<StockDetails[]> getAllStocks = restTemplate.getForEntity(stockSvc + "/stocks/",
				StockDetails[].class);
		StockDetails[] stocks = null;
		if (getAllStocks.getStatusCode() == HttpStatus.OK) {
			stocks = getAllStocks.getBody();
			for (StockDetails stock : stocks) {
				this.allStocksMap.put(stock.getName(), stock);
			}
			return stocks;
		}
		return new StockDetails[0];
	}

	public void viewAllStocks() {
		StockDetails[] stocks = fetchAllStocks();
		String[] headers = { "Name", "Price", "Available" };
		String[][] data = new String[stocks.length][];
		int i = 0;
		for (StockDetails stock : stocks) {
			data[i++] = new String[] { stock.getName(), String.valueOf(stock.getPrice()),
					String.valueOf(stock.getAvailable()) };
			this.allStocksMap.put(stock.getName(), stock);
		}

		System.out.println(FlipTable.of(headers, data));
	}

	// 2. Get only my stocks in a table
	Map<String, UserStockInfo> myStockMap = new HashMap<>();

	// Fetch only my stocks
	private UserStockInfo[] fetchMyStocks() {
		ResponseEntity<UserStockInfo[]> myStocks = restTemplate.getForEntity(userSvc + "/stocks/user/" + username,
				UserStockInfo[].class);
		if (myStocks.getStatusCode() == HttpStatus.OK) {
			for (UserStockInfo stock : myStocks.getBody()) {
				myStockMap.put(stock.getStockName(), stock);
			}
			return myStocks.getBody();
		}
		return new UserStockInfo[0];
	}

	public void viewMyStocks() {
		UserStockInfo[] mystocks = fetchMyStocks();
		String[] headers = { "StockName", "Quantity", "Current Stock Price" };
		String[][] data = new String[mystocks.length][];
		int i = 0;
		for (UserStockInfo stock : mystocks) {
			data[i++] = new String[] { stock.getStockName(), String.valueOf(stock.getQuantity()),
					String.valueOf(allStocksMap.get(stock.getStockName()).getPrice()) };
		}
		if(data.length == 0) {
			inABox("My Stocks", "No stocks on your account");
			return;
		}
		System.out.println(FlipTable.of(headers, data));
	}

	// 3. Sell a stock with quantity
	public void sellStock() {

		say("Select stock from below list to sell: ");
		viewMyStocks();
		String stockname = ask("Enter stock name (or Q to quit): ");
		if (stockname.equals("Q")) {
			return;
		}

		if (!myStockMap.containsKey(stockname)) {
			inABox("Operation failed", "You don't have any " + stockname + " stocks yet!");
			return;
		}

		UserStockInfo stockProperties = myStockMap.get(stockname);
		Integer quantity = askWithChecker("How many stocks you want to sell (Q to quit)? ", new PositiveIntegerChecker());

		if (quantity > stockProperties.getQuantity()) {
			inABox("Transaction failed", "You don't have " + quantity + " of " + stockname + " to sell");
			return;
		}

		OrderDetails sellOrder = new OrderDetails();
		sellOrder.setUsername(username);
		sellOrder.setStockName(stockname);
		sellOrder.setQuantity(Integer.valueOf(quantity));
		sellOrder.setOrderType("SELL");

		ResponseEntity<ExecutedOrder> sellResponse = restTemplate.postForEntity(userSvc + "/user/order", sellOrder,
				ExecutedOrder.class);
		ExecutedOrder body = sellResponse.getBody();
		inABox("Order successful",
				body.getQuantity() + " shares of " + stockname + " sold at " + body.getExecutedPrice() + " per share");

	}

	// 4. Buy a stock with quantity
	public void buyStock() {

		say("Select stock from below list to buy: ");
		viewAllStocks();
		String stockname = ask("Enter Stock name (or Q to quit): ");
		if (stockname.toLowerCase().equals("q")) {
			return;
		}

		if (!allStocksMap.containsKey(stockname)) {
			inABox("Operation failed", "Stock " + stockname + " was not found in our system");
			return;
		}

		Integer quantity = askWithChecker("How many stocks do you want to buy (Q to quit)? ", new PositiveIntegerChecker());
		if (quantity < 0) {
			inABox("Transaction failed", "Invalid amount of quantity");
			return;
		}

		StockDetails stockProperties = allStocksMap.get(stockname);
		long available = stockProperties.getAvailable();

		if (quantity > available) {
			inABox("Transaction failed", "Quantity cannot exceed availabilty");
			return;
		}

		double stockPrice = stockProperties.getPrice();
		double estimatedCost = stockPrice * quantity;
		double balance = getBalance();

		if (balance < estimatedCost) {
			inABox("Transaction failed", "You don't have enough funds for this order");
			return;
		}

		OrderDetails order = new OrderDetails();
		order.setUsername(username);
		order.setStockName(stockname);
		order.setQuantity(Integer.valueOf(quantity));
		order.setOrderType("BUY");

		ResponseEntity<ExecutedOrder> response = restTemplate.postForEntity(userSvc + "/user/order", order,
				ExecutedOrder.class);
		if (response.getStatusCode() != HttpStatus.OK) {
			inABox("System failure", "Order failed.");
			return;
		}
		ExecutedOrder body = response.getBody();
		inABox("Order successful", body.getQuantity() + " " + body.getStockName() + " stocks were bought at "
				+ body.getExecutedPrice() + " a share");

	}

	// 5. Subscribe a stock with min or max value
	public void subscribeStock() {

		say("Select stock from below list : ");
		viewAllStocks();
		String stockname = ask("Enter stock name (or Q to quit): ");

		if (!allStocksMap.containsKey(stockname)) {
			inABox("Subscription failed", "Pick a valid stock name");
			return;
		}
		String minPriceResponse = ask("Enter min price (Q to skip): ");
		double minPrice = STOCK_MIN - 0.1;
		if (!minPriceResponse.equals("Q")) {
			minPrice = Double.valueOf(minPriceResponse);
		}

		String maxPriceResponse = ask("Enter max price (Q to skip): ");
		double maxPrice = STOCK_MAX + 0.1;
		if (!maxPriceResponse.equals("Q")) {
			maxPrice = Double.valueOf(maxPriceResponse);
		}

		if (minPrice < STOCK_MIN && maxPrice > STOCK_MAX) {
			inABox("Subscription failed", "Pick a valid min price or max price");
			return;
		}

		if (minPrice > maxPrice) {
			inABox("Subscription failed", "Min price should be less than max price");
			return;
		}

		UserSubscription sub = new UserSubscription();
		sub.setUsername(username);
		sub.setMinPrice(minPrice);
		sub.setMaxPrice(maxPrice);
		sub.setStockname(stockname);
		ResponseEntity<String> response = restTemplate.postForEntity(userSvc + "/user/stock/subscribe", sub, String.class);

		inABox("Subscription status", response.getBody());
	}

	// 6. To unsubscribe a stock
	private void unsubscribeStock() {

		say("Select stock from below list : ");
		viewSubscribedStocks();
		String stockname = ask("Enter stock name (or Q to quit): ");
		if ("Q".equalsIgnoreCase(stockname)) {
			return;
		}

		if (!allStocksMap.containsKey(stockname)) {
			inABox("Subscription failed", "Pick a valid stock name");
			return;
		}
		Unsubscribe sub = new Unsubscribe();
		sub.setUsername(username);
		sub.setStockname(stockname);
		ResponseEntity<String> response = restTemplate.postForEntity(userSvc + "/user/stock/unsubscribe", sub,
				String.class);
		inABox("Subscriptions updated", response.getBody());
	}

	// 7. To view all subscribed stocks in a table
	private void viewSubscribedStocks() {

		UserSubscription[] us = fetchUserSubscriptions();
		String[] headers = new String[] { "Stock name", "Min Price", "Max Price" };
		String[][] data = new String[us.length][];
		int i = 0;
		for (UserSubscription u : us) {
			data[i++] = new String[] { 
					u.getStockname(), 
					u.getMinPrice() < STOCK_MIN ? "" : String.valueOf(u.getMinPrice()),
					u.getMaxPrice() > STOCK_MAX ? "" : String.valueOf(u.getMaxPrice()) };

		}
		if(data.length == 0) {
			inABox("Subscriptions", "You don't have any subscribed stocks !");
		}
		System.out.println(FlipTable.of(headers, data));

	}

	// To fetch all the subscribed stocks
	// To know current stock price from map
	private double currentStockPrice(String stockName) {
		return allStocksMap.get(stockName).getPrice();
	}

	// to send out notifications
	private UserSubscription[] fetchUserSubscriptions() {

		ResponseEntity<UserSubscription[]> subs = restTemplate
				.getForEntity(userSvc + "/user/stock/" + username + "/subscriptions", UserSubscription[].class);
		for (UserSubscription sub : subs.getBody()) {
			String stockName = sub.getStockname();
			double minPrice = sub.getMinPrice();
			double maxPrice = sub.getMaxPrice();
			double currentPrice = currentStockPrice(stockName);

			Notification nof = new Notification();

			nof.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
			nof.setMinPrice(minPrice);
			nof.setMaxPrice(maxPrice);
			nof.setStockname(stockName);
			nof.setUsername(username);
			nof.setCurrentPrice(currentPrice);

			if (currentPrice < minPrice) {
				nof.setComments("Min. Price alert: " + stockName + "(" + currentPrice + ") has fallen below " + minPrice);
				notifications.put(stockName, nof);
			} else if (currentPrice > maxPrice) {
				nof.setComments("Max. Price alert: " + stockName + "(" + currentPrice + ")  has topped " + maxPrice);
				notifications.put(stockName, nof);
			}
		}
		return subs.getBody();
	}

	// 8. To view stock history with stock name
	public void viewStockHistory() {
		viewAllStocks();
		String stockname = ask("Select stock name from above to view history : ");
		if (!allStocksMap.containsKey(stockname)) {
			inABox("Operation failed", "Stock " + stockname + " was not found in our system");
			return;
		}

		ResponseEntity<StockHistory[]> getMyStocks = restTemplate.getForEntity(stockSvc + "/stocks/stock/" + stockname + "/history",
				StockHistory[].class);

		if (getMyStocks.getStatusCode() == HttpStatus.OK) {
			StockHistory[] mystocks = getMyStocks.getBody();

			int entries = 10;
			int skip = mystocks.length - entries;

			String[] headers = { "Stock name", "Timestamp", "Current price", "Gain/Loss " };
			int rows = Math.min(mystocks.length, entries);
			String[][] data = new String[rows][];
			int i = 0;
			for (StockHistory stock : mystocks) {
				if (skip-- > 0)
					continue;
				data[i++] = new String[] { stock.getStockName(), String.valueOf(stock.getTimestamp()),
						String.valueOf(stock.getCurrentPrice()), String.valueOf(stock.getGain()) };
			}
			System.out.println(FlipTable.of(headers, data));
		}
	}

	// 9. To view user balance in a table
	private void checkBalance() {
		inABox("Your balance", BigDecimal.valueOf(getBalance()).setScale(2, RoundingMode.HALF_UP).toString());
	}

	// To get user balance from the database
	private double getBalance() {
		ResponseEntity<Balance> balanceRes = restTemplate.getForEntity(userSvc + "/user/profile/" + username, Balance.class);
		Balance balance = null;
		if (balanceRes.getStatusCode() == HttpStatus.OK) {
			balance = balanceRes.getBody();
		}

		return balance != null ? balance.getBalance() : 0.0;
	}

	// 10. To deposit amount
	private void deposit() {
		Double amount = askWithChecker("Please enter amount to deposit (Q to quit): ", new PositiveDoubleChecker());
		if (amount == PositiveDoubleChecker.QUIT) {
			inABox("Deposit failed", "You have chosen to skip");
			return;
		}

		Deposit d = new Deposit();
		d.setusername(username);
		d.setDeposit(amount);
		ResponseEntity<String> bal = restTemplate.postForEntity(userSvc + "/user/deposit", d, String.class);
		inABox(bal.getBody(), "Total balance: " + BigDecimal.valueOf(getBalance()).setScale(2, RoundingMode.HALF_UP));
	}

	// 11. Get my net worth which is my balance and my stocks
	private void netWorth() {
		fetchMyStocks();
		double currentBal = getBalance();
		for (UserStockInfo stock : myStockMap.values()) {
			currentBal += stock.getQuantity() * currentStockPrice(stock.getStockName());
		}
		inABox("Your net worth", BigDecimal.valueOf(currentBal).setScale(2, RoundingMode.HALF_UP).toString());
	}

	// 12. Get my order history
	private void orderHistory() {
		ResponseEntity<UserOrder[]> orders = restTemplate.getForEntity(stockSvc + "/stocks/user/" + username + "/history",
				UserOrder[].class);
		UserOrder[] body = orders.getBody();
		String[] headers = { "Stock name", "Quantity", "Order type", "Executed price", "Order Date" };
		String[][] data = new String[body.length][];
		int i = 0;
		for (UserOrder order : body) {
			data[i++] = new String[] { order.getStockName(), String.valueOf(order.getQuantity()), order.getOrderType(),
					String.valueOf(order.getExecutedPrice()), order.getTimestamp().toString() };
		}
		if(data.length == 0) {
			inABox("Order history","You don't have any past orders ");
			return;
		}
		System.out.println(FlipTable.of(headers, data));
	}

	// 13. To view notifications in a table
	private void viewNotifications() {

		int numberOfNotifications = notifications.size();
		String[] headers = { "Stock name", "Current Price", "Message", "Timestamp" };
		String[][] data = new String[numberOfNotifications][];
		int i = 0;
		for (Notification nof : notifications.values()) {
			data[i++] = new String[] { nof.getStockname(), String.valueOf(nof.getCurrentPrice()), nof.getComments(),
					nof.getTimestamp().toString() };
		}
		System.out.println(FlipTable.of(headers, data));
		notifications.clear();
	}

	// 14. To quit the system
	private void quit() {
		say("You have been exited successfully");
		System.exit(0);

	}

	//Util function to print header and data in a table
	private void inABox(String header, String data) {
		String[][] body = new String[1][1];
		body[0][0] = data;
		System.out.println(FlipTable.of(new String[] { header.toUpperCase() }, body));
	}
}
