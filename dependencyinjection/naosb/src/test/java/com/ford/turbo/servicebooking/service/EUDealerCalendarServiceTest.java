package com.ford.turbo.servicebooking.service;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ford.turbo.servicebooking.models.osb.OSBCCalendarLoadDealerDetailsData;
import com.ford.turbo.servicebooking.models.osb.Scheme;
import com.ford.turbo.servicebooking.models.osb.ServiceSettings;

@RunWith(MockitoJUnitRunner.class)
public class EUDealerCalendarServiceTest {

    @Mock
    private MutualAuthRestTemplate restTemplate;
    @Mock
    private TraceInfo traceInfo;
    @Mock
    private CredentialsSource credentialsSource;

    @InjectMocks
    private EUDealerCalendarService service;

    @Test
    public void getServiceSettings_returnsServiceSettingsForDayOfWeek() {
        ServiceSettings mondaySettings = new ServiceSettings();
        mondaySettings.setDayOfWeek(BigDecimal.valueOf(DayOfWeek.MONDAY.getValue()));
        ServiceSettings tuesdaySettings = new ServiceSettings();
        tuesdaySettings.setDayOfWeek(BigDecimal.valueOf(DayOfWeek.TUESDAY.getValue()));
        Scheme scheme = Scheme.builder().serviceSettings(Arrays.asList(mondaySettings, tuesdaySettings)).build();

        ServiceSettings serviceSettings = service.getServiceSettings(LocalDate.now().with(DayOfWeek.TUESDAY), scheme);
        assertThat(serviceSettings).isEqualTo(tuesdaySettings);
    }

    @Test
    public void getServiceSettings_returnsServiceSettingsWithDefaults_when_dayOfWeekSettingNotFound() {
        ServiceSettings mondaySettings = new ServiceSettings();
        mondaySettings.setDayOfWeek(BigDecimal.valueOf(DayOfWeek.MONDAY.getValue()));
        Scheme scheme = Scheme.builder().serviceSettings(Arrays.asList(mondaySettings)).build();

        ServiceSettings serviceSettings = service.getServiceSettings(LocalDate.now().with(DayOfWeek.TUESDAY), scheme);
        assertThat(serviceSettings).isEqualTo(ServiceSettings.builder()
                .workingFrom(BigDecimal.ZERO).workingTo(BigDecimal.ZERO)
                .breakFrom(BigDecimal.ZERO).breakTo(BigDecimal.ZERO).build());
    }

    @Test
    public void getAvailableTimeSlots_should_returnEmptyIfNonWorkingDay() {
        final LocalDate now = LocalDate.now();
        OSBCCalendarLoadDealerDetailsData data = OSBCCalendarLoadDealerDetailsData.builder()
    		.scheme(Scheme.builder()
        		.timeSlotDuration(BigDecimal.valueOf(20))
        		.serviceSettings(Arrays.asList(
        				ServiceSettings.builder()
        					.dayOfWeek(BigDecimal.valueOf(now.getDayOfWeek().getValue()))
        					.workingFrom(BigDecimal.ZERO)
        					.workingTo(BigDecimal.ZERO)
        					.breakFrom(BigDecimal.ZERO)
        					.breakTo(BigDecimal.ZERO)
    					.build()))
        		.build())
    		.build();

        assertThat(service.getAvailableTimeSlots(now, data)).isEmpty();
    }

    @Test
    public void getAvailableTimeSlots_should_work() {
        final LocalDate date = LocalDate.now();

        OSBCCalendarLoadDealerDetailsData data = new OSBCCalendarLoadDealerDetailsData();
        data.setScheme(Scheme.builder().timeSlotDuration(BigDecimal.valueOf(20)).serviceSettings(Arrays.asList(
                ServiceSettings.builder().dayOfWeek(BigDecimal.valueOf(date.getDayOfWeek().getValue()))
                        .workingFrom(BigDecimal.ZERO).workingTo(BigDecimal.ONE)
                        .breakFrom(BigDecimal.ZERO).breakTo(BigDecimal.ZERO).build())).build());

        final List<LocalTime> timeSlots = service.getAvailableTimeSlots(date, data);
        assertThat(timeSlots).hasSize(3);
        assertThat(timeSlots).contains(LocalTime.of(0, 0, 0));
        assertThat(timeSlots).contains(LocalTime.of(0, 20, 0));
        assertThat(timeSlots).contains(LocalTime.of(0, 40, 0));
    }

    @Test
    public void noAvailableTimeSlotsPredicate_returnsTrue_whenNoAvailableTimeSlots() {
        LocalDate date = LocalDate.now();
        ServiceSettings serviceSettings = ServiceSettings.builder()
                .dayOfWeek(BigDecimal.valueOf(date.getDayOfWeek().getValue()))
                .workingFrom(BigDecimal.ZERO).workingTo(BigDecimal.ZERO)
                .breakFrom(BigDecimal.ZERO).breakTo(BigDecimal.ZERO).build();

        Scheme scheme = new Scheme();
        scheme.setTimeSlotDuration(BigDecimal.valueOf(20));
        scheme.setServiceSettings(Arrays.asList(serviceSettings));

        OSBCCalendarLoadDealerDetailsData data = new OSBCCalendarLoadDealerDetailsData();
        data.setScheme(scheme);

        assertThat(service.noAvailableTimeSlotsPredicate(data).test(date)).isTrue();
    }

    @Test
    public void noAvailableTimeSlotsPredicate_returnsFalse_whenAvailableTimeSlots() {
        LocalDate date = LocalDate.now();
        ServiceSettings serviceSettings = ServiceSettings.builder()
                .dayOfWeek(BigDecimal.valueOf(date.getDayOfWeek().getValue()))
                .workingFrom(BigDecimal.ZERO).workingTo(BigDecimal.ONE)
                .breakFrom(BigDecimal.ZERO).breakTo(BigDecimal.ZERO).build();

        Scheme scheme = new Scheme();
        scheme.setTimeSlotDuration(BigDecimal.valueOf(20));
        scheme.setServiceSettings(Arrays.asList(serviceSettings));

        OSBCCalendarLoadDealerDetailsData data = new OSBCCalendarLoadDealerDetailsData();
        data.setScheme(scheme);

        assertThat(service.noAvailableTimeSlotsPredicate(data).test(date)).isFalse();
    }

    @Test
    public void shouldStreamPotentialTimeSlots_noBreakHours() {
        List<LocalTime> potentialTimeSlots = service.streamPotentialTimeSlots(30, LocalTime.of(10, 00), LocalTime.of(14, 00),
                LocalTime.of(0, 0), LocalTime.of(0, 0)).collect(toList());

        assertThat(potentialTimeSlots).hasSize(8);
        assertThat(potentialTimeSlots).contains(LocalTime.of(10, 00));
        assertThat(potentialTimeSlots).contains(LocalTime.of(10, 30));
        assertThat(potentialTimeSlots).contains(LocalTime.of(13, 30));
        assertThat(potentialTimeSlots).doesNotContain(LocalTime.of(14, 00));
    }

    @Test
    public void shouldStreamPotentialTimeSlots_removingBreakHours() {
        List<LocalTime> potentialTimeSlots = service.streamPotentialTimeSlots(30, LocalTime.of(10, 00), LocalTime.of(14, 00),
                LocalTime.of(12, 00), LocalTime.of(13, 00)).collect(toList());

        assertThat(potentialTimeSlots).hasSize(6);
        assertThat(potentialTimeSlots).contains(LocalTime.of(10, 00));
        assertThat(potentialTimeSlots).contains(LocalTime.of(10, 30));
        assertThat(potentialTimeSlots).doesNotContain(LocalTime.of(12, 00));
        assertThat(potentialTimeSlots).doesNotContain(LocalTime.of(12, 30));
        assertThat(potentialTimeSlots).contains(LocalTime.of(13, 30));
        assertThat(potentialTimeSlots).doesNotContain(LocalTime.of(14, 00));
    }

    @Test
    public void shouldStreamPotentialTimeSlots_unevenTimePeriods() {
        List<LocalTime> potentialTimeSlots = service.streamPotentialTimeSlots(45, LocalTime.of(11, 00), LocalTime.of(14, 00),
                LocalTime.of(12, 00), LocalTime.of(13, 00)).collect(toList());

        assertThat(potentialTimeSlots).hasSize(4);
        assertThat(potentialTimeSlots).contains(LocalTime.of(11, 00));
        assertThat(potentialTimeSlots).contains(LocalTime.of(11, 45));
        assertThat(potentialTimeSlots).doesNotContain(LocalTime.of(12, 30));
        assertThat(potentialTimeSlots).contains(LocalTime.of(13, 00));
        assertThat(potentialTimeSlots).contains(LocalTime.of(13, 45));
        assertThat(potentialTimeSlots).doesNotContain(LocalTime.of(14, 30));
    }

}