package com.ford.turbo.aposb.common.authsupport.validator;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.ActiveDirectoryBearerTokenException;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.springframework.beans.factory.annotation.Qualifier;

import com.microsoft.aad.adal4j.AuthenticationCallback;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AcquireAzureAccessTokenCommand extends TimedHystrixCommand<String> {

    private static final String HYSTRIX_GROUP_KEY = "AzureAD";
    private static final int EXPIRY_LIMIT_IN_MILLISECONDS = 300000;

    protected static AtomicReference<AuthenticationResult> authenticationResultRef = new AtomicReference<>();

    private final String AAD_CLIENT_SECRET;
    private final String AAD_CLIENT_ID;
    private final String AAD_SERVICE_URI;
    private final String AAD_RESOURCE_URI;

    public AcquireAzureAccessTokenCommand(
            TraceInfo traceInfo,
            @Qualifier("FIG_AUTHENTICATION") CredentialsSource figCredentials) {
        super(traceInfo, HYSTRIX_GROUP_KEY);
        this.AAD_CLIENT_ID = figCredentials.getExtraCredentials().get("activeDirectoryClientId").toString();
        this.AAD_CLIENT_SECRET = figCredentials.getExtraCredentials().get("activeDirectoryClientSecret").toString();
        this.AAD_SERVICE_URI = figCredentials.getExtraCredentials().get("activeDirectoryEndpoint").toString();
        this.AAD_RESOURCE_URI = figCredentials.getExtraCredentials().get("activeDirectoryResource").toString();
    }

    @Override
    public String doRun() {

        if (notExpired()) {
            return authenticationResultRef.get().getAccessToken();
        }

        try {
            AuthenticationResult authenticationResult = getAuthenticationResult();
            authenticationResultRef.set(authenticationResult);
            String accessToken = authenticationResult.getAccessToken();
            log.info("Bearer token expires at " + authenticationResult.getExpiresOnDate().toString());

            return accessToken;
        } catch (Exception e) {
            clearCache(); // something bad happened, reset the reference
            throw new ActiveDirectoryBearerTokenException("Failed to obtain new bearer token: " + e.getMessage(), e);
        }
    }

    private boolean notExpired() {
        if (authenticationResultRef.get() != null) {
            Date currentTimeDate = new Date();
            Date authExpiryDate = authenticationResultRef.get().getExpiresOnDate();
            return authExpiryDate.after(currentTimeDate) &&
                    authExpiryDate.getTime() - currentTimeDate.getTime() > EXPIRY_LIMIT_IN_MILLISECONDS;
        } else {
            return false;
        }
    }

    protected AuthenticationResult getAuthenticationResult() throws InterruptedException, ExecutionException, MalformedURLException {
        return new AuthenticationContext(AAD_SERVICE_URI, true, threadPool.getExecutor())
                .acquireToken(AAD_RESOURCE_URI,
                        new ClientCredential(AAD_CLIENT_ID, AAD_CLIENT_SECRET),
                        authenticationCallbackForActiveDirectory())
                .get();
    }

    private AuthenticationCallback authenticationCallbackForActiveDirectory() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult result) {
                log.info("Validation bearer token received");
            }

            @Override
            public void onFailure(Throwable e) {
                log.error("Rejecting auth token due to HTTP error on validation", e);
                throw new ActiveDirectoryBearerTokenException("Failed to obtain new bearer token", e);
            }
        };
    }

    public static void clearCache() {
        authenticationResultRef.set(null);
    }
}
