package com.vegmarket.shoppingcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vegmarket.shoppingcart.model.Product;
import com.vegmarket.shoppingcart.model.User;

@Repository
public interface  ProductRepository extends JpaRepository<Product, Integer>{

}
