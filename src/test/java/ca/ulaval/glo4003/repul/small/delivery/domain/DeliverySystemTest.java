package ca.ulaval.glo4003.repul.small.delivery.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidCargoIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidDeliveryPersonIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.fixture.delivery.DeliverySystemFixture;
import ca.ulaval.glo4003.repul.fixture.delivery.LocationsCatalogFixture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeliverySystemTest {
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("A_LOCATION_ID");
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("ANOTHER_LOCATION_ID");
    private static final UniqueIdentifier A_MEALKIT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_CARGO_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final String A_LOCATION_NAME = "A_LOCATION_NAME";
    private static final String ANOTHER_LOCATION_NAME = "ANOTHER_LOCATION_NAME";
    private static final UniqueIdentifier ANOTHER_MEALKIT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_DELIVERY_PERSON_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier ANOTHER_DELIVERY_PERSON_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_INVALID_CARGO_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("A_LOCATION_NAME 1", 1));

    @Mock
    private LocationsCatalog mockLocationsCatalog;

    @Test
    public void whenCreateMealKit_shouldGetDeliveryLocation() {
        DeliverySystem deliverySystem = new DeliverySystemFixture().withLocationsCatalog(mockLocationsCatalog).build();

        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        verify(mockLocationsCatalog).getDeliveryLocation(A_DELIVERY_LOCATION_ID);
    }

    @Test
    public void givenInvalidMealKitId_whenReceivingReadyToBeDeliveredMealKit_shouldThrowInvalidMealKitIdException() {
        DeliverySystem deliverySystem = new DeliverySystemFixture().build();

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID)));
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldCreateCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(1, deliverySystem.getCargos().size());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldMarkAsReadyToDeliver() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(DeliveryStatus.READY_TO_BE_DELIVERED, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldAssignLockerToMealKit() {
        DeliverySystem deliverySystem = createDeliverySystem();

        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(A_LOCKER_ID, deliverySystem.getCargos().get(0).getMealKits().get(0).getLockerId());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldGetPickupLocation() {
        DeliverySystem deliverySystem = new DeliverySystemFixture().withLocationsCatalog(mockLocationsCatalog).build();
        when(mockLocationsCatalog.getDeliveryLocation(A_DELIVERY_LOCATION_ID)).thenReturn(new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 100));
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        verify(mockLocationsCatalog).getKitchenLocation(A_KITCHEN_LOCATION_ID);
    }

    @Test
    public void whenGetDeliveryLocation_shouldReturnDeliveryLocations() {
        DeliverySystem deliverySystem = createDeliverySystem();

        List<DeliveryLocation> deliveryLocations = deliverySystem.getDeliveryLocations();

        assertEquals(1, deliveryLocations.size());
        assertEquals(A_DELIVERY_LOCATION_ID, deliveryLocations.get(0).getLocationId());
    }

    @Test
    public void givenValidDeliveryPersonId_whenPickupCargo_shouldSetCargoDeliveryPersonIdToRightValue() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(A_DELIVERY_PERSON_ID, deliverySystem.getCargos().get(0).getDeliveryPersonId());
    }

    @Test
    public void whenPickupCargo_shouldSetDeliveryStatusToInProgress() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(DeliveryStatus.IN_DELIVERY, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenPickupCargo_shouldReturnListOfMealKit() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        List<MealKit> mealKits = deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(A_MEALKIT_ID, mealKits.get(0).getMealKitId());
    }

    @Test
    public void givenInvalidCargoId_whenPickupCargo_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID));
    }

    @Test
    public void whenCancelCargo_shouldSetDeliveryStatusToReadyToDeliver() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        deliverySystem.cancelCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(DeliveryStatus.READY_TO_BE_DELIVERED, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenInvalidCargoId_whenCancelCargo_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.cancelCargo(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID));
    }

    @Test
    public void givenInvalidDeliveryPersonId_whenCancelCargo_shouldThrowInvalidDeliveryPersonIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidDeliveryPersonIdException.class, () -> deliverySystem.cancelCargo(ANOTHER_DELIVERY_PERSON_ID, cargoId));
    }

    @Test
    public void whenConfirmDelivery_shouldSetDeliveryStatusToDelivered() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertEquals(DeliveryStatus.DELIVERED, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenInvalidCargoId_whenConfirmDelivery_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidDeliveryPersonId_whenConfirmDelivery_shouldThrowInvalidDeliveryPersonIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidDeliveryPersonIdException.class, () -> deliverySystem.confirmDelivery(ANOTHER_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenValidCargoIdButInvalidMealKitId_whenConfirmDelivery_shouldThrowInvalidMealKitIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, ANOTHER_MEALKIT_ID));
    }

    @Test
    public void whenRecallDelivery_shouldSetDeliveryStatusToInDelivery() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertEquals(DeliveryStatus.IN_DELIVERY, deliverySystem.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenInvalidCargoId_whenRecallDelivery_shouldThrowInvalidCargoIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(InvalidCargoIdException.class, () -> deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidDeliveryPersonId_whenRecallDelivery_shouldThrowInvalidDeliveryPersonIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertThrows(InvalidDeliveryPersonIdException.class, () -> deliverySystem.recallDelivery(ANOTHER_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidMealKitId_whenRecallDelivery_shouldThrowInvalidMealKitIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = deliverySystem.getCargos().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, cargoId, ANOTHER_MEALKIT_ID));
    }

    @Test
    public void givenInvalidDeliveryPerson_whenPickupCargo_shouldThrowDeliveryPersonNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(DeliveryPersonNotFoundException.class, () -> deliverySystem.pickupCargo(A_DELIVERY_PERSON_ID, A_CARGO_ID));
    }

    @Test
    public void whenAddingDeliveryPerson_shouldBeAbleToGet() {
        DeliverySystem deliverySystem = createDeliverySystem();
        UniqueIdentifier expectedId = new UniqueIdentifier(UUID.randomUUID());

        deliverySystem.addDeliveryPerson(expectedId);
        List<UniqueIdentifier> deliveryPersonIds = deliverySystem.getDeliveryPeople();

        assertEquals(1, deliveryPersonIds.size());
        assertEquals(expectedId, deliveryPersonIds.get(0));
    }

    @Test
    public void givenCargoWithASingleMealKit_whenMovingMealKitFromCargosToPending_shouldRemoveCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEALKIT_ID)));

        deliverySystem.moveMealKitFromCargosToPending(A_MEALKIT_ID);

        assertEquals(0, deliverySystem.getCargos().size());
    }

    @Test
    public void givenCargoWithTwoMealKits_whenMovingMealKitFromCargosToPending_shouldRemoveMealKitFromCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, ANOTHER_MEALKIT_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, new ArrayList<>(Arrays.asList(A_MEALKIT_ID, ANOTHER_MEALKIT_ID)));

        deliverySystem.moveMealKitFromCargosToPending(A_MEALKIT_ID);

        assertEquals(1, deliverySystem.getCargos().get(0).getMealKits().size());
    }

    @Test
    public void whenMovingMealKitFromCargosToPending_shouldMakeMealKitBeAbleToBeReadyToBeDelivered() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        List<UniqueIdentifier> newMealKitId = new ArrayList<>(List.of(A_MEALKIT_ID));
        deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, newMealKitId);

        deliverySystem.moveMealKitFromCargosToPending(A_MEALKIT_ID);

        assertDoesNotThrow(() -> deliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, newMealKitId));
    }

    private DeliverySystem createDeliverySystem() {
        return new DeliverySystemFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addDeliveryLocation(new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 100))
                .addKitchenLocation(new KitchenLocation(A_KITCHEN_LOCATION_ID, ANOTHER_LOCATION_NAME)).build()).build();
    }
}
