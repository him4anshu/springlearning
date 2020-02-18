package com.vegmarket.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vegmarket.shoppingcart.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{

}
