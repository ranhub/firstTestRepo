package com.ford.turbo.servicebooking.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.util.ServiceAuthenticationWrapper;
import com.ford.turbo.aposb.common.authsupport.util.ServiceAuthenticationWrapper.AuthenticationMethods;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.response.VinLookupDetailsResponse;
import com.ford.turbo.servicebooking.models.msl.response.VinLookupResponse;
import com.ford.turbo.servicebooking.utils.Utilities;

@RunWith(MockitoJUnitRunner.class)
public class VinLookupCommandTest {

	private String vin = "12345678901234567";
	private String authToken = "auth-token";
	private String bearerToken = "bearer-token";
	private String figApplicationId = "application-id";
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";
    private ServiceAuthenticationWrapper serviceAuthenticationWrapper;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TraceInfo traceInfo;
    
    private String vinlookupServiceHost = "vinlookup.com";
    private VinLookupCommand command;
    
    @Captor
    private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
    
    @Before
    public void beforeEachTest() {
    	serviceAuthenticationWrapper = new ServiceAuthenticationWrapper(AuthenticationMethods.FIG);
    	serviceAuthenticationWrapper.setFigAuthorizationToken(authToken);
    	serviceAuthenticationWrapper.setApplicationId(figApplicationId);
    	command = new VinLookupCommand(traceInfo, restTemplate, vin, serviceAuthenticationWrapper, vinlookupServiceHost);
	}
    
    @Test
    public void shouldReturnVinlookupResponse() throws Exception {
    	ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern,	VinLookupCommand.class);
    	VinLookupResponse vinlookupResponse = getVinlookupResponse();
    	
		when(restTemplate.exchange(contains("v1/vins/" + vin + "/detail"), eq(GET), any(), eq(VinLookupResponse.class)))
    		.thenReturn(new ResponseEntity<>(vinlookupResponse, HttpStatus.OK));
    	
    	VinLookupDetailsResponse response = command.doRun();
    	String logs = capturedLogs.toString();

    	assertThat(response).isEqualTo(vinlookupResponse.getValue());
    	verify(restTemplate).exchange(contains("v1/vins/" + vin + "/detail"), eq(GET), httpEntityCaptor.capture(), eq(VinLookupResponse.class));
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("auth-token").get(0)).isEqualTo(authToken);
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("application-id").get(0)).isEqualTo(figApplicationId);
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("Content-Type").get(0)).isEqualTo(MediaType.APPLICATION_JSON.toString());
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
    	
    	assertThat(logs).containsPattern("DEBUG .*VinLookupCommand *: Response body*");
    }
    
    private VinLookupResponse getVinlookupResponse() {
    	return VinLookupResponse.builder().value(new VinLookupDetailsResponse()).build();
    }
    
    @Test
    public void shouldSetAuthTokenHeader() {
    	serviceAuthenticationWrapper = new ServiceAuthenticationWrapper(AuthenticationMethods.FIG);
    	serviceAuthenticationWrapper.setFigAuthorizationToken(authToken);
    	
    	HttpHeaders headers = new HttpHeaders();
    	command = new VinLookupCommand(traceInfo, restTemplate, vinlookupServiceHost, serviceAuthenticationWrapper, vinlookupServiceHost);
    	command.populateAuthHeader(headers);
    	
    	assertThat(headers.get("auth-token").get(0)).isEqualTo(authToken);
    	assertThat(headers.get("Authorization")).isNull();
    }
    
    @Test
    public void shouldSetBearerTokenHeader() {
    	serviceAuthenticationWrapper = new ServiceAuthenticationWrapper(AuthenticationMethods.OAUTH2);
    	serviceAuthenticationWrapper.setOauthBearerToken(bearerToken);
    	
    	HttpHeaders headers = new HttpHeaders();
    	command = new VinLookupCommand(traceInfo, restTemplate, vinlookupServiceHost, serviceAuthenticationWrapper, vinlookupServiceHost);
    	command.populateAuthHeader(headers);
    	
    	assertThat(headers.get("Authorization").get(0)).isEqualTo("Bearer " + bearerToken);
    	assertThat(headers.get("auth-token")).isNull();
    }
}
