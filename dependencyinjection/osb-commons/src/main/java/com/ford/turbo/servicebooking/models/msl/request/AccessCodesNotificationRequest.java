package com.ford.turbo.servicebooking.models.msl.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessCodesNotificationRequest {
	
	private String marketCode;
	private String email;
	private Boolean osbSiteTermsRequired;
}
