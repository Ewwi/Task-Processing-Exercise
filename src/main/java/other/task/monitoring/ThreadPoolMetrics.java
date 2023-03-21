package other.task.monitoring;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
class ThreadPoolMetrics {
    ThreadPoolMetrics(
            @Qualifier("taskProcessingExecutor") ThreadPoolTaskExecutor executor,
            MeterRegistry meterRegistry) {
        Gauge.builder("task-processing.queue-size", executor::getQueueSize)
                .register(meterRegistry);
        Gauge.builder("task-processing.queue-capacity", executor::getQueueCapacity)
                .register(meterRegistry);
        Gauge.builder("task-processing.queue-utilization",
                        () -> 1.0 * executor.getQueueSize() / executor.getQueueCapacity())
                .register(meterRegistry);
        Gauge.builder("task-processing.active-threads", executor::getActiveCount)
                .register(meterRegistry);
    }
}
