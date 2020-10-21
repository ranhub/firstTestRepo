package com.ford.turbo.servicebooking.predicates.dealercalendar;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.ford.turbo.servicebooking.models.osb.TimeAsDate;

public class TimeSlotNotBookedPredicate implements Predicate<LocalDateTime> {

    private final Set<LocalDateTime> bookedTimeSlots = new HashSet<>();

    public TimeSlotNotBookedPredicate(List<TimeAsDate> bookedTimeSlots) {
    	
    	if(bookedTimeSlots != null) {
        for ( TimeAsDate bookedTimeSlot : bookedTimeSlots ) {
                this.bookedTimeSlots.add( LocalDateTime.of(
                        bookedTimeSlot.getYear().intValue()+1900,
                        bookedTimeSlot.getMonth().intValue()+1,
                        bookedTimeSlot.getDate().intValue(),
                        bookedTimeSlot.getHours().intValue(),
                        bookedTimeSlot.getMinutes().intValue()));
        	}
    	}
    }

    @Override
    public boolean test(LocalDateTime timeSlot) {
        return !bookedTimeSlots.contains(timeSlot);
    }
}
