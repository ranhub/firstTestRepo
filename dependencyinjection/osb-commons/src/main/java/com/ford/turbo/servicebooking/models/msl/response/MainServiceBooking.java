package com.ford.turbo.servicebooking.models.msl.response;

import java.math.BigDecimal;

public class MainServiceBooking {
	private String name;
	private BigDecimal price;
	private String serviceId;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
}
