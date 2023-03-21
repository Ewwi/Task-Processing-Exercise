package other.task.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static other.task.model.Task.State.NEW;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class NewTask extends Task {
    @Builder
    private NewTask(long id, int base, int exponent) {
        super(id, base, exponent, NEW);
    }
}
