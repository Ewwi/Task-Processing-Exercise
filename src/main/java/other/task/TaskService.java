package other.task;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import other.task.model.NewTask;
import other.task.model.Task;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RequiredArgsConstructor
@Service
class TaskService {
    private final TaskRepository repository;
    private final AsyncTaskProcessor taskProcessor;

    List<Task> findAllTasks(long offset, int limit) {
        return repository.findAll(offset, limit);
    }

    Optional<Task> findTaskById(long id) {
        return repository.findById(id);
    }

    long createNewTask(int base, int exponent) {
        NewTask task = repository.createNewTask(base, exponent);

        try {
            taskProcessor.startProcessingTask(task);
        } catch (TaskRejectedException e) {
            repository.deleteTask(task.getId());

            throw new ResponseStatusException(SERVICE_UNAVAILABLE);
        }

        return task.getId();
    }
}
