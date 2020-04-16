package com.vegmarket.shoppingcart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ShopLocationMapping {

	@Id
	private long shopLocationId;
	
	@Column
	private String shop24By7Flag;
	
	@Column
	private String shop24_By7Flag;
	
	@Column
	private String shop24_By_7Flag;
	
	@Column
	private String shop_24_By7_Flag;
}
