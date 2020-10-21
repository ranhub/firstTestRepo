package com.ford.turbo.aposb.common;

import org.junit.Test;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WatchdogTest {

    @Test
    public void isCloudProfileReturnsTrue_when_activeProfilesHasCloud() {
        Watchdog watchdog = new Watchdog();

        Environment environment = mock(Environment.class);
        when(environment.getActiveProfiles()).thenReturn(new String[] {"default", "cloud"});
        watchdog.setEnvironment(environment);

        assertThat(watchdog.isCloudProfile()).isTrue();
    }

    @Test
    public void isCloudProfileReturnsFalse_when_activeProfilesDoesNotHaveCloud() {
        Watchdog watchdog = new Watchdog();

        Environment environment = mock(Environment.class);
        when(environment.getActiveProfiles()).thenReturn(new String[] {"default", "sky"});
        watchdog.setEnvironment(environment);

        assertThat(watchdog.isCloudProfile()).isFalse();
    }
}
