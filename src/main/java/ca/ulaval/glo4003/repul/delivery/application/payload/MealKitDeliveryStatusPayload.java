package ca.ulaval.glo4003.repul.delivery.application.payload;

import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public record MealKitDeliveryStatusPayload(String deliveryStatus, String deliveryLocationId, String lockerId) {
    public static MealKitDeliveryStatusPayload from(MealKit mealKit) {
        return new MealKitDeliveryStatusPayload(mealKit.getStatus().toString(), mealKit.getDeliveryLocation().getLocationId().value(),
            mealKit.getLockerId() != null ? mealKit.getLockerId().id() : "To be determined");
    }
}
