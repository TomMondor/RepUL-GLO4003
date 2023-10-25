package ca.ulaval.glo4003.repul.delivery.domain.cargo;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInDeliveryException;

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

    public DeliveryLocation getDeliveryLocation() {
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

    public void cancelDelivery() {
        status = DeliveryStatus.READY_TO_BE_DELIVERED;
    }

    public void confirmDelivery() {
        if (status != DeliveryStatus.IN_DELIVERY) {
            throw new MealKitNotInDeliveryException();
        }
        // TODO : Venir enlever le unassignLocker parce que le meal kit est toujours dans le casier
        this.deliveryLocation.unassignLocker(this.lockerId);
        status = DeliveryStatus.DELIVERED;
    }

    public LockerId recallDelivery() {
        if (status != DeliveryStatus.DELIVERED) {
            throw new MealKitNotDeliveredException();
        }
        status = DeliveryStatus.IN_DELIVERY;
        // TODO : Vérifier si on doit assigner le locker si on unassign pas
        return this.deliveryLocation.assignLocker(this);
    }
}
