package ca.ulaval.glo4003.repul.delivery.application.payload;

import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public record MealKitPayload(String mealKitId, String deliveryLocationId, String lockerNumber) {
    public static MealKitPayload from(MealKit mealKit) {
        String lockerNumber = mealKit.getLockerId().isEmpty() ? "Not assigned at this moment" : String.valueOf(mealKit.getLockerId().get().lockerNumber());

        return new MealKitPayload(
            mealKit.getMealKitId().getUUID().toString(),
            mealKit.getDeliveryLocationId().toString(),
            lockerNumber
        );
    }
}
