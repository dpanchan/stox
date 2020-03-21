package com.stock.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.user.models.database.Subscription;


public interface SubscriptionDAO extends JpaRepository<Subscription, Long> {
	List<Subscription> findByUsername(String username);
}
