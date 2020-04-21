package com.example.demo.Model;

import java.io.Serializable;
import javax.persistence.*;
@Entity
public class User  implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userid;
	private String email;
	private String username;
	private String password;
	private String usertype;
	private int age;
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
	private Address address;
	
	public User() {
		super();
	}

	public User(int userid, String email, String username, String password, String usertype, int age, Address address) {
		super();
		this.userid = userid;
		this.email = email;
		this.username = username;
		this.password = password;
		this.usertype = usertype;
		this.age = age;
		this.address = address;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "User [userid=" + userid + ", email=" + email + ", username=" + username + ", password=" + password
				+ ", usertype=" + usertype + ", age=" + age + ", address=" + address + "]";
	}
	
	
	
}
