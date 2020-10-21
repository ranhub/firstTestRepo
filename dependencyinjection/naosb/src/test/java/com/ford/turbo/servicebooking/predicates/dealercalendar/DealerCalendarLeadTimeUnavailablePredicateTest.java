package com.ford.turbo.servicebooking.predicates.dealercalendar;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DealerCalendarLeadTimeUnavailablePredicateTest {

    @Test
    public void testShouldMatchDaysWithinLeadTime() {
        BigDecimal leadTime = BigDecimal.valueOf(2);
        DealerCalendarLeadTimeUnavailablePredicate predicate = new DealerCalendarLeadTimeUnavailablePredicate(leadTime, new HashSet<>());

        LocalDate now = LocalDate.now();
        assertThat(predicate.test(now)).isTrue();
        assertThat(predicate.test(now.plusDays(1))).isTrue();
        assertThat(predicate.test(now.plusDays(2))).isTrue();
        assertThat(predicate.test(now.plusDays(3))).isFalse();
    }

    @Test
    public void testShouldMatchDaysWithinLeadTime_excludedDatesAddedToLeadTime() {

        LocalDate now = LocalDate.now();
        BigDecimal leadTime = BigDecimal.valueOf(2);
        Set<LocalDate> excludedDates = new HashSet<>(Arrays.asList(now, now.plusDays(2), now.plusDays(6)));

        DealerCalendarLeadTimeUnavailablePredicate predicate = new DealerCalendarLeadTimeUnavailablePredicate(leadTime, excludedDates);

        assertThat(predicate.test(now)).isTrue();
        assertThat(predicate.test(now.plusDays(1))).isTrue();
        assertThat(predicate.test(now.plusDays(2))).isTrue();
        assertThat(predicate.test(now.plusDays(3))).isTrue();
        assertThat(predicate.test(now.plusDays(4))).isTrue();
        assertThat(predicate.test(now.plusDays(5))).isFalse();
    }
}