package ca.ulaval.glo4003.repul.fixture.delivery;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public class MealKitFixture {
    private DeliveryLocationId deliveryLocationId;
    private MealKitUniqueIdentifier mealKitId;
    private Optional<LockerId> lockerId;
    private DeliveryStatus status;

    public MealKitFixture() {
        this.deliveryLocationId = DeliveryLocationId.VACHON;
        this.mealKitId = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
        this.status = DeliveryStatus.READY_TO_BE_DELIVERED;
        this.lockerId = Optional.empty();
    }

    public MealKitFixture withDeliveryLocation(DeliveryLocationId deliveryLocationId) {
        this.deliveryLocationId = deliveryLocationId;
        return this;
    }

    public MealKitFixture withMealKitId(MealKitUniqueIdentifier mealKitId) {
        this.mealKitId = mealKitId;
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

    public MealKitFixture withStatus(DeliveryStatus status) {
        this.status = status;
        return this;
    }

    public MealKit build() {
        MealKit mealKit = new MealKit(deliveryLocationId, mealKitId, status);
        mealKit.assignLocker(lockerId);

        return mealKit;
    }
}
