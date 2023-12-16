package ca.ulaval.glo4003.repul.delivery.domain.mealkit;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.locker.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.exception.LockerNotAssignedException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInDeliveryException;

public class MealKit {
    private final SubscriberUniqueIdentifier subscriberId;
    private final SubscriptionUniqueIdentifier subscriptionId;
    private final MealKitUniqueIdentifier mealKitId;
    private final DeliveryLocationId deliveryLocationId;

    private DeliveryStatus status;
    private Optional<LockerId> lockerId;

    public MealKit(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId, MealKitUniqueIdentifier mealKitId,
                   DeliveryLocationId deliveryLocationId, DeliveryStatus status) {
        this.subscriberId = subscriberId;
        this.subscriptionId = subscriptionId;
        this.mealKitId = mealKitId;
        this.deliveryLocationId = deliveryLocationId;
        this.status = status;
        this.lockerId = Optional.empty();
    }

    public MealKitUniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public Optional<LockerId> getLockerId() {
        return lockerId;
    }

    public DeliveryLocationId getDeliveryLocationId() {
        return deliveryLocationId;
    }

    public void assignLocker(Optional<LockerId> lockerId) {
        this.lockerId = lockerId;
    }

    public void markAsReadyToBeDelivered() {
        status = DeliveryStatus.READY_TO_BE_DELIVERED;
    }

    public void markAsPickedUp() {
        status = DeliveryStatus.IN_DELIVERY;
    }

    public void confirmDelivery() {
        if (status != DeliveryStatus.IN_DELIVERY) {
            throw new MealKitNotInDeliveryException();
        }
        if (lockerId.isEmpty()) {
            throw new LockerNotAssignedException();
        }
        status = DeliveryStatus.DELIVERED;
    }

    public void recallDelivery() {
        if (status != DeliveryStatus.DELIVERED) {
            throw new MealKitNotDeliveredException();
        }
        status = DeliveryStatus.IN_DELIVERY;
    }

    public boolean isNotAlreadyDelivered() {
        return status == DeliveryStatus.READY_TO_BE_DELIVERED || status == DeliveryStatus.PENDING || status == DeliveryStatus.IN_DELIVERY;
    }

    public MealKitDto toDto() {
        return new MealKitDto(subscriberId, subscriptionId, mealKitId);
    }
}
