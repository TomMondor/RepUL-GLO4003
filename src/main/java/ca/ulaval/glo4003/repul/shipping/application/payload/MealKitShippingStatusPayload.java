package ca.ulaval.glo4003.repul.shipping.application.payload;

import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfo;

public record MealKitShippingStatusPayload(String shippingStatus, String shippingLocationId, String caseId) {
    public static MealKitShippingStatusPayload from(MealKitShippingInfo mealKitShippingInfo) {
        return new MealKitShippingStatusPayload(mealKitShippingInfo.getStatus().toString(),
            mealKitShippingInfo.getShippingLocation().getLocationId().value(),
            mealKitShippingInfo.getCaseId() != null ? mealKitShippingInfo.getCaseId().id() : "To be determined");
    }
}
