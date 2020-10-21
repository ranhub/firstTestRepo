package com.ford.turbo.aposb.common.authsupport.vehicles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CSDNVehicle {
	
	private String vin;
	private String nickName ;
	private String vehicleType;
	private String color;
	private String modelName;
	private String modelCode;
	private String modelYear;
	private String tcuEnabled;
	private String make;
	private String cylinders;
	private String drivetrain;
	private String engineDisp;
	private String fuelType;
	private String series;
	private String productVariant;
	private String averageMiles;
	private String estimatedMileage;
	private String mileage;
	private String mileageDate;
	private String mileageSource;
	private String drivingConditionId;
	private String configurationId;
	private String primaryIndicator;
	private String licenseplate;
	private String purchaseDate;
	private String registrationDate;
	private String ownerCycle;
	private String ownerindicator;
	private String brandCode;
	private String vehicleImageId;
	private String headUnitType;
	private String steeringWheelType;
	private String lifeStyleXML;
	private String syncVehicleIndicator;
	private String vhrReadyDate;
	private String vhrNotificationDate;
	private String vhrUrgentNotificationStatus;
	private String vhrStatus;
	private String vhrNotificationStatus;
	private String ngSdnManaged;
	private String transmission;
	private String bodyStyle;
	private String preferredDealer;
	private String assignedDealer;
	private String sellingDealer;
	private String vhrReadyIndicator;
    private String vehicleAuthorizationIndicator;

}
