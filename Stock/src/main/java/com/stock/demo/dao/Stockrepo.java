package com.stock.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.demo.models.db.Stock;

public interface Stockrepo extends JpaRepository<Stock, Long> {

	Stock findByName(String name);

}
