package com.ford.turbo.servicebooking.service.eu.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.eu.web.EUWebVehicleDetails;
import com.ford.turbo.servicebooking.models.eu.web.VehicleDetailsWrapper;
import com.ford.turbo.servicebooking.models.msl.request.EUWebVehicleLookupRequest;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.servicebooking.command.eu.web.EUWebVehicleLookupCommand;
import com.ford.turbo.servicebooking.models.osb.eu.web.response.EUOSBVehicleLookupResponse;


@RunWith(MockitoJUnitRunner.class)
public class EUWebServiceBookingServiceTest {
	@Mock
	private MutualAuthRestTemplate mockMutualAuthRestTemplate;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private TraceInfo mockTraceInfo;

	@Mock
	private CredentialsSource mockEuOsbCredentialsSource;

	@Mock
	private EUWebVehicleLookupCommand mockEUVehicleLookupCommand;

	@Spy
	@InjectMocks
	EUWebServiceBookingService euBookingService;

	String vin = "12345678901234567";
	String registrationNumber = "REG 1234";
	String locale = "en-gb";
	String marketCode = "GBR";
	long mileage = 1000000;
	String ecatMarketCode = "GB";
	boolean osbSiteTermsRequired = true;

	@Test
	public void shouldReturnVehicleLookupResponse_whenVehicleDetailsAvailable() throws Exception {

		EUOSBVehicleLookupResponse expectedResponse = getMockEUOSBVehicleLookupResponse();
		EUWebVehicleLookupRequest vehicleLookupRequest = buildVehicleRequest();

		ArgumentCaptor<EUWebVehicleLookupRequest>  argumentCaptorVehicleLookupRequest= ArgumentCaptor.forClass(EUWebVehicleLookupRequest.class);
		doReturn(mockEUVehicleLookupCommand).when(euBookingService)
		.getEUVehicleLookupCommand(eq(vehicleLookupRequest));
		when(mockEUVehicleLookupCommand.execute()).thenReturn(expectedResponse);

		VehicleDetailsWrapper actualResponse = euBookingService.getVehicleLookup(vin, registrationNumber, locale,marketCode, mileage, ecatMarketCode, osbSiteTermsRequired);

		verify(mockEUVehicleLookupCommand).execute();
		verify(euBookingService).getEUVehicleLookupCommand(argumentCaptorVehicleLookupRequest.capture());
		assertNotNull(actualResponse);
		assertEquals(expectedResponse.getVehicleDetails(), actualResponse.getVehicleDetails());
		assertThat(argumentCaptorVehicleLookupRequest.getValue().getVin()).isEqualTo(vin);		
	}

	@Test
	public void shouldReturnCommand_whenVehicleRequestPassed() {
		EUWebVehicleLookupRequest vehicleLookupRequest = buildVehicleRequest();
		EUWebVehicleLookupCommand euVehicleLookupCommand = euBookingService.getEUVehicleLookupCommand(vehicleLookupRequest);
		
		assertTrue(euVehicleLookupCommand instanceof TimedHystrixCommand);
	}

	@Test
	public void shouldReturnVehicleLookupRequest_whenPassedRequestParameters() {
		EUWebVehicleLookupRequest vehicleLookupRequest = euBookingService.buildVehicleRequest(vin, registrationNumber,
				locale, marketCode, mileage, ecatMarketCode, osbSiteTermsRequired);
		
		assertEquals(vin, vehicleLookupRequest.getVin());
		assertEquals(registrationNumber, vehicleLookupRequest.getRegistrationNumber());
		assertEquals(locale, vehicleLookupRequest.getLocale());
		assertEquals(marketCode, vehicleLookupRequest.getMarketCode());
		assertEquals(mileage, vehicleLookupRequest.getMileage());
		assertEquals(ecatMarketCode, vehicleLookupRequest.getEcatMarketCode());
		assertEquals(osbSiteTermsRequired, vehicleLookupRequest.isOsbSiteTermsRequired());
	}
	
	private EUOSBVehicleLookupResponse getMockEUOSBVehicleLookupResponse() {
		return EUOSBVehicleLookupResponse.builder().vehicleDetails(getMockVehicleDetails()).build();
	}

	private EUWebVehicleDetails getMockVehicleDetails() {

		return EUWebVehicleDetails
				.builder()
				.vin("vin")
				.vehicleLineCode("vehicleLineCode")
				.version("version")
				.bodyStyle("bodyStyle")
				.buildDate("buildDate")
				.color("color")
				.engine("engine")
				.fuelType("fuelType")
				.mileageInKm("mileageInKm")
				.mileageInMiles("mileageInMiles")
				.registrationNumber("registrationNumber")
				.transmission("transmission")
				.transmissionType("transmissionType")
				.modelName("modelName")
				.build();
	}

	protected EUWebVehicleLookupRequest buildVehicleRequest() {
		EUWebVehicleLookupRequest vehicleLookupRequest = 
				EUWebVehicleLookupRequest
				.builder()
				.vin(vin)
				.registrationNumber(registrationNumber)
				.locale(locale).marketCode(marketCode)
				.mileage(mileage)
				.ecatMarketCode(ecatMarketCode)
				.osbSiteTermsRequired(osbSiteTermsRequired)
				.build();
		return vehicleLookupRequest;
	}
}
