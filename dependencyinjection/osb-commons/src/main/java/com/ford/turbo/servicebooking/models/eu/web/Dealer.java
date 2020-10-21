package com.ford.turbo.servicebooking.models.eu.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dealer {
	
	private String dealerName;
	private String dealerCode;
}
