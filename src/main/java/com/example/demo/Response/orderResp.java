package com.example.demo.Response;

import java.util.List;

import com.example.demo.Model.Bufcart;

public class orderResp {
	
	private int orderId;
	private List<Bufcart> cartList;
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public List<Bufcart> getCartList() {
		return cartList;
	}
	public void setCartList(List<Bufcart> cartList) {
		this.cartList = cartList;
	}
	@Override
	public String toString() {
		return "orderResp [orderId=" + orderId + ", cartList=" + cartList + "]";
	}
	
	

}
