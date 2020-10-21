package com.ford.turbo.servicebooking.utils;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ford.turbo.servicebooking.models.msl.response.OSBOVService;
import com.ford.turbo.servicebooking.models.osb.response.bookingmodels.VehicleDetails;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;
import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;
import com.ford.turbo.servicebooking.models.msl.response.ServicesListResponse;
import com.ford.turbo.servicebooking.models.osb.OldServiceType;

@RunWith(SpringRunner.class)
public class ServiceBookingUtilsTest {
	
	@InjectMocks
	private ServiceBookingUtils serviceBookingUtils;
	
	@Mock
	private ContinentCodeExtractor continentCodeExtractor;
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	List<String> optionaloldServiceIds = Arrays.asList(
			"3040:"+OldServiceType.REPAIR,
			"3017:"+OldServiceType.MOT,
			"3004:"+OldServiceType.VALUE,
			"3022:"+OldServiceType.REPAIR
			);

	@Test
	public void should_filter_old_services_as_per_type() {
		ServicesListResponse response = new ServicesListResponse();
		response.setOldServices(createOldServicesList());
		
		List<com.ford.turbo.servicebooking.models.osb.OSBOVService> motServicesData = ServiceBookingUtils.filterOldServices(response.getOldServices(),optionaloldServiceIds , OldServiceType.MOT);
		assertEquals("3017",motServicesData.get(0).getUniqueId());
		assertEquals("3017",motServicesData.get(0).getSelectedVehicle().getPrice());
		assertEquals("3000",motServicesData.get(0).getSelectedVehicle().getPriceAfterDiscount());
		assertEquals("3017-MName",motServicesData.get(0).getName());
		assertEquals("3017-MDescription",motServicesData.get(0).getSelectedVehicle().getDescription());
		assertEquals("3000",motServicesData.get(0).getDiscountPrice());
		assertEquals("30.17",motServicesData.get(0).getDiscountPercentage());
		
		
		List<com.ford.turbo.servicebooking.models.osb.OSBOVService> valueServicesData = ServiceBookingUtils.filterOldServices(response.getOldServices(),optionaloldServiceIds, OldServiceType.VALUE);
		com.ford.turbo.servicebooking.models.osb.OSBOVService value3004 = null;
		for(com.ford.turbo.servicebooking.models.osb.OSBOVService service:valueServicesData)
		{
			if(service.getUniqueId().startsWith("3004"))
			{
				value3004 = service;
				assertEquals("3004",value3004.getSelectedVehicle().getPrice());
				assertEquals("3001",value3004.getSelectedVehicle().getPriceAfterDiscount());
				assertEquals("3004",value3004.getUniqueId());
				assertEquals("3004-VName",value3004.getName());
				assertEquals("3004-VDescription",value3004.getSelectedVehicle().getDescription());
				assertEquals("3000",value3004.getDiscountPrice());
				assertEquals("30.04",value3004.getDiscountPercentage());
			}
		}
		assertNotNull(value3004);
		
		List<com.ford.turbo.servicebooking.models.osb.OSBOVService> repairServicesData = ServiceBookingUtils.filterOldServices(response.getOldServices(),optionaloldServiceIds, OldServiceType.REPAIR);
		com.ford.turbo.servicebooking.models.osb.OSBOVService repair3040 = null;
		for(com.ford.turbo.servicebooking.models.osb.OSBOVService service:repairServicesData)
		{
			if(service.getUniqueId().startsWith("3040"))
			{
				repair3040 = service;
				assertEquals("3040",repair3040.getSelectedVehicle().getPrice());
				assertEquals("3030",repair3040.getSelectedVehicle().getPriceAfterDiscount());
				assertEquals("3040",repair3040.getUniqueId());
				assertEquals("3040-Name",repair3040.getName());
				assertEquals("3040-Description",repair3040.getSelectedVehicle().getDescription());
				assertEquals("3000",repair3040.getDiscountPrice());
				assertEquals("30.40",repair3040.getDiscountPercentage());
			}
		}
		
		
		assertNotNull(repair3040);
	}

	private List<OSBOVService> createOldServicesList(){
		List<OSBOVService> servicesList = new ArrayList<OSBOVService>(){{
			add(new OSBOVService(){{
				setServiceId("3017:"+OldServiceType.MOT.toString());
				setPrice("3017");
				setPriceAfterDiscount("3000");
				setDescription("3017-MDescription");
				setName("3017-MName");
				setDiscountPrice("3000");
				setDiscountPercentage("30.17");
			}});
			add(new OSBOVService(){{
				setServiceId("3004:"+OldServiceType.VALUE.toString());
				setPrice("3004");
				setPriceAfterDiscount("3001");
				setDescription("3004-VDescription");
				setName("3004-VName");
				setDiscountPrice("3000");
				setDiscountPercentage("30.04");
			}});
			add(new OSBOVService(){{
				setServiceId("3017:"+OldServiceType.VALUE.toString());
				setPrice("3017");
				setDescription("3017-VDescription");
				setName("3017-VName");
			}});
			add(new OSBOVService(){{
				setServiceId("3004:"+OldServiceType.REPAIR.toString());
				setPrice("3004");
				setDescription("3004-Description");
				setName("3004-Name");
			}});
			add(new OSBOVService(){{
				setServiceId("3013:"+OldServiceType.REPAIR.toString());
				setPrice("3013");
				setDescription("3013-Description");
				setName("3013-Name");
			}});
			add(new OSBOVService(){{
				setServiceId("3022:"+OldServiceType.REPAIR.toString());
				setPrice("3022");
				setDescription("3022-Description");
				setName("3022-Name");
			}});
			add(new OSBOVService(){{
				setServiceId("3031:"+OldServiceType.REPAIR.toString());
				setPrice("3031");
				setDescription("3031-Description");
				setName("3031-Name");
			}});
			add(new OSBOVService(){{
				setServiceId("3040:"+OldServiceType.REPAIR.toString());
				setPrice("3040");
				setPriceAfterDiscount("3030");
				setDescription("3040-Description");
				setName("3040-Name");
				setDiscountPrice("3000");
				setDiscountPercentage("30.40");
			}});
			add(new OSBOVService(){{
				setServiceId("3049:"+OldServiceType.REPAIR.toString());
				setPrice("3049");
				setDescription("3049-Description");
				setName("3049-Name");
			}});
			
		}};
		return servicesList;
	}
	
	@Test
	public void should_Return_OSBBookingVehicleDetails_From_OSBResponseVehicleDetails() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException{
		VehicleDetails
		osbResponseVehicleDetails = new VehicleDetails();
		osbResponseVehicleDetails.setBodyStyle("5 Door Saloon");
		osbResponseVehicleDetails.setBuildDate("2012-07");
		osbResponseVehicleDetails.setColor("Frozen White");
		osbResponseVehicleDetails.setEngine("1.0L EcoBoost 120PS/125PS");
		osbResponseVehicleDetails.setFuelType("Petrol");
		osbResponseVehicleDetails.setMileageInKm((int)(1000*1.6));
		osbResponseVehicleDetails.setMileageInMiles(1000);
		osbResponseVehicleDetails.setModelName("Focus 2011-2015");
		osbResponseVehicleDetails.setRegistrationNumber("ABC9812EF");
		osbResponseVehicleDetails.setTransmission("6 Speed Manual Trans - B6");
		osbResponseVehicleDetails.setTransmissionType("Manual");
		osbResponseVehicleDetails.setVehicleLineCode("VLB80");
		osbResponseVehicleDetails.setVersion("Series 70");
		osbResponseVehicleDetails.setVin("WF0KXXGCBKCE79983");
		
		com.ford.turbo.servicebooking.models.osb.VehicleDetails vehicleDetails = ServiceBookingUtils.getOSBBookingVehicleDetailsFromOSBResponseVehicleDetails(osbResponseVehicleDetails);
		
		assertEquals(osbResponseVehicleDetails.getBodyStyle(),vehicleDetails.getBodyStyle());
		assertEquals(osbResponseVehicleDetails.getBuildDate(),vehicleDetails.getBuildDate());
		assertEquals(osbResponseVehicleDetails.getColor(),vehicleDetails.getColor());
		assertEquals(osbResponseVehicleDetails.getEngine(),vehicleDetails.getEngine());
		assertEquals(osbResponseVehicleDetails.getFuelType(),vehicleDetails.getFuelType());
		assertEquals(osbResponseVehicleDetails.getMileageInKm().doubleValue(),vehicleDetails.getMileageInKm().doubleValue(),0.0);
		assertEquals(osbResponseVehicleDetails.getMileageInMiles().doubleValue(),vehicleDetails.getMileageInMiles().doubleValue(),0.0);
		assertEquals(osbResponseVehicleDetails.getModelName(),vehicleDetails.getModelName());
		assertEquals(osbResponseVehicleDetails.getRegistrationNumber(),vehicleDetails.getRegistrationNumber());
		assertEquals(osbResponseVehicleDetails.getTransmission(),vehicleDetails.getTransmission());
		assertEquals(osbResponseVehicleDetails.getTransmissionType(),vehicleDetails.getTransmissionType());
		assertEquals(osbResponseVehicleDetails.getVehicleLineCode(),vehicleDetails.getVehicleLineCode());
		assertEquals(osbResponseVehicleDetails.getVersion(),vehicleDetails.getVersion());
		assertEquals(osbResponseVehicleDetails.getVin(),vehicleDetails.getVin());
	}
	
	@Test
	public void shouldNotThrowException_WhenApplicationIDValidator_isCalledWith_EU()
	{
		when(continentCodeExtractor.getContinent("EU")).thenReturn(ContinentCode.EU);
		serviceBookingUtils.validateApplicationId("EU");
		Mockito.verify(continentCodeExtractor).getContinent("EU");
	}
	
	@Test
	public void shouldNotThrowException_WhenApplicationIDValidator_isCalledWith_AP()
	{
		when(continentCodeExtractor.getContinent("AP")).thenReturn(ContinentCode.AP);
		serviceBookingUtils.validateApplicationId("AP");
	}
	
	@Test(expected=NoBackendAvailableException.class)
	public void shouldThrowException_WhenApplicationIDValidator_isCalledWith_NA()
	{
		when(continentCodeExtractor.getContinent("NA")).thenReturn(ContinentCode.NA);
		serviceBookingUtils.validateApplicationId("NA");
	}
	
	@Test
	public void shouldNotThrowException_WhenEUApplicationIDValidator_isCalledWith_EU()
	{
		when(continentCodeExtractor.getContinent("EU")).thenReturn(ContinentCode.EU);
		serviceBookingUtils.validateEUApplicationId("EU");
	}

	@Test(expected=NoBackendAvailableException.class)
	public void shouldThrowException_WhenEUApplicationIDValidator_isCalledWith_NA()
	{
		when(continentCodeExtractor.getContinent("NA")).thenReturn(ContinentCode.NA);
		serviceBookingUtils.validateEUApplicationId("NA");
	}

	@Test
	public void shouldReturnDateTimeString_whenGivenPattern() {
		String dateTimeString =  "2017-12-05T01:30:00.000Z";
		ZonedDateTime time = ZonedDateTime.parse(dateTimeString);
		String expectedResult = "20171205";
		String result = ServiceBookingUtils.getDateTimeString(time, "yyyyMMdd");
		
		assertEquals(expectedResult, result);
		
		dateTimeString =  "2017-12-12T01:30:00.000Z";
		time = ZonedDateTime.parse(dateTimeString);
		expectedResult = "20171212";
		
		result = ServiceBookingUtils.getDateTimeString(time, "yyyyMMdd");
		assertEquals(expectedResult, result);
		
		dateTimeString =  "2017-12-12T01:30:00.000Z";
		time = ZonedDateTime.parse(dateTimeString);
		expectedResult = "01:30";
		
		result = ServiceBookingUtils.getDateTimeString(time, "HH:mm");
		assertEquals(expectedResult, result);
		
		dateTimeString =  "2017-12-12T11:30:00.000Z";
		time = ZonedDateTime.parse(dateTimeString);
		expectedResult = "11:30";
		
		result = ServiceBookingUtils.getDateTimeString(time, "HH:mm");
		assertEquals(expectedResult, result);
		
		dateTimeString =  "2017-12-12T11:05:00.000Z";
		time = ZonedDateTime.parse(dateTimeString);
		expectedResult = "11:05";
		
		result = ServiceBookingUtils.getDateTimeString(time, "HH:mm");
		assertEquals(expectedResult, result);
		
		dateTimeString =  "2017-12-12T01:00:00.000Z";
		time = ZonedDateTime.parse(dateTimeString);
		expectedResult = "01:00";
		
		result = ServiceBookingUtils.getDateTimeString(time, "HH:mm");
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void applicationIdValidator_ShouldNotThrowException_whenAppIdInWhitelist() {
		String applicationId = "AP-Application_id";
		
		when(continentCodeExtractor.getContinent(applicationId)).thenReturn(ContinentCode.AP);
		
		serviceBookingUtils.validateApplicationId(applicationId, Arrays.asList(ContinentCode.AP, ContinentCode.SA));
	}
	
	@Test
	public void applicationIdValidator_ShouldThrowException_whenAppIdNotInWhitelist() {
		expected.expect(NoBackendAvailableException.class);
		expected.expectMessage("Service is not configured for the request Application ID");
		
		String applicationId = "NA-Application_id";
		
		when(continentCodeExtractor.getContinent(applicationId)).thenReturn(ContinentCode.NA);
		
		serviceBookingUtils.validateApplicationId(applicationId, Arrays.asList(ContinentCode.AP, ContinentCode.SA));
	}

  @Test
	public void shouldRespondTrueIfADateStringIsParsableWithTheGivenFormat() {
		assertThat(serviceBookingUtils.isDateParsable("20181201", "yyyyMMdd"), is(Boolean.TRUE));
	}
	
	@Test
	public void shouldRespondFalseIfADateStringIsNotParsableWithTheGivenFormat() {
		assertThat(serviceBookingUtils.isDateParsable("20181301", "yyyyMMdd"), is(Boolean.FALSE));
	}

}
