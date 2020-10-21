package com.ford.turbo.servicebooking.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ford.turbo.servicebooking.models.msl.response.BookedServiceResponse;
import com.ford.turbo.servicebooking.models.osb.DealerProfile;
import com.ford.turbo.servicebooking.models.osb.OSBOVService;
import com.ford.turbo.servicebooking.models.osb.SelectedVehicleWithPrice;
import com.ford.turbo.servicebooking.models.osb.TimeAsDate;
import org.junit.Test;

import com.ford.turbo.servicebooking.models.osb.BookedAdditionalService;
import com.ford.turbo.servicebooking.models.osb.Dealer;
import com.ford.turbo.servicebooking.models.osb.OSBBookingData;

public class BookedServiceResponseMapperTest {


	@Test
	public void shouldReturn_EmptyBookedServicesList_whenCalled_mapBookedServicesMethodWithEmptyOsbdata() {
		OSBBookingData[] bookedServices = new OSBBookingData[0];
		List<BookedServiceResponse> mapBookedServices = BookedServiceResponseMapper.mapBookedServices(bookedServices);
		assertThat(mapBookedServices.size()).isEqualTo(0);
	}

	@Test
	public void shouldReturn_EmptyBookedServicesList_whenCalled_mapBookedServicesMethodWithNullOsbdata() {
		OSBBookingData[] bookedServices = null;
		List<BookedServiceResponse> mapBookedServices = BookedServiceResponseMapper.mapBookedServices(bookedServices);
		assertThat(mapBookedServices.size()).isEqualTo(0);
	}

	@Test
	public void shouldReturn_BookedServiceResponse_whenCalled_mapBookedServices_with_OsbBookingDataArray() {
		OSBBookingData[] bookedServices = new OSBBookingData[1];

		ArrayList<BookedAdditionalService> bookedAdditionalServices = new ArrayList<>();
		bookedAdditionalServices.add(BookedAdditionalService.builder()
				.additionalServiceName("additional-service")
				.price(new BigDecimal("100"))
				.additionalServiceId("additional-id")
				.additionalServiceComments("comments")
				.build());

		OSBOVService[] motJSON = new OSBOVService[1];

		motJSON[0] = OSBOVService.builder()
				.name("motService-name")
				.selectedVehicle(SelectedVehicleWithPrice.builder()
						.price("200")
						.priceAfterDiscount("100")
						.build())
				.uniqueId("motService-uniqueId")
				.build();

		OSBOVService[] valueServiceJSON = new OSBOVService[1];
		valueServiceJSON[0] = OSBOVService.builder()
				.name("valueService-name")
				.selectedVehicle(SelectedVehicleWithPrice.builder()
						.price("300")
						.priceAfterDiscount("200")
						.build())
				.uniqueId("valueService-uniqueId")
				.build();


		OSBOVService[] repairsJSON = new OSBOVService[1];
		repairsJSON[0] = OSBOVService.builder()
				.name("repairService-name")
				.selectedVehicle(SelectedVehicleWithPrice.builder()
						.price("400")
						.priceAfterDiscount("300")
						.build())
				.uniqueId("repairService-uniqueId")
				.build();

		OSBBookingData osbBookingData1 = OSBBookingData.builder()
				.mainServiceId(new BigDecimal(10001))
				.appointmentTimeAsDate( TimeAsDate.builder()
						.timezoneOffset(new BigDecimal("600"))
						.time(new BigDecimal("60"))
						.build())
				.mainServiceDescription("mainService-desc")
				.mainServicePrice("200")
				.bookingCustomerRefNum("100")
				.customerAnnotation("customer annotation")
				.dealer(Dealer.builder()
						.dealerProfile(DealerProfile.builder().build())
						.build())
				.bookedAdditionalServices(bookedAdditionalServices)
				.motJSON(motJSON)
				.valueServiceJSON(valueServiceJSON)
				.repairsJSON(repairsJSON)
				.totalPrice("1000")
				.totalPriceAfterDiscount("700")
				.build();

		bookedServices[0] = osbBookingData1;

		List<BookedServiceResponse> mapBookedServices = BookedServiceResponseMapper.mapBookedServices(bookedServices);

		assertThat(mapBookedServices.size()).isEqualTo(1);
		BookedServiceResponse bookedServiceResponse = mapBookedServices.get(0);
		
		assertThat(bookedServiceResponse.getTotalBookedServices()).isEqualTo(5);
		assertThat(bookedServiceResponse.getTotalPriceAfterDiscount()).isEqualTo("700");
		assertThat(bookedServiceResponse.getTotalPrice()).isEqualTo("1000");
		
		assertThat(bookedServiceResponse.getOldServices().size()).isEqualTo(3);
		assertThat(bookedServiceResponse.getOldServices().get(0).getServiceId()).isEqualTo("motService-uniqueId:MOT");
		assertThat(bookedServiceResponse.getOldServices().get(1).getServiceId()).isEqualTo("valueService-uniqueId:VALUE");
		assertThat(bookedServiceResponse.getOldServices().get(2).getServiceId()).isEqualTo("repairService-uniqueId:REPAIR");
		assertThat(bookedServiceResponse.getOldServices().get(0).getName()).isEqualTo("motService-name");
		assertThat(bookedServiceResponse.getOldServices().get(1).getName()).isEqualTo("valueService-name");
		assertThat(bookedServiceResponse.getOldServices().get(2).getName()).isEqualTo("repairService-name");
		
		assertThat(bookedServiceResponse.getMainServices().get(0).getName()).isEqualTo("mainService-desc");
		assertThat(bookedServiceResponse.getMainServices().get(0).getServiceId()).isEqualTo("10001");
		assertThat(bookedServiceResponse.getMainServices().get(0).getPrice()).isEqualTo("200.0");
		
		assertThat(bookedServiceResponse.getAdditionalServices().get(0).getName()).isEqualTo("additional-service");
		assertThat(bookedServiceResponse.getAdditionalServices().get(0).getPrice()).isEqualTo("100");
		assertThat(bookedServiceResponse.getAdditionalServices().get(0).getServiceId()).isEqualTo("additional-id");
	}
	
	@Test
	public void shouldReturn_emptyBookedServiceResponseList_when_mainServiceIdNotPresent() {
		
		OSBBookingData[] bookedServices = new OSBBookingData[2];
		
		OSBBookingData osbBookingData1 = OSBBookingData.builder()
														.mainServiceId(new BigDecimal(0))
														.appointmentTimeAsDate( TimeAsDate.builder()
																.timezoneOffset(new BigDecimal("600"))
																.time(new BigDecimal("60"))
																.build())
														.dealer(Dealer.builder()
																.dealerProfile(DealerProfile.builder().build())
																.build())
														.bookedAdditionalServices(new ArrayList<>())
														.build();
		bookedServices[0] = osbBookingData1;
		
		OSBBookingData osbBookingData2 = OSBBookingData.builder()
				.mainServiceId(new BigDecimal(0))
				.appointmentTimeAsDate( TimeAsDate.builder()
						.timezoneOffset(new BigDecimal("600"))
						.time(new BigDecimal("60"))
						.build())
				.dealer(Dealer.builder()
						.dealerProfile(DealerProfile.builder().build())
						.build())
				.bookedAdditionalServices(new ArrayList<>())
				.build();
		bookedServices[1] = osbBookingData2;

		List<BookedServiceResponse> mapBookedServices = BookedServiceResponseMapper.mapBookedServices(bookedServices);
		assertThat(mapBookedServices.size()).isEqualTo(0);
	}
	
	@Test
	public void shouldReturn_EmptyList_when_OSBBookingData_DoesNotContainsBookingServices() {
		
		OSBBookingData[] bookedServices = new OSBBookingData[1];
		
		OSBBookingData osbBookingData1 = OSBBookingData.builder()
														.mainServiceId(new BigDecimal(1))
														.appointmentTimeAsDate( TimeAsDate.builder()
																.timezoneOffset(new BigDecimal("600"))
																.time(new BigDecimal("60"))
																.build())
														.dealer(Dealer.builder()
																.dealerProfile(DealerProfile.builder().build())
																.build())
														.bookedAdditionalServices(new ArrayList<>())
														.build();
		bookedServices[0] = osbBookingData1;


		List<BookedServiceResponse> mapBookedServices = BookedServiceResponseMapper.mapBookedServices(bookedServices);
		assertThat(mapBookedServices.size()).isEqualTo(1);
		assertNull(mapBookedServices.get(0).getMainServices().get(0).getPrice());
		assertThat(mapBookedServices.get(0).getAdditionalServices().size()).isEqualTo(0);
		assertThat(mapBookedServices.get(0).getOldServices().size()).isEqualTo(0);
	}
}
