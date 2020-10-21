package com.ford.turbo.servicebooking.service.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleFeaturesData;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleFeaturesRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.servicebooking.command.eu.web.EUWebVehicleFeaturesCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleFeaturesResponse;

@RunWith(MockitoJUnitRunner.class)
public class EUWebVehicleFeaturesServiceTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo traceInfo;

	@Mock
	private MutualAuthRestTemplate mutualAuthRestTemplate;

	@Mock
	private CredentialsSource euOsbCredentialsSource;

	@Spy
	@InjectMocks
	EUWebVehicleFeaturesService vehicleFeaturesService;

	@Mock
	EUWebVehicleFeaturesCommand command;

	@Captor
	private ArgumentCaptor<EUWebVehicleFeaturesRequest> argumentCaptorRequest;

	private String locale = "en-gb";
	private String marketCode = "GBR";

	@Test
	public void shouldReturnResponseWithModelsAndModelYears_whenDataExistsForLocaleAndMarketCode() {

		EUWebVehicleFeaturesRequest request = getEUWebVehicleFeaturesRequest();
		doReturn(command).when(vehicleFeaturesService).getEUWebVehicleFeaturesCommand(Mockito.eq(request));

		EUOSBVehicleFeaturesResponse expectedResponse = mockEUOSBVehicleFeaturesResponse();
		doReturn(expectedResponse).when(command).execute();

		EUWebVehicleFeaturesData actualVehicleFeaturesData = vehicleFeaturesService.getVehicleFeatures(request);

		Mockito.verify(command).execute();
		Mockito.verify(vehicleFeaturesService).getEUWebVehicleFeaturesCommand(argumentCaptorRequest.capture());
		assertEquals(argumentCaptorRequest.getValue().getLocale(), "en-gb");
		assertEquals(argumentCaptorRequest.getValue().getMarketCode(), "GBR");

		assertThat(actualVehicleFeaturesData.getModel()).isEqualTo(expectedResponse.getData().getModel());
		assertThat(actualVehicleFeaturesData.getModelYear()).isEqualTo(expectedResponse.getData().getModelYear());
	}

	@Test
	public void shouldReturnCommand_when_EUWebVehicleFeaturesRequest_Passed() {

		EUWebVehicleFeaturesRequest request = getEUWebVehicleFeaturesRequest();
		EUWebVehicleFeaturesCommand euWebVehicleFeaturesCommand = vehicleFeaturesService
				.getEUWebVehicleFeaturesCommand(request);
		assertTrue(euWebVehicleFeaturesCommand instanceof EUWebVehicleFeaturesCommand);
		assertTrue(euWebVehicleFeaturesCommand instanceof TimedHystrixCommand);
	}

	private EUOSBVehicleFeaturesResponse mockEUOSBVehicleFeaturesResponse() {
		List<String> modelList = new ArrayList<>();
		modelList.add("B-MAX");
		modelList.add("EcoSport");

		List<String> modelYearList = new ArrayList<>();
		modelYearList.add("2017");
		modelYearList.add("2016");
		EUOSBVehicleFeaturesResponse response = new EUOSBVehicleFeaturesResponse();
		EUWebVehicleFeaturesData expectedVehicleFeaturesData = new EUWebVehicleFeaturesData(modelList, modelYearList);
		response.setData(expectedVehicleFeaturesData);
		return response;
	}

	private EUWebVehicleFeaturesRequest getEUWebVehicleFeaturesRequest() {
		return new EUWebVehicleFeaturesRequest(locale, marketCode);
	}
}
