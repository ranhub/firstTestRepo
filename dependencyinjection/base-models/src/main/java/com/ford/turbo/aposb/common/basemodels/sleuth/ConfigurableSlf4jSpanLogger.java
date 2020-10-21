package com.ford.turbo.aposb.common.basemodels.sleuth;

import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.log.Slf4jSpanLogger;

import java.util.List;
import java.util.Objects;

public class ConfigurableSlf4jSpanLogger extends Slf4jSpanLogger {

    private final List<String> mdcTags;

    public ConfigurableSlf4jSpanLogger(String nameSkipPattern, List<String> mdcTags) {
        super(nameSkipPattern);
        this.mdcTags = Objects.requireNonNull(mdcTags);
    }

    @Override
    public void logStartedSpan(Span parent, Span span) {
        for (String tag : mdcTags) {
            if (span.tags().containsKey(tag)) {
                MDC.put(tag, span.tags().get(tag));
            }
        }
        super.logStartedSpan(parent, span);
    }

    @Override
    public void logContinuedSpan(Span span) {
        for (String tag : mdcTags) {
            if (span.tags().containsKey(tag)) {
                MDC.put(tag, span.tags().get(tag));
            } else {
                MDC.remove(tag);
            }
        }

        super.logContinuedSpan(span);
    }

    @Override
    public void logStoppedSpan(Span parent, Span span) {
        super.logStoppedSpan(parent, span);

        for (String tag : mdcTags) {
            if (parent == null || !parent.tags().containsKey(tag)) {
                MDC.remove(tag);
            } else {
                String tagValue = parent.tags().get(tag);
                MDC.put(tag, tagValue);
            }
        }
    }
}
