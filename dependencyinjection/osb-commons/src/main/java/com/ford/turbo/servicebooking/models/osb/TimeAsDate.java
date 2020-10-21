package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


/**
 * TimeAsDate
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeAsDate   {
  
  private BigDecimal date = null;
  private BigDecimal day = null;
  private BigDecimal timezoneOffset = null;
  private BigDecimal year = null;
  private BigDecimal month = null;
  private BigDecimal hours = null;
  private BigDecimal seconds = null;
  private BigDecimal minutes = null;
  private BigDecimal time = null;
  private String type = null;

  
  /**
   **/
  public TimeAsDate date(BigDecimal date) {
    this.date = date;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("date")
  public BigDecimal getDate() {
    return date;
  }
  public void setDate(BigDecimal date) {
    this.date = date;
  }


  /**
   **/
  public TimeAsDate day(BigDecimal day) {
    this.day = day;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("day")
  public BigDecimal getDay() {
    return day;
  }
  public void setDay(BigDecimal day) {
    this.day = day;
  }


  /**
   **/
  public TimeAsDate timezoneOffset(BigDecimal timezoneOffset) {
    this.timezoneOffset = timezoneOffset;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("timezoneOffset")
  public BigDecimal getTimezoneOffset() {
    return timezoneOffset;
  }
  public void setTimezoneOffset(BigDecimal timezoneOffset) {
    this.timezoneOffset = timezoneOffset;
  }


  /**
   **/
  public TimeAsDate year(BigDecimal year) {
    this.year = year;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("year")
  public BigDecimal getYear() {
    return year;
  }
  public void setYear(BigDecimal year) {
    this.year = year;
  }


  /**
   **/
  public TimeAsDate month(BigDecimal month) {
    this.month = month;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("month")
  public BigDecimal getMonth() {
    return month;
  }
  public void setMonth(BigDecimal month) {
    this.month = month;
  }


  /**
   **/
  public TimeAsDate hours(BigDecimal hours) {
    this.hours = hours;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("hours")
  public BigDecimal getHours() {
    return hours;
  }
  public void setHours(BigDecimal hours) {
    this.hours = hours;
  }


  /**
   **/
  public TimeAsDate seconds(BigDecimal seconds) {
    this.seconds = seconds;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("seconds")
  public BigDecimal getSeconds() {
    return seconds;
  }
  public void setSeconds(BigDecimal seconds) {
    this.seconds = seconds;
  }


  /**
   **/
  public TimeAsDate minutes(BigDecimal minutes) {
    this.minutes = minutes;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("minutes")
  public BigDecimal getMinutes() {
    return minutes;
  }
  public void setMinutes(BigDecimal minutes) {
    this.minutes = minutes;
  }


  /**
   **/
  public TimeAsDate time(BigDecimal time) {
    this.time = time;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("time")
  public BigDecimal getTime() {
    return time;
  }
  public void setTime(BigDecimal time) {
    this.time = time;
  }


  /**
   **/
  public TimeAsDate type(String type) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimeAsDate timeAsDate = (TimeAsDate) o;
    return Objects.equals(this.date, timeAsDate.date) &&
        Objects.equals(this.day, timeAsDate.day) &&
        Objects.equals(this.timezoneOffset, timeAsDate.timezoneOffset) &&
        Objects.equals(this.year, timeAsDate.year) &&
        Objects.equals(this.month, timeAsDate.month) &&
        Objects.equals(this.hours, timeAsDate.hours) &&
        Objects.equals(this.seconds, timeAsDate.seconds) &&
        Objects.equals(this.minutes, timeAsDate.minutes) &&
        Objects.equals(this.time, timeAsDate.time) &&
        Objects.equals(this.type, timeAsDate.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, day, timezoneOffset, year, month, hours, seconds, minutes, time, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TimeAsDate {\n");
    
    sb.append("    date: ").append(toIndentedString(date)).append("\n");
    sb.append("    day: ").append(toIndentedString(day)).append("\n");
    sb.append("    timezoneOffset: ").append(toIndentedString(timezoneOffset)).append("\n");
    sb.append("    year: ").append(toIndentedString(year)).append("\n");
    sb.append("    month: ").append(toIndentedString(month)).append("\n");
    sb.append("    hours: ").append(toIndentedString(hours)).append("\n");
    sb.append("    seconds: ").append(toIndentedString(seconds)).append("\n");
    sb.append("    minutes: ").append(toIndentedString(minutes)).append("\n");
    sb.append("    time: ").append(toIndentedString(time)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

