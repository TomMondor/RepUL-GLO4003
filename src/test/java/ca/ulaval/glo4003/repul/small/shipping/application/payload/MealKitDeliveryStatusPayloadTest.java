package ca.ulaval.glo4003.repul.small.shipping.application.payload;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.application.payload.MealKitShippingStatusPayload;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.MealKit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MealKitDeliveryStatusPayloadTest {
    private static final DeliveryLocationId DELIVERY_LOCATION_ID = new DeliveryLocationId("Location id");
    private static final String LOCATION_NAME = "VACHON";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(DELIVERY_LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final DeliveryStatus SHIPPING_STATUS = DeliveryStatus.IN_DELIVERY;
    private static final UniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_MEAL_KIT_ID);
    private static final MealKit MEAL_KIT = new MealKit(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, SHIPPING_STATUS);
    private static final MealKit ANOTHER_SHIPPING_INFO =
        new MealKit(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, SHIPPING_STATUS);
    private static final String TO_BE_DETERMINE_LOCKER_ID = "To be determined";
    private static final String VACHON_1_LOCKER_ID = "VACHON 1";

    @Test
    public void givenLockerId_whenUsingFrom_shouldReturnCorrectLockerPayload() {
        MealKitShippingStatusPayload expectedMealKitShippingStatusPayload =
            new MealKitShippingStatusPayload(SHIPPING_STATUS.toString(), DELIVERY_LOCATION_ID.value(), TO_BE_DETERMINE_LOCKER_ID);

        MealKitShippingStatusPayload actualMealKitShippingStatusPayload = MealKitShippingStatusPayload.from(ANOTHER_SHIPPING_INFO);

        assertEquals(expectedMealKitShippingStatusPayload, actualMealKitShippingStatusPayload);
    }

    @Test
    public void givenNullLockerId_whenUsingFrom_shouldReturnCorrectLockerPayload() {
        MealKitShippingStatusPayload expectedMealKitShippingStatusPayload =
            new MealKitShippingStatusPayload(SHIPPING_STATUS.toString(), DELIVERY_LOCATION_ID.value(), VACHON_1_LOCKER_ID);
        MealKit mealKit = MEAL_KIT;
        mealKit.assignLocker();

        MealKitShippingStatusPayload actualMealKitShippingStatusPayload = MealKitShippingStatusPayload.from(MEAL_KIT);

        assertEquals(expectedMealKitShippingStatusPayload, actualMealKitShippingStatusPayload);
    }
}
