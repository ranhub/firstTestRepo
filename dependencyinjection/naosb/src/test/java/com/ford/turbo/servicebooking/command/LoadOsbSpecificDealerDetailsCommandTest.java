package com.ford.turbo.servicebooking.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.servicebooking.models.osb.OSBOVServicesResponse;

@RunWith(MockitoJUnitRunner.class)
public class LoadOsbSpecificDealerDetailsCommandTest {
	
	@Mock
	private MutualAuthRestTemplate mutualAuthRestTemplate;
    
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;
    private LoadOsbSpecificDealerDetailsCommand command;
    private String BASE_URL = "services.com"; 
    private String COUNTRY_CODE = "GBR";
    private List<String> dealerCodes = Arrays.asList("46396AA", "46396BA");
    
    @Captor
    private ArgumentCaptor<HttpEntity<Void>> httpEntityCaptor;
    
    private static OSBOVServicesResponse[] response ;
    
    @Before
    public void setup() throws IOException
    {		
          command = new LoadOsbSpecificDealerDetailsCommand(traceInfo, mutualAuthRestTemplate, BASE_URL, dealerCodes, COUNTRY_CODE);
          Mockito.when(mutualAuthRestTemplate.exchange(Mockito.contains(BASE_URL), eq(HttpMethod.GET), any(), Mockito.eq(String.class)))
          	.thenReturn(new ResponseEntity<String>(new ObjectMapper().writeValueAsString(response),HttpStatus.OK));
    }
    
    @Test
    public void shouldCreateURLandCheck() {
    	String url = command.buildURL();
    	assertThat(url).isEqualTo("services.com/rest/c/b/r/se?p=46396AA,46396BA&jk=GBR");
    }
}
