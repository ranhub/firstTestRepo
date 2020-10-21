package com.ford.turbo.servicebooking;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ServiceBookingRestTemplate extends RestTemplate{
	
	public ServiceBookingRestTemplate(HttpComponentsClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}
}
