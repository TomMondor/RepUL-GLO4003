package ca.ulaval.glo4003.repul.fixture.delivery;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;

public class MealKitFixture {
    private SubscriberUniqueIdentifier subscriberId;
    private SubscriptionUniqueIdentifier subscriptionId;
    private MealKitUniqueIdentifier mealKitId;
    private DeliveryLocationId deliveryLocationId;
    private DeliveryStatus status;
    private Optional<LockerId> lockerId;

    public MealKitFixture() {
        this.subscriberId = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
        this.subscriptionId = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
        this.mealKitId = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
        this.deliveryLocationId = DeliveryLocationId.VACHON;
        this.status = DeliveryStatus.READY_TO_BE_DELIVERED;
        this.lockerId = Optional.empty();
    }

    public MealKitFixture withSubscriberId(SubscriberUniqueIdentifier subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public MealKitFixture withSubscriptionId(SubscriptionUniqueIdentifier subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public MealKitFixture withMealKitId(MealKitUniqueIdentifier mealKitId) {
        this.mealKitId = mealKitId;
        return this;
    }

    public MealKitFixture withDeliveryLocation(DeliveryLocationId deliveryLocationId) {
        this.deliveryLocationId = deliveryLocationId;
        return this;
    }

    public MealKitFixture withStatus(DeliveryStatus status) {
        this.status = status;
        return this;
    }

    public MealKitFixture withLockerId(LockerId lockerId) {
        this.lockerId = Optional.of(lockerId);
        return this;
    }

    public MealKitFixture withLockerId(Optional<LockerId> lockerId) {
        this.lockerId = lockerId;
        return this;
    }

    public MealKit build() {
        MealKit mealKit = new MealKit(subscriberId, subscriptionId, mealKitId, deliveryLocationId, status);
        mealKit.assignLocker(lockerId);

        return mealKit;
    }
}
