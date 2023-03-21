package other.task;

import org.springframework.stereotype.Repository;
import other.task.model.NewTask;
import other.task.model.Task;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Comparator.comparing;

@Repository
class TaskRepository {
    private final Map<Long, Task> taskById = new ConcurrentHashMap<>();
    private final AtomicLong nextTaskId = new AtomicLong();

    Optional<Task> findById(long id) {
        return Optional.ofNullable(taskById.get(id));
    }

    List<Task> findAll(long offset, int limit) {
        return taskById.values()
                .stream()
                .filter(task -> task.getId() >= offset)
                .sorted(comparing(Task::getId))
                .limit(limit)
                .toList();
    }

    NewTask createNewTask(int base, int exponent) {
        long taskId = nextTaskId.getAndIncrement();

        NewTask task = NewTask.builder()
                .base(base)
                .exponent(exponent)
                .id(taskId)
                .build();

        taskById.put(taskId, task);

        return task;
    }

    void updateTask(Task task) {
        taskById.put(task.getId(), task);
    }

    boolean deleteTask(long id) {
        Task removed = taskById.remove(id);
        return removed != null;
    }

    void deleteAllTasks() {
        taskById.keySet().forEach(taskById::remove);
    }
}
