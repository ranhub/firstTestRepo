package com.ford.turbo.servicebooking.predicates.dealercalendar;

import com.ford.turbo.servicebooking.models.osb.TimeAsDate;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class DealerCalendarBookedDayPredicateTest {

    @Test
    public void testShouldMatchOnBookedDays() {
        final TimeAsDate timeAsDate1 = createTimeAsDate(116, 1, 1);
        final TimeAsDate timeAsDate2 = createTimeAsDate(116, 1, 2);

        List<TimeAsDate> bookedDays = Arrays.asList(timeAsDate1, timeAsDate2);
        DealerCalendarBookedDayPredicate predicate = new DealerCalendarBookedDayPredicate(bookedDays);

        assertThat(predicate.test(LocalDate.of(2016, 2, 1))).isTrue();
        assertThat(predicate.test(LocalDate.of(2016, 2, 2))).isTrue();
        assertThat(predicate.test(LocalDate.of(2016, 1, 3))).isFalse();
    }

    private TimeAsDate createTimeAsDate(int year, int month, int date) {
    	 return new TimeAsDate(){{ setYear(BigDecimal.valueOf(year));
	      setMonth(BigDecimal.valueOf(month));
	      setDate(BigDecimal.valueOf(date));
		}};
    }
}