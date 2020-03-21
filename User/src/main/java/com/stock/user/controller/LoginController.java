package com.stock.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stock.user.dao.UserDAO;
import com.stock.user.models.database.User;
import com.stock.user.models.web.UserLogin;

@RestController
public class LoginController {

	@Autowired
	private UserDAO userRepo;

	@PostMapping("/user/login")
	public ResponseEntity<String> loginByUserName(@RequestBody UserLogin loginDetails) {
		String username = loginDetails.getUsername();
		String password = loginDetails.getPassword();
		User dbUser = userRepo.findByUsername(username);
		if (dbUser == null)
			return new ResponseEntity<>("username does not exist", HttpStatus.UNAUTHORIZED);

		String actualPassword = dbUser.getPassword();
		if (!actualPassword.equals(password))
			return new ResponseEntity<>("wrong password", HttpStatus.UNAUTHORIZED);

		return new ResponseEntity<>("login successful", HttpStatus.OK);
	}

}
