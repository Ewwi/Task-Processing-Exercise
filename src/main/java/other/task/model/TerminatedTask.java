package other.task.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static other.task.model.Task.State.TERMINATED;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class TerminatedTask extends Task {
    @Builder
    private TerminatedTask(long id, int base, int exponent) {
        super(id, base, exponent, TERMINATED);
    }
}
