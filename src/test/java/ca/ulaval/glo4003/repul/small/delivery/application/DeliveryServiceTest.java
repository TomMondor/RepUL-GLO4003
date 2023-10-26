package ca.ulaval.glo4003.repul.small.delivery.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.payload.MealKitDeliveryStatusPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeliveryServiceTest {
    private static final String A_DELIVERY_PERSON_ID = UUID.randomUUID().toString();
    private static final String A_CARGO_ID = UUID.randomUUID().toString();
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_CARGO_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_CARGO_ID);
    private static final UniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_MEAL_KIT_ID);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("Vachon");
    private static final MealKitsCookedEvent A_MEAL_KITS_COOKED_EVENT =
        new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID.value(), List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER));
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Pouliot");
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now().plusDays(1);
    private static final KitchenLocation A_KITCHEN_LOCATION = new KitchenLocation(A_KITCHEN_LOCATION_ID, "Vachon");
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, "Pouliot", 10);
    private static final MealKit A_MEAL_KIT = new MealKit(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, DeliveryStatus.PENDING);
    private static final Cargo A_CARGO = new Cargo(A_CARGO_UNIQUE_IDENTIFIER, A_KITCHEN_LOCATION, new ArrayList<>(List.of(A_MEAL_KIT)));
    private static final UniqueIdentifier A_DELIVERY_PERSON_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_DELIVERY_PERSON_ID);
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT =
        new MealKitConfirmedEvent(A_MEAL_KIT_UNIQUE_IDENTIFIER, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID, MealKitType.STANDARD, A_DELIVERY_LOCATION_ID,
            A_DELIVERY_DATE);
    private static final RecallCookedMealKitEvent A_RECALL_COOKED_MEAL_KIT_EVENT = new RecallCookedMealKitEvent(A_MEAL_KIT_UNIQUE_IDENTIFIER);
    private static final UniqueIdentifier DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final Email DELIVERY_ACCOUNT_EMAIL = new Email("email@ulaval.ca");
    private static final DeliveryPersonAccountCreatedEvent DELIVERY_ACCOUNT_CREATED_EVENT =
        new DeliveryPersonAccountCreatedEvent(DELIVERY_ACCOUNT_ID, DELIVERY_ACCOUNT_EMAIL);
    private static final MealKitDeliveryStatusPayload A_MEAL_KIT_DELIVERY_STATUS_PAYLOAD =
        new MealKitDeliveryStatusPayload(DeliveryStatus.PENDING.toString(), A_DELIVERY_LOCATION_ID.value(), "To be determined");
    private DeliveryService deliveryService;
    @Mock
    private DeliverySystemRepository mockDeliverySystemRepository;
    @Mock
    private DeliverySystem mockDeliverySystem;
    @Mock
    private RepULEventBus mockRepULEventBus;

    @BeforeEach
    public void createDeliveryService() {
        deliveryService = new DeliveryService(mockDeliverySystemRepository, mockRepULEventBus);
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldAddDeliveryPerson() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleDeliveryPersonAccountCreatedEvent(DELIVERY_ACCOUNT_CREATED_EVENT);

        verify(mockDeliverySystem).addDeliveryPerson(any(UniqueIdentifier.class));
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldGetDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleDeliveryPersonAccountCreatedEvent(DELIVERY_ACCOUNT_CREATED_EVENT);

        verify(mockDeliverySystemRepository).get();
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldSaveOrUpdateDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleDeliveryPersonAccountCreatedEvent(DELIVERY_ACCOUNT_CREATED_EVENT);

        verify(mockDeliverySystemRepository).saveOrUpdate(mockDeliverySystem);
    }

    @Test
    public void whenPickupCargo_shouldGetDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.pickupCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystemRepository).get();
    }

    @Test
    public void whenPickupCargo_shouldPublishPickedupCargoEvent() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.pickupCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockRepULEventBus).publish(any(PickedUpCargoEvent.class));
    }

    @Test
    public void whenPickupCargo_shouldSaveOrUpdateDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.pickupCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystemRepository).saveOrUpdate(mockDeliverySystem);
    }

    @Test
    public void givenValidUniqueIdentifier_whenPickupCargo_shouldPickupCargoWithRightUniqueIdentifier() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.pickupCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystem).pickupCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenCancelCargo_shouldGetDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.cancelCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystemRepository).get();
    }

    @Test
    public void whenCancelCargo_shouldSaveOrUpdateDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.cancelCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystemRepository).saveOrUpdate(mockDeliverySystem);
    }

    @Test
    public void givenValidUniqueIdentifier_whenCancelCargo_shouldCancelCargoWithRightUniqueIdentifier() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.cancelCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystem).cancelCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldGetDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));
        when(mockDeliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_CARGO);

        deliveryService.handleMealKitsCookedEvent(A_MEAL_KITS_COOKED_EVENT);

        verify(mockDeliverySystemRepository).get();
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldSaveOrUpdateDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));
        when(mockDeliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_CARGO);

        deliveryService.handleMealKitsCookedEvent(A_MEAL_KITS_COOKED_EVENT);

        verify(mockDeliverySystemRepository).saveOrUpdate(mockDeliverySystem);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldReceiveReadyToBeDeliveredMealKit() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));
        when(mockDeliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_CARGO);

        deliveryService.handleMealKitsCookedEvent(A_MEAL_KITS_COOKED_EVENT);

        verify(mockDeliverySystem).receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER));
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldPublishMealKitReadyToDeliverEvent() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));
        when(mockDeliverySystem.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_CARGO);

        deliveryService.handleMealKitsCookedEvent(A_MEAL_KITS_COOKED_EVENT);

        verify(mockRepULEventBus).publish(any(MealKitReceivedForDeliveryEvent.class));
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldGetDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(mockDeliverySystemRepository).get();
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldSaveOrUpdateDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(mockDeliverySystemRepository).saveOrUpdate(mockDeliverySystem);
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldCreateMealKit() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(mockDeliverySystem).createMealKit(A_DELIVERY_LOCATION_ID, A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenHandlingRecallCookedMealKitEvent_shouldGetDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleRecallCookedMealKitEvent(A_RECALL_COOKED_MEAL_KIT_EVENT);

        verify(mockDeliverySystemRepository).get();
    }

    @Test
    public void whenHandlingRecallCookedMealKitEvent_shouldMoveMealKitFromCargosToPendingInDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleRecallCookedMealKitEvent(A_RECALL_COOKED_MEAL_KIT_EVENT);

        verify(mockDeliverySystem).moveMealKitFromCargosToPending(A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenHandlingRecallCookedMealKitEvent_shouldSaveOrUpdateDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.handleRecallCookedMealKitEvent(A_RECALL_COOKED_MEAL_KIT_EVENT);

        verify(mockDeliverySystemRepository).saveOrUpdate(mockDeliverySystem);
    }

    @Test
    public void whenConfirmDelivery_shouldGetDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.confirmDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystemRepository).get();
    }

    @Test
    public void whenConfirmDelivery_shouldSaveOrUpdateDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.confirmDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystemRepository).saveOrUpdate(mockDeliverySystem);
    }

    @Test
    public void givenValidUniqueIdentifiers_whenConfirmDelivery_shouldConfirmDeliveryWithRightUniqueIdentifier() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.confirmDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystem).confirmDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenRecallDelivery_shouldGetDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.recallDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystemRepository).get();
    }

    @Test
    public void whenRecallDelivery_shouldSaveOrUpdateDeliverySystem() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.recallDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystemRepository).saveOrUpdate(mockDeliverySystem);
    }

    @Test
    public void givenValidUniqueIdentifiers_whenRecallDelivery_shouldRecallDeliveryWithRightUniqueIdentifier() {
        when(mockDeliverySystemRepository.get()).thenReturn(Optional.of(mockDeliverySystem));

        deliveryService.recallDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockDeliverySystem).recallDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }
}
