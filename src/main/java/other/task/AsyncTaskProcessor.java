package other.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Component;
import other.task.model.FinishedTask;
import other.task.model.NewTask;
import other.task.model.RunningTask;
import other.task.model.Task;
import other.task.model.TerminatedTask;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
class AsyncTaskProcessor {
    private final int targetProcessingTimeInMs;
    private final int updateIntervalInMs;
    private final AsyncTaskExecutor taskExecutor;
    private final TaskRepository repository;

    AsyncTaskProcessor(
            @Value("${task-processing-time-in-ms:30000}") int targetProcessingTimeInMs,
            @Value("${task-processing-update-interval-in-ms:100}") int updateIntervalInMs,
            @Qualifier("taskProcessingExecutor") AsyncTaskExecutor taskExecutor,
            TaskRepository repository) {
        this.targetProcessingTimeInMs = targetProcessingTimeInMs;
        this.updateIntervalInMs = updateIntervalInMs;
        this.taskExecutor = taskExecutor;
        this.repository = repository;
    }

    void startProcessingTask(NewTask task) {
        taskExecutor.submit(() -> {
            Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();
            currentThread.setName("ProcessingTask-" + task.getId());
            try {
                simulateTaskProcessing(task);
            } finally {
                currentThread.setName(oldName);
            }
        });
    }

    private void simulateTaskProcessing(NewTask task) {
        try {
            for (int timePassed = 0; timePassed <= targetProcessingTimeInMs; timePassed += updateIntervalInMs) {
                int progress = calculateProgressAsPercent(timePassed, targetProcessingTimeInMs);
                repository.updateTask(buildRunningTaskWithProgress(task, progress));

                TimeUnit.MILLISECONDS.sleep(updateIntervalInMs);
            }

            log.info("Task {} processing finished", task.getId());
            repository.updateTask(buildFinishedTask(task));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("Task {} processing interrupted", task.getId());
            repository.updateTask(buildTerminatedTask(task));
        } catch (Exception e) {
            log.error("Exception while processing task {}", task.getId(), e);
            repository.updateTask(buildTerminatedTask(task));
        }
    }

    private int calculateProgressAsPercent(int current, int max) {
        return (int) (current * 1.0 / max * 100);
    }

    private RunningTask buildRunningTaskWithProgress(Task task, int progress) {
        return RunningTask.builder()
                .id(task.getId())
                .base(task.getBase())
                .exponent(task.getExponent())
                .progress(progress)
                .build();
    }

    private FinishedTask buildFinishedTask(Task task) {
        return FinishedTask.builder()
                .id(task.getId())
                .base(task.getBase())
                .exponent(task.getExponent())
                .result(calculatePower(task.getBase(), task.getExponent()))
                .build();
    }

    private TerminatedTask buildTerminatedTask(Task task) {
        return TerminatedTask.builder()
                .id(task.getId())
                .base(task.getBase())
                .exponent(task.getExponent())
                .build();
    }

    private long calculatePower(int base, int exponent) {
        return new BigInteger(Integer.toString(base)).pow(exponent).longValueExact();
    }
}
