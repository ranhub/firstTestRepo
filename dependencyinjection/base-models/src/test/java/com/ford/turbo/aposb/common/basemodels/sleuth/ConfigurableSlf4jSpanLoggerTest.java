package com.ford.turbo.aposb.common.basemodels.sleuth;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Span;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurableSlf4jSpanLoggerTest {

    ConfigurableSlf4jSpanLogger spanLogger;
    List<String> mdcTags;

    @Before
    public void setup() {
        mdcTags = Arrays.asList("foo", "bar");
        spanLogger = new ConfigurableSlf4jSpanLogger("", mdcTags);
        MDC.clear();
    }

    @Test
    public void should_setAllNonNullSpanKeysInMdc_and_omitNullSpanKeys_when_logStartedSpan() {
        Span span = Span.builder()
                .tag("decoy", "don't put me in mdc because I'm not configured in mdcTags")
                .tag("bar", "add me to mdc")
                .build();
        spanLogger.logStartedSpan(null, span);

        assertThat(MDC.getCopyOfContextMap().keySet())
                .containsOnly("bar", "X-B3-TraceId", "X-B3-SpanId", "X-Span-Export");
    }

    @Test
    public void should_updateMdcValueWithStartedSpanValue_when_logStartedSpan() {
        Span span = Span.builder()
                .tag("bar", "started-span value")
                .build();
        spanLogger.logStartedSpan(null, span);

        assertThat(MDC.getCopyOfContextMap().containsKey("bar")).isTrue();
        assertThat(MDC.getCopyOfContextMap().get("bar")).isEqualTo("started-span value");
    }


    @Test
    public void should_setAllNonNullSpanKeysInMdc_when_logContinuedSpan() {
        MDC.put("bar", "old bar value");
        MDC.put("X-B3-TraceId", "old trace id");

        Span span = Span.builder()
                .tag("decoy", "don't add or remove me in mdc")
                .tag("bar", "replace me in mdc")
                .build();
        spanLogger.logContinuedSpan(span);

        assertThat(MDC.getCopyOfContextMap().keySet())
                .containsOnly("bar", "X-B3-TraceId", "X-B3-SpanId", "X-Span-Export");

        assertThat(MDC.get("bar")).isEqualTo("replace me in mdc");
        assertThat(MDC.get("X-B3-TraceId")).isNotEqualTo("old trace id");
    }

    @Test
    public void should_eraseNullSpanKeys_when_logContinuedSpan() {
        MDC.put("bar", "should get removed from mdc");

        Span span = Span.builder()
                .tag("decoy", "don't add or remove me in mdc")
                .build();
        spanLogger.logContinuedSpan(span);

        assertThat(MDC.getCopyOfContextMap().containsKey("bar")).isFalse();
    }

    @Test
    public void should_updateMdcWithContinuedSpanValue_when_logContinuedSpan() {
        Span span = Span.builder()
                .tag("bar", "continued-span value")
                .build();
        spanLogger.logContinuedSpan(span);

        assertThat(MDC.getCopyOfContextMap().containsKey("bar")).isTrue();
        assertThat(MDC.getCopyOfContextMap().get("bar")).isEqualTo("continued-span value");
    }


    @Test
    public void should_notAffectNonConfiguredMdcTags_when_logContinuedSpan() {
        MDC.put("decoy", "should remain in mdc because decoy is not a configured mdcTag");

        Span span = Span.builder()
                .tag("decoy", "don't add or remove me in mdc")
                .build();
        spanLogger.logContinuedSpan(span);

        assertThat(MDC.get("decoy")).isEqualTo("should remain in mdc because decoy is not a configured mdcTag");
    }

    @Test
    public void should_removeTagFromMdc_when_logStoppedSpan_with_noParent() {
        MDC.put("bar", "should get removed from mdc");
        MDC.put("foo", "wouldn't expect this to be there but should get removed anyway");

        Span span = Span.builder()
                .tag("bar", "should get removed from mdc")
                .build();
        spanLogger.logStoppedSpan(null, span);

        assertThat(MDC.getCopyOfContextMap().containsKey("bar")).isFalse();
    }

    @Test
    public void should_replaceWithParentTags_when_logStoppedSpan_and_parentExists() {
        MDC.put("bar", "should get replaced in mdc");
        MDC.put("decoy", "do not change me");

        Span span = Span.builder()
                .tag("bar", "should get replaced in mdc")
                .build();

        Span parent = Span.builder()
                .tag("bar", "replace bar with me")
                .build();

        spanLogger.logStoppedSpan(parent, span);

        assertThat(MDC.get("bar")).isEqualTo("replace bar with me");
        assertThat(MDC.get("decoy")).isEqualTo("do not change me");
    }

    @Test
    public void should_replaceWithParentTagValue_when_logStoppedSpan() {
        MDC.put("bar", "should get replaced in mdc");

        Span span = Span.builder()
                .tag("bar", "should get replaced in mdc")
                .build();

        Span parent = Span.builder()
                .tag("bar", "parent value")
                .build();
        spanLogger.logStoppedSpan(parent, span);

        assertThat(MDC.getCopyOfContextMap().containsKey("bar")).isTrue();
        assertThat(MDC.getCopyOfContextMap().get("bar")).isEqualTo("parent value");
    }

    @Test
    public void should_removeTagFromMdc_when_parentDoesNotHaveTag() {
        MDC.put("bar", "should get replaced in mdc");

        Span span = Span.builder()
                .tag("bar", "should get replaced in mdc")
                .build();

        Span parent = Span.builder()
                .build();
        spanLogger.logStoppedSpan(parent, span);

        assertThat(MDC.getCopyOfContextMap().containsKey("bar")).isFalse();
    }

}
