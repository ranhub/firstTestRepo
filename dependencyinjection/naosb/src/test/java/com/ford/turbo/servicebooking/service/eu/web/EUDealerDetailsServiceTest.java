package com.ford.turbo.servicebooking.service.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.eu.web.DealerDetails;
import com.ford.turbo.servicebooking.models.msl.request.DealersDetailsRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import com.ford.turbo.servicebooking.utils.Utilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.servicebooking.command.eu.web.EUDealerDetailsCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBDealerDetailsResponse;

@RunWith(MockitoJUnitRunner.class)
public class EUDealerDetailsServiceTest {

	private String gbrMarketCode = "GBR";
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;
	@Mock
	private CredentialsSource mockEuOsbCredentialsSource;
	@Mock
	private EUDealerDetailsCommand mockCommand;
	@Spy
	@InjectMocks
	private EUDealerDetailsService service;

	@Test
	public void shouldReturnResponse_whenDealerDetailsExistForRequestParams() throws Exception {

		ArgumentCaptor<DealersDetailsRequest> argumentCaptorDealersDetailsRequest = ArgumentCaptor
				.forClass(DealersDetailsRequest.class);
		DealersDetailsRequest request = DealersDetailsRequest.builder().marketCode(gbrMarketCode).build();
		List<String> dealerCodes = new ArrayList<>();
		dealerCodes.add("fake-dealer-code-1");
		request.setDealerCodes(dealerCodes);
		doReturn(mockCommand).when(service).getEUDealerDetailsCommand(request);
		EUOSBDealerDetailsResponse osbResponse = Utilities.getJsonFileData("euDealerDetailsSuccessfulResponse.json",
				EUOSBDealerDetailsResponse.class);
		when(mockCommand.execute()).thenReturn(osbResponse);
		List<DealerDetails> actualResponse = service.getDealerDetails(request);

		verify(mockCommand).execute();
		verify(service).getEUDealerDetailsCommand(argumentCaptorDealersDetailsRequest.capture());
		List<DealerDetails> expectedResponse = osbResponse.getData();
		assertNotNull(actualResponse);
		assertFalse(actualResponse.isEmpty());
		assertEquals(expectedResponse, actualResponse);
		assertThat(argumentCaptorDealersDetailsRequest.getValue().getMarketCode()).isEqualTo(gbrMarketCode);
	}

	@Test
	public void shouldReturnEUDealerDetailsCommand_whenDealersDetailsRequest() {

		DealersDetailsRequest request = DealersDetailsRequest.builder().dealerCodes(new ArrayList<String>()).build();
		EUDealerDetailsCommand command = service.getEUDealerDetailsCommand(request);
		assertNotNull(command);
		assertTrue(TimedHystrixCommand.class.isAssignableFrom(command.getClass()));
	}
}
