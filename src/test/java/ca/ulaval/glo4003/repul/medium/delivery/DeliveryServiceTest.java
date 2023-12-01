package ca.ulaval.glo4003.repul.medium.delivery;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargoPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.delivery.domain.exception.MealKitNotInDeliveryException;
import ca.ulaval.glo4003.repul.delivery.infrastructure.InMemoryDeliverySystemRepository;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryServiceTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ACCOUNT_ID = new SubscriberUniqueIdentifier(UUID.randomUUID());
    private static final DeliveryPersonUniqueIdentifier A_DELIVERY_PERSON_ACCOUNT_ID = new DeliveryPersonUniqueIdentifier(UUID.randomUUID());
    private static final MealKitUniqueIdentifier A_MEALKIT_ID = new MealKitUniqueIdentifier(UUID.randomUUID());
    private static final MealKitUniqueIdentifier ANOTHER_MEAL_KIT_ID = new MealKitUniqueIdentifier(UUID.randomUUID());
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new SubscriptionUniqueIdentifier(UUID.randomUUID());
    private static final Email AN_EMAIL = new Email("test12@ulaval.ca");
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("VACHON");
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("DESJARDINS");
    private static final LocalDate A_DATE = LocalDate.now();

    private static final LocationsCatalog A_LOCATIONS_CATALOG = new LocationsCatalog(List.of(new DeliveryLocation(A_DELIVERY_LOCATION_ID, "vch-1", 10)),
        List.of(new KitchenLocation(A_KITCHEN_LOCATION_ID, "Desjardins")));

    private DeliverySystemRepository deliverySystemRepository;
    private RepULEventBus eventBus;
    private DeliveryService deliveryService;

    @BeforeEach
    public void createDeliveryService() {
        deliverySystemRepository = new InMemoryDeliverySystemRepository();
        deliverySystemRepository.save(new DeliverySystem(A_LOCATIONS_CATALOG));
        eventBus = new GuavaEventBus();
        deliveryService = new DeliveryService(deliverySystemRepository, eventBus);
        eventBus.register(deliveryService);
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldAddDeliveryPersonAccountToDeliverySystem() {
        DeliveryPersonAccountCreatedEvent event = new DeliveryPersonAccountCreatedEvent(A_DELIVERY_PERSON_ACCOUNT_ID, AN_EMAIL);

        eventBus.publish(event);

        assertEquals(1, deliverySystemRepository.get().get().getDeliveryPeople().size());
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldAddMealKitToDeliverySystem() {
        MealKitsCookedEvent mealKitsCookedEvent = new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID.value(), List.of(A_MEALKIT_ID));
        MealKitConfirmedEvent mealKitConfirmedEvent =
            new MealKitConfirmedEvent(A_MEALKIT_ID, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ACCOUNT_ID, A_MEALKIT_TYPE, A_DELIVERY_LOCATION_ID, A_DATE);

        eventBus.publish(mealKitConfirmedEvent);

        assertDoesNotThrow(() -> deliveryService.handleMealKitsCookedEvent(mealKitsCookedEvent));
    }

    @Test
    public void givenConfirmedMealKits_whenHandlingMealKitsCookedEvent_shouldCreateCargo() {
        givenConfirmedMealKit(A_MEALKIT_ID);
        givenConfirmedMealKit(ANOTHER_MEAL_KIT_ID);
        MealKitsCookedEvent mealKitsCookedEvent = new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID.value(), List.of(A_MEALKIT_ID, ANOTHER_MEAL_KIT_ID));

        eventBus.publish(mealKitsCookedEvent);

        assertEquals(1, deliveryService.getCargosReadyToPickUp().cargoPayloads().size());
        assertEquals(2, deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).mealKitsPayload().size());
    }

    @Test
    public void givenMealKitInCargo_whenHandlingRecallCookedMealKitEvent_shouldRemoveMealKitFromCargo() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEALKIT_ID);
        givenCookedMealKit(A_MEALKIT_ID);
        RecallCookedMealKitEvent recallCookedMealKitEvent = new RecallCookedMealKitEvent(A_MEALKIT_ID);

        eventBus.publish(recallCookedMealKitEvent);

        assertEquals(0, deliveryService.getCargosReadyToPickUp().cargoPayloads().size());
    }

    @Test
    public void givenPendingCargo_whenPickupCargo_shouldRemoveCargoFromAvailableCargos() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEALKIT_ID);
        givenCookedMealKit(A_MEALKIT_ID);
        CargoUniqueIdentifier cargoId = deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).cargoId();

        deliveryService.pickupCargo(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId);

        assertEquals(0, deliveryService.getCargosReadyToPickUp().cargoPayloads().size());
    }

    @Test
    public void givenPickedUpCargo_whenConfirmDelivery_should() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEALKIT_ID);
        givenCookedMealKit(A_MEALKIT_ID);
        CargoUniqueIdentifier cargoId = deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).cargoId();
        deliveryService.pickupCargo(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId);

        deliveryService.confirmDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, A_MEALKIT_ID);

        assertThrows(MealKitNotInDeliveryException.class, () -> deliveryService.confirmDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenPickedUpCargo_whenCancelCargo_shouldMakeCargoAvailableForDelivery() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEALKIT_ID);
        givenCookedMealKit(A_MEALKIT_ID);
        CargoUniqueIdentifier cargoId = deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).cargoId();
        givenPickedUpCargo(cargoId);

        deliveryService.cancelCargo(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId);
        List<CargoPayload> cargosPayloads = deliveryService.getCargosReadyToPickUp().cargoPayloads();

        assertEquals(1, cargosPayloads.size());
    }

    @Test
    public void givenDeliveredMealKit_whenRecallDelivery_shouldBeAbleToReconfirmMealKit() {
        givenDeliveryPerson(A_DELIVERY_PERSON_ACCOUNT_ID);
        givenConfirmedMealKit(A_MEALKIT_ID);
        givenCookedMealKit(A_MEALKIT_ID);
        CargoUniqueIdentifier cargoId = deliveryService.getCargosReadyToPickUp().cargoPayloads().get(0).cargoId();
        givenPickedUpCargo(cargoId);
        givenDeliveredMealKit(cargoId, A_MEALKIT_ID);

        deliveryService.recallDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, A_MEALKIT_ID);

        assertDoesNotThrow(() -> deliveryService.confirmDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, A_MEALKIT_ID));
    }

    private void givenConfirmedMealKit(MealKitUniqueIdentifier mealKitId) {
        MealKitConfirmedEvent mealKitConfirmedEvent =
            new MealKitConfirmedEvent(mealKitId, A_SUBSCRIPTION_ID, A_SUBSCRIBER_ACCOUNT_ID, A_MEALKIT_TYPE, A_DELIVERY_LOCATION_ID, A_DATE);
        eventBus.publish(mealKitConfirmedEvent);
    }

    private void givenCookedMealKit(MealKitUniqueIdentifier mealKitId) {
        MealKitsCookedEvent mealKitsCookedEvent = new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID.value(), List.of(mealKitId));
        eventBus.publish(mealKitsCookedEvent);
    }

    private void givenDeliveryPerson(DeliveryPersonUniqueIdentifier accountId) {
        DeliveryPersonAccountCreatedEvent event = new DeliveryPersonAccountCreatedEvent(accountId, AN_EMAIL);
        eventBus.publish(event);
    }

    private void givenPickedUpCargo(CargoUniqueIdentifier cargoId) {
        deliveryService.pickupCargo(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId);
    }

    private void givenDeliveredMealKit(CargoUniqueIdentifier cargoId, MealKitUniqueIdentifier mealKitId) {
        deliveryService.confirmDelivery(A_DELIVERY_PERSON_ACCOUNT_ID, cargoId, mealKitId);
    }
}
