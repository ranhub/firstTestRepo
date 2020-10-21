package com.ford.turbo.servicebooking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.models.osb.response.dealercalendar.DealerCalendarDayResponse;
import com.ford.turbo.servicebooking.models.osb.response.dealercalendar.DealerCalendarResponse;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ford.turbo.servicebooking.command.EUDealerCalendarCommand;
import com.ford.turbo.servicebooking.models.osb.OSBCCalendarLoadDealerDetailsData;
import com.ford.turbo.servicebooking.models.osb.Scheme;
import com.ford.turbo.servicebooking.models.osb.ServiceSettings;
import com.ford.turbo.servicebooking.models.osb.request.dealercalendar.OSBAdditionalService;
import com.ford.turbo.servicebooking.models.osb.request.dealercalendar.OSBDealerCalendarRequest;
import com.ford.turbo.servicebooking.models.osb.response.dealercalendar.OSBDealerCalendarResponse;
import com.ford.turbo.servicebooking.predicates.dealercalendar.DealerCalendarBookedDayPredicate;
import com.ford.turbo.servicebooking.predicates.dealercalendar.DealerCalendarExceptionUnavailablePredicate;
import com.ford.turbo.servicebooking.predicates.dealercalendar.DealerCalendarLeadTimeUnavailablePredicate;
import com.ford.turbo.servicebooking.predicates.dealercalendar.DealerCalendarNormalUnavailablePredicate;
import com.ford.turbo.servicebooking.predicates.dealercalendar.TimeSlotNotBookedPredicate;
import com.ford.turbo.servicebooking.predicates.dealercalendar.TimeSlotNotDuringBreakPredicate;
import com.google.common.collect.Sets;

@Service
public class EUDealerCalendarService implements DealerCalendarService {

    private final MutualAuthRestTemplate restTemplate;
    private final TraceInfo traceInfo;
    private final CredentialsSource credentialsSource;

    @Autowired
    public EUDealerCalendarService(MutualAuthRestTemplate restTemplate, TraceInfo traceInfo, @Qualifier("OSB_DATAPOWER") CredentialsSource credentialsSource) {
        this.restTemplate = restTemplate;
        this.traceInfo = traceInfo;
        this.credentialsSource = credentialsSource;
    }

    @Override
    public DealerCalendarResponse getCalendar(String dealerCode, String marketCode, List<String> additionalServiceIds) {

        OSBDealerCalendarResponse response = doCalendarRequest(dealerCode, marketCode, additionalServiceIds);

        LocalDate calendarStart = LocalDate.now();
        LocalDate calendarEnd = calendarStart.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        List<DealerCalendarDayResponse> availableDates = getAvailableDates(response.getData(), calendarStart, calendarEnd);
        int timeSlotDuration = response.getData().getScheme().getTimeSlotDuration().intValue();

        return new DealerCalendarResponse(calendarStart, calendarEnd, availableDates, timeSlotDuration);
    }

    private List<DealerCalendarDayResponse> getAvailableDates(OSBCCalendarLoadDealerDetailsData data, LocalDate calendarStart, LocalDate calendarEnd) {
        Set<LocalDate> exceptionUnavailableDates = filterCalendarDays(new DealerCalendarExceptionUnavailablePredicate(data.getExceptionDates()), calendarStart, calendarEnd);
        Set<LocalDate> normalUnavailableDates = filterCalendarDays(new DealerCalendarNormalUnavailablePredicate(data.getScheme().getServiceSettings()), calendarStart, calendarEnd);
        Set<LocalDate> leadTimeUnavailableDates = filterCalendarDays(new DealerCalendarLeadTimeUnavailablePredicate(data.getScheme().getLeadTime(), Sets.union(exceptionUnavailableDates, normalUnavailableDates)), calendarStart, calendarEnd);
        Set<LocalDate> bookedUnavailableDates = filterCalendarDays(new DealerCalendarBookedDayPredicate(data.getBookedDates()), calendarStart, calendarEnd);
        Set<LocalDate> noTimeSlotsUnavailableDates = filterCalendarDays(noAvailableTimeSlotsPredicate(data), calendarStart, calendarEnd);

        Set<LocalDate> allUnavailableDates = combineUnavailableDates(exceptionUnavailableDates, normalUnavailableDates, leadTimeUnavailableDates, bookedUnavailableDates, noTimeSlotsUnavailableDates);

        return streamCalendarDays(calendarStart, calendarEnd)
                .filter(date -> !allUnavailableDates.contains(date))
                .map(date -> new DealerCalendarDayResponse(date, getAvailableTimeSlots(date, data)))
                .collect(Collectors.toList());
    }

    private Set<LocalDate> combineUnavailableDates(Set<LocalDate>... allUnavailableDates) {
        Set<LocalDate> combined = new HashSet<>();
        for (Set<LocalDate> unavailableDates : allUnavailableDates) {
            combined.addAll(unavailableDates);
        }
        return combined;
    }

    private Set<LocalDate> filterCalendarDays(Predicate<LocalDate> predicate, LocalDate calendarStart, LocalDate calendarEnd) {
        return streamCalendarDays(calendarStart, calendarEnd)
                .filter(predicate)
                .collect(Collectors.toSet());
    }

    private Stream<LocalDate> streamCalendarDays(LocalDate calendarStart, LocalDate calendarEnd) {
        return Stream.iterate(calendarStart, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(calendarStart, calendarEnd.plusDays(1)));
    }


    protected Predicate<LocalDate> noAvailableTimeSlotsPredicate(OSBCCalendarLoadDealerDetailsData data) {
        return day -> getAvailableTimeSlots(day, data).isEmpty();
    }

    protected List<LocalTime> getAvailableTimeSlots(LocalDate day, OSBCCalendarLoadDealerDetailsData data) {
        final int timeSlotDuration = data.getScheme().getTimeSlotDuration().intValue();

        ServiceSettings serviceSettings = getServiceSettings(day, data.getScheme());
        LocalTime startTime = LocalTime.of(serviceSettings.getWorkingFrom().intValue(), 0);
        LocalTime endTime = LocalTime.of(serviceSettings.getWorkingTo().intValue(), 0);
        LocalTime breakFrom = LocalTime.of(serviceSettings.getBreakFrom().intValue(), 0);
        LocalTime breakTo = LocalTime.of(serviceSettings.getBreakTo().intValue(), 0);

        return streamPotentialTimeSlots(timeSlotDuration, startTime, endTime, breakFrom, breakTo)
                .map(time -> LocalDateTime.of(day, time))
                .filter(new TimeSlotNotBookedPredicate(data.getUnavailableTimeSlots()))
                .filter(new TimeSlotNotDuringBreakPredicate(breakFrom, breakTo, timeSlotDuration))
                .map(LocalDateTime::toLocalTime)
                .collect(Collectors.toList());
    }

    protected Stream<LocalTime> streamPotentialTimeSlots(int timeSlotDuration, LocalTime startTime, LocalTime endTime, LocalTime breakFrom, LocalTime breakTo) {
        if (breakFrom.compareTo(breakTo) == 0) {
            return Stream.iterate(startTime, time -> time.plusMinutes(timeSlotDuration))
                    .limit(numberOfSlotsBetween(startTime, endTime, timeSlotDuration));
        }
        return Stream.concat(
                // TimeSlots up to the break
                Stream.iterate(startTime, time -> time.plusMinutes(timeSlotDuration))
                        .limit(numberOfSlotsBetween(startTime, breakFrom, timeSlotDuration)),
                // TimeSlots starting after break
                Stream.iterate(breakTo, time -> time.plusMinutes(timeSlotDuration))
                        .limit(numberOfSlotsBetween(breakTo, endTime, timeSlotDuration)));
    }

    private long numberOfSlotsBetween(LocalTime start, LocalTime end, double slotDuration) {
        return (long) Math.ceil(ChronoUnit.MINUTES.between(start, end) / slotDuration);
    }

    protected ServiceSettings getServiceSettings(LocalDate day, Scheme scheme) {
        return scheme.getServiceSettings().stream()
                .filter(serviceSetting -> serviceSetting.getDayOfWeek().intValue() == day.getDayOfWeek().getValue())
                .findFirst().orElse(ServiceSettings.builder()
                        .workingFrom(BigDecimal.ZERO).workingTo(BigDecimal.ZERO)
                        .breakFrom(BigDecimal.ZERO).breakTo(BigDecimal.ZERO).build());
    }

    private OSBDealerCalendarResponse doCalendarRequest(String dealerCode, String marketCode, List<String> additionalServiceIds) {
        OSBDealerCalendarRequest request = createCalendarRequest(dealerCode, marketCode, additionalServiceIds);
        return getEUDealerCalendarCommand(request).execute();
    }

    public EUDealerCalendarCommand getEUDealerCalendarCommand(OSBDealerCalendarRequest request) {
        return new EUDealerCalendarCommand(traceInfo, restTemplate, credentialsSource.getBaseUri(), request);
    }
    
    private OSBDealerCalendarRequest createCalendarRequest(String dealerCode, String marketCode, List<String> additionalServiceIds) {
        OSBDealerCalendarRequest request = new OSBDealerCalendarRequest();
        request.setDealerCode(dealerCode);
        request.setMarketCode(marketCode);
        request.setSelectedAdditionalServices(additionalServiceIds.stream()
                .map(serviceId -> {return OSBAdditionalService.builder().additionalServiceId(serviceId).build();})
                .collect(Collectors.toList()));
        return request;
    }
}
