package com.ford.turbo.servicebooking.models.osb;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;


/**
 * VehicleDetails
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDetails   {
  
  private String buildDate = null;
  private String vin = null;
  private String transmissionType = null;
  private String color = null;
  private String fuelType = null;
  private String modelName = null;
  private String registrationNumber = null;
  private String version = null;
  private BigDecimal mileageInMiles = null;
  private String transmission = null;
  private String vehicleLineCode = null;
  private String engine = null;
  private BigDecimal mileageInKm = null;
  private String bodyStyle = null;

  
  /**
   **/
  public VehicleDetails buildDate(String buildDate) {
    this.buildDate = buildDate;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("buildDate")
  public String getBuildDate() {
    return buildDate;
  }
  public void setBuildDate(String buildDate) {
    this.buildDate = buildDate;
  }


  /**
   **/
  public VehicleDetails vin(String vin) {
    this.vin = vin;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("vin")
  public String getVin() {
    return vin;
  }
  public void setVin(String vin) {
    this.vin = vin;
  }


  /**
   **/
  public VehicleDetails transmissionType(String transmissionType) {
    this.transmissionType = transmissionType;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("transmissionType")
  public String getTransmissionType() {
    return transmissionType;
  }
  public void setTransmissionType(String transmissionType) {
    this.transmissionType = transmissionType;
  }


  /**
   **/
  public VehicleDetails color(String color) {
    this.color = color;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("color")
  public String getColor() {
    return color;
  }
  public void setColor(String color) {
    this.color = color;
  }


  /**
   **/
  public VehicleDetails fuelType(String fuelType) {
    this.fuelType = fuelType;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("fuelType")
  public String getFuelType() {
    return fuelType;
  }
  public void setFuelType(String fuelType) {
    this.fuelType = fuelType;
  }


  /**
   **/
  public VehicleDetails modelName(String modelName) {
    this.modelName = modelName;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("modelName")
  public String getModelName() {
    return modelName;
  }
  public void setModelName(String modelName) {
    this.modelName = modelName;
  }


  /**
   **/
  public VehicleDetails registrationNumber(String registrationNumber) {
    this.registrationNumber = registrationNumber;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("registrationNumber")
  public String getRegistrationNumber() {
    return registrationNumber;
  }
  public void setRegistrationNumber(String registrationNumber) {
    this.registrationNumber = registrationNumber;
  }


  /**
   **/
  public VehicleDetails version(String version) {
    this.version = version;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }


  /**
   **/
  public VehicleDetails mileageInMiles(BigDecimal mileageInMiles) {
    this.mileageInMiles = mileageInMiles;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("mileageInMiles")
  public BigDecimal getMileageInMiles() {
    return mileageInMiles;
  }
  public void setMileageInMiles(BigDecimal mileageInMiles) {
    this.mileageInMiles = mileageInMiles;
  }


  /**
   **/
  public VehicleDetails transmission(String transmission) {
    this.transmission = transmission;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("transmission")
  public String getTransmission() {
    return transmission;
  }
  public void setTransmission(String transmission) {
    this.transmission = transmission;
  }


  /**
   **/
  public VehicleDetails vehicleLineCode(String vehicleLineCode) {
    this.vehicleLineCode = vehicleLineCode;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("vehicleLineCode")
  public String getVehicleLineCode() {
    return vehicleLineCode;
  }
  public void setVehicleLineCode(String vehicleLineCode) {
    this.vehicleLineCode = vehicleLineCode;
  }


  /**
   **/
  public VehicleDetails engine(String engine) {
    this.engine = engine;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("engine")
  public String getEngine() {
    return engine;
  }
  public void setEngine(String engine) {
    this.engine = engine;
  }


  /**
   **/
  public VehicleDetails mileageInKm(BigDecimal mileageInKm) {
    this.mileageInKm = mileageInKm;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("mileageInKm")
  public BigDecimal getMileageInKm() {
    return mileageInKm;
  }
  public void setMileageInKm(BigDecimal mileageInKm) {
    this.mileageInKm = mileageInKm;
  }


  /**
   **/
  public VehicleDetails bodyStyle(String bodyStyle) {
    this.bodyStyle = bodyStyle;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("bodyStyle")
  public String getBodyStyle() {
    return bodyStyle;
  }
  public void setBodyStyle(String bodyStyle) {
    this.bodyStyle = bodyStyle;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VehicleDetails vehicleDetails = (VehicleDetails) o;
    return Objects.equals(this.buildDate, vehicleDetails.buildDate) &&
        Objects.equals(this.vin, vehicleDetails.vin) &&
        Objects.equals(this.transmissionType, vehicleDetails.transmissionType) &&
        Objects.equals(this.color, vehicleDetails.color) &&
        Objects.equals(this.fuelType, vehicleDetails.fuelType) &&
        Objects.equals(this.modelName, vehicleDetails.modelName) &&
        Objects.equals(this.registrationNumber, vehicleDetails.registrationNumber) &&
        Objects.equals(this.version, vehicleDetails.version) &&
        Objects.equals(this.mileageInMiles, vehicleDetails.mileageInMiles) &&
        Objects.equals(this.transmission, vehicleDetails.transmission) &&
        Objects.equals(this.vehicleLineCode, vehicleDetails.vehicleLineCode) &&
        Objects.equals(this.engine, vehicleDetails.engine) &&
        Objects.equals(this.mileageInKm, vehicleDetails.mileageInKm) &&
        Objects.equals(this.bodyStyle, vehicleDetails.bodyStyle);
  }

  @Override
  public int hashCode() {
    return Objects.hash(buildDate, vin, transmissionType, color, fuelType, modelName, registrationNumber, version, mileageInMiles, transmission, vehicleLineCode, engine, mileageInKm, bodyStyle);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VehicleDetails {\n");
    
    sb.append("    buildDate: ").append(toIndentedString(buildDate)).append("\n");
    sb.append("    vin: ").append(toIndentedString(vin)).append("\n");
    sb.append("    transmissionType: ").append(toIndentedString(transmissionType)).append("\n");
    sb.append("    color: ").append(toIndentedString(color)).append("\n");
    sb.append("    fuelType: ").append(toIndentedString(fuelType)).append("\n");
    sb.append("    modelName: ").append(toIndentedString(modelName)).append("\n");
    sb.append("    registrationNumber: ").append(toIndentedString(registrationNumber)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    mileageInMiles: ").append(toIndentedString(mileageInMiles)).append("\n");
    sb.append("    transmission: ").append(toIndentedString(transmission)).append("\n");
    sb.append("    vehicleLineCode: ").append(toIndentedString(vehicleLineCode)).append("\n");
    sb.append("    engine: ").append(toIndentedString(engine)).append("\n");
    sb.append("    mileageInKm: ").append(toIndentedString(mileageInKm)).append("\n");
    sb.append("    bodyStyle: ").append(toIndentedString(bodyStyle)).append("\n");
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

