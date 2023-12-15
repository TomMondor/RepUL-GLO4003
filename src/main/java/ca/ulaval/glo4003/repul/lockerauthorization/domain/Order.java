package ca.ulaval.glo4003.repul.lockerauthorization.domain;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;

public class Order {
    private final SubscriberUniqueIdentifier subscriberId;
    private final SubscriptionUniqueIdentifier subscriptionId;
    private final MealKitUniqueIdentifier mealKitId;
    private Optional<LockerId> lockerId;

    public Order(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId, MealKitUniqueIdentifier mealKitId) {
        this.subscriberId = subscriberId;
        this.subscriptionId = subscriptionId;
        this.mealKitId = mealKitId;
        this.lockerId = Optional.empty();
    }

    public void assignLocker(LockerId lockerId) {
        this.lockerId = Optional.of(lockerId);
    }

    public void unassignLocker() {
        this.lockerId = Optional.empty();
    }

    public SubscriberUniqueIdentifier getSubscriberId() {
        return subscriberId;
    }

    public MealKitUniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public Optional<LockerId> getLockerId() {
        return lockerId;
    }

    public boolean hasLockerId(LockerId lockerIdToCompare) {
        return lockerId.isPresent() && lockerId.get().equals(lockerIdToCompare);
    }

    public MealKitDto toDto() {
        return new MealKitDto(subscriberId, subscriptionId, mealKitId);
    }
}
