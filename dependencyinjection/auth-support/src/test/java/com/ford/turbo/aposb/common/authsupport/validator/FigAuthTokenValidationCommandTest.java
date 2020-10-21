package com.ford.turbo.aposb.common.authsupport.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthTokenExpiredException;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthTokenFailedException;
import com.ford.turbo.aposb.common.basemodels.command.exceptions.FigAuthTokenRevokedException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.BadGatewayException;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.common.sharedtests.AuthTestHelper;
import com.netflix.hystrix.exception.HystrixRuntimeException;

@RunWith(MockitoJUnitRunner.class)
public class FigAuthTokenValidationCommandTest {

    private CredentialsSource figCredentials;
    private CredentialsSource appIdsCredentials;
    private String sdnFordNaAppId;

    @Mock
    private Tracer mockTracer;

    private TraceInfo traceInfo;

    @Before
    public void setup() throws IOException {
        traceInfo = new TraceInfo(mockTracer, new TraceKeys());
        figCredentials = new CredentialsSource("FIG_AUTHENTICATION");
        appIdsCredentials = new CredentialsSource("APPLICATION_ID_MAPPINGS");
        AuthTestHelper authTestHelper = new AuthTestHelper(null, null, appIdsCredentials);
        sdnFordNaAppId = authTestHelper.getAppIdFordNA();
    }

    @Test
    public void should_extendTimedHystrixCommand() throws IOException {
        assertThat(TimedHystrixCommand.class.isAssignableFrom(FigAuthTokenValidationCommand.class)).isTrue();
    }

    @Test
    public void should_throwAuthTokenFailedException_whenFigReturnsFailed() {
        String body = "{\"Profile\":null, \"Status\":2}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(responseEntity);
        FigAuthTokenValidationCommand command = new FigAuthTokenValidationCommand(traceInfo, figCredentials, restTemplate);
        command.setAuthToken("");
        try {
            command.execute();
            failBecauseExceptionWasNotThrown(FigAuthTokenFailedException.class);
        } catch (FigAuthTokenFailedException e) {
            assertThat(e.getMessage()).isEqualTo("Auth token validation failed");
        }
    }

    @Test
    public void should_throwAuthTokenExpiredException_whenFigReturnsFailed() {
        String body = "{\"Profile\":null, \"Status\":3}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(responseEntity);
        FigAuthTokenValidationCommand command = new FigAuthTokenValidationCommand(traceInfo, figCredentials, restTemplate);
        command.setAuthToken("");
        try {
            command.execute();
            failBecauseExceptionWasNotThrown(FigAuthTokenExpiredException.class);
        } catch (FigAuthTokenExpiredException e) {
            assertThat(e.getMessage()).isEqualTo("Auth token is expired");
        }
    }

    @Test
    public void should_throwAuthTokenRevokedException_whenFigReturnsFailed() {
        String body = "{\"Profile\":null, \"Status\":4}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(body, HttpStatus.OK);

        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenReturn(responseEntity);
        FigAuthTokenValidationCommand command = new FigAuthTokenValidationCommand(traceInfo, figCredentials, restTemplate);
        command.setAuthToken("");
        try {
            command.execute();
            failBecauseExceptionWasNotThrown(FigAuthTokenRevokedException.class);
        } catch (FigAuthTokenRevokedException e) {
            assertThat(e.getMessage()).isEqualTo("Auth token has been revoked");
        }
    }

    @Test
    public void should_throwBadRequest_when_FigRespondsWithUnmappableJson() {

        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockRestTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("this is unparseable garbage data", HttpStatus.OK));
        FigAuthTokenValidationCommand command = new FigAuthTokenValidationCommand(traceInfo, figCredentials, mockRestTemplate);
        command.setAuthToken("");
        try {
            command.performValidation();
            failBecauseExceptionWasNotThrown(BadGatewayException.class);
        } catch (BadGatewayException e) {
            assertThat(e.getMessage().contains("this is unparseable garbage data"));
        }
    }

    @Test
    public void should_throwBadGatewayException_whenFigHasUnexpectedResponse() {

        RestTemplate restTemplate = mock(RestTemplate.class);
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed");
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class))).thenThrow(exception);
        FigAuthTokenValidationCommand command = new FigAuthTokenValidationCommand(traceInfo, figCredentials, restTemplate);
        command.setAuthToken("");
        try {
            command.execute();
            failBecauseExceptionWasNotThrown(HystrixRuntimeException.class);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            assertThat(cause).isInstanceOf(BadGatewayException.class);
            //assertThat(cause.getMessage()).contains("FIG request failed)");
        }
    }
}