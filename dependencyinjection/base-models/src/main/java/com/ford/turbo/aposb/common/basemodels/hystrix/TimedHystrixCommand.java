package com.ford.turbo.aposb.common.basemodels.hystrix;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.instrument.hystrix.TraceCommand;

import com.ford.turbo.aposb.common.basemodels.sleuth.TraceInfo;
import com.netflix.hystrix.HystrixCommandGroupKey;

public abstract class TimedHystrixCommand<R> extends TraceCommand<R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimedHystrixCommand.class.getName());

    private final TraceInfo traceInfo;

    protected TimedHystrixCommand(@NotNull TraceInfo traceInfo, @NotNull String commandGroupKey) {
        this(traceInfo, Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(commandGroupKey)));
    }

    protected TimedHystrixCommand(@NotNull TraceInfo traceInfo, @NotNull Setter setter) {
        super(traceInfo.getTracer(), setter);
        this.traceInfo = Objects.requireNonNull(traceInfo);
    }

    /**
     * Overridden to mark final. This prevents subclasses from accidentally overriding the Sleuth-provided
     * run method and breaking span creation.
     * <p>
     * Override {@link #doRun()} instead.
     */
    @Override
    public final R run() throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            return super.run();
        } finally {
            LOGGER.debug("Execution time: command={}, groupKey={}, time={} ms", this.getClass().getSimpleName(), this.getCommandGroup().name(), System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public R execute() {
            return super.execute();
    }

    public TraceInfo getTraceInfo() {
        return traceInfo;        
    }
}
