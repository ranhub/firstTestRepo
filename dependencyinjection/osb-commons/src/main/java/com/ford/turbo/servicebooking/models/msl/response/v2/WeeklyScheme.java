package com.ford.turbo.servicebooking.models.msl.response.v2;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;


/**
 * WeeklyScheme
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeeklyScheme   {
  
  private BigDecimal leadTime = null;
  private BigDecimal timeSlotDuration = null;
  private BigDecimal lastUpdateUser = null;
  private String lastUpdateTime = null;
  private String serviceSettings = null;
  private String createUser = null;
  private BigDecimal createTime = null;
  private String type = null;
  private BigDecimal id = null;

  
  /**
   **/
  public WeeklyScheme leadTime(BigDecimal leadTime) {
    this.leadTime = leadTime;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("leadTime")
  public BigDecimal getLeadTime() {
    return leadTime;
  }
  public void setLeadTime(BigDecimal leadTime) {
    this.leadTime = leadTime;
  }


  /**
   **/
  public WeeklyScheme timeSlotDuration(BigDecimal timeSlotDuration) {
    this.timeSlotDuration = timeSlotDuration;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("timeSlotDuration")
  public BigDecimal getTimeSlotDuration() {
    return timeSlotDuration;
  }
  public void setTimeSlotDuration(BigDecimal timeSlotDuration) {
    this.timeSlotDuration = timeSlotDuration;
  }


  /**
   **/
  public WeeklyScheme lastUpdateUser(BigDecimal lastUpdateUser) {
    this.lastUpdateUser = lastUpdateUser;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("lastUpdateUser")
  public BigDecimal getLastUpdateUser() {
    return lastUpdateUser;
  }
  public void setLastUpdateUser(BigDecimal lastUpdateUser) {
    this.lastUpdateUser = lastUpdateUser;
  }


  /**
   **/
  public WeeklyScheme lastUpdateTime(String lastUpdateTime) {
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
  public WeeklyScheme serviceSettings(String serviceSettings) {
    this.serviceSettings = serviceSettings;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("serviceSettings")
  public String getServiceSettings() {
    return serviceSettings;
  }
  public void setServiceSettings(String serviceSettings) {
    this.serviceSettings = serviceSettings;
  }


  /**
   **/
  public WeeklyScheme createUser(String createUser) {
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
  public WeeklyScheme createTime(BigDecimal createTime) {
    this.createTime = createTime;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("createTime")
  public BigDecimal getCreateTime() {
    return createTime;
  }
  public void setCreateTime(BigDecimal createTime) {
    this.createTime = createTime;
  }


  /**
   **/
  public WeeklyScheme type(String type) {
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
  public WeeklyScheme id(BigDecimal id) {
    this.id = id;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("id")
  public BigDecimal getId() {
    return id;
  }
  public void setId(BigDecimal id) {
    this.id = id;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WeeklyScheme weeklyScheme = (WeeklyScheme) o;
    return Objects.equals(this.leadTime, weeklyScheme.leadTime) &&
        Objects.equals(this.timeSlotDuration, weeklyScheme.timeSlotDuration) &&
        Objects.equals(this.lastUpdateUser, weeklyScheme.lastUpdateUser) &&
        Objects.equals(this.lastUpdateTime, weeklyScheme.lastUpdateTime) &&
        Objects.equals(this.serviceSettings, weeklyScheme.serviceSettings) &&
        Objects.equals(this.createUser, weeklyScheme.createUser) &&
        Objects.equals(this.createTime, weeklyScheme.createTime) &&
        Objects.equals(this.type, weeklyScheme.type) &&
        Objects.equals(this.id, weeklyScheme.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(leadTime, timeSlotDuration, lastUpdateUser, lastUpdateTime, serviceSettings, createUser, createTime, type, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WeeklyScheme {\n");
    
    sb.append("    leadTime: ").append(toIndentedString(leadTime)).append("\n");
    sb.append("    timeSlotDuration: ").append(toIndentedString(timeSlotDuration)).append("\n");
    sb.append("    lastUpdateUser: ").append(toIndentedString(lastUpdateUser)).append("\n");
    sb.append("    lastUpdateTime: ").append(toIndentedString(lastUpdateTime)).append("\n");
    sb.append("    serviceSettings: ").append(toIndentedString(serviceSettings)).append("\n");
    sb.append("    createUser: ").append(toIndentedString(createUser)).append("\n");
    sb.append("    createTime: ").append(toIndentedString(createTime)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

