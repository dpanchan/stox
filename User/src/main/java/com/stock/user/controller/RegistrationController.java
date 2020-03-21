package com.stock.user.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stock.user.dao.UserDAO;
import com.stock.user.models.database.User;
import com.stock.user.models.web.UserRegistration;

@RestController
public class RegistrationController {

	@Autowired
	private UserDAO userRepo;

	@PostMapping("/user/register")
	private ResponseEntity<String> registerUser(@RequestBody UserRegistration userDetails) {

		User user = new User();

		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setUsername(userDetails.getUsername());
		user.setPassword(userDetails.getPassword());
		user.setBalance(BigDecimal.valueOf(0));

		userRepo.save(user);
		return new ResponseEntity<>("Registration complete", HttpStatus.OK);
	}
}
