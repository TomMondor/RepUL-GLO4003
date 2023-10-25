package ca.ulaval.glo4003.repul.small.delivery.application.payload;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.payload.MealKitDeliveryStatusPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LockerAssignator;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MealKitDeliveryStatusPayloadTest {
    private static final DeliveryLocationId DELIVERY_LOCATION_ID = new DeliveryLocationId("Location id");
    private static final String LOCATION_NAME = "VACHON";
    private static final int LOCATION_TOTAL_CAPACITY = 10;
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(DELIVERY_LOCATION_ID, LOCATION_NAME, LOCATION_TOTAL_CAPACITY);
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final DeliveryStatus DELIVERY_STATUS = DeliveryStatus.IN_DELIVERY;
    private static final UniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_MEAL_KIT_ID);
    private static final MealKit A_MEAL_KIT = new MealKit(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, DELIVERY_STATUS);
    private static final String TO_BE_DETERMINE_LOCKER_ID = "To be determined";
    private static final String VACHON_1_LOCKER_ID = "VACHON 1";
    private LockerAssignator lockerAssignator = new LockerAssignator(List.of(A_DELIVERY_LOCATION));

    @Test
    public void givenLockerId_whenUsingFrom_shouldReturnCorrectLockerPayload() {
        MealKitDeliveryStatusPayload expectedMealKitDeliveryStatusPayload =
            new MealKitDeliveryStatusPayload(DELIVERY_STATUS.toString(), DELIVERY_LOCATION_ID.value(), TO_BE_DETERMINE_LOCKER_ID);

        MealKitDeliveryStatusPayload actualMealKitDeliveryStatusPayload = MealKitDeliveryStatusPayload.from(A_MEAL_KIT);

        assertEquals(expectedMealKitDeliveryStatusPayload, actualMealKitDeliveryStatusPayload);
    }

    @Test
    public void givenNullLockerId_whenUsingFrom_shouldReturnCorrectLockerPayload() {
        MealKitDeliveryStatusPayload expectedMealKitDeliveryStatusPayload =
            new MealKitDeliveryStatusPayload(DELIVERY_STATUS.toString(), DELIVERY_LOCATION_ID.value(), VACHON_1_LOCKER_ID);
        MealKit mealKit = new MealKit(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, DELIVERY_STATUS);
        lockerAssignator.assignLocker(mealKit);

        MealKitDeliveryStatusPayload actualMealKitDeliveryStatusPayload = MealKitDeliveryStatusPayload.from(mealKit);

        assertEquals(expectedMealKitDeliveryStatusPayload, actualMealKitDeliveryStatusPayload);
    }
}
