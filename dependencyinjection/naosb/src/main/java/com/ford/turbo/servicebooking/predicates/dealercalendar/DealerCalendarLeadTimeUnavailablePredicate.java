package com.ford.turbo.servicebooking.predicates.dealercalendar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.function.Predicate;

public class DealerCalendarLeadTimeUnavailablePredicate implements Predicate<LocalDate> {

    private final LocalDate leadDate;

    public DealerCalendarLeadTimeUnavailablePredicate(BigDecimal leadTime, Set<LocalDate> excludedDates) {
        this.leadDate = computeLeadDate(leadTime.intValue(), excludedDates);
    }

    private LocalDate computeLeadDate(int leadTime, Set<LocalDate> excludedDates) {
        LocalDate now = LocalDate.now();
        LocalDate leadDate = now.plusDays(leadTime);

        int additionalLeadTime = 0;
        for (LocalDate date : excludedDates) {
            if (date.compareTo(now) >= 0 && date.compareTo(leadDate) <= 0) {
                additionalLeadTime++;
            }
        }

        return now.plusDays(leadTime + additionalLeadTime);
    }

    @Override
    public boolean test(LocalDate localDate) {
        return leadDate.compareTo(localDate) >= 0;
    }
}
