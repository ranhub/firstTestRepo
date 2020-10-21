package com.ford.turbo.aposb.common.basemodels.sleuth;

import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.Tracer;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class TraceInfo {
    private final Tracer tracer;
    private final TraceKeys traceKeys;

    public TraceInfo(@NotNull Tracer tracer, @NotNull TraceKeys traceKeys) {
        this.tracer = Objects.requireNonNull(tracer);
        this.traceKeys = Objects.requireNonNull(traceKeys);
    }

    public Tracer getTracer() {
        return tracer;
    }

    public TraceKeys getTraceKeys() {
        return traceKeys;
    }
}
