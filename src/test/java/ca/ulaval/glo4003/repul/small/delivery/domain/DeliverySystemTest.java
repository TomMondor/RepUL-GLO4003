package ca.ulaval.glo4003.repul.small.delivery.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.locker.Locker;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.locker.LockerId;
import ca.ulaval.glo4003.repul.delivery.domain.exception.CargoNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotDeliveredException;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.fixture.delivery.DeliverySystemFixture;
import ca.ulaval.glo4003.repul.fixture.delivery.LocationsCatalogFixture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DeliverySystemTest {
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = KitchenLocationId.DESJARDINS;
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final CargoUniqueIdentifier A_CARGO_ID = new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate();
    private static final String A_LOCATION_NAME = "A_LOCATION_NAME";
    private static final String ANOTHER_LOCATION_NAME = "ANOTHER_LOCATION_NAME";
    private static final MealKitUniqueIdentifier ANOTHER_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier A_DELIVERY_PERSON_ID = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final DeliveryPersonUniqueIdentifier ANOTHER_DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    private static final CargoUniqueIdentifier A_INVALID_CARGO_ID = new UniqueIdentifierFactory<>(CargoUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_INVALID_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final Optional<LockerId> A_LOCKER_ID = Optional.of(new LockerId("A_LOCATION_NAME 1", 1));

    @Mock
    private LocationsCatalog mockLocationsCatalog;

    @Test
    public void givenInvalidMealKitId_whenReceivingReadyToBeDeliveredMealKit_shouldThrowInvalidMealKitIdException() {
        DeliverySystem deliverySystem = new DeliverySystemFixture().build();

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID)));
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldCreateCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));

        assertEquals(1, deliverySystem.getCargosReadyToPickUp().size());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldMarkAsReadyToDeliver() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));

        assertEquals(DeliveryStatus.READY_TO_BE_DELIVERED, deliverySystem.getCargosReadyToPickUp().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldAssignLockerToMealKit() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);

        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));

        assertEquals(A_LOCKER_ID, deliverySystem.getCargosReadyToPickUp().get(0).getMealKits().get(0).getLockerId());
    }

    @Test
    public void whenGetDeliveryLocation_shouldReturnDeliveryLocations() {
        DeliverySystem deliverySystem = createDeliverySystem();

        List<DeliveryLocation> deliveryLocations = deliverySystem.getDeliveryLocations();

        assertEquals(1, deliveryLocations.size());
        assertEquals(A_DELIVERY_LOCATION_ID, deliveryLocations.get(0).getLocationId());
    }

    @Test
    public void whenPickUpCargo_shouldSetDeliveryStatusToInProgress() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(DeliveryStatus.IN_DELIVERY, deliverySystem.getCargosInDelivery().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenPickUpCargo_shouldReturnListOfMealKits() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        List<MealKit> mealKits = deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(A_MEAL_KIT_ID, mealKits.get(0).getMealKitId());
    }

    @Test
    public void givenInvalidCargoId_whenPickUpCargo_shouldThrowCargoNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        assertThrows(CargoNotFoundException.class, () -> deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID));
    }

    @Test
    public void givenAlreadyPickedUpCargo_whenPickUpCargo_shouldThrowCargoNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(CargoNotFoundException.class, () -> deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId));
    }

    @Test
    public void whenConfirmDelivery_shouldSetDeliveryStatusToDelivered() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);

        assertEquals(DeliveryStatus.DELIVERED, deliverySystem.getCargosInDelivery().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenInvalidCargoId_whenConfirmDelivery_shouldThrowCargoNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        assertThrows(CargoNotFoundException.class, () -> deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID, A_MEAL_KIT_ID));
    }

    @Test
    public void givenValidDeliveryPersonIdButNotRightDeliveryPerson_whenConfirmDelivery_shouldThrowCargoNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.addDeliveryPerson(ANOTHER_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(CargoNotFoundException.class, () -> deliverySystem.confirmDelivery(ANOTHER_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID));
    }

    @Test
    public void givenValidCargoIdButInvalidMealKitId_whenConfirmDelivery_shouldThrowMealKitNotInCargoException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, ANOTHER_MEAL_KIT_ID));
    }

    @Test
    public void whenRecallingDelivery_shouldSetDeliveryStatusToInDelivery() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);

        deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);

        assertEquals(DeliveryStatus.IN_DELIVERY, deliverySystem.getCargosInDelivery().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenMealKitInDelivery_whenRecallingDelivery_shouldThrowMealKitNotDeliveredException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(MealKitNotDeliveredException.class, () -> deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID));
    }

    @Test
    public void givenInvalidCargoId_whenRecallingDelivery_shouldThrowCargoNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        assertThrows(CargoNotFoundException.class, () -> deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID, A_MEAL_KIT_ID));
    }

    @Test
    public void givenValidDeliveryPersonIdButNotRightDeliveryPerson_whenRecallingDelivery_shouldThrowCargoNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.addDeliveryPerson(ANOTHER_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);

        assertThrows(CargoNotFoundException.class, () -> deliverySystem.recallDelivery(ANOTHER_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID));
    }

    @Test
    public void givenMealKitIdNotInCargo_whenRecallingDelivery_shouldThrowMealKitNotInCargoException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.recallDelivery(A_DELIVERY_PERSON_ID, cargoId, ANOTHER_MEAL_KIT_ID));
    }

    @Test
    public void givenInvalidDeliveryPerson_whenPickUpCargo_shouldThrowDeliveryPersonNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();

        assertThrows(DeliveryPersonNotFoundException.class, () -> deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, A_CARGO_ID));
    }

    @Test
    public void whenAddingDeliveryPerson_shouldBeAbleToGet() {
        DeliverySystem deliverySystem = createDeliverySystem();
        DeliveryPersonUniqueIdentifier expectedId = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();

        deliverySystem.addDeliveryPerson(expectedId);
        List<DeliveryPersonUniqueIdentifier> deliveryPersonIds = deliverySystem.getDeliveryPeopleIds();

        assertEquals(1, deliveryPersonIds.size());
        assertEquals(expectedId, deliveryPersonIds.get(0));
    }

    @Test
    public void givenCargoWithASingleMealKit_whenMovingMealKitFromCargosToPending_shouldRemoveCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEAL_KIT_ID)));

        deliverySystem.recallMealKitToPending(A_MEAL_KIT_ID);

        assertEquals(0, deliverySystem.getCargosReadyToPickUp().size());
    }

    @Test
    public void givenCargoWithTwoMealKits_whenMovingMealKitFromCargosToPending_shouldRemoveMealKitFromCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, ANOTHER_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(Arrays.asList(A_MEAL_KIT_ID, ANOTHER_MEAL_KIT_ID)));

        deliverySystem.recallMealKitToPending(A_MEAL_KIT_ID);

        assertEquals(1, deliverySystem.getCargosReadyToPickUp().get(0).getMealKits().size());
    }

    @Test
    public void whenMovingMealKitFromCargosToPending_shouldMakeMealKitReadyToBeDeliveredAgain() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        List<MealKitUniqueIdentifier> newMealKitId = new ArrayList<>(List.of(A_MEAL_KIT_ID));
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, newMealKitId);

        deliverySystem.recallMealKitToPending(A_MEAL_KIT_ID);

        assertDoesNotThrow(() -> deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, newMealKitId));
    }

    @Test
    public void whenMovingMealKitFromCargosToPending_shouldUnassignLocker() {
        DeliverySystem deliverySystem = createDeliverySystemWithOneLocker();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        List<MealKitUniqueIdentifier> newMealKitId = new ArrayList<>(List.of(A_MEAL_KIT_ID));
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, newMealKitId);

        deliverySystem.recallMealKitToPending(A_MEAL_KIT_ID);

        assertEquals(1, deliverySystem.getDeliveryLocations().get(0).getRemainingCapacity());
    }

    @Test
    public void givenNoCargos_whenGettingCargosReadyToPickUp_shouldReturnEmptyList() {
        DeliverySystem deliverySystem = createDeliverySystem();

        List<Cargo> cargos = deliverySystem.getCargosReadyToPickUp();

        assertEquals(0, cargos.size());
    }

    @Test
    public void givenOneCargo_whenGettingCargosReadyToPickUpy_shouldReturnListOfOneCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEAL_KIT_ID)));

        List<Cargo> cargos = deliverySystem.getCargosReadyToPickUp();

        assertEquals(1, cargos.size());
        assertEquals(A_MEAL_KIT_ID, cargos.get(0).getMealKits().get(0).getMealKitId());
    }

    @Test
    public void givenOnePickedUpCargo_whenGettingCargosReadyToPickUp_shouldReturnEmptyList() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEAL_KIT_ID)));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        List<Cargo> cargos = deliverySystem.getCargosReadyToPickUp();

        assertEquals(0, cargos.size());
    }

    @Test
    public void whenGettingMealKit_shouldReturnMealKit() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEAL_KIT_ID)));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        MealKit mealKit = deliverySystem.findMealKit(cargoId, A_MEAL_KIT_ID);

        assertEquals(A_MEAL_KIT_ID, mealKit.getMealKitId());
    }

    @Test
    public void givenInvalidCargoId_whenGettingMealKit_shouldThrowCargoNotFoundException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEAL_KIT_ID)));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(CargoNotFoundException.class, () -> deliverySystem.findMealKit(A_INVALID_CARGO_ID, A_MEAL_KIT_ID));
    }

    @Test
    public void givenInvalidCargoId_whenGettingMealKit_shouldThrowInvalidMealKitIdException() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, new ArrayList<>(List.of(A_MEAL_KIT_ID)));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.findMealKit(cargoId, A_INVALID_MEAL_KIT_ID));
    }

    @Test
    public void whenRemovingMealKitFromLocker_shouldDeleteMealKitFromCargo() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, ANOTHER_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID, ANOTHER_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);

        deliverySystem.removeMealKitFromLocker(A_MEAL_KIT_ID);

        assertThrows(InvalidMealKitIdException.class, () -> deliverySystem.findMealKit(cargoId, A_MEAL_KIT_ID));
    }

    @Test
    public void givenCargoWithOnlyOneMealKitRemaining_whenRemovingMealKitFromLocker_shouldDeleteCargo() {
        DeliverySystem deliverySystem = createDeliverySystemWithOneLocker();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);

        deliverySystem.removeMealKitFromLocker(A_MEAL_KIT_ID);

        assertTrue(deliverySystem.getCargosInDelivery().isEmpty());
    }

    @Test
    public void whenRemovingMealKitFromLocker_shouldDeleteMeal() {
        DeliverySystem deliverySystem = createDeliverySystem();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);
        MealKit mealKit = deliverySystem.findMealKit(cargoId, A_MEAL_KIT_ID);
        DeliveryLocation deliveryLocationOfMealKit = deliverySystem.getDeliveryLocations().stream().filter(
            deliveryLocation -> deliveryLocation.getLocationId().equals(mealKit.getDeliveryLocationId())).findFirst().get();
        Locker locker = deliveryLocationOfMealKit.findLockerById(mealKit.getLockerId().get());

        assertTrue(locker.isAssigned());

        deliverySystem.removeMealKitFromLocker(A_MEAL_KIT_ID);

        assertTrue(locker.isUnassigned());
    }

    @Test
    public void givenWaitingMealkit_whenRemovingMealKitFromLocker_shouldAssignLockerToWaitingMealkit() {
        DeliverySystem deliverySystem = createDeliverySystemWithOneLocker();
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, A_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.createPendingMealKit(A_SUBSCRIBER_ID, A_SUBSCRIPTION_ID, ANOTHER_MEAL_KIT_ID, A_DELIVERY_LOCATION_ID);
        deliverySystem.receiveReadyToBeDeliveredMealKits(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_ID, ANOTHER_MEAL_KIT_ID));
        CargoUniqueIdentifier cargoId = deliverySystem.getCargosReadyToPickUp().get(0).getCargoId();
        deliverySystem.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        deliverySystem.pickUpCargo(A_DELIVERY_PERSON_ID, cargoId);
        deliverySystem.confirmDelivery(A_DELIVERY_PERSON_ID, cargoId, A_MEAL_KIT_ID);
        MealKit mealKit = deliverySystem.findMealKit(cargoId, A_MEAL_KIT_ID);
        MealKit anotherMealKit = deliverySystem.findMealKit(cargoId, ANOTHER_MEAL_KIT_ID);
        DeliveryLocation deliveryLocationOfMealKit = deliverySystem.getDeliveryLocations().stream().filter(
            deliveryLocation -> deliveryLocation.getLocationId().equals(mealKit.getDeliveryLocationId())).findFirst().get();
        Locker locker = deliveryLocationOfMealKit.findLockerById(mealKit.getLockerId().get());

        assertTrue(locker.isAssigned());

        deliverySystem.removeMealKitFromLocker(A_MEAL_KIT_ID);

        assertTrue(locker.isAssigned());
        assertEquals(locker.getLockerId(), anotherMealKit.getLockerId().get());
    }

    private DeliverySystem createDeliverySystem() {
        return new DeliverySystemFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addDeliveryLocation(new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 100))
                .addKitchenLocation(new KitchenLocation(A_KITCHEN_LOCATION_ID, ANOTHER_LOCATION_NAME)).build()).build();
    }

    private DeliverySystem createDeliverySystemWithOneLocker() {
        return new DeliverySystemFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addDeliveryLocation(new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 1))
                .addKitchenLocation(new KitchenLocation(A_KITCHEN_LOCATION_ID, ANOTHER_LOCATION_NAME)).build()).build();
    }
}
