package other.task.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static other.task.model.Task.State.RUNNING;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class RunningTask extends Task {
    private final int progress;

    @Builder
    private RunningTask(long id, int base, int exponent, int progress) {
        super(id, base, exponent, RUNNING);
        this.progress = progress;
    }
}
