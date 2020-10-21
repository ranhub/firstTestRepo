package com.ford.turbo.servicebooking.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.ford.turbo.aposb.common.basemodels.hystrix.TimedHystrixCommand;
import org.junit.Test;

public class GetBookingsCommandTest {
    @Test
    public void should_extendTimedHystrixCommand() throws IOException {
        assertThat(TimedHystrixCommand.class.isAssignableFrom(GetBookingsCommand.class)).isTrue();
    }
}