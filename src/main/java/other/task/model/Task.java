package other.task.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract sealed class Task permits NewTask, RunningTask, FinishedTask, TerminatedTask {
    private final long id;

    @JsonIgnore
    private final int base;

    @JsonIgnore
    private final int exponent;

    private final State state;

    public enum State {
        NEW,
        RUNNING,
        FINISHED,
        TERMINATED,
    }
}
