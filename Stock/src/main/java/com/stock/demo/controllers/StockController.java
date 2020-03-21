package com.stock.demo.controllers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.demo.dao.OrderDAO;
import com.stock.demo.dao.StockHistoryDAO;
import com.stock.demo.dao.Stockrepo;
import com.stock.demo.models.db.Order;
import com.stock.demo.models.db.Stock;
import com.stock.demo.models.db.StockHistory;
import com.stock.demo.models.web.ExecutedOrder;
import com.stock.demo.models.web.OrderDetails;
import com.stock.demo.models.web.StockHistoryweb;
import com.stock.demo.models.web.UserOrder;

@RestController
@RequestMapping("/stocks")
public class StockController {

	@Autowired
	private Stockrepo stockRepo;

	@Autowired
	private OrderDAO orderRepo;

	@Autowired
	private StockHistoryDAO stockHistoryRepo;
	
	private final String BUY = "BUY";
	private final String SELL = "SELL";
	

	@GetMapping("/")
	private List<Stock> getAllStocks() {
		return stockRepo.findAll();
	}

	@GetMapping("/stock/{stockname}/history")
	private List<StockHistoryweb> history(@PathVariable("stockname") String stockName) {
		List<StockHistory> stockHistory = stockHistoryRepo.findByStockName(stockName);
		List<StockHistoryweb> response = new ArrayList<>();
		for (StockHistory sh : stockHistory) {
			StockHistoryweb sw = new StockHistoryweb();
			sw.setStockName(sh.getStockName());
			sw.setCurrentPrice(sh.getCurrentPrice().doubleValue());
			sw.setTimestamp(sh.getTimestamp());
			sw.setGain(sh.getGain().doubleValue());
			response.add(sw);
		}
		return response;
	}

	@PostMapping("/order/buy")
	private ExecutedOrder buyStock(@RequestBody OrderDetails orderDetails) {
		return orderHelper(orderDetails, BUY);
	}

	@PostMapping("/order/sell")
	private ExecutedOrder sellStock(@RequestBody OrderDetails orderDetails) {
		return orderHelper(orderDetails,  SELL);
	}

	private ExecutedOrder orderHelper(OrderDetails orderDetails, String orderType) {

		Order order = new Order();
		var stockName = orderDetails.getStockName();
		order.setStockName(stockName);
		BigDecimal currentPrice = stockRepo.findByName(stockName).getPrice();
		order.setCurrentPrice(currentPrice);
		order.setUserName(orderDetails.getUserName());
		order.setQuantity(orderDetails.getQuantity());
		order.setBuyOrSell(orderType);
		order.setDate(new Timestamp(new Date().getTime()));
		orderRepo.save(order);
		
		Stock stock = stockRepo.findByName(stockName);
		long currentAvailability = stock.getAvailable();
		if (BUY.equals(orderType)) {
			currentAvailability -= orderDetails.getQuantity();
		} else {
			currentAvailability += orderDetails.getQuantity();
		}
		stock.setAvailable(currentAvailability);
		stockRepo.save(stock);

		ExecutedOrder eOrder = new ExecutedOrder();
		eOrder.setQuantity(orderDetails.getQuantity());
		eOrder.setExecutedPrice(currentPrice.doubleValue());
		eOrder.setStockName(stockName);

		return eOrder;
	}
	
	@GetMapping("/user/{username}/history")
	public List<UserOrder> userOrders(
			@PathVariable("username") String username) {
		List<Order> userOrds = orderRepo.findByUserName(username);
		List<UserOrder> response = new ArrayList<>();
		for(Order o: userOrds) {
			UserOrder order = new UserOrder();
			order.setStockName(o.getStockName());
			order.setQuantity(o.getQuantity());
			order.setOrderType(o.getBuyOrSell());
			order.setExecutedPrice(o.getCurrentPrice().doubleValue());
			order.setTimestamp(o.getDate());
			response.add(order);
		}
		return response;
	}
	
}
