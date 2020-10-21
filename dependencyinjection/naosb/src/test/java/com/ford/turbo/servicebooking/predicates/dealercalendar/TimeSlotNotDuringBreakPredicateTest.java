package com.ford.turbo.servicebooking.predicates.dealercalendar;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeSlotNotDuringBreakPredicateTest {

    @Test
    public void test_shouldFilterOutTimeSlotBreaks() {
        TimeSlotNotDuringBreakPredicate predicate = new TimeSlotNotDuringBreakPredicate(LocalTime.of(12, 00), LocalTime.of(13, 00), 30);

        assertTrue(predicate.test(LocalDateTime.of(2016, 8, 1, 11, 30)));
        assertFalse(predicate.test(LocalDateTime.of(2016, 8, 1, 11, 45)));
        assertFalse(predicate.test(LocalDateTime.of(2016, 8, 1, 12, 30)));
        assertFalse(predicate.test(LocalDateTime.of(2016, 8, 1, 12, 45)));
        assertTrue(predicate.test(LocalDateTime.of(2016, 8, 1, 13, 00)));
        assertTrue(predicate.test(LocalDateTime.of(2016, 8, 1, 13, 30)));
    }

    @Test
    public void test_shouldFilterOutTimeSlotBreaks_whenBreakFromAndBreakToAreZero() {
        TimeSlotNotDuringBreakPredicate predicate = new TimeSlotNotDuringBreakPredicate(LocalTime.of(0, 0), LocalTime.of(0, 0), 30);

        assertTrue(predicate.test(LocalDateTime.of(2016, 8, 1, 0, 30)));
    }

}