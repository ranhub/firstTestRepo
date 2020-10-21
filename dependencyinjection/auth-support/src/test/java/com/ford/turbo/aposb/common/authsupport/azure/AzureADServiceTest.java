package com.ford.turbo.aposb.common.authsupport.azure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.time.LocalDateTime;

import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.netflix.hystrix.exception.HystrixBadRequestException;

@RunWith(MockitoJUnitRunner.class)
public class AzureADServiceTest {
	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private CredentialsSource azureCredentialSource;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;
	
	@Mock
	private AzureADTokenCommand command;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Captor
	private ArgumentCaptor<URI> uriCaptor;

	@Captor
	private ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> httpEntityCaptor;
	
	private AzureADService service;
	private AzureADService serviceCaching;
	private AzureADService serviceSpy;
	private AzureADService serviceCachingSpy;
	private static String hystrixGroupKey = "group-key";
	private static String hystrixGroupKey2 = "group-key-2";
	
	@Before
	public void setup() {
		service = new AzureADService(traceInfo, restTemplate, false);
		serviceSpy = Mockito.spy(service);
		
		serviceCaching = new AzureADService(traceInfo, restTemplate, true);
		serviceCachingSpy = Mockito.spy(serviceCaching);
	}
	
	@Test
	public void shouldCallExecute_NoCaching() throws InterruptedException {
		Mockito.doReturn(command).when(serviceSpy)
			.getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		
		serviceSpy.getToken(azureCredentialSource, hystrixGroupKey);
		serviceSpy.getToken(azureCredentialSource, hystrixGroupKey);
		
		verify(serviceSpy, times(2)).getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		verify(command, times(2)).execute();
	}
	
	@Test
	public void shouldCallExecute_andReturnToken() {
		Mockito.doReturn(command).when(serviceCachingSpy)
			.getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		
		AzureADToken expectedToken = new AzureADToken("bearer-token", LocalDateTime.now());
		Mockito.when(command.execute()).thenReturn(expectedToken);
		
		AzureADToken token = serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey);
		
		assertThat(token).isEqualTo(expectedToken);
		verify(serviceCachingSpy).getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		verify(command).execute();
	}
	
	@Test
	public void shouldCallExecuteOnlyOnce_whenExpiryIsAfter30Seconds_andReturnCachedToken() {
		Mockito.doReturn(command).when(serviceCachingSpy)
			.getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		
		AzureADToken expectedToken = new AzureADToken("bearer-token", LocalDateTime.now().plusMinutes(15));
		Mockito.when(command.execute()).thenReturn(expectedToken);
		
		serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey);
		AzureADToken token = serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey);
		
		assertThat(token).isEqualTo(expectedToken);
		verify(serviceCachingSpy, times(1)).getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		verify(command, times(1)).execute();
	}
	
	@Test
	public void shouldCallExecuteTwice_whenExpiryIsAfter30Seconds_andDifferentGroupKey() {
		Mockito.doReturn(command).when(serviceCachingSpy)
			.getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		Mockito.doReturn(command).when(serviceCachingSpy)
			.getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey2);
		
		AzureADToken expectedToken = new AzureADToken("bearer-token", LocalDateTime.now().plusMinutes(15));
		Mockito.when(command.execute()).thenReturn(expectedToken);
		
		serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey);
		serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey2);
		AzureADToken token = serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey);
		
		assertThat(token).isEqualTo(expectedToken);
		verify(serviceCachingSpy, times(1)).getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		verify(serviceCachingSpy, times(1)).getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey2);
		verify(command, times(2)).execute();
	}
	
	@Test
	public void shouldCallExecuteOnlyOnce_whenMultipleThreadsInvoke_andReturnCachedToken() throws InterruptedException {
		Mockito.doReturn(command).when(serviceCachingSpy)
			.getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		
		AzureADToken expectedToken = new AzureADToken("bearer-token", LocalDateTime.now().plusMinutes(15));
		Mockito.when(command.execute())
			.thenAnswer(new Answer<AzureADToken>() {
				@Override
				   public AzureADToken answer(InvocationOnMock invocation) throws InterruptedException{
				     Thread.sleep(100);
				     return expectedToken;
				   }
			});
		
		Thread t1 = getThread();
		Thread t2 = getThread();
		Thread t3 = getThread();
		Thread t4 = getThread();
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
		t1.join();
		t2.join();
		t3.join();
		t4.join();
		
		verify(serviceCachingSpy, times(1)).getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		verify(command, times(1)).execute();
	}
	
	@Test
	public void shouldCallExecuteTwice_whenMultipleThreadsInvoke_andOneCallFails_andReturnCachedToken() throws InterruptedException {
		Mockito.doReturn(command).when(serviceCachingSpy)
			.getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		
		AzureADToken expectedToken = new AzureADToken("bearer-token", LocalDateTime.now().plusMinutes(15));
		Mockito.when(command.execute())
			.thenAnswer(new Answer<AzureADToken>() {
				@Override
				   public AzureADToken answer(InvocationOnMock invocation) throws InterruptedException{
				     Thread.sleep(100);
				     throw new HystrixBadRequestException("Bad Request");
				   }
			})
			.thenAnswer(new Answer<AzureADToken>() {
				@Override
				   public AzureADToken answer(InvocationOnMock invocation) throws InterruptedException{
				     Thread.sleep(100);
				     return expectedToken;
				   }
			});
		
		Thread t1 = getThread();
		Thread t2 = getThread();
		Thread t3 = getThread();
		Thread t4 = getThread();
		
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		
		t1.join();
		t2.join();
		t3.join();
		t4.join();
		
		verify(serviceCachingSpy, times(2)).getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		verify(command, times(2)).execute();
	}

	private Thread getThread() {
		return new Thread(() -> {
			serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey);
		});
	}
	
	@Test
	public void shouldFetchNewToken_ifExpiryIsWithin_30Seconds() {
		Mockito.doReturn(command).when(serviceCachingSpy)
			.getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		
		AzureADToken expectedToken = new AzureADToken("bearer-token", LocalDateTime.now().plusSeconds(29));
		Mockito.when(command.execute()).thenReturn(expectedToken);
		
		serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey);
		AzureADToken token = serviceCachingSpy.getToken(azureCredentialSource, hystrixGroupKey);
		
		assertThat(token).isEqualTo(expectedToken);
		verify(serviceCachingSpy, times(2)).getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);
		verify(command, times(2)).execute();
	}
	
	@Test
	public void shouldThrowAssertionError_whenCredentialSourceIsNull() throws Exception {
		exception.expect(RuntimeException.class);
		exception.expectMessage("CredentialSource is required");
		
		service.getToken(null, null);
	}
	
	@Test
	public void shouldThrowAssertionError_whenHystrixGroupKeyIsNull() throws Exception {
		exception.expect(RuntimeException.class);
		exception.expectMessage("HystrixGroupKey is required");
		
		service.getToken(azureCredentialSource, null);
	}
}
