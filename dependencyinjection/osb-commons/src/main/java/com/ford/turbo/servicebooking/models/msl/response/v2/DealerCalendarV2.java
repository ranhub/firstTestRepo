package com.ford.turbo.servicebooking.models.msl.response.v2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.servicebooking.models.osb.response.dealercalendar.DealerCalendarDayResponse;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DealerCalendarV2 {

    @JsonFormat(pattern=BaseResponse.LOCAL_DATE_FORMAT_PATTERN)
    @ApiModelProperty(required = true, example = "2009-02-12", dataType = "java.lang.String")
    private LocalDate calendarStart;

    @JsonFormat(pattern=BaseResponse.LOCAL_DATE_FORMAT_PATTERN)
    @ApiModelProperty(required = true, example = "2009-02-12", dataType = "java.lang.String")
    private LocalDate calendarEnd;


    @ApiModelProperty(value = "Duration of a time slot in minutes", required = true)
    private int timeSlotDuration;

    @JsonFormat(pattern=BaseResponse.LOCAL_DATE_FORMAT_PATTERN)
    @ApiModelProperty(required = true, value = "Example: [\"2009-02-12\"]", dataType = "com.ford.turbo.servicebooking.models.osb.response.dealercalendar.DealerCalendarDayResponse")
    private List<DealerCalendarDayResponse> availableDates = new ArrayList<>();
}
