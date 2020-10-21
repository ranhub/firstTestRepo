package com.ford.turbo.servicebooking.service.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.msl.request.DealerCalendarRequest;
import com.ford.turbo.servicebooking.models.msl.response.v2.DealerCalendarV2;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.servicebooking.command.eu.web.EUWebDealerCalendarCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerCalendarResponse;

@RunWith(MockitoJUnitRunner.class)
public class EUWebDealerCalendarServiceTest {

	@Spy
	@InjectMocks
	private EUWebDealerCalendarService service;
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	@Mock
	private CredentialsSource mockEuOsbCredentialsSource;
	@Mock
	private EUWebDealerCalendarCommand mockCommand;
	private String dealerCode = "dealer-code";
	private String marketCode = "GBR";
	private String locale = "en-GB";
	private String modelName = "Fiesta";
	private List<String> additionalService = new ArrayList<String>(Arrays.asList("service-one","service-two"));
	private String motService = "MOT";
	
	@Test
	public void shouldReturnResponse_whenGetDealerCalendar() throws Exception {

		DealerCalendarV2 expectedData = Utilities.getJsonFileData("dealer-calendar-response.json", DealerCalendarV2.class);
		EUOSBDealerCalendarResponse osbResponse = EUOSBDealerCalendarResponse.builder().build();
		osbResponse.setData(expectedData);
		DealerCalendarRequest request = createDealerCalendarRequest();
		doReturn(mockCommand).when(service).getEUWebDealerCalendarCommand(request);
		when(mockCommand.execute()).thenReturn(osbResponse);
		DealerCalendarV2 wrapper = service.getCalendar(dealerCode, marketCode, locale, modelName,
				additionalService, motService);
		
		assertNotNull(wrapper);
		assertThat(wrapper).isEqualTo(expectedData);
		verify(mockCommand).execute();
		verify(service).createDealerCalendarRequest(dealerCode, marketCode, locale, modelName,
				additionalService, motService);
		verify(service).getEUWebDealerCalendarCommand(request);
	}

	@Test
	public void shouldReturnEUWebDealerCalendarCommand_whenDealerCalendarRequestPassed() {

		DealerCalendarRequest request = createDealerCalendarRequest();
		EUWebDealerCalendarCommand command = service.getEUWebDealerCalendarCommand(request);
		assertNotNull(command);
		assertTrue(TimedHystrixCommand.class.isAssignableFrom(command.getClass()));
	}
	
	@Test
	public void shouldReturnDealerCalendarRequest() {
		
		DealerCalendarRequest request = service.createDealerCalendarRequest(dealerCode, marketCode, locale, modelName,
				additionalService, motService);
		assertNotNull(request);
		assertThat(request.getMarketCode()).isEqualTo("GBR");
		assertThat(request.getDealerCode()).isEqualTo("dealer-code");
		assertThat(request.getLocale()).isEqualTo("en-GB");
		assertThat(request.getModelName()).isEqualTo("Fiesta");
		assertThat(request.getAdditionalService()).isEqualTo(additionalService);
		assertThat(request.getMotService()).isEqualTo(motService);
	}
	
	private DealerCalendarRequest createDealerCalendarRequest() {
		
		return DealerCalendarRequest.builder()
				.dealerCode(dealerCode)
				.marketCode(marketCode)
				.locale(locale)
				.modelName(modelName)
				.additionalService(additionalService)
				.motService(motService)
				.build();
	}
}
