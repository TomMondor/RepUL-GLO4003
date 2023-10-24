package ca.ulaval.glo4003.repul.shipping.domain.cargo;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;
import ca.ulaval.glo4003.repul.shipping.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.MealKitNotInDeliveryException;

public class MealKit {
    private final DeliveryLocation deliveryLocation;
    private final UniqueIdentifier mealKitId;
    private LockerId lockerId;
    private DeliveryStatus status;

    public MealKit(DeliveryLocation deliveryLocation, UniqueIdentifier mealKitId, DeliveryStatus status) {
        this.deliveryLocation = deliveryLocation;
        this.mealKitId = mealKitId;
        this.status = status;
    }

    public UniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public LockerId getLockerId() {
        return lockerId;
    }

    public DeliveryLocation getShippingLocation() {
        return deliveryLocation;
    }

    public void assignLocker() {
        this.lockerId = this.deliveryLocation.assignLocker(this);
    }

    public void markAsReadyToBeDelivered() {
        status = DeliveryStatus.READY_TO_BE_DELIVERED;
    }

    public void pickupReadyToDeliverMealKit() {
        status = DeliveryStatus.IN_DELIVERY;
    }

    public void cancelInDeliveryShipping() {
        status = DeliveryStatus.READY_TO_BE_DELIVERED;
    }

    public void confirmShipping() {
        if (status != DeliveryStatus.IN_DELIVERY) {
            throw new MealKitNotInDeliveryException();
        }
        this.deliveryLocation.unassignLocker(this.lockerId);
        status = DeliveryStatus.DELIVERED;
    }

    public LockerId unconfirmShipping() {
        if (status != DeliveryStatus.DELIVERED) {
            throw new MealKitNotDeliveredException();
        }
        status = DeliveryStatus.IN_DELIVERY;
        return this.deliveryLocation.assignLocker(this);
    }
}
