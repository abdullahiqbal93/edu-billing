package com.pahana.model;

public class Customer {

	private int id;
	private String accountNumber;
	private String name;
	private String address;
	private String telephone;
	private String email;

	public Customer() {
	}

	public Customer(int id, String accountNumber, String name, String address, String telephone, String email) {
		this.id = id;
		this.accountNumber = accountNumber;
		this.name = name;
		this.address = address;
		this.telephone = telephone;
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPhone() {
		return telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
