package com.ford.turbo.servicebooking.models.msl.response;

import java.math.BigDecimal;

import com.ford.turbo.servicebooking.models.osb.OldServiceType;

import lombok.Data;

@Data
public class OldServiceBooking {
	private String name;
	private BigDecimal price;
	private String serviceId;
	private String priceAfterDiscount;
	private OldServiceType subType;
}
