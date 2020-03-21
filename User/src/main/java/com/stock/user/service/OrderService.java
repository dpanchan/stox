package com.stock.user.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.stock.user.dao.AssetDAO;
import com.stock.user.dao.UserDAO;
import com.stock.user.models.database.User;
import com.stock.user.models.database.UserStockInfo;
import com.stock.user.models.web.ExecutedOrder;
import com.stock.user.models.web.OrderDetails;

@Service
public class OrderService {

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private AssetDAO assetDAO;

	@Autowired
	DiscoveryClient dc;

	@Autowired
	RestTemplate restTemplate;
	
	public ExecutedOrder execute(OrderDetails orderDetails) {
		String orderType = orderDetails.getOrderType().toLowerCase();

		List<ServiceInstance> stockInstances = dc.getInstances("STOCK");
		ServiceInstance stockSvc = stockInstances.get(0);

		String stockSvcUrl = stockSvc.getUri().toString();
		String stockOrderUrl = stockSvcUrl + "/stocks/order/" + orderType;
				
		ResponseEntity<ExecutedOrder> response = restTemplate.postForEntity(stockOrderUrl, orderDetails, ExecutedOrder.class);
		ExecutedOrder confirmedOrder = response.getBody();
		double transactionAmt = confirmedOrder.getExecutedPrice() * confirmedOrder.getQuantity();
		
		
		String stockName = orderDetails.getStock();
		int orderQty = orderDetails.getQuantity();
		
		String username = orderDetails.getUsername();
		User user = userDAO.findByUsername(username);
		double currBalance = user.getBalance().doubleValue();
		if ("sell".equals(orderType)) {
			currBalance += transactionAmt;
		} else {
			currBalance -= transactionAmt;
		}
		user.setBalance(BigDecimal.valueOf(currBalance));
		
		userDAO.save(user);
		
		List<UserStockInfo> userStocks = assetDAO.findByUsername(username);
		boolean isANewStock = true;
		for (UserStockInfo userStock: userStocks) {
			if (userStock.getStockName().equals(stockName)) {
				
				isANewStock = false;
				
				int currQuantity = userStock.getQuantity();
				if ("sell".equals(orderType)) {
					currQuantity -= orderQty;
				} else {
					currQuantity += orderQty;
				}
				userStock.setQuantity(currQuantity);
				if (currQuantity == 0) {
					assetDAO.delete(userStock);
				} else {
					assetDAO.save(userStock);
				}
				break;
			}
		}
		
		if (isANewStock) {
			UserStockInfo newStock = new UserStockInfo();
			newStock.setQuantity(orderQty);
			newStock.setStockName(stockName);
			newStock.setUsername(username);
			assetDAO.save(newStock);
		}
		
		return confirmedOrder;
	}
}