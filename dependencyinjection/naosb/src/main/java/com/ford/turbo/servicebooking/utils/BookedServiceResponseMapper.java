package com.ford.turbo.servicebooking.utils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ford.turbo.servicebooking.models.osb.OSBOVService;
import org.apache.commons.lang3.ArrayUtils;

import com.ford.turbo.servicebooking.models.msl.response.AdditionalServiceBooking;
import com.ford.turbo.servicebooking.models.msl.response.BookedServiceResponse;
import com.ford.turbo.servicebooking.models.msl.response.DealerProfileMslResponse;
import com.ford.turbo.servicebooking.models.msl.response.MainServiceBooking;
import com.ford.turbo.servicebooking.models.msl.response.OldServiceBooking;
import com.ford.turbo.servicebooking.models.osb.BookedAdditionalService;
import com.ford.turbo.servicebooking.models.osb.OSBBookingData;
import com.ford.turbo.servicebooking.models.osb.OldServiceType;
import com.ford.turbo.servicebooking.models.osb.TimeAsDate;

public class BookedServiceResponseMapper {
    private static final int OLD_MAINSERVICE_ID = 0;

	public static BookedServiceResponse map(OSBBookingData[] bookedServices) {
		BookedServiceResponse response = new BookedServiceResponse();
	    for (OSBBookingData bookedService : bookedServices) {
	        if (isNewService(bookedService)) {
	            response.setAppointmentTimeAsDate(convertAppointmentToZonedDate(bookedService.getAppointmentTimeAsDate()));
	            response.setBookingCustomerRefNum(bookedService.getBookingCustomerRefNum());
	            response.setCustomerAnnotation(bookedService.getCustomerAnnotation());
	            response.setDealerProfile(new DealerProfileMslResponse(bookedService.getDealer().getDealerProfile()));
	
	            MainServiceBooking mainServiceBooking = new MainServiceBooking();
	            mainServiceBooking.setName(bookedService.getMainServiceDescription());
	            mainServiceBooking.setServiceId(bookedService.getMainServiceId().toString());
	
	            if(bookedService.getMainServicePrice() != null){
	            	mainServiceBooking.setPrice(BigDecimal.valueOf(Double.parseDouble(bookedService.getMainServicePrice())));
	            }
	
	            response.getMainServices().add(mainServiceBooking);
	
	            for(BookedAdditionalService bookedAdditionalService : bookedService.getBookedAdditionalServices()){
	
	                AdditionalServiceBooking additionalServiceBooking = new AdditionalServiceBooking();
	                additionalServiceBooking.setName(bookedAdditionalService.getAdditionalServiceName());
	                additionalServiceBooking.setPrice(bookedAdditionalService.getPrice());
	                additionalServiceBooking.setServiceId(bookedAdditionalService.getAdditionalServiceId());
	                additionalServiceBooking.setComments(bookedAdditionalService.getAdditionalServiceComments());
	                response.getAdditionalServices().add(additionalServiceBooking);
	            }
	          
	            //Add MOT to Main Service List
	            if(bookedService.getMotJSON() != null)
		            for(OSBOVService oldService: bookedService.getMotJSON())
		            {
		            	 OldServiceBooking oldServiceBooking = new OldServiceBooking();
		            	 oldServiceBooking.setName(oldService.getName());
		            	 oldServiceBooking.setPrice(new BigDecimal(oldService.getSelectedVehicle().getPrice()));
		            	 oldServiceBooking.setPriceAfterDiscount(oldService.getSelectedVehicle().getPriceAfterDiscount());
		            	 oldServiceBooking.setServiceId(oldService.getUniqueId()+":"+ OldServiceType.MOT);
		            	 oldServiceBooking.setSubType(OldServiceType.MOT);
		            	 response.getOldServices().add(oldServiceBooking);
		            }
	            
	            //Add Value to Main Service List
	            if(bookedService.getValueServiceJSON() != null)
		            for(OSBOVService oldService: bookedService.getValueServiceJSON())
		            {
		            	 OldServiceBooking oldServiceBooking = new OldServiceBooking();
		            	 oldServiceBooking.setName(oldService.getName());
		            	 oldServiceBooking.setPrice(new BigDecimal(oldService.getSelectedVehicle().getPrice()));
		            	 oldServiceBooking.setPriceAfterDiscount(oldService.getSelectedVehicle().getPriceAfterDiscount());
		            	 oldServiceBooking.setServiceId(oldService.getUniqueId()+":"+OldServiceType.VALUE);
		            	 oldServiceBooking.setSubType(OldServiceType.VALUE);
		            	 response.getOldServices().add(oldServiceBooking);
		            }
	            
	            //Add Repair to Additional Service List
	            if(bookedService.getRepairsJSON() != null)
		            for(OSBOVService oldService: bookedService.getRepairsJSON())
		            {
		            	OldServiceBooking oldServiceBooking = new OldServiceBooking();
		            	 oldServiceBooking.setName(oldService.getName());
		            	 oldServiceBooking.setPrice(new BigDecimal(oldService.getSelectedVehicle().getPrice()));
		            	 oldServiceBooking.setPriceAfterDiscount(oldService.getSelectedVehicle().getPriceAfterDiscount());
		            	 oldServiceBooking.setServiceId(oldService.getUniqueId()+":"+OldServiceType.REPAIR);
		            	 oldServiceBooking.setSubType(OldServiceType.REPAIR);
		                 response.getOldServices().add(oldServiceBooking);
		            }
	            
	            response.setTotalPrice(bookedService.getTotalPrice());
	            response.setTotalPriceAfterDiscount(bookedService.getTotalPriceAfterDiscount());
	        }
	    }
	    response.setTotalBookedServices(response.getMainServices().size() + response.getAdditionalServices().size() + response.getOldServices().size());
	    return response;
	}

	public static List<BookedServiceResponse> mapBookedServices(OSBBookingData[] bookedServices) {
		List<BookedServiceResponse> listResponse = new ArrayList<BookedServiceResponse>();
		if(ArrayUtils.isEmpty(bookedServices)) {
			return listResponse;
		}
	    for (OSBBookingData bookedService : bookedServices) {
	    	BookedServiceResponse response = new BookedServiceResponse();
	        if (isNewService(bookedService)) {
	            response.setAppointmentTimeAsDate(convertAppointmentToZonedDate(bookedService.getAppointmentTimeAsDate()));
	            response.setBookingCustomerRefNum(bookedService.getBookingCustomerRefNum());
	            response.setCustomerAnnotation(bookedService.getCustomerAnnotation());
	            response.setDealerProfile(new DealerProfileMslResponse(bookedService.getDealer().getDealerProfile()));
	
	            MainServiceBooking mainServiceBooking = new MainServiceBooking();
	            mainServiceBooking.setName(bookedService.getMainServiceDescription());
	            mainServiceBooking.setServiceId(bookedService.getMainServiceId().toString());
	
	            if(bookedService.getMainServicePrice() != null){
	            	mainServiceBooking.setPrice(BigDecimal.valueOf(Double.parseDouble(bookedService.getMainServicePrice())));
	            }
	
	            response.getMainServices().add(mainServiceBooking);
	
	            for(BookedAdditionalService bookedAdditionalService : bookedService.getBookedAdditionalServices()){
	
	                AdditionalServiceBooking additionalServiceBooking = new AdditionalServiceBooking();
	                additionalServiceBooking.setName(bookedAdditionalService.getAdditionalServiceName());
	                additionalServiceBooking.setPrice(bookedAdditionalService.getPrice());
	                additionalServiceBooking.setServiceId(bookedAdditionalService.getAdditionalServiceId());
	                additionalServiceBooking.setComments(bookedAdditionalService.getAdditionalServiceComments());
	                response.getAdditionalServices().add(additionalServiceBooking);
	            }
	          
	            //Add MOT to Main Service List
	            if(bookedService.getMotJSON() != null)
		            for(OSBOVService oldService: bookedService.getMotJSON())
		            {
		            	 OldServiceBooking oldServiceBooking = new OldServiceBooking();
		            	 oldServiceBooking.setName(oldService.getName());
		            	 oldServiceBooking.setPrice(new BigDecimal(oldService.getSelectedVehicle().getPrice()));
		            	 oldServiceBooking.setPriceAfterDiscount(oldService.getSelectedVehicle().getPriceAfterDiscount());
		            	 oldServiceBooking.setServiceId(oldService.getUniqueId()+":"+OldServiceType.MOT);
		            	 oldServiceBooking.setSubType(OldServiceType.MOT);
		            	 response.getOldServices().add(oldServiceBooking);
		            }
	            
	            //Add Value to Main Service List
	            if(bookedService.getValueServiceJSON() != null)
		            for(OSBOVService oldService: bookedService.getValueServiceJSON())
		            {
		            	 OldServiceBooking oldServiceBooking = new OldServiceBooking();
		            	 oldServiceBooking.setName(oldService.getName());
		            	 oldServiceBooking.setPrice(new BigDecimal(oldService.getSelectedVehicle().getPrice()));
		            	 oldServiceBooking.setPriceAfterDiscount(oldService.getSelectedVehicle().getPriceAfterDiscount());
		            	 oldServiceBooking.setServiceId(oldService.getUniqueId()+":"+OldServiceType.VALUE);
		            	 oldServiceBooking.setSubType(OldServiceType.VALUE);
		            	 response.getOldServices().add(oldServiceBooking);
		            }
	            
	            //Add Repair to Additional Service List
	            if(bookedService.getRepairsJSON() != null)
		            for(OSBOVService oldService: bookedService.getRepairsJSON())
		            {
		            	OldServiceBooking oldServiceBooking = new OldServiceBooking();
		            	 oldServiceBooking.setName(oldService.getName());
		            	 oldServiceBooking.setPrice(new BigDecimal(oldService.getSelectedVehicle().getPrice()));
		            	 oldServiceBooking.setPriceAfterDiscount(oldService.getSelectedVehicle().getPriceAfterDiscount());
		            	 oldServiceBooking.setServiceId(oldService.getUniqueId()+":"+OldServiceType.REPAIR);
		            	 oldServiceBooking.setSubType(OldServiceType.REPAIR);
		                 response.getOldServices().add(oldServiceBooking);
		            }
	            
	            response.setTotalPrice(bookedService.getTotalPrice());
	            response.setTotalPriceAfterDiscount(bookedService.getTotalPriceAfterDiscount());
	            response.setTotalBookedServices(response.getMainServices().size() + response.getAdditionalServices().size() + response.getOldServices().size());
	            listResponse.add(response);
	        }
	    }
	    return listResponse;
	}
	private static boolean isNewService(OSBBookingData data) {
		return data.getMainServiceId().intValueExact() != OLD_MAINSERVICE_ID;
	}
	
	private static ZonedDateTime convertAppointmentToZonedDate(TimeAsDate d){

        ZoneOffset offsetInHours = ZoneOffset.ofHours(d.getTimezoneOffset().intValueExact()/60);
        ZoneId zoneId = ZoneId.ofOffset("UTC", offsetInHours);

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.getTime().longValue()), zoneId);

        return zonedDateTime;
    }
}
