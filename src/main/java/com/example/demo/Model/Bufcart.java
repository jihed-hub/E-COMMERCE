package com.example.demo.Model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
public class Bufcart implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bufcartId;
	@Column(nullable = true)
	private int orderId;
	private String email;
	private Date dateAdded;
	private int quantity;
	private double price;
	private int productId;
	private String productname;
	
	public Bufcart() {
		super();
	}

	public Bufcart(int bufcartId, int orderId, String email, Date dateAdded, int quantity, double price, int productId,
			String productname) {
		super();
		this.bufcartId = bufcartId;
		this.orderId = orderId;
		this.email = email;
		this.dateAdded = dateAdded;
		this.quantity = quantity;
		this.price = price;
		this.productId = productId;
		this.productname = productname;
	}

	public int getBufcartId() {
		return bufcartId;
	}

	public void setBufcartId(int bufcartId) {
		this.bufcartId = bufcartId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductname() {
		return productname;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	@Override
	public String toString() {
		return "Bufcart [bufcartId=" + bufcartId + ", orderId=" + orderId + ", email=" + email + ", dateAdded="
				+ dateAdded + ", quantity=" + quantity + ", price=" + price + ", productId=" + productId
				+ ", productname=" + productname + "]";
	}
	
	
}
