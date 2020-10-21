package com.ford.turbo.servicebooking.predicates.dealercalendar;

import com.ford.turbo.servicebooking.models.osb.ServiceSettings;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DealerCalendarNormalUnavailablePredicateTest {

    @Test
    public void testShouldMatchNonWorkingDayOfWeek() throws Exception {
        ServiceSettings monday = new ServiceSettings();
        monday.setDayOfWeek(BigDecimal.valueOf(DayOfWeek.MONDAY.getValue()));
        monday.setWorkingFrom(BigDecimal.ONE);
        monday.setWorkingTo(BigDecimal.TEN);

        ServiceSettings tuesday = new ServiceSettings();
        tuesday.setDayOfWeek(BigDecimal.valueOf(DayOfWeek.TUESDAY.getValue()));
        tuesday.setWorkingFrom(BigDecimal.ONE);
        tuesday.setWorkingTo(BigDecimal.TEN);

        ServiceSettings saturday = new ServiceSettings();
        saturday.setDayOfWeek(BigDecimal.valueOf(DayOfWeek.SATURDAY.getValue()));
        saturday.setWorkingFrom(BigDecimal.ZERO);
        saturday.setWorkingTo(BigDecimal.ZERO);

        ServiceSettings sunday = new ServiceSettings();
        sunday.setDayOfWeek(BigDecimal.valueOf(DayOfWeek.SUNDAY.getValue()));
        sunday.setWorkingFrom(new BigDecimal("0.000")); // Ensure still equal when comparing BigDecimals with different scales
        sunday.setWorkingTo(BigDecimal.ZERO);

        List<ServiceSettings> serviceSettings = Arrays.asList(monday, tuesday, saturday, sunday);

        DealerCalendarNormalUnavailablePredicate predicate = new DealerCalendarNormalUnavailablePredicate(serviceSettings);

        assertThat(predicate.test(LocalDate.now().with(DayOfWeek.MONDAY))).isFalse();
        assertThat(predicate.test(LocalDate.now().with(DayOfWeek.TUESDAY))).isFalse();
        assertThat(predicate.test(LocalDate.now().with(DayOfWeek.WEDNESDAY))).isFalse();
        assertThat(predicate.test(LocalDate.now().with(DayOfWeek.SATURDAY))).isTrue();
        assertThat(predicate.test(LocalDate.now().with(DayOfWeek.SUNDAY))).isTrue();
    }
}