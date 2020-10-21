package com.ford.turbo.servicebooking.models.msl.response.v2;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DealerProfile   {
  
  private String contactFirstPersonFirstName = null;
  private String contactSecondPersonLastName = null;
  private String contactFirstPersonLastName = null;
  private String contactSecondPersonFirstName = null;
  private String town = null;
  private String openingHoursServiceSunday = null;
  private String createUser = null;
  private String openingHoursServiceSaturday = null;
  private String country = null;
  private Boolean osbovEnabled = null;
  private String openingHoursServiceFriday = null;
  private String openingHoursServiceTuesday = null;
  private String dealerName = null;
  private String contactSecondPersonEmail = null;
  private String lastUpdated = null;
  private String osbSecondEmail = null;
  private String contactEmail = null;
  private String contactFirstPersonTitle = null;
  private String contactFirstPersonAreacode = null;
  private String fax = null;
  private String marketCode = null;
  private String district = null;
  private String openingHoursServiceWednesday = null;
  private String osbEmails = null;
  private String dealerCode = null;
  private String contactFirstPersonPhone = null;
  private String businessHours = null;
  private String contactSecondPersonTitle = null;
  private String postalCode = null;
  private String contactSecondPersonPhone = null;
  private String type = null;
  private String contactFirstPersonEmail = null;
  private String contactSecondPersonAreacode = null;
  private String contactFax = null;
  private String lastUpdateUser = null;
  private String state = null;
  private String openingHoursServiceThursday = null;
  private String street = null;
  private String lastUpdateTime = null;
  private String phone = null;
  private String openingHoursServiceMonday = null;
  private String createTime = null;

  
  /**
   **/
  public DealerProfile contactFirstPersonFirstName(String contactFirstPersonFirstName) {
    this.contactFirstPersonFirstName = contactFirstPersonFirstName;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactFirstPersonFirstName")
  public String getContactFirstPersonFirstName() {
    return contactFirstPersonFirstName;
  }
  public void setContactFirstPersonFirstName(String contactFirstPersonFirstName) {
    this.contactFirstPersonFirstName = contactFirstPersonFirstName;
  }


  /**
   **/
  public DealerProfile contactSecondPersonLastName(String contactSecondPersonLastName) {
    this.contactSecondPersonLastName = contactSecondPersonLastName;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactSecondPersonLastName")
  public String getContactSecondPersonLastName() {
    return contactSecondPersonLastName;
  }
  public void setContactSecondPersonLastName(String contactSecondPersonLastName) {
    this.contactSecondPersonLastName = contactSecondPersonLastName;
  }


  /**
   **/
  public DealerProfile contactFirstPersonLastName(String contactFirstPersonLastName) {
    this.contactFirstPersonLastName = contactFirstPersonLastName;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactFirstPersonLastName")
  public String getContactFirstPersonLastName() {
    return contactFirstPersonLastName;
  }
  public void setContactFirstPersonLastName(String contactFirstPersonLastName) {
    this.contactFirstPersonLastName = contactFirstPersonLastName;
  }


  /**
   **/
  public DealerProfile contactSecondPersonFirstName(String contactSecondPersonFirstName) {
    this.contactSecondPersonFirstName = contactSecondPersonFirstName;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactSecondPersonFirstName")
  public String getContactSecondPersonFirstName() {
    return contactSecondPersonFirstName;
  }
  public void setContactSecondPersonFirstName(String contactSecondPersonFirstName) {
    this.contactSecondPersonFirstName = contactSecondPersonFirstName;
  }


  /**
   **/
  public DealerProfile town(String town) {
    this.town = town;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("town")
  public String getTown() {
    return town;
  }
  public void setTown(String town) {
    this.town = town;
  }


  /**
   **/
  public DealerProfile openingHoursServiceSunday(String openingHoursServiceSunday) {
    this.openingHoursServiceSunday = openingHoursServiceSunday;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("openingHoursServiceSunday")
  public String getOpeningHoursServiceSunday() {
    return openingHoursServiceSunday;
  }
  public void setOpeningHoursServiceSunday(String openingHoursServiceSunday) {
    this.openingHoursServiceSunday = openingHoursServiceSunday;
  }


  /**
   **/
  public DealerProfile createUser(String createUser) {
    this.createUser = createUser;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("createUser")
  public String getCreateUser() {
    return createUser;
  }
  public void setCreateUser(String createUser) {
    this.createUser = createUser;
  }


  /**
   **/
  public DealerProfile openingHoursServiceSaturday(String openingHoursServiceSaturday) {
    this.openingHoursServiceSaturday = openingHoursServiceSaturday;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("openingHoursServiceSaturday")
  public String getOpeningHoursServiceSaturday() {
    return openingHoursServiceSaturday;
  }
  public void setOpeningHoursServiceSaturday(String openingHoursServiceSaturday) {
    this.openingHoursServiceSaturday = openingHoursServiceSaturday;
  }


  /**
   **/
  public DealerProfile country(String country) {
    this.country = country;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("country")
  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }


  /**
   **/
  public DealerProfile osbovEnabled(Boolean osbovEnabled) {
    this.osbovEnabled = osbovEnabled;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("osbovEnabled")
  public Boolean getOsbovEnabled() {
    return osbovEnabled;
  }
  public void setOsbovEnabled(Boolean osbovEnabled) {
    this.osbovEnabled = osbovEnabled;
  }


  /**
   **/
  public DealerProfile openingHoursServiceFriday(String openingHoursServiceFriday) {
    this.openingHoursServiceFriday = openingHoursServiceFriday;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("openingHoursServiceFriday")
  public String getOpeningHoursServiceFriday() {
    return openingHoursServiceFriday;
  }
  public void setOpeningHoursServiceFriday(String openingHoursServiceFriday) {
    this.openingHoursServiceFriday = openingHoursServiceFriday;
  }


  /**
   **/
  public DealerProfile openingHoursServiceTuesday(String openingHoursServiceTuesday) {
    this.openingHoursServiceTuesday = openingHoursServiceTuesday;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("openingHoursServiceTuesday")
  public String getOpeningHoursServiceTuesday() {
    return openingHoursServiceTuesday;
  }
  public void setOpeningHoursServiceTuesday(String openingHoursServiceTuesday) {
    this.openingHoursServiceTuesday = openingHoursServiceTuesday;
  }


  /**
   **/
  public DealerProfile dealerName(String dealerName) {
    this.dealerName = dealerName;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("dealerName")
  public String getDealerName() {
    return dealerName;
  }
  public void setDealerName(String dealerName) {
    this.dealerName = dealerName;
  }


  /**
   **/
  public DealerProfile contactSecondPersonEmail(String contactSecondPersonEmail) {
    this.contactSecondPersonEmail = contactSecondPersonEmail;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactSecondPersonEmail")
  public String getContactSecondPersonEmail() {
    return contactSecondPersonEmail;
  }
  public void setContactSecondPersonEmail(String contactSecondPersonEmail) {
    this.contactSecondPersonEmail = contactSecondPersonEmail;
  }


  /**
   **/
  public DealerProfile lastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("lastUpdated")
  public String getLastUpdated() {
    return lastUpdated;
  }
  public void setLastUpdated(String lastUpdated) {
    this.lastUpdated = lastUpdated;
  }


  /**
   **/
  public DealerProfile osbSecondEmail(String osbSecondEmail) {
    this.osbSecondEmail = osbSecondEmail;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("osbSecondEmail")
  public String getOsbSecondEmail() {
    return osbSecondEmail;
  }
  public void setOsbSecondEmail(String osbSecondEmail) {
    this.osbSecondEmail = osbSecondEmail;
  }


  /**
   **/
  public DealerProfile contactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactEmail")
  public String getContactEmail() {
    return contactEmail;
  }
  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }


  /**
   **/
  public DealerProfile contactFirstPersonTitle(String contactFirstPersonTitle) {
    this.contactFirstPersonTitle = contactFirstPersonTitle;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactFirstPersonTitle")
  public String getContactFirstPersonTitle() {
    return contactFirstPersonTitle;
  }
  public void setContactFirstPersonTitle(String contactFirstPersonTitle) {
    this.contactFirstPersonTitle = contactFirstPersonTitle;
  }


  /**
   **/
  public DealerProfile contactFirstPersonAreacode(String contactFirstPersonAreacode) {
    this.contactFirstPersonAreacode = contactFirstPersonAreacode;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactFirstPersonAreacode")
  public String getContactFirstPersonAreacode() {
    return contactFirstPersonAreacode;
  }
  public void setContactFirstPersonAreacode(String contactFirstPersonAreacode) {
    this.contactFirstPersonAreacode = contactFirstPersonAreacode;
  }


  /**
   **/
  public DealerProfile fax(String fax) {
    this.fax = fax;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("fax")
  public String getFax() {
    return fax;
  }
  public void setFax(String fax) {
    this.fax = fax;
  }


  /**
   **/
  public DealerProfile marketCode(String marketCode) {
    this.marketCode = marketCode;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("marketCode")
  public String getMarketCode() {
    return marketCode;
  }
  public void setMarketCode(String marketCode) {
    this.marketCode = marketCode;
  }


  /**
   **/
  public DealerProfile district(String district) {
    this.district = district;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("district")
  public String getDistrict() {
    return district;
  }
  public void setDistrict(String district) {
    this.district = district;
  }


  /**
   **/
  public DealerProfile openingHoursServiceWednesday(String openingHoursServiceWednesday) {
    this.openingHoursServiceWednesday = openingHoursServiceWednesday;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("openingHoursServiceWednesday")
  public String getOpeningHoursServiceWednesday() {
    return openingHoursServiceWednesday;
  }
  public void setOpeningHoursServiceWednesday(String openingHoursServiceWednesday) {
    this.openingHoursServiceWednesday = openingHoursServiceWednesday;
  }


  /**
   **/
  public DealerProfile osbEmails(String osbEmails) {
    this.osbEmails = osbEmails;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("osbEmails")
  public String getOsbEmails() {
    return osbEmails;
  }
  public void setOsbEmails(String osbEmails) {
    this.osbEmails = osbEmails;
  }


  /**
   **/
  public DealerProfile dealerCode(String dealerCode) {
    this.dealerCode = dealerCode;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("dealerCode")
  public String getDealerCode() {
    return dealerCode;
  }
  public void setDealerCode(String dealerCode) {
    this.dealerCode = dealerCode;
  }


  /**
   **/
  public DealerProfile contactFirstPersonPhone(String contactFirstPersonPhone) {
    this.contactFirstPersonPhone = contactFirstPersonPhone;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactFirstPersonPhone")
  public String getContactFirstPersonPhone() {
    return contactFirstPersonPhone;
  }
  public void setContactFirstPersonPhone(String contactFirstPersonPhone) {
    this.contactFirstPersonPhone = contactFirstPersonPhone;
  }


  /**
   **/
  public DealerProfile businessHours(String businessHours) {
    this.businessHours = businessHours;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("businessHours")
  public String getBusinessHours() {
    return businessHours;
  }
  public void setBusinessHours(String businessHours) {
    this.businessHours = businessHours;
  }


  /**
   **/
  public DealerProfile contactSecondPersonTitle(String contactSecondPersonTitle) {
    this.contactSecondPersonTitle = contactSecondPersonTitle;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactSecondPersonTitle")
  public String getContactSecondPersonTitle() {
    return contactSecondPersonTitle;
  }
  public void setContactSecondPersonTitle(String contactSecondPersonTitle) {
    this.contactSecondPersonTitle = contactSecondPersonTitle;
  }


  /**
   **/
  public DealerProfile postalCode(String postalCode) {
    this.postalCode = postalCode;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("postalCode")
  public String getPostalCode() {
    return postalCode;
  }
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }


  /**
   **/
  public DealerProfile contactSecondPersonPhone(String contactSecondPersonPhone) {
    this.contactSecondPersonPhone = contactSecondPersonPhone;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactSecondPersonPhone")
  public String getContactSecondPersonPhone() {
    return contactSecondPersonPhone;
  }
  public void setContactSecondPersonPhone(String contactSecondPersonPhone) {
    this.contactSecondPersonPhone = contactSecondPersonPhone;
  }


  /**
   **/
  public DealerProfile type(String type) {
    this.type = type;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("_type")
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }


  /**
   **/
  public DealerProfile contactFirstPersonEmail(String contactFirstPersonEmail) {
    this.contactFirstPersonEmail = contactFirstPersonEmail;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactFirstPersonEmail")
  public String getContactFirstPersonEmail() {
    return contactFirstPersonEmail;
  }
  public void setContactFirstPersonEmail(String contactFirstPersonEmail) {
    this.contactFirstPersonEmail = contactFirstPersonEmail;
  }


  /**
   **/
  public DealerProfile contactSecondPersonAreacode(String contactSecondPersonAreacode) {
    this.contactSecondPersonAreacode = contactSecondPersonAreacode;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactSecondPersonAreacode")
  public String getContactSecondPersonAreacode() {
    return contactSecondPersonAreacode;
  }
  public void setContactSecondPersonAreacode(String contactSecondPersonAreacode) {
    this.contactSecondPersonAreacode = contactSecondPersonAreacode;
  }


  /**
   **/
  public DealerProfile contactFax(String contactFax) {
    this.contactFax = contactFax;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("contactFax")
  public String getContactFax() {
    return contactFax;
  }
  public void setContactFax(String contactFax) {
    this.contactFax = contactFax;
  }


  /**
   **/
  public DealerProfile lastUpdateUser(String lastUpdateUser) {
    this.lastUpdateUser = lastUpdateUser;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("lastUpdateUser")
  public String getLastUpdateUser() {
    return lastUpdateUser;
  }
  public void setLastUpdateUser(String lastUpdateUser) {
    this.lastUpdateUser = lastUpdateUser;
  }


  /**
   **/
  public DealerProfile state(String state) {
    this.state = state;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("state")
  public String getState() {
    return state;
  }
  public void setState(String state) {
    this.state = state;
  }


  /**
   **/
  public DealerProfile openingHoursServiceThursday(String openingHoursServiceThursday) {
    this.openingHoursServiceThursday = openingHoursServiceThursday;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("openingHoursServiceThursday")
  public String getOpeningHoursServiceThursday() {
    return openingHoursServiceThursday;
  }
  public void setOpeningHoursServiceThursday(String openingHoursServiceThursday) {
    this.openingHoursServiceThursday = openingHoursServiceThursday;
  }


  /**
   **/
  public DealerProfile street(String street) {
    this.street = street;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("street")
  public String getStreet() {
    return street;
  }
  public void setStreet(String street) {
    this.street = street;
  }


  /**
   **/
  public DealerProfile lastUpdateTime(String lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("lastUpdateTime")
  public String getLastUpdateTime() {
    return lastUpdateTime;
  }
  public void setLastUpdateTime(String lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }


  /**
   **/
  public DealerProfile phone(String phone) {
    this.phone = phone;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("phone")
  public String getPhone() {
    return phone;
  }
  public void setPhone(String phone) {
    this.phone = phone;
  }


  /**
   **/
  public DealerProfile openingHoursServiceMonday(String openingHoursServiceMonday) {
    this.openingHoursServiceMonday = openingHoursServiceMonday;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("openingHoursServiceMonday")
  public String getOpeningHoursServiceMonday() {
    return openingHoursServiceMonday;
  }
  public void setOpeningHoursServiceMonday(String openingHoursServiceMonday) {
    this.openingHoursServiceMonday = openingHoursServiceMonday;
  }


  /**
   **/
  public DealerProfile createTime(String createTime) {
    this.createTime = createTime;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("createTime")
  public String getCreateTime() {
    return createTime;
  }
  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DealerProfile dealerProfile = (DealerProfile) o;
    return Objects.equals(this.contactFirstPersonFirstName, dealerProfile.contactFirstPersonFirstName) &&
        Objects.equals(this.contactSecondPersonLastName, dealerProfile.contactSecondPersonLastName) &&
        Objects.equals(this.contactFirstPersonLastName, dealerProfile.contactFirstPersonLastName) &&
        Objects.equals(this.contactSecondPersonFirstName, dealerProfile.contactSecondPersonFirstName) &&
        Objects.equals(this.town, dealerProfile.town) &&
        Objects.equals(this.openingHoursServiceSunday, dealerProfile.openingHoursServiceSunday) &&
        Objects.equals(this.createUser, dealerProfile.createUser) &&
        Objects.equals(this.openingHoursServiceSaturday, dealerProfile.openingHoursServiceSaturday) &&
        Objects.equals(this.country, dealerProfile.country) &&
        Objects.equals(this.osbovEnabled, dealerProfile.osbovEnabled) &&
        Objects.equals(this.openingHoursServiceFriday, dealerProfile.openingHoursServiceFriday) &&
        Objects.equals(this.openingHoursServiceTuesday, dealerProfile.openingHoursServiceTuesday) &&
        Objects.equals(this.dealerName, dealerProfile.dealerName) &&
        Objects.equals(this.contactSecondPersonEmail, dealerProfile.contactSecondPersonEmail) &&
        Objects.equals(this.lastUpdated, dealerProfile.lastUpdated) &&
        Objects.equals(this.osbSecondEmail, dealerProfile.osbSecondEmail) &&
        Objects.equals(this.contactEmail, dealerProfile.contactEmail) &&
        Objects.equals(this.contactFirstPersonTitle, dealerProfile.contactFirstPersonTitle) &&
        Objects.equals(this.contactFirstPersonAreacode, dealerProfile.contactFirstPersonAreacode) &&
        Objects.equals(this.fax, dealerProfile.fax) &&
        Objects.equals(this.marketCode, dealerProfile.marketCode) &&
        Objects.equals(this.district, dealerProfile.district) &&
        Objects.equals(this.openingHoursServiceWednesday, dealerProfile.openingHoursServiceWednesday) &&
        Objects.equals(this.osbEmails, dealerProfile.osbEmails) &&
        Objects.equals(this.dealerCode, dealerProfile.dealerCode) &&
        Objects.equals(this.contactFirstPersonPhone, dealerProfile.contactFirstPersonPhone) &&
        Objects.equals(this.businessHours, dealerProfile.businessHours) &&
        Objects.equals(this.contactSecondPersonTitle, dealerProfile.contactSecondPersonTitle) &&
        Objects.equals(this.postalCode, dealerProfile.postalCode) &&
        Objects.equals(this.contactSecondPersonPhone, dealerProfile.contactSecondPersonPhone) &&
        Objects.equals(this.type, dealerProfile.type) &&
        Objects.equals(this.contactFirstPersonEmail, dealerProfile.contactFirstPersonEmail) &&
        Objects.equals(this.contactSecondPersonAreacode, dealerProfile.contactSecondPersonAreacode) &&
        Objects.equals(this.contactFax, dealerProfile.contactFax) &&
        Objects.equals(this.lastUpdateUser, dealerProfile.lastUpdateUser) &&
        Objects.equals(this.state, dealerProfile.state) &&
        Objects.equals(this.openingHoursServiceThursday, dealerProfile.openingHoursServiceThursday) &&
        Objects.equals(this.street, dealerProfile.street) &&
        Objects.equals(this.lastUpdateTime, dealerProfile.lastUpdateTime) &&
        Objects.equals(this.phone, dealerProfile.phone) &&
        Objects.equals(this.openingHoursServiceMonday, dealerProfile.openingHoursServiceMonday) &&
        Objects.equals(this.createTime, dealerProfile.createTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(contactFirstPersonFirstName, contactSecondPersonLastName, contactFirstPersonLastName, contactSecondPersonFirstName, town, openingHoursServiceSunday, createUser, openingHoursServiceSaturday, country, osbovEnabled, openingHoursServiceFriday, openingHoursServiceTuesday, dealerName, contactSecondPersonEmail, lastUpdated, osbSecondEmail, contactEmail, contactFirstPersonTitle, contactFirstPersonAreacode, fax, marketCode, district, openingHoursServiceWednesday, osbEmails, dealerCode, contactFirstPersonPhone, businessHours, contactSecondPersonTitle, postalCode, contactSecondPersonPhone, type, contactFirstPersonEmail, contactSecondPersonAreacode, contactFax, lastUpdateUser, state, openingHoursServiceThursday, street, lastUpdateTime, phone, openingHoursServiceMonday, createTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DealerProfile {\n");
    
    sb.append("    contactFirstPersonFirstName: ").append(toIndentedString(contactFirstPersonFirstName)).append("\n");
    sb.append("    contactSecondPersonLastName: ").append(toIndentedString(contactSecondPersonLastName)).append("\n");
    sb.append("    contactFirstPersonLastName: ").append(toIndentedString(contactFirstPersonLastName)).append("\n");
    sb.append("    contactSecondPersonFirstName: ").append(toIndentedString(contactSecondPersonFirstName)).append("\n");
    sb.append("    town: ").append(toIndentedString(town)).append("\n");
    sb.append("    openingHoursServiceSunday: ").append(toIndentedString(openingHoursServiceSunday)).append("\n");
    sb.append("    createUser: ").append(toIndentedString(createUser)).append("\n");
    sb.append("    openingHoursServiceSaturday: ").append(toIndentedString(openingHoursServiceSaturday)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
    sb.append("    osbovEnabled: ").append(toIndentedString(osbovEnabled)).append("\n");
    sb.append("    openingHoursServiceFriday: ").append(toIndentedString(openingHoursServiceFriday)).append("\n");
    sb.append("    openingHoursServiceTuesday: ").append(toIndentedString(openingHoursServiceTuesday)).append("\n");
    sb.append("    dealerName: ").append(toIndentedString(dealerName)).append("\n");
    sb.append("    contactSecondPersonEmail: ").append(toIndentedString(contactSecondPersonEmail)).append("\n");
    sb.append("    lastUpdated: ").append(toIndentedString(lastUpdated)).append("\n");
    sb.append("    osbSecondEmail: ").append(toIndentedString(osbSecondEmail)).append("\n");
    sb.append("    contactEmail: ").append(toIndentedString(contactEmail)).append("\n");
    sb.append("    contactFirstPersonTitle: ").append(toIndentedString(contactFirstPersonTitle)).append("\n");
    sb.append("    contactFirstPersonAreacode: ").append(toIndentedString(contactFirstPersonAreacode)).append("\n");
    sb.append("    fax: ").append(toIndentedString(fax)).append("\n");
    sb.append("    marketCode: ").append(toIndentedString(marketCode)).append("\n");
    sb.append("    district: ").append(toIndentedString(district)).append("\n");
    sb.append("    openingHoursServiceWednesday: ").append(toIndentedString(openingHoursServiceWednesday)).append("\n");
    sb.append("    osbEmails: ").append(toIndentedString(osbEmails)).append("\n");
    sb.append("    dealerCode: ").append(toIndentedString(dealerCode)).append("\n");
    sb.append("    contactFirstPersonPhone: ").append(toIndentedString(contactFirstPersonPhone)).append("\n");
    sb.append("    businessHours: ").append(toIndentedString(businessHours)).append("\n");
    sb.append("    contactSecondPersonTitle: ").append(toIndentedString(contactSecondPersonTitle)).append("\n");
    sb.append("    postalCode: ").append(toIndentedString(postalCode)).append("\n");
    sb.append("    contactSecondPersonPhone: ").append(toIndentedString(contactSecondPersonPhone)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    contactFirstPersonEmail: ").append(toIndentedString(contactFirstPersonEmail)).append("\n");
    sb.append("    contactSecondPersonAreacode: ").append(toIndentedString(contactSecondPersonAreacode)).append("\n");
    sb.append("    contactFax: ").append(toIndentedString(contactFax)).append("\n");
    sb.append("    lastUpdateUser: ").append(toIndentedString(lastUpdateUser)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    openingHoursServiceThursday: ").append(toIndentedString(openingHoursServiceThursday)).append("\n");
    sb.append("    street: ").append(toIndentedString(street)).append("\n");
    sb.append("    lastUpdateTime: ").append(toIndentedString(lastUpdateTime)).append("\n");
    sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
    sb.append("    openingHoursServiceMonday: ").append(toIndentedString(openingHoursServiceMonday)).append("\n");
    sb.append("    createTime: ").append(toIndentedString(createTime)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

