package com.ford.turbo.servicebooking.models.osb.response.dealercalendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class DealerCalendarDayResponse {

    @JsonFormat(pattern = BaseResponse.LOCAL_DATE_FORMAT_PATTERN)
    @ApiModelProperty(
            required = true,
            example = "2009-02-12",
            dataType = "java.lang.String"
    )
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm:ss")
    @ApiModelProperty(
            required = true,
            dataType = "[Ljava.lang.String;"
    )
    private List<LocalTime> availableTimeSlots = new ArrayList<>();
}
