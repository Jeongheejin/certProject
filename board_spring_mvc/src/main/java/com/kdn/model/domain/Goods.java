package com.kdn.model.domain;

import java.io.Serializable;

public class Goods implements Serializable {
	private String proname;
	private int proprice;
	private int quantity;
	private int totalPrice;
	public Goods(){}
	public Goods(String proname, int proprice, int quantity) {
		this.proname = proname;
		this.proprice = proprice;
		this.quantity = quantity;
	}
	public String getProname() {
		return proname;
	}
	public void setProname(String proname) {
		this.proname = proname;
	}
	public int getProprice() {
		return proprice;
	}
	public void setProprice(int proprice) {
		this.proprice = proprice;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getTotalPrice() {
		return quantity*proprice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("proname=").append(proname).append(", proprice=").append(proprice).append(", quantity=")
				.append(quantity).append(", totalPrice=").append(getTotalPrice());
		return builder.toString();
	}
	
}
