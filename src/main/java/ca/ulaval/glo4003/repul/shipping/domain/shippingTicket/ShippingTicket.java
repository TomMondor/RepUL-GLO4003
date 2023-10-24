package ca.ulaval.glo4003.repul.shipping.domain.shippingTicket;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShipperException;

public class ShippingTicket {
    private final UniqueIdentifier ticketId;
    private final List<MealKitShippingInfo> mealKitShippingInfos;
    private KitchenLocation kitchenLocation;
    private UniqueIdentifier shipperId;

    public ShippingTicket(UniqueIdentifier ticketId, KitchenLocation kitchenLocation, List<MealKitShippingInfo> mealKitShippingInfos) {
        this.ticketId = ticketId;
        this.kitchenLocation = kitchenLocation;
        this.mealKitShippingInfos = mealKitShippingInfos;
    }

    public UniqueIdentifier getTicketId() {
        return ticketId;
    }

    public UniqueIdentifier getShipperId() {
        return shipperId;
    }

    public KitchenLocation getPickupLocation() {
        return kitchenLocation;
    }

    public List<MealKitShippingInfo> getMealKitShippingInfos() {
        return mealKitShippingInfos;
    }

    public List<MealKitShippingInfo> pickupCargo(UniqueIdentifier accountId) {
        this.shipperId = accountId;
        mealKitShippingInfos.forEach(MealKitShippingInfo::pickupReadyToDeliverMealKit);
        return mealKitShippingInfos;
    }

    public void cancelShipping(UniqueIdentifier accountId) {
        if (!accountId.equals(shipperId)) {
            throw new InvalidShipperException();
        }
        this.shipperId = null;
        mealKitShippingInfos.forEach(MealKitShippingInfo::cancelInDeliveryShipping);
    }

    public void confirmShipping(UniqueIdentifier accountId, UniqueIdentifier mealKitId) {
        if (!accountId.equals(shipperId)) {
            throw new InvalidShipperException();
        }
        mealKitShippingInfos.stream()
            .filter(mealKitShippingInfo -> mealKitShippingInfo.getMealKitId().equals(mealKitId))
            .findFirst()
            .orElseThrow(InvalidMealKitIdException::new)
            .confirmShipping();
    }

    public LockerId unconfirmShipping(UniqueIdentifier accountId, UniqueIdentifier mealKitId) {
        if (!accountId.equals(shipperId)) {
            throw new InvalidShipperException();
        }
        return mealKitShippingInfos.stream()
            .filter(mealKitShippingInfo -> mealKitShippingInfo.getMealKitId().equals(mealKitId))
            .findFirst()
            .orElseThrow(InvalidMealKitIdException::new)
            .unconfirmShipping();
    }
}
