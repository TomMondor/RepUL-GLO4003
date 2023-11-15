package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class Locker {
    private final LockerId lockerId;
    private Optional<UniqueIdentifier> mealKitId = Optional.empty();

    public Locker(LockerId lockerId) {
        this.lockerId = lockerId;
    }

    public boolean isAssigned() {
        return this.mealKitId.isPresent();
    }

    public boolean isUnassigned() {
        return this.mealKitId.isEmpty();
    }

    public void assign(UniqueIdentifier mealKitId) {
        this.mealKitId = Optional.of(mealKitId);
    }

    public void unassign() {
        this.mealKitId = Optional.empty();
    }

    public LockerId getLockerId() {
        return lockerId;
    }
}
