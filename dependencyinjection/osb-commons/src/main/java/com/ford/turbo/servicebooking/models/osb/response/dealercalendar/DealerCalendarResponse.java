package com.ford.turbo.servicebooking.models.osb.response.dealercalendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ford.turbo.aposb.common.basemodels.model.BaseResponse;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DealerCalendarResponse extends BaseResponse {

    @JsonFormat(pattern=LOCAL_DATE_FORMAT_PATTERN)
    @ApiModelProperty(required = true, example = "2009-02-12", dataType = "java.lang.String")
    private LocalDate calendarStart;

    @JsonFormat(pattern=LOCAL_DATE_FORMAT_PATTERN)
    @ApiModelProperty(required = true, example = "2009-02-12", dataType = "java.lang.String")
    private LocalDate calendarEnd;


    @ApiModelProperty(value = "Duration of a time slot in minutes", required = true)
    private int timeSlotDuration;

    @JsonFormat(pattern=LOCAL_DATE_FORMAT_PATTERN)
    @ApiModelProperty(required = true, value = "Example: [\"2009-02-12\"]", dataType = "com.ford.turbo.servicebooking.models.osb.response.dealercalendar.DealerCalendarDayResponse")
    private List<DealerCalendarDayResponse> availableDates = new ArrayList<>();

    public DealerCalendarResponse() {
        // For Jackson
    }

    public DealerCalendarResponse(LocalDate calendarStart, LocalDate calendarEnd, List<DealerCalendarDayResponse> availableDates, int timeSlotDuration) {
        this.calendarStart = calendarStart;
        this.calendarEnd = calendarEnd;
        this.availableDates = availableDates;
        this.timeSlotDuration = timeSlotDuration;
    }

    public int getTimeSlotDuration() {
        return timeSlotDuration;
    }

    public void setTimeSlotDuration(int timeSlotDuration) {
        this.timeSlotDuration = timeSlotDuration;
    }

    public List<DealerCalendarDayResponse> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(List<DealerCalendarDayResponse> availableDates) {
        this.availableDates = availableDates;
    }

    public LocalDate getCalendarStart() {
        return calendarStart;
    }

    public void setCalendarStart(LocalDate calendarStart) {
        this.calendarStart = calendarStart;
    }

    public LocalDate getCalendarEnd() {
        return calendarEnd;
    }

    public void setCalendarEnd(LocalDate calendarEnd) {
        this.calendarEnd = calendarEnd;
    }

}
