package com.ford.turbo.aposb.common.basemodels.sleuth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.SpanReporter;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.log.SpanLogger;
import org.springframework.cloud.sleuth.trace.DefaultTracer;

public class ConfigurableTracer extends DefaultTracer {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableTracer.class);

    /**
     * A private library method we have to call reflectively.
     */
    private static Method pushMethod;

    private final Random random;
    private final SpanLogger spanLogger;
    private final List<String> tagsToPropagate;

    public ConfigurableTracer(
            Sampler defaultSampler,
            Random random,
            SpanNamer spanNamer,
            SpanLogger spanLogger,
            SpanReporter spanReporter,
            List<String> tagsToPropagate,
            TraceKeys traceKeys) {
        super(defaultSampler, random, spanNamer, spanLogger, spanReporter,traceKeys);
        this.random = Objects.requireNonNull(random);
        this.spanLogger = Objects.requireNonNull(spanLogger);
        this.tagsToPropagate = Objects.requireNonNull(tagsToPropagate);
    }

//    protected Span createChild(Span parent, String name) {
//        if (parent == null) {
//            return super.createChild(parent, name);
//        } else {
//            if (!isTracing()) {
//                pushSpanIntoContext(parent, true);
//            }
//
//            Map<String, String> childTags = new HashMap<>(tagsToPropagate.size());
//            for (String tagName : tagsToPropagate) {
//                if (parent.tags().containsKey(tagName)) {
//                    childTags.put(tagName, parent.tags().get(tagName));
//                }
//            }
//            Span span = Span.builder().begin(System.currentTimeMillis()).name(name)
//                    .traceId(parent.getTraceId()).parent(parent.getSpanId()).spanId(generateId())
//                    .processId(parent.getProcessId()).savedSpan(parent)
//                    .exportable(parent.isExportable())
//                    .tags(childTags)
//                    .build();
//            spanLogger.logStartedSpan(parent, span);
//            return span;
//        }
//    }

    private long generateId() {
        return random.nextLong();
    }

    private static void pushSpanIntoContext(Span span, boolean autoClose) {
        try {
            if (pushMethod == null) {
                Class<?> spanContextHolderClass = Class.forName("org.springframework.cloud.sleuth.trace.SpanContextHolder");
                pushMethod = spanContextHolderClass.getDeclaredMethod("push", Span.class, boolean.class);
                pushMethod.setAccessible(true);
            }
            pushMethod.invoke(null, span, autoClose);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Can't push span into context. Continuing anyway.", e);
        }
    }
}
