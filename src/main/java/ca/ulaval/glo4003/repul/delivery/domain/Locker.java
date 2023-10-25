package ca.ulaval.glo4003.repul.delivery.domain;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class Locker {
    private final LockerId lockerId;
    private UniqueIdentifier mealKitId;

    public Locker(LockerId lockerId) {
        this.lockerId = lockerId;
    }

    public boolean isAssigned() {
        return this.mealKitId != null;
    }

    public boolean isUnassigned() {
        return this.mealKitId == null;
    }

    public void assign(UniqueIdentifier mealKitId) {
        this.mealKitId = mealKitId;
    }

    public void unassign() {
        this.mealKitId = null;
    }

    public LockerId getLockerId() {
        return lockerId;
    }
}
