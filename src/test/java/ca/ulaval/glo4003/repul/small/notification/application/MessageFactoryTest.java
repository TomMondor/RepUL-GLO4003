package ca.ulaval.glo4003.repul.small.notification.application;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.notification.application.MessageFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageFactoryTest {
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory().generate();
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Desjardins");
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("Desjardins");
    private static final LocalTime A_DELIVERY_TIME = LocalTime.now();
    private static final LockerId A_LOCKER_ID = new LockerId("locker", 5);
    private static final List<MealKitDto>
        A_MEAL_KIT_DTO = List.of(new MealKitDto(A_DELIVERY_LOCATION_ID, Optional.of(A_LOCKER_ID), A_MEAL_KIT_ID));
    private static final String EXPECTED_CREATED_DELIVERY_MESSAGE =
        "Your meal kit with id " + A_MEAL_KIT_ID.value() + " is ready to be fetched from " +
            A_DELIVERY_LOCATION_ID.value() + " in the locker " + A_LOCKER_ID.lockerNumber() + ".\n" +
        "It arrived at " + A_DELIVERY_TIME + ".\n" + "Come get it!";
    private static final String EXPECTED_CREATED_READY_TO_BE_DELIVERED_MESSAGE =
        "Your meal kits (cargo id: " + A_CARGO_ID.value() + ") are ready to be fetched from "
            + A_KITCHEN_LOCATION_ID.value() + ".\n" +
        "Here is the list of meal kits to be delivered:\n" +
        "MealKit ID " + A_MEAL_KIT_DTO.get(0).mealKitId().value() + " to " +
            A_MEAL_KIT_DTO.get(0).deliveryLocationId().value() + " in box 5\n";

    private MessageFactory messageFactory;

    @BeforeEach
    public void createMessageFactory() {
        messageFactory = new MessageFactory();
    }
    @Test
    public void whenCreatingDeliveryMessage_shouldReturnMessage() {
        String message = messageFactory.createDeliveredMessage(A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID,
            A_DELIVERY_TIME, A_LOCKER_ID);

        assertEquals(message, EXPECTED_CREATED_DELIVERY_MESSAGE);
    }

    @Test
    public void whenCreatingReadyToBeDeliveredMessage_shouldReturnMessage() {
        String message = messageFactory.createReadyToBeDeliveredMessage(A_CARGO_ID,
            A_KITCHEN_LOCATION_ID, A_MEAL_KIT_DTO);

        assertEquals(message, EXPECTED_CREATED_READY_TO_BE_DELIVERED_MESSAGE);
    }
}
