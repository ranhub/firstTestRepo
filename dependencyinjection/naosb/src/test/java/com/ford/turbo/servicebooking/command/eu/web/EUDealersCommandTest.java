package com.ford.turbo.servicebooking.command.eu.web;

import static java.nio.file.Files.readAllBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.EUDealersRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealersResponse;

@RunWith(MockitoJUnitRunner.class)
public class EUDealersCommandTest {
	
	private EUDealersCommand command;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;
	
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;

	private final String BASE_URL = "http://dealers.com";
	
	private ObjectMapper mapper = new ObjectMapper();
	@Captor
	private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;

	private String dealerSearchUrl = "/rest/v1/dealer/dealerData";
	
	private String consoleLoggingPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [trace=%X{X-B3-TraceId:-},span=%X{X-B3-SpanId:-},vcap_request=%X{http.x-vcap-request-id}] [%15.15t] %-40.40logger{39}: %m%n";
	
	@Test
    public void should_extendTimedHystrixCommand() throws IOException {
        assertThat(TimedHystrixCommand.class.isAssignableFrom(EUDealersCommand.class)).isTrue();
    }
	
	@Test
	public void shouldReturn_listOfEUDealer() throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException{
		ResponseEntity<EUOSBDealersResponse> responseEntity = createResponseEntity();
		
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBDealersResponse.class))).thenReturn(responseEntity); 
		
		EUOSBDealersResponse actualResponse = getCommand().execute();
		
		EUOSBDealersResponse expectedResponse = Utilities.getJsonFileData("euDealersSuccessfulResponse.json", EUOSBDealersResponse.class);
		
		assertThat(actualResponse).isNotNull();
		assertThat(actualResponse.getData().size()).isEqualTo(12);
		assertEquals(actualResponse.getData().get(0).getDealerCode(),expectedResponse.getData().get(0).getDealerCode());
		assertEquals(actualResponse.getData().get(0).getDealerName(),expectedResponse.getData().get(0).getDealerName());
		verify(mockMutualAuthRestTemplate).exchange(contains(dealerSearchUrl), eq(HttpMethod.GET), httpEntityCaptor.capture(),
				eq(EUOSBDealersResponse.class));
		
		assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-TraceId").get(0)).isEqualTo("0");
    	assertThat(httpEntityCaptor.getValue().getHeaders().get("X-B3-SpanId").get(0)).isEqualTo("0");
	}
	
	@Test
	public void should_logResponseHeaders() throws Exception {
		
		final ByteArrayOutputStream capturedLogs = Utilities.getLogContent(consoleLoggingPattern, EUDealersCommand.class);
		
		ResponseEntity<EUOSBDealersResponse> responseEntity = createResponseEntity();
		
		when(mockMutualAuthRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class),
				eq(EUOSBDealersResponse.class))).thenReturn(responseEntity); 
		
		EUOSBDealersResponse actualResponse = getCommand().execute();

		final String logs = capturedLogs.toString();
		
		assertThat(actualResponse).isNotNull();
		assertThat(logs).containsPattern("DEBUG .*EUDealersCommand *: Response body*");
	}
	
	private ResponseEntity<EUOSBDealersResponse> createResponseEntity()
			throws UnsupportedEncodingException, IOException, JsonParseException, JsonMappingException {
		Resource resource = new ClassPathResource("euDealersSuccessfulResponse.json");
		String euVehicleFeaturesResponse = new String(readAllBytes(resource.getFile().toPath()), "UTF8");
		
		ResponseEntity<EUOSBDealersResponse> responseEntity = ResponseEntity.ok(mapper.readValue(euVehicleFeaturesResponse, EUOSBDealersResponse.class));
		return responseEntity;
	}
	
	private EUDealersRequest createEUDealersRequest(){
		String marketCode = "GBR";	
		return new EUDealersRequest(marketCode);
	}
	
	private EUDealersCommand getCommand(){
		command = new EUDealersCommand(traceInfo,mockMutualAuthRestTemplate, BASE_URL, createEUDealersRequest());
		return command;
	}
}
