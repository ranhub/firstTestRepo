package com.ford.turbo.aposb.common.authsupport.userprofile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import com.ford.turbo.aposb.common.basemodels.model.CSDNError;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.authsupport.validator.FigAuthTokenValidationCommand;
import com.ford.turbo.aposb.common.authsupport.validator.ValidateAuthTokenCommand;
import com.ford.turbo.common.sharedtests.AuthTestHelper;
import com.netflix.config.ConfigurationManager;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileTests {

    private static String tokenNa;
    private static String sdnAppIdNa;
    private static String tokenEu;
    private static String sdnAppIdEu;
    private static String tokenAp;
    private static String sdnAppIdAp;

    private CredentialsSource figSource;

    private ObjectFactory<ValidateAuthTokenCommand> commandFactory;

    private ContinentCodeExtractor continentCodeExtractor;

    @Mock
    private Tracer mockTracer;

    private TraceInfo traceInfo;
    
    private ByteArrayOutputStream capturedLogs;
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";

    @BeforeClass
    public static void fetchAuthToken() throws IOException {
        AuthTestHelper authTestHelper = new AuthTestHelper(new CredentialsSource("NGSDN"), new CredentialsSource("LIGHTHOUSE"), new CredentialsSource("APPLICATION_ID_MAPPINGS"));
        sdnAppIdNa = authTestHelper.getAppIdFordNA();
        tokenNa = authTestHelper.getToken(sdnAppIdNa);
        assertThat(tokenNa).isNotEmpty();
        assertThat(sdnAppIdNa).isNotEmpty();

        sdnAppIdEu = authTestHelper.getAppIdFordEU();
        tokenEu = authTestHelper.getToken(sdnAppIdEu);
        assertThat(tokenEu).isNotEmpty();
        assertThat(sdnAppIdEu).isNotEmpty();

        sdnAppIdAp = authTestHelper.getAppIdFordAP();
        tokenAp = authTestHelper.getToken(sdnAppIdAp);
        assertThat(tokenAp).isNotEmpty();
        assertThat(sdnAppIdAp).isNotEmpty();
    }

    @Before
    public void setUp() throws IOException {
    	capturedLogs = given_requestContentsBeingLogged(Level.INFO);
    	
        Span fakeSpan = Span.builder().build();
        traceInfo = new TraceInfo(mockTracer, new TraceKeys());

        when(mockTracer.getCurrentSpan()).thenReturn(fakeSpan);
        when(mockTracer.close(any())).thenReturn(fakeSpan);

        continentCodeExtractor = new ContinentCodeExtractor(new CredentialsSource("APPLICATION_ID_MAPPINGS"));
        figSource = new CredentialsSource("FIG_AUTHENTICATION");

        commandFactory =
                () -> new FigAuthTokenValidationCommand(traceInfo, figSource, new RestTemplate());

        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.ValidateAuthTokenValidationCommand.execution.isolation.thread.timeoutInMilliseconds", 120 * 1000);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.FigAuthTokenValidationCommand.execution.isolation.thread.timeoutInMilliseconds", 120 * 1000);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.GetUserProfileCommand.execution.isolation.thread.timeoutInMilliseconds", 120 * 1000);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.UpdateUserProfileCommand.execution.isolation.thread.timeoutInMilliseconds", 120 * 1000);
    }

    @Test
    public void should_retrieveUserProfile_andNotLogBody(){
    	System.out.println(sdnAppIdEu + " token is "+tokenEu );
        GetUserProfileCommand getUserProfileCommand = new GetUserProfileCommand(continentCodeExtractor, traceInfo, new RestTemplate(), sdnAppIdNa, tokenNa);

        UserProfile userProfile = getUserProfileCommand.execute();
        String logs = capturedLogs.toString();

        assertThat(logs).doesNotContain("Response Body: ");
        assertThat(logs).contains("Request headers: ");
        assertThat(logs).contains("auth-token=" + tokenNa.substring(0, 16) + "*");
        assertThat(logs).contains("Application-Id=[" + sdnAppIdNa + "]");
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getUserId()).isNotEmpty();
    }
    
    @Test
    public void should_retrieveUserProfile_andLogBody(){
    	capturedLogs = given_requestContentsBeingLogged(Level.DEBUG);
    	
    	System.out.println(sdnAppIdEu + " token is "+tokenEu );
        GetUserProfileCommand getUserProfileCommand = new GetUserProfileCommand(continentCodeExtractor, traceInfo, new RestTemplate(), sdnAppIdNa, tokenNa);

        UserProfile userProfile = getUserProfileCommand.execute();
        String logs = capturedLogs.toString();

        assertThat(logs).contains("Response Body: ");
        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getUserId()).isNotEmpty();
    }

    @Test
    public void should_updateUserProfile(){

        GetUserProfileCommand getUserProfileCommand = new GetUserProfileCommand(continentCodeExtractor, traceInfo, new RestTemplate(), sdnAppIdNa, tokenNa);
        UserProfile userProfile = getUserProfileCommand.execute();

         String phoneNumber = generateRandomPhoneNumber(); //#157985635
//        String phoneNumber = "5179290191".equals(userProfile.getPhoneNumber()) ? "7627338098" : "5179290191";
        userProfile.setPhoneNumber(phoneNumber);
        
        // #157985635
        userProfile.setPartnerRelationships(null);
        
        UpdateUserProfileCommand updateUserProfileCommand = new UpdateUserProfileCommand(continentCodeExtractor, traceInfo, new RestTemplate(), sdnAppIdNa, tokenNa, userProfile);
        updateUserProfileCommand.execute();

        getUserProfileCommand = new GetUserProfileCommand(continentCodeExtractor, traceInfo, new RestTemplate(), sdnAppIdNa, tokenNa);
        userProfile = getUserProfileCommand.execute();

        assertThat(userProfile).isNotNull();
        assertThat(userProfile.getPhoneNumber()).isEqualTo(phoneNumber);
    }
    
    @Test
    public void shouldLogError_whenUserProfileResponseHasError(){
    	
    	RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
		UserProfile profile = new UserProfile();
		UserProfileResponse userProfileResponse = UserProfileResponse.builder()
															.error(CSDNError.builder()
															  .message("Mock Error Message")
															  .statusCode("MockStatusCode")
															  .statusContext("MockStatusContext")
															  .build())
															.profile(profile)
															.build();
		ResponseEntity<UserProfileResponse> response = ResponseEntity.ok(userProfileResponse);
    	Mockito.when(mockRestTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(UserProfileResponse.class))).thenReturn(response);
    	GetUserProfileCommand getUserProfileCommand = new GetUserProfileCommand(continentCodeExtractor, traceInfo, mockRestTemplate, sdnAppIdNa, tokenNa);

        UserProfile userProfile = getUserProfileCommand.execute();

        assertThat(userProfile).isNotNull();
       
        String logs = capturedLogs.toString();
        assertThat(logs).contains("Mock Error Message");
		assertThat(logs).contains("MockStatusCode");
		assertThat(logs).contains("MockStatusContext");
    }

    private String generateRandomPhoneNumber(){

        String phoneNumber = "";

        Integer phoneLength = 10;
        for(Integer i = 0; i<phoneLength; i++){
            phoneNumber = phoneNumber + (new Random().nextInt(7) + 2);
        }

        return phoneNumber;
    }
    
    protected ByteArrayOutputStream given_requestContentsBeingLogged(Level logLevel) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern(consoleLoggingPattern);
        ple.setContext(lc);
        ple.start();

        ByteArrayOutputStream capturedLogs = new ByteArrayOutputStream();
        OutputStreamAppender<ILoggingEvent> logAppender = new OutputStreamAppender<>();
        logAppender.setEncoder(ple);
        logAppender.setContext(lc);
        logAppender.setOutputStream(capturedLogs);
        logAppender.start();

        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).addAppender(logAppender);
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(Level.ERROR);
        ((Logger) LoggerFactory.getLogger(GetUserProfileCommand.class)).setLevel(logLevel);

        return capturedLogs;
    }
}
