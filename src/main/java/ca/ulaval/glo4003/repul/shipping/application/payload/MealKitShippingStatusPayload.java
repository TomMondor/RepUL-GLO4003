package ca.ulaval.glo4003.repul.shipping.application.payload;

import ca.ulaval.glo4003.repul.shipping.domain.cargo.MealKit;

public record MealKitShippingStatusPayload(String shippingStatus, String shippingLocationId, String lockerId) {
    public static MealKitShippingStatusPayload from(MealKit mealKit) {
        return new MealKitShippingStatusPayload(mealKit.getStatus().toString(),
            mealKit.getShippingLocation().getLocationId().value(),
            mealKit.getLockerId() != null ? mealKit.getLockerId().id() : "To be determined");
    }
}
