package com.ford.turbo.aposb.common.authsupport.userprofile;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CSDNUserProfile {

	@JsonProperty("firstName")
	private String firstName;
	@JsonProperty("lastName")
	private String lastName;
	@JsonProperty("userId")
	private Object userId;
	@JsonProperty("middleName")
	private Object middleName;
	@JsonProperty("title")
	private String title;
	@JsonProperty("suffix")
	private Object suffix;
	@JsonProperty("language")
	private String language;
	@JsonProperty("uomDistance")
	private String uomDistance;
	@JsonProperty("uomSpeed")
	private Object uomSpeed;
	@JsonProperty("uomPressure")
	private Object uomPressure;
	@JsonProperty("address1")
	private String address1;
	@JsonProperty("address2")
	private Object address2;
	@JsonProperty("address3")
	private Object address3;
	@JsonProperty("address4")
	private Object address4;
	@JsonProperty("city")
	private String city;
	@JsonProperty("state")
	private String state;
	@JsonProperty("zip")
	private String zip;
	@JsonProperty("country")
	private String country;
	@JsonProperty("timeZone")
	private String timeZone;
	@JsonProperty("versionNumber")
	private Object versionNumber;
	@JsonProperty("termsAccepted")
	private String termsAccepted;
	@JsonProperty("termsVersion")
	private String termsVersion;
	@JsonProperty("deviceID")
	private String deviceID;
	@JsonProperty("lastUpdatedDate")
	private String lastUpdatedDate;
	@JsonProperty("email")
	private String email;
	@JsonProperty("phoneNumber")
	private String phoneNumber;
	@JsonProperty("alternatePhoneNumber")
	private String alternatePhoneNumber;
	@JsonProperty("notificationPrefVehicleRemote")
	private String notificationPrefVehicleRemote;
	@JsonProperty("notificationPrefVehicleHealth")
	private String notificationPrefVehicleHealth;
	@JsonProperty("notificationPrefBillReminder")
	private String notificationPrefBillReminder;
	@JsonProperty("notificationPrefOwnership")
	private String notificationPrefOwnership;
	@JsonProperty("notificationPrefMarketingMsg")
	private String notificationPrefMarketingMsg;
	@JsonProperty("communicationMethod")
	private String communicationMethod;
	@JsonProperty("gender")
	private Object gender;
	@JsonProperty("birthYearMonth")
	private Object birthYearMonth;
	@JsonProperty("appreciationId")
	private String appreciationId;
	@JsonProperty("userDisplayName")
	private Object userDisplayName;
	@JsonProperty("partnerRelationships")
	private List<Object> partnerRelationships = null;

	
}
