package com.stock.user.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.user.dao.SubscriptionDAO;
import com.stock.user.models.database.Subscription;
import com.stock.user.models.web.UserSubscription;

@RestController
@RequestMapping("/user/stock")
public class SubscriptionController {
	
	@Autowired
	private SubscriptionDAO subscriptionDAO;
	
	// Save the subscribed record in the database
	@PostMapping("/subscribe")
	private String subscribeStocks( @RequestBody UserSubscription body) {
		
		String username = body.getUsername();
		String stockName = body.getStockname();
		double minPrice = body.getMinPrice();
		double maxPrice = body.getMaxPrice();
		
		List<Subscription> subs = subscriptionDAO.findByUsername(username);
		for(Subscription sub: subs) {
			if(sub.getStockname().equals(stockName)) {
				sub.setMaxPrice(maxPrice);
				sub.setMinPrice(minPrice);
				subscriptionDAO.save(sub);
				return "Subscription updated.";
			}
		}
		
		Subscription sub = new Subscription();
		sub.setUsername(username);
		sub.setStockname(stockName);
		sub.setMinPrice(minPrice);
		sub.setMaxPrice(maxPrice);
		subscriptionDAO.save(sub);
		
		return "Subscription successful";
	}
	
	@PostMapping("/unsubscribe")
	public String unsubscribe(@RequestBody UserSubscription unsub) {
		List<Subscription> subs = subscriptionDAO.findByUsername(unsub.getUsername());
		
		for(Subscription sub: subs) {
			if(sub.getStockname().equals(unsub.getStockname())) {
				subscriptionDAO.delete(sub);
			}
		}	
		return "Unsubscribed stock successfully";
	}
	
	@GetMapping("/{username}/subscriptions")
	private List<UserSubscription> getUserSubscriptions(@PathVariable("username") String username) {
		List<Subscription> subs = subscriptionDAO.findByUsername(username);
		List<UserSubscription> response = new ArrayList<>();
		for(Subscription sub: subs) {
			UserSubscription us = new UserSubscription();
			us.setMaxPrice(sub.getMaxPrice());
			us.setMinPrice(sub.getMinPrice());
			us.setStockname(sub.getStockname());
			response.add(us);
		}
		return response;
	}
	
	
}
