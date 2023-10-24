package ca.ulaval.glo4003.repul.small.shipping.application.payload;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.application.payload.MealKitShippingStatusPayload;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKitShippingInfo;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MealKitShippingStatusPayloadTest {
    private static final DeliveryLocationId DELIVERY_LOCATION_ID = new DeliveryLocationId("Location id");
    private static final String LOCATION_NAME = "VACHON";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(DELIVERY_LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_MEAL_KIT_ID);
    private static final ShippingStatus SHIPPING_STATUS = ShippingStatus.IN_DELIVERY;
    private static final MealKitShippingInfo SHIPPING_INFO = new MealKitShippingInfo(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, SHIPPING_STATUS);
    private static final String TO_BE_DETERMINE_CASE_ID = "To be determined";
    private static final String VACHON_1_CASE_ID = "VACHON 1";

    @Test
    public void givenCaseId_whenUsingFrom_shouldReturnCorrectCasePayload() {
        MealKitShippingStatusPayload expectedMealKitShippingStatusPayload =
            new MealKitShippingStatusPayload(SHIPPING_STATUS.toString(), DELIVERY_LOCATION_ID.value(), TO_BE_DETERMINE_CASE_ID);

        MealKitShippingStatusPayload actualMealKitShippingStatusPayload = MealKitShippingStatusPayload.from(SHIPPING_INFO);

        assertEquals(expectedMealKitShippingStatusPayload, actualMealKitShippingStatusPayload);
    }

    @Test
    public void givenNullCaseId_whenUsingFrom_shouldReturnCorrectCasePayload() {
        MealKitShippingStatusPayload expectedMealKitShippingStatusPayload =
            new MealKitShippingStatusPayload(SHIPPING_STATUS.toString(), DELIVERY_LOCATION_ID.value(), VACHON_1_CASE_ID);
        MealKitShippingInfo shippingInfo = SHIPPING_INFO;
        shippingInfo.assignCase();

        MealKitShippingStatusPayload actualMealKitShippingStatusPayload = MealKitShippingStatusPayload.from(SHIPPING_INFO);

        assertEquals(expectedMealKitShippingStatusPayload, actualMealKitShippingStatusPayload);
    }
}
