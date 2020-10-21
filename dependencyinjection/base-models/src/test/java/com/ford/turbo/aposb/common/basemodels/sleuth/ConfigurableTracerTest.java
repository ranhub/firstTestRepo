package com.ford.turbo.aposb.common.basemodels.sleuth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.sleuth.DefaultSpanNamer;
import org.springframework.cloud.sleuth.NoOpSpanReporter;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.log.SpanLogger;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.sleuth.trace.DefaultTracer;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurableTracerTest {

    @Mock
    SpanLogger mockSpanLogger;

    /**
     * A generic parent span we can use in tests.
     */
    Span parent = Span.builder()
            .name("the-parent")
            .traceId(42L)
            .spanId(4242L)
            .processId("parent-process-id")
            .exportable(false)
            .tag("http.x-one", "1")
            .tag("http.x-two", "2")
            .tag("http.x-three", "3")
            .build();

    /**
     * This is the thing we're testing.
     */
    DefaultTracer tracer;

    @Before
    public void setup() {
        tracer = new ConfigurableTracer(
                new AlwaysSampler(),
                new Random(),
                new DefaultSpanNamer(),
                mockSpanLogger,
                new NoOpSpanReporter(),
                Arrays.asList("http.x-one", "http.x-three"),
                new TraceKeys());
    }

    @Test
    public void should_copyStandardTraceInfoToChild_when_createSpan() {
        Span child = tracer.createSpan("child", parent);

        assertThat(child.getParents()).containsOnly(4242L);
        assertThat(child.getTraceId()).isEqualTo(42L);
        assertThat(child.getSpanId()).isNotEqualTo(4242L);
        assertThat(child.getProcessId()).isEqualTo("parent-process-id");
        assertThat(child.isExportable()).isFalse();
    }

   // @Test
    public void should_copyConfiguredTagsToChild_when_createSpan() {
        Span child = tracer.createSpan("child", parent);

        assertThat(child.tags().keySet()).containsOnly("http.x-one", "http.x-three");
        assertThat(child.tags().get("http.x-one")).isEqualTo("1");
        // in setup() we configured it not to copy http.x-two tag
        assertThat(child.tags().get("http.x-three")).isEqualTo("3");
    }

    @Test
    public void should_logChildSpan_when_createSpan() {
        Span child = tracer.createSpan("child", parent);

        verify(mockSpanLogger).logStartedSpan(parent, child);
    }


}
