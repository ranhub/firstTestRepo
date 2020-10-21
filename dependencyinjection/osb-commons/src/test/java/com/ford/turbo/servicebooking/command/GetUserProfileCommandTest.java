package com.ford.turbo.servicebooking.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

import org.junit.Before;
import org.junit.BeforeClass;
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

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.exception.CustomerIdNotFoundException;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfile;
import com.ford.turbo.servicebooking.models.ngsdn.UserProfileResponse;

@RunWith(MockitoJUnitRunner.class)
public class GetUserProfileCommandTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS) 
    private TraceInfo traceInfo;
	
	@Mock
	private RestTemplate restTemplate;

	private String euToken = "eu-auth-token";
	private String euAppId = "eu-application-id";
	
	private static CredentialsSource ngsdnCredentialSource;
	private GetUserProfileCommand command;
	
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		ngsdnCredentialSource = new CredentialsSource("NGSDN");
	}
	
	@Before
	public void beforeEachTest() {
		command = new GetUserProfileCommand(traceInfo, restTemplate, euToken , euAppId, ngsdnCredentialSource);
	}

    @Test
    public void should_return_userProfile() throws Exception {
    	UserProfileResponse userProfileResponse = getUserProfile();
    	
    	ResponseEntity<UserProfileResponse> value = ResponseEntity.ok(userProfileResponse);
		when(restTemplate.exchange(contains("users"), eq(GET), any(HttpEntity.class), eq(UserProfileResponse.class)))
			.thenReturn(value);
        
		UserProfile actualResponse = command.doRun();
        
        assertThat(actualResponse).isEqualTo(userProfileResponse.getProfile());
        
        verify(restTemplate).exchange(contains("users"), eq(GET), httpEntityCaptor.capture(), eq(UserProfileResponse.class));
        
        assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
    }
    
    @Test(expected = CustomerIdNotFoundException.class)
    public void shouldThrow_CustomerIdNotFoundException_whenStatusNot200() throws Exception {
    	UserProfileResponse userProfileResponse = getUserProfile();
    	userProfileResponse.setStatus(400);
    	when(restTemplate.exchange(contains("users"), eq(GET), any(HttpEntity.class), eq(UserProfileResponse.class)))
			.thenReturn(new ResponseEntity<UserProfileResponse>(userProfileResponse, HttpStatus.OK));
    	
    	command.doRun();
    }
    
    @Test
    public void shouldReturnHeaders() {
    	GetUserProfileCommand command = new GetUserProfileCommand(traceInfo, restTemplate, euToken , euAppId, ngsdnCredentialSource);
    	
    	HttpHeaders requestHeaders = command.getRequestHeaders();
    	
    	assertThat(requestHeaders.get("auth-token").get(0)).isEqualTo(euToken);
    	assertThat(requestHeaders.get("Application-Id").get(0)).isEqualTo(euAppId);
    	assertThat(requestHeaders.get("Content-Type").get(0)).isEqualTo(MediaType.APPLICATION_JSON.toString());
    	assertThat(requestHeaders.get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(requestHeaders.get("X-B3-SpanId").get(0)).isEqualTo("0");
    }
    
    @Test
    public void shouldReturnHeaders_withNoApplicationId() {
    	GetUserProfileCommand command = new GetUserProfileCommand(traceInfo, restTemplate, euToken , null, ngsdnCredentialSource);
    	
    	HttpHeaders requestHeaders = command.getRequestHeaders();
    	
    	assertThat(requestHeaders.get("Application-Id")).isNull();
    }

	private UserProfileResponse getUserProfile() {
		UserProfileResponse userProfileResponse = new UserProfileResponse();
    	UserProfile userProfile= new UserProfile();
    	userProfile.setTitle("Mr.");
    	userProfile.setFirstName("ford");
    	userProfile.setLastName("eu");
    	userProfile.setUserId("fordqaeu@gmail.com");
    	userProfileResponse.setStatus(200);
    	userProfileResponse.setProfile(userProfile);
		return userProfileResponse;
	}
}