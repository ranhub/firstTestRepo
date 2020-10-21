package com.ford.turbo.aposb.common.authsupport.environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.aposb.common.authsupport.environment.model.VCAPServices;

public class VCAPServicesEnvironment {
	
	public VCAPServices getVCAPServices() {
		try {
			return new ObjectMapper().readValue(getVCAPServicesValue(), VCAPServices.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getVCAPServicesValue() {
		String vcap = getEnvValue("VCAP_SERVICES");
		return newExpressionParser().parse(vcap);
	}
	
	protected String getEnvValue(String name) {
		return System.getenv(name);
	}
	
	protected ExpressionParser newExpressionParser() {
		return new ExpressionParser();
	}

}
