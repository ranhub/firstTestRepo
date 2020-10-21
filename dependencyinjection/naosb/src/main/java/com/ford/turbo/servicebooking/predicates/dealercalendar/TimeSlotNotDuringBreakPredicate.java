package com.ford.turbo.servicebooking.predicates.dealercalendar;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Predicate;

public class TimeSlotNotDuringBreakPredicate implements Predicate<LocalDateTime> {

    private final LocalTime breakFrom;
    private final LocalTime breakTo;
    private final int timeSlotDuration;

    public TimeSlotNotDuringBreakPredicate(LocalTime breakFrom, LocalTime breakTo, int timeSlotDuration) {
        this.breakFrom = breakFrom;
        this.breakTo = breakTo;
        this.timeSlotDuration = timeSlotDuration;
    }

    @Override
    public boolean test(LocalDateTime timeSlot) {
        LocalTime timeSlotStart = LocalTime.from(timeSlot);
        LocalTime timeSlotEnd = timeSlotStart.plusMinutes(timeSlotDuration);

        return !(timeSlotStart.compareTo(breakFrom) >= 0 && timeSlotStart.compareTo(breakTo) < 0) &&
                !(timeSlotStart.compareTo(breakFrom) < 0 && timeSlotEnd.compareTo(breakFrom) > 0);
    }
}
