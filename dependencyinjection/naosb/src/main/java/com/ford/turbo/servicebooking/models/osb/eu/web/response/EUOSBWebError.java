package com.ford.turbo.servicebooking.models.osb.eu.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EUOSBWebError {
	private String message;
	private String code;
	private String statusCode;
}

