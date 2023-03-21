package other.task;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
class ThreadPoolConfiguration {
    @Bean
    ThreadPoolTaskExecutor taskProcessingExecutor(
            @Value("${task-processing-thread-pool-size:5}") int poolSize,
            @Value("${task-processing-queue-capacity:100}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        return executor;
    }
}
