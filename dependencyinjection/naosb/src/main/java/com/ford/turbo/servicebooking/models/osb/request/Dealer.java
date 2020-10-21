package com.ford.turbo.servicebooking.models.osb.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dealer {
    private String dealerCode;
	private String name;
	private String phone;
	private String address;
}
