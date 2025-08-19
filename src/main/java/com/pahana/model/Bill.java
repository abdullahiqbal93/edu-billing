package com.pahana.model;

public class Bill {
	private int id;
	private int customerId;
	private int totalUnits;
	private double totalAmount;
	private String createdAt;
	private java.util.List<BillItem> items;

	public Bill() {
	}

	public Bill(int id, int customerId, int totalUnits, double totalAmount, String createdAt) {
		this.id = id;
		this.customerId = customerId;
		this.totalUnits = totalUnits;
		this.totalAmount = totalAmount;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getTotalUnits() {
		return totalUnits;
	}

	public void setTotalUnits(int totalUnits) {
		this.totalUnits = totalUnits;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public java.util.List<BillItem> getItems() {
		return items;
	}

	public void setItems(java.util.List<BillItem> items) {
		this.items = items;
	}
}
