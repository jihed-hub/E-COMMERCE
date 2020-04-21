package com.example.demo.Model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "PlaceOrder")
public class PlaceOrder implements Serializable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int orderId;
	private String email, orderStatus;
	private Date orderDate;
	private double totalCost;
	
	public PlaceOrder() {
		super();
	}
	
	public PlaceOrder(int orderId, String email, String orderStatus, Date orderDate, double totalCost) {
		super();
		this.orderId = orderId;
		this.email = email;
		this.orderStatus = orderStatus;
		this.orderDate = orderDate;
		this.totalCost = totalCost;
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

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	@Override
	public String toString() {
		return "PlaceOrder [orderId=" + orderId + ", email=" + email + ", orderStatus=" + orderStatus + ", orderDate="
				+ orderDate + ", totalCost=" + totalCost + "]";
	}
	
	
	
	

}
