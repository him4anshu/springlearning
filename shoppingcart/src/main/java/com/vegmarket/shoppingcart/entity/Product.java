package com.vegmarket.shoppingcart.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Products")
public class Product {

	 	@Id
	    @Column(name = "Code", length = 20, nullable = false)
	    private String code;
	 
	    @Column(name = "Name", length = 255, nullable = false)
	    private String name;
	 
	    @Column(name = "Price", nullable = false)
	    private double price;
	 
	    @Lob
	    @Column(name = "Image", length = Integer.MAX_VALUE, nullable = true)
	    private byte[] image;
	     
	    @Column(name = "Create_Date", nullable = false)
	    private Date createDate;
	 
}
