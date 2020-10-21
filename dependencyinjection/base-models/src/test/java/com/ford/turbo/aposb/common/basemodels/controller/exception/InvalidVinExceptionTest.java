package com.ford.turbo.aposb.common.basemodels.controller.exception;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InvalidVinExceptionTest {

    @Test
    public void should_includeVinInTheErrorMessage() {
        InvalidVinException exception = new InvalidVinException("1234");
        assertThat(exception.getMessage()).isEqualTo("VIN (1234) must be 17 uppercase alphanumeric characters");
        assertThat(exception.getFordError().getMessage()).isEqualTo("VIN (1234) must be 17 uppercase alphanumeric characters");
    }

    @Test
    public void should_includeMultipleVinsInTheErrorMessage() {
        InvalidVinException exception = new InvalidVinException("1234", "5678");
        assertThat(exception.getMessage()).isEqualTo("VIN (1234, 5678) must be 17 uppercase alphanumeric characters");
        assertThat(exception.getFordError().getMessage()).isEqualTo("VIN (1234, 5678) must be 17 uppercase alphanumeric characters");
    }

}