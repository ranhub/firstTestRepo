package com.ford.turbo.servicebooking.predicates.dealercalendar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import com.ford.turbo.servicebooking.models.osb.TimeAsDate;
import org.junit.Test;

public class DealerCalendarExceptionUnavailablePredicateTest {

    @Test
    public void testShouldMatchLocalDate() throws Exception {
        final TimeAsDate timeAsDate = createTimeAsDate(116, 1, 1);

        final DealerCalendarExceptionUnavailablePredicate predicate = new DealerCalendarExceptionUnavailablePredicate(Arrays.asList(timeAsDate));

        assertThat(predicate.test(LocalDate.of(2016, 2, 1))).isTrue();
        assertThat(predicate.test(LocalDate.of(2016, 1, 2))).isFalse();
    }

    @Test
    public void testShouldMatchMultipleLocalDates() throws Exception {
        final TimeAsDate timeAsDate1 = createTimeAsDate(116, 1, 1);
        final TimeAsDate timeAsDate2 = createTimeAsDate(116, 1, 2);

        final DealerCalendarExceptionUnavailablePredicate predicate = new DealerCalendarExceptionUnavailablePredicate(Arrays.asList(timeAsDate1, timeAsDate2));

        assertThat(predicate.test(LocalDate.of(2016, 2, 1))).isTrue();
        assertThat(predicate.test(LocalDate.of(2016, 2, 2))).isTrue();
        assertThat(predicate.test(LocalDate.of(2016, 2, 3))).isFalse();
    }


    private TimeAsDate createTimeAsDate(int year, int month, int date) {
        return new TimeAsDate(){{ setYear(BigDecimal.valueOf(year));
							      setMonth(BigDecimal.valueOf(month));
							      setDate(BigDecimal.valueOf(date));
        						}};
    }
}