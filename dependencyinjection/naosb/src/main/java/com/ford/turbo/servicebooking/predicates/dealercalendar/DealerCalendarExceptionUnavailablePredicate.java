package com.ford.turbo.servicebooking.predicates.dealercalendar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.ford.turbo.servicebooking.models.osb.TimeAsDate;

public class DealerCalendarExceptionUnavailablePredicate implements Predicate<LocalDate> {
    private List<LocalDate> exceptionUnavailableDates = new ArrayList<>();

    public DealerCalendarExceptionUnavailablePredicate(List<TimeAsDate> exceptionDates) {
         if(exceptionDates != null)
        	 this.exceptionUnavailableDates = exceptionDates.stream()
        	 .map(exceptionDate -> LocalDate.of(exceptionDate.getYear().intValue() + 1900, exceptionDate.getMonth().intValue() + 1, exceptionDate.getDate().intValue()))
        	 .collect(Collectors.toList());
    }

    @Override
    public boolean test(LocalDate localDate) {
        return exceptionUnavailableDates.contains(localDate);
    }
 }
