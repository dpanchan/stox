package com.stock.user;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.stock.user.controller.OrderController;
import com.stock.user.dao.SubscriptionDAO;
import com.stock.user.dao.UserDAO;
import com.stock.user.models.database.Subscription;
import com.stock.user.models.database.User;
import com.stock.user.models.web.OrderDetails;
import com.stock.user.service.OrderService;

@EnableDiscoveryClient
@SpringBootApplication
public class UserApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder b) {
		return b.build();
	}

	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private SubscriptionDAO subscriptionDAO;
	
	@Autowired
	private OrderService orderService;
	
	@Override
	public void run(String... args) throws Exception {
		
		// dummy user creation
		User user = new User(); //database model
		
		user.setFirstName("Test");
		user.setLastName("User");
		user.setUsername("test");
		user.setPassword("123");
		user.setBalance(BigDecimal.valueOf(100000.00)); // dummy balance
		
		userDAO.save(user);
		
		//dummy user subscription
		Subscription us = new Subscription();
		us.setStockname("BABA");
		us.setUsername("test");
		us.setMinPrice(200.00);
		us.setMaxPrice(250.00);
		
		subscriptionDAO.save(us);
	
		Thread.sleep(10000);
		
		OrderDetails buy = new OrderDetails();
		buy.setUsername("test");
		buy.setStock("AAPL");
		buy.setQuantity(10);
		buy.setOrderType("BUY");
		
		orderService.execute(buy);
		
		OrderDetails sell = new OrderDetails();
		sell.setUsername("test");
		sell.setStock("AAPL");
		sell.setQuantity(5);
		sell.setOrderType("SELL");
		
		orderService.execute(sell);
	}
}
