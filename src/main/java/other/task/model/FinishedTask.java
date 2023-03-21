package other.task.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import static other.task.model.Task.State.FINISHED;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class FinishedTask extends Task {
    private final long result;

    @Builder
    private FinishedTask(long id, int base, int exponent, long result) {
        super(id, base, exponent, FINISHED);
        this.result = result;
    }
}
