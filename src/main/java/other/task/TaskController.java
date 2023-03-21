package other.task;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import other.task.model.FinishedTask;
import other.task.model.NewTask;
import other.task.model.RunningTask;
import other.task.model.Task;
import other.task.model.TerminatedTask;

import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
class TaskController {
    private final TaskService service;

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            oneOf = {NewTask.class, RunningTask.class, FinishedTask.class, TerminatedTask.class},
                            discriminatorProperty = "state",
                            discriminatorMapping = {
                                    @DiscriminatorMapping(value = "NEW", schema = NewTask.class),
                                    @DiscriminatorMapping(value = "FINISHED", schema = FinishedTask.class),
                                    @DiscriminatorMapping(value = "RUNNING", schema = RunningTask.class),
                                    @DiscriminatorMapping(value = "TERMINATED", schema = TerminatedTask.class),
                            }))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema())),
    })
    @GetMapping("/tasks/{taskId}")
    ResponseEntity<?> getTask(@PathVariable long taskId) {
        return service.findTaskById(taskId)
                .map(task -> ResponseEntity.status(OK).body(task))
                .orElseGet(() -> ResponseEntity.status(NOT_FOUND).build());
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            oneOf = {NewTask.class, RunningTask.class, FinishedTask.class, TerminatedTask.class},
                            discriminatorProperty = "state",
                            discriminatorMapping = {
                                    @DiscriminatorMapping(value = "NEW", schema = NewTask.class),
                                    @DiscriminatorMapping(value = "FINISHED", schema = FinishedTask.class),
                                    @DiscriminatorMapping(value = "RUNNING", schema = RunningTask.class),
                                    @DiscriminatorMapping(value = "TERMINATED", schema = TerminatedTask.class),
                            }))),
    })
    @GetMapping("/tasks")
    List<Task> getTasks(
            @RequestParam(required = false, defaultValue = "0") Long offset,
            @RequestParam(required = false, defaultValue = "100") Integer limit) {
        return service.findAllTasks(offset, limit);
    }

    @Operation(responses = {
            @ApiResponse(responseCode = "202",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = TaskCreatedResponse.class))),
            @ApiResponse(responseCode = "503", content = @Content(schema = @Schema())),
    })
    @PostMapping("/tasks")
    @ResponseStatus(ACCEPTED)
    TaskCreatedResponse createNewTask(@RequestBody NewTaskRequest request) {
        long taskId = service.createNewTask(request.base(), request.exponent());
        return new TaskCreatedResponse(taskId);
    }

    record NewTaskRequest(int base, int exponent) {
    }

    record TaskCreatedResponse(long id) {
    }
}
