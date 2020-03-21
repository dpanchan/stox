package com.stock.user.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.user.dao.UserDAO;
import com.stock.user.models.database.User;
import com.stock.user.models.web.Deposit;
import com.stock.user.models.web.UserProfile;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserDAO userRepo;

	@GetMapping("/profile/{username}")
	private ResponseEntity<UserProfile> getByUserName(@PathVariable("username") String username) {
		User user = userRepo.findByUsername(username);
		if (user != null) {
			UserProfile usrProf = new UserProfile();
			usrProf.setBalance(user.getBalance().doubleValue());
			usrProf.setLastName(user.getLastName());
			usrProf.setFirstName(user.getFirstName());
			usrProf.setUsername(user.getUsername());
			return new ResponseEntity<>(usrProf, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/deposit")
	private String depositValue(@RequestBody Deposit deposit) {

		User user = userRepo.findByUsername(deposit.getusername());
		double currBalance = user.getBalance().doubleValue();
		currBalance += deposit.getDeposit();
		user.setBalance(BigDecimal.valueOf(currBalance));
		userRepo.save(user);
		
		return "Deposited";
	}
}
