package com.ford.turbo.servicebooking.models.osb;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


/**
 * SelectedVehicleWithPrice
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelectedVehicleWithPrice   {
  
  private String vlcCode;
  private String description;
  private String price;
  private String priceAfterDiscount;

  
  /**
   **/
  public SelectedVehicleWithPrice vlcCode(String vlcCode) {
    this.vlcCode = vlcCode;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("vlcCode")
  public String getVlcCode() {
    return vlcCode;
  }
  public void setVlcCode(String vlcCode) {
    this.vlcCode = vlcCode;
  }


  /**
   **/
  public SelectedVehicleWithPrice description(String description) {
    this.description = description;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   **/
  public SelectedVehicleWithPrice price(String price) {
    this.price = price;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("price")
  public String getPrice() {
    return price;
  }
  public void setPrice(String price) {
    this.price = price;
  }

  @ApiModelProperty(example = "null", value = "")
  @JsonProperty("priceAfterDiscount")
  public String getPriceAfterDiscount() {
    return priceAfterDiscount;
  }
  public void setPriceAfterDiscount(String priceAfterDiscount) {
    this.priceAfterDiscount = priceAfterDiscount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SelectedVehicleWithPrice selectedVehicleWithPrice = (SelectedVehicleWithPrice) o;
    return Objects.equals(this.vlcCode, selectedVehicleWithPrice.vlcCode) &&
        Objects.equals(this.description, selectedVehicleWithPrice.description) &&
        Objects.equals(this.price, selectedVehicleWithPrice.price);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vlcCode, description, price);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SelectedVehicleWithPrice {\n");
    
    sb.append("    vlcCode: ").append(toIndentedString(vlcCode)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    price: ").append(toIndentedString(price)).append("\n");
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

