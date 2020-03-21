package com.stock.user.models.web;

public class UserLogin {

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	private String username;
	private String password;

	public void setPassword(String password) {
		this.password = password;
	}

}
