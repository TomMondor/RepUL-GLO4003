package ca.ulaval.glo4003.repul.shipping.domain.shippingTicket;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;
import ca.ulaval.glo4003.repul.shipping.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.MealKitNotInDeliveryException;

public class MealKit {
    private final DeliveryLocation deliveryLocation;
    private final UniqueIdentifier mealKitId;
    private LockerId lockerId;
    private ShippingStatus status;

    public MealKit(DeliveryLocation deliveryLocation, UniqueIdentifier mealKitId, ShippingStatus status) {
        this.deliveryLocation = deliveryLocation;
        this.mealKitId = mealKitId;
        this.status = status;
    }

    public UniqueIdentifier getMealKitId() {
        return mealKitId;
    }

    public ShippingStatus getStatus() {
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
        status = ShippingStatus.READY_TO_BE_DELIVERED;
    }

    public void pickupReadyToDeliverMealKit() {
        status = ShippingStatus.IN_DELIVERY;
    }

    public void cancelInDeliveryShipping() {
        status = ShippingStatus.READY_TO_BE_DELIVERED;
    }

    public void confirmShipping() {
        if (status != ShippingStatus.IN_DELIVERY) {
            throw new MealKitNotInDeliveryException();
        }
        this.deliveryLocation.unassignLocker(this.lockerId);
        status = ShippingStatus.DELIVERED;
    }

    public LockerId unconfirmShipping() {
        if (status != ShippingStatus.DELIVERED) {
            throw new MealKitNotDeliveredException();
        }
        status = ShippingStatus.IN_DELIVERY;
        return this.deliveryLocation.assignLocker(this);
    }
}
