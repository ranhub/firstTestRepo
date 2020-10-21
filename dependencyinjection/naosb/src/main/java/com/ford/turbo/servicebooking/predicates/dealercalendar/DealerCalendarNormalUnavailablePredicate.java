package com.ford.turbo.servicebooking.predicates.dealercalendar;

import com.ford.turbo.servicebooking.models.osb.ServiceSettings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DealerCalendarNormalUnavailablePredicate implements Predicate<LocalDate> {

    private final Set<Integer> unavailableDaysOfWeek;

    public DealerCalendarNormalUnavailablePredicate(List<ServiceSettings> serviceSettings) {
        this.unavailableDaysOfWeek = serviceSettings.stream()
                .filter(serviceSetting ->
                        BigDecimal.ZERO.compareTo(serviceSetting.getWorkingFrom()) == 0 &&
                        BigDecimal.ZERO.compareTo(serviceSetting.getWorkingTo()) == 0)
                .map(ServiceSettings::getDayOfWeek)
                .map(BigDecimal::intValue)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean test(LocalDate localDate) {
        return unavailableDaysOfWeek.contains(localDate.getDayOfWeek().getValue());
    }
}
