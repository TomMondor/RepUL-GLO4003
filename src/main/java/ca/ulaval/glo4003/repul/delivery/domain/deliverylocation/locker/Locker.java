package ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.locker;

import java.util.Optional;

import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;

public class Locker {
    private final LockerId lockerId;
    private Optional<MealKit> mealKit = Optional.empty();

    public Locker(LockerId lockerId) {
        this.lockerId = lockerId;
    }

    public boolean isAssigned() {
        return this.mealKit.isPresent();
    }

    public boolean isUnassigned() {
        return this.mealKit.isEmpty();
    }

    public void assign(MealKit mealKit) {
        this.mealKit = Optional.of(mealKit);
    }

    public void unassign() {
        this.mealKit = Optional.empty();
    }

    public LockerId getLockerId() {
        return lockerId;
    }
}
