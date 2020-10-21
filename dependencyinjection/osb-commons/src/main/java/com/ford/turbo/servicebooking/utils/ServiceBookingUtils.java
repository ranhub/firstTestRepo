package com.ford.turbo.servicebooking.utils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.ford.turbo.servicebooking.models.osb.OSBOVService;
import com.ford.turbo.servicebooking.models.osb.OldServiceType;
import com.ford.turbo.servicebooking.models.osb.SelectedVehicleWithPrice;
import com.ford.turbo.servicebooking.models.osb.VehicleDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ford.turbo.aposb.common.authsupport.fordmapping.continentmapping.ContinentCodeExtractor;
import com.ford.turbo.aposb.common.authsupport.models.ContinentCode;
import com.ford.turbo.aposb.common.basemodels.controller.exception.NoBackendAvailableException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ServiceBookingUtils {
	
	@Autowired
	private ContinentCodeExtractor continentCodeExtractor;
	
	public static final List<OSBOVService> filterOldServices(List<com.ford.turbo.servicebooking.models.msl.response.OSBOVService> allOldServices, List<String> oldServiceIds, OldServiceType serviceType){
		List<OSBOVService> oldServices = new ArrayList<>();
    	if(oldServiceIds!=null){
    		for(com.ford.turbo.servicebooking.models.msl.response.OSBOVService service:allOldServices)
    		{
    			for(String oldServiceId:oldServiceIds)
    			{
    				if(oldServiceId.split(":")[1].equals(serviceType.toString()) && service.getServiceId().equals(oldServiceId))
    				{
    					OSBOVService osbov = new OSBOVService();
    					osbov.setUniqueId(oldServiceId.split(":")[0]);
    					osbov.setName(service.getName());
    					SelectedVehicleWithPrice selectedVehicle = new SelectedVehicleWithPrice();
    					selectedVehicle.setDescription(service.getDescription());
    					selectedVehicle.setPrice(service.getPrice());
    					selectedVehicle.setPriceAfterDiscount(service.getPriceAfterDiscount());
    					osbov.setSelectedVehicle(selectedVehicle);
    					osbov.setDiscountPrice(service.getDiscountPrice());
    					osbov.setDiscountPercentage(service.getDiscountPercentage());
    					oldServices.add(osbov);
    				}
    			}
    		}
    	}
    	return oldServices;
    }
	
	public static final VehicleDetails getOSBBookingVehicleDetailsFromOSBResponseVehicleDetails(com.ford.turbo.servicebooking.models.osb.response.bookingmodels.VehicleDetails vehicleDetails) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException{
    	ObjectMapper mapper = new ObjectMapper();
    	VehicleDetails createBookingVehicleDetails  = mapper.readValue(mapper.writeValueAsString(vehicleDetails),VehicleDetails.class);
    	return createBookingVehicleDetails;
	}
	
	public ContinentCode getContinentCode(String appId) {
		return continentCodeExtractor.getContinent(appId);
	}
	
	public void validateApplicationId(String appId) {
		ContinentCode continentCode = continentCodeExtractor.getContinent(appId);
		if (!ContinentCode.EU.equals(continentCode) && !ContinentCode.AP.equals(continentCode)) {
			throw new NoBackendAvailableException();
		}
	}
	
	public void validateEUApplicationId(String appId) {
		ContinentCode continentCode = continentCodeExtractor.getContinent(appId);
		if (!ContinentCode.EU.equals(continentCode) ) {
			throw new NoBackendAvailableException();
		}
	}

	public void validateApplicationId(String appId, List<ContinentCode> whiteListRegions) {
		ContinentCode continentCode = continentCodeExtractor.getContinent(appId);
		
		for(ContinentCode code : whiteListRegions) {
			if(code == continentCode) {
				return;
			}
		}
		throw new NoBackendAvailableException();
	}
	
	public static String getDateTimeString(ZonedDateTime dateTime, String pattern){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    	return dateTime.format(formatter);
	}
	
	public Boolean isDateParsable(String date, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		try {
			formatter.parse(date);
		} catch (DateTimeParseException pe) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
}