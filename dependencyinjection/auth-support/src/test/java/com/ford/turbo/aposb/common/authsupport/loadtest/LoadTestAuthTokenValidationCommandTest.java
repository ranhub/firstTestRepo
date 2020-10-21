package com.ford.turbo.aposb.common.authsupport.loadtest;

import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.ford.turbo.common.sharedtests.AuthTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LoadTestAuthTokenValidationCommandTest {

    @Mock
    private Tracer mockTracer;

    private TraceInfo traceInfo;

    @Before
    public void setup() {
        traceInfo = new TraceInfo(mockTracer, new TraceKeys());
    }

    @Test
    public void should_delayByGivenAmount(){
        long sleepTime = 750;
        LoadTestAuthTokenValidationCommand loadTestAuthTokenValidationCommand =
                new LoadTestAuthTokenValidationCommand(traceInfo, sleepTime);
        loadTestAuthTokenValidationCommand.setAuthToken(AuthTestHelper.getWellFormedInvalidToken());

        long tStart = System.currentTimeMillis();

        loadTestAuthTokenValidationCommand.execute();

        long timeComplete = System.currentTimeMillis();
        long timeDiff = timeComplete - tStart;

        assertThat(timeDiff).isGreaterThanOrEqualTo(sleepTime);
    }
}
