package com.ford.turbo.servicebooking.predicates.dealercalendar;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.ford.turbo.servicebooking.models.osb.TimeAsDate;

public class DealerCalendarBookedDayPredicate implements Predicate<LocalDate> {

    private Set<LocalDate> bookedDays = new HashSet<>();

    public DealerCalendarBookedDayPredicate(List<TimeAsDate> bookedDays) {
    	if(bookedDays != null)
    		this.bookedDays = bookedDays.stream()
                .map(timeAsDate -> LocalDate.of(timeAsDate.getYear().intValue() + 1900, timeAsDate.getMonth().intValue() + 1, timeAsDate.getDate().intValue()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean test(LocalDate localDate) {
        return bookedDays.contains(localDate);
    }
}
