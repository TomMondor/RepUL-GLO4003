package ca.ulaval.glo4003.repul.lockerauthorization.domain;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class Order {
    private final UniqueIdentifier userId;
    private final UniqueIdentifier mealKitId;
    private Optional<LockerId> lockerId;

    public Order(UniqueIdentifier userId, UniqueIdentifier mealKitId) {
        this.userId = userId;
        this.mealKitId = mealKitId;
        this.lockerId = Optional.empty();
    }

    public void assignLocker(LockerId lockerId) {
        this.lockerId = Optional.of(lockerId);
    }

    public void unassignLocker() {
        this.lockerId = Optional.empty();
    }

    public UniqueIdentifier getUserId() {
        return userId;
    }

    public UniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public Optional<LockerId> getLockerId() {
        return lockerId;
    }
}
