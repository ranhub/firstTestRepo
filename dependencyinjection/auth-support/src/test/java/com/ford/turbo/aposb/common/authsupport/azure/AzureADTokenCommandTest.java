package com.ford.turbo.aposb.common.authsupport.azure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.azure.AzureADTokenCommand.AzureADTokenResponse;
import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;

@RunWith(MockitoJUnitRunner.class)
public class AzureADTokenCommandTest {
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
    private CredentialsSource azureADCredentials;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;
	
	@Captor
	private ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> httpEntityCaptor;
	
	@Captor
	private ArgumentCaptor<URI> uriCaptor;
	
	private AzureADTokenCommand command;
	private static String hystrixGroupKey = "group-key";
	private static String baseURI = "http://microsoft.com";
	
	@Before
	public void beforeEachTest() {
		command = new AzureADTokenCommand(traceInfo, restTemplate, azureADCredentials, hystrixGroupKey);
		
		mockCredentials();
	}

	private void mockCredentials() {
		Map<String, Object> extraCredentials = new HashMap<>();
		extraCredentials.put("clientId", "mockClientId");
		extraCredentials.put("resource", "mockresource");
		extraCredentials.put("grant_type", "grantType");
		extraCredentials.put("clientSecret", "mockclient_secret");
		
		when(azureADCredentials.getBaseUri()).thenReturn(baseURI);
		when(azureADCredentials.getExtraCredentials()).thenReturn(extraCredentials);
	}
	
	@Test
	public void shouldReturnToken() throws Exception {
		AzureADTokenResponse tokenResponse = getAzureTokenResponse();
		when(restTemplate.postForEntity(any(URI.class), any(HttpEntity.class), eq(AzureADTokenResponse.class)))
			.thenReturn(ResponseEntity.ok().body(tokenResponse));
		
		AzureADToken azureADToken = command.doRun();
		
		assertEquals(tokenResponse.getAccess_token(), azureADToken.getBearerToken());
		assertNotNull(azureADToken.getExpireDate());
		
		verify(restTemplate).postForEntity(uriCaptor.capture(), httpEntityCaptor.capture(), eq(AzureADTokenResponse.class));
		assertEquals(uriCaptor.getValue().toString(), "http://microsoft.com");
		
		Map<String, List<String>> actualBody = httpEntityCaptor.getValue().getBody();
		
		assertEquals("mockClientId", actualBody.get("client_id").get(0));
		assertEquals("mockresource", actualBody.get("resource").get(0));
		assertEquals("grantType", actualBody.get("grant_type").get(0));
		assertEquals("mockclient_secret", actualBody.get("client_secret").get(0));
	}
	
	private AzureADTokenResponse getAzureTokenResponse() {
		long expiresOnEpochSeconds = 1500925104L;
		
		AzureADTokenResponse tokenResponse = new AzureADTokenResponse();
		tokenResponse.setAccess_token("abccddkdkdkd");
		tokenResponse.setExpires_on(String.valueOf(expiresOnEpochSeconds));
		return tokenResponse;
	}
}
