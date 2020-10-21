package com.ford.turbo.servicebooking.predicates.dealercalendar;

import com.ford.turbo.servicebooking.models.osb.TimeAsDate;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class TimeSlotNotBookedPredicateTest {

    @Test
    public void test_shouldMatchForNonBookedTimeSlot() {
        List<TimeAsDate> bookedTimeSlots = Arrays.asList(
                new TimeAsDate().year(BigDecimal.valueOf(116))
                        .month(BigDecimal.valueOf(7))
                        .date(BigDecimal.valueOf(25))
                        .hours(BigDecimal.valueOf(10))
                        .minutes(BigDecimal.valueOf(0)),
                new TimeAsDate().year(BigDecimal.valueOf(116))
                        .month(BigDecimal.valueOf(7))
                        .date(BigDecimal.valueOf(25))
                        .hours(BigDecimal.valueOf(12))
                        .minutes(BigDecimal.valueOf(0))
        );

        TimeSlotNotBookedPredicate predicate = new TimeSlotNotBookedPredicate(bookedTimeSlots);

        assertThat(predicate.test(LocalDateTime.of(2016, 7, 25, 9, 0))).isTrue();
        assertThat(predicate.test(LocalDateTime.of(2016, 8, 25, 10, 0))).isFalse();
        assertThat(predicate.test(LocalDateTime.of(2016, 7, 25, 11, 0))).isTrue();
        assertThat(predicate.test(LocalDateTime.of(2016, 8, 25, 12, 0))).isFalse();
        assertThat(predicate.test(LocalDateTime.of(2016, 7, 25, 13, 0))).isTrue();
    }

}