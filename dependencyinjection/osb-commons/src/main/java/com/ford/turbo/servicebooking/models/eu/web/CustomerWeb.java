package com.ford.turbo.servicebooking.models.eu.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWeb {

	private String lastName;
	private String title;
	private String contactNumber;
	private String firstName;
	private String email;
	private String phone;
}
