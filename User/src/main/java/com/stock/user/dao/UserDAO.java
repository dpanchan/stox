package com.stock.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.user.models.database.User;

public interface UserDAO extends JpaRepository<User, Integer> {
	User findByUsername(String username);
}
