package com.ford.turbo.aposb.common.authsupport.environment;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.core.JsonParseException;
import org.springframework.cloud.cloudfoundry.com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.core.io.ClassPathResource;

public class VCAPServicesEnvironmentTest {

	VCAPServicesEnvironment vcapServicesEnvironment;
	ExpressionParser mockParser;

	@Before
	public void setup() throws Exception {
		vcapServicesEnvironment = Mockito.spy(new VCAPServicesEnvironment());
		mockParser = Mockito.mock(ExpressionParser.class);
	}
	
	@Test
	public void shouldReturnCorrectVCAPServicesValue() {
		when(vcapServicesEnvironment.getEnvValue("VCAP_SERVICES")).thenReturn("VCAP_SERVICES_VALUE");
		when(vcapServicesEnvironment.newExpressionParser()).thenReturn(mockParser);
		when(mockParser.parse("VCAP_SERVICES_VALUE")).thenReturn("PARSED_VCAP_VALUE");
		
		assertThat(vcapServicesEnvironment.getVCAPServicesValue(),equalTo("PARSED_VCAP_VALUE"));
	}
	
	@Test
	public void shouldReturnCorrectVCAPServices() throws JsonParseException, JsonMappingException, IOException {
		ClassPathResource resource = new ClassPathResource("VCAP_Application_ID_Mappings");
		String mockedVCAPJson = IOUtils.toString(resource.getInputStream());
		when(vcapServicesEnvironment.getVCAPServicesValue()).thenReturn(mockedVCAPJson);
		
		assertThat(vcapServicesEnvironment.getVCAPServices().getUserProvidedService("APPLICATION_ID_MAPPINGS"), notNullValue());
	}
}
