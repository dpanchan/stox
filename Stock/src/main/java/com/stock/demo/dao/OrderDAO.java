package com.stock.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.demo.models.db.Order;

public interface OrderDAO extends JpaRepository<Order, Long> {
	List<Order> findByUserName(String username);
}
