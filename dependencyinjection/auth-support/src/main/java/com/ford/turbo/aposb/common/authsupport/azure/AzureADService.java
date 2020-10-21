package com.ford.turbo.aposb.common.authsupport.azure;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class AzureADService {
	private TraceInfo traceInfo;
	private RestTemplate restTemplate;
	private boolean cachingEnabled;
	
	private Map<String, AzureADToken> tokens;
	private ReentrantLock commandExecutionLock;
	
	public AzureADService(TraceInfo traceInfo, 
			RestTemplate restTemplate, 
			@Value("${cache.azuread.enabled:false}") boolean cachingEnabled) {
		this.traceInfo = traceInfo;
		this.restTemplate = restTemplate;
		this.cachingEnabled = cachingEnabled;
		
		tokens = new HashMap<>();
		commandExecutionLock = new ReentrantLock();
	}
	
	public AzureADToken getToken(CredentialsSource azureCredentialSource, String hystrixGroupKey) {
		validateParameters(azureCredentialSource, hystrixGroupKey);
		
		if(cachingEnabled) {
			AzureADToken azureADToken = null;
			while(true) {
				Optional<AzureADToken> cachedToken = getCachedToken(hystrixGroupKey);
				
				if(cachedToken.isPresent()) {
					return cachedToken.get();
				}
				
				if(fetchLock()) {
					break;
				}
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// Swallow
				}
			}
			
			try {
				azureADToken = getAzureToken(azureCredentialSource, hystrixGroupKey);
				cacheToken(hystrixGroupKey, azureADToken);
			} finally {
				unlock();
			}
			
			return azureADToken;
		} else {
			return getAzureToken(azureCredentialSource, hystrixGroupKey);
		}
	}
	
	private synchronized boolean fetchLock() {
		if(commandExecutionLock.isLocked() == false) {
			commandExecutionLock.lock();
			return true;
		}
		return false;
	}
	
	private synchronized void unlock() {
		if(commandExecutionLock.isLocked()) {
			commandExecutionLock.unlock();
		}
	}

	private void validateParameters(CredentialsSource azureCredentialSource, String hystrixGroupKey) {
		if(azureCredentialSource == null) {
			throw new RuntimeException("CredentialSource is required");
		}
		
		if(StringUtils.isEmpty(hystrixGroupKey)) {
			throw new RuntimeException("HystrixGroupKey is required");
		}
	}

	private AzureADToken getAzureToken(CredentialsSource azureCredentialSource, String hystrixGroupKey) {
		AzureADTokenCommand command = getAzureADTokenCommand(azureCredentialSource, hystrixGroupKey);

		return command.execute();
	}

	protected AzureADTokenCommand getAzureADTokenCommand(CredentialsSource azureCredentialSource,
			String hystrixGroupKey) {
		AzureADTokenCommand command = new AzureADTokenCommand(traceInfo, restTemplate, azureCredentialSource, hystrixGroupKey);
		return command;
	}
	
	private synchronized void cacheToken(String hystrixGroupKey, AzureADToken azureADToken) {
		tokens.put(hystrixGroupKey, azureADToken);
	}
 
	private Optional<AzureADToken> getCachedToken(String hystrixGroupKey) {
		AzureADToken azureADToken = tokens.get(hystrixGroupKey);
		
		if(azureADToken == null) {
			return Optional.empty();
		}
			
		if(azureADToken.getExpireDate().minusSeconds(30).isBefore(LocalDateTime.now())) {
			tokens.remove(hystrixGroupKey);
			return Optional.empty();
		}
		
		return Optional.of(azureADToken);
	}
}