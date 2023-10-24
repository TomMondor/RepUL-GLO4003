package ca.ulaval.glo4003.repul.small.shipping.application;

import java.time.LocalDate;
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
import ca.ulaval.glo4003.repul.shipping.application.ShippingService;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.shipping.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.shipping.application.payload.MealKitShippingStatusPayload;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.ShippingRepository;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.MealKit;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingStatus;
import ca.ulaval.glo4003.repul.shipping.domain.shippingTicket.ShippingTicket;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingServiceTest {
    private static final String A_SHIPPING_ID = UUID.randomUUID().toString();
    private static final String A_SHIPPER_ID = UUID.randomUUID().toString();
    private static final String A_TICKET_ID = UUID.randomUUID().toString();
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_TICKET_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_TICKET_ID);
    private static final UniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_MEAL_KIT_ID);
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("Vachon");
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Pouliot");
    private static final LocalDate A_DELIVERY_DATE = LocalDate.now().plusDays(1);
    private static final KitchenLocation A_KITCHEN_LOCATION = new KitchenLocation(A_KITCHEN_LOCATION_ID, "Vachon");
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, "Pouliot", 10);
    private static final MealKit A_MEAL_KIT =
        new MealKit(A_DELIVERY_LOCATION, A_MEAL_KIT_UNIQUE_IDENTIFIER, ShippingStatus.PENDING);
    private static final ShippingTicket A_SHIPPING_TICKET =
        new ShippingTicket(A_TICKET_UNIQUE_IDENTIFIER, A_KITCHEN_LOCATION, List.of(A_MEAL_KIT));
    private static final UniqueIdentifier A_SHIPPER_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_SHIPPER_ID);
    private static final MealKitsCookedEvent A_MEAL_KITS_COOKED_EVENT =
        new MealKitsCookedEvent(A_KITCHEN_LOCATION_ID.value(), List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER));
    private static final UniqueIdentifier A_SHIPPING_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_SHIPPING_ID);
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT =
        new MealKitConfirmedEvent(A_MEAL_KIT_UNIQUE_IDENTIFIER, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID, MealKitType.STANDARD, A_DELIVERY_LOCATION_ID,
            A_DELIVERY_DATE);
    private static final UniqueIdentifier DELIVERY_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final Email DELIVERY_ACCOUNT_EMAIL = new Email("email@ulaval.ca");
    private static final DeliveryPersonAccountCreatedEvent DELIVERY_ACCOUNT_CREATED_EVENT =
        new DeliveryPersonAccountCreatedEvent(DELIVERY_ACCOUNT_ID, DELIVERY_ACCOUNT_EMAIL);
    private static final MealKitShippingStatusPayload A_MEAL_KIT_SHIPPING_STATUS_PAYLOAD =
        new MealKitShippingStatusPayload(ShippingStatus.PENDING.toString(), A_DELIVERY_LOCATION_ID.value(), "To be determined");

    private ShippingService shippingService;
    @Mock
    private ShippingRepository shippingRepository;
    @Mock
    private Shipping mockShipping;
    @Mock
    private RepULEventBus mockRepULEventBus;

    @BeforeEach
    public void createShippingService() {
        shippingService = new ShippingService(shippingRepository, mockRepULEventBus);
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldAddDeliveryPerson() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));

        shippingService.handleDeliveryPersonAccountCreatedEvent(DELIVERY_ACCOUNT_CREATED_EVENT);

        verify(mockShipping).addDeliveryPerson(any(UniqueIdentifier.class));
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldGetShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));

        shippingService.handleDeliveryPersonAccountCreatedEvent(DELIVERY_ACCOUNT_CREATED_EVENT);

        verify(shippingRepository).get();
    }

    @Test
    public void whenHandlingDeliveryPersonAccountCreatedEvent_shouldSaveOrUpdateShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));

        shippingService.handleDeliveryPersonAccountCreatedEvent(DELIVERY_ACCOUNT_CREATED_EVENT);

        verify(shippingRepository).saveOrUpdate(mockShipping);
    }

    @Test
    public void whenPickupCargo_shouldGetShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(shippingRepository).get();
    }

    @Test
    public void whenPickupCargo_shouldPublishPickedupCargoEvent() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(mockRepULEventBus).publish(any(PickedUpCargoEvent.class));
    }

    @Test
    public void whenPickupCargo_shouldSaveOrUpdateShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(shippingRepository).saveOrUpdate(any(Shipping.class));
    }

    @Test
    public void givenValidUniqueIdentifier_whenPickupCargo_shouldPickupCargoWithRightUniqueIdentifier() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(mockShipping).pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenCancelShipping_shouldGetShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(shippingRepository).get();
    }

    @Test
    public void whenCancelShipping_shouldSaveOrUpdateShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(shippingRepository).saveOrUpdate(any(Shipping.class));
    }

    @Test
    public void givenValidUniqueIdentifier_whenCancelShipping_shouldPickupCargoWithRightUniqueIdentifier() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(mockShipping).cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldGetShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        when(mockShipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_SHIPPING_TICKET);

        shippingService.receiveReadyToBeDeliveredMealKit(A_MEAL_KITS_COOKED_EVENT);

        verify(shippingRepository).get();
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldSaveOrUpdate() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        when(mockShipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_SHIPPING_TICKET);

        shippingService.receiveReadyToBeDeliveredMealKit(A_MEAL_KITS_COOKED_EVENT);

        verify(shippingRepository).saveOrUpdate(any(Shipping.class));
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldReturnShippingTicket() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        when(mockShipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_SHIPPING_TICKET);

        shippingService.receiveReadyToBeDeliveredMealKit(A_MEAL_KITS_COOKED_EVENT);

        verify(mockShipping).receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER));
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldPublishMealKitReadyToDeliverEvent() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        when(mockShipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEAL_KIT_UNIQUE_IDENTIFIER))).thenReturn(A_SHIPPING_TICKET);

        shippingService.receiveReadyToBeDeliveredMealKit(A_MEAL_KITS_COOKED_EVENT);

        verify(mockRepULEventBus).publish(any(MealKitReceivedForDeliveryEvent.class));
    }

    @Test
    public void whenReceiveShippingRequest_shouldGetShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.receiveShippingRequest(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(shippingRepository).get();
    }

    @Test
    public void whenReceiveShippingRequest_shouldSaveOrUpdate() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.receiveShippingRequest(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(shippingRepository).saveOrUpdate(any(Shipping.class));
    }

    @Test
    public void whenReceiveShippingRequest_shouldCreateMealKit() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.receiveShippingRequest(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(mockShipping).createMealKit(A_DELIVERY_LOCATION_ID, A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenGettingShippingStatus_shouldGetShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        when(mockShipping.getMealKit(A_MEAL_KIT_UNIQUE_IDENTIFIER)).thenReturn(A_MEAL_KIT);

        shippingService.getShippingStatus(A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(shippingRepository).get();
    }

    @Test
    public void givenValidUniqueIdentifier_whenGettingShippingStatus_shouldReturnMealKitShippingStatusPayload() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        when(mockShipping.getMealKit(A_MEAL_KIT_UNIQUE_IDENTIFIER)).thenReturn(A_MEAL_KIT);

        MealKitShippingStatusPayload payload = shippingService.getShippingStatus(A_MEAL_KIT_UNIQUE_IDENTIFIER);

        assertEquals(A_MEAL_KIT_SHIPPING_STATUS_PAYLOAD, payload);
    }

    @Test
    public void whenConfirmShipping_shouldGetShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.confirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(shippingRepository).get();
    }

    @Test
    public void whenConfirmShipping_shouldSaveOrUpdateShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.confirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(shippingRepository).saveOrUpdate(any(Shipping.class));
    }

    @Test
    public void givenValidUniqueIdentifiers_whenConfirmShipping_shouldConfirmShippingWithRightUniqueIdentifier() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.confirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockShipping).confirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenUnconfirmShipping_shouldGetShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.unconfirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(shippingRepository).get();
    }

    @Test
    public void whenUnconfirmShipping_shouldSaveOrUpdateShipping() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.unconfirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(shippingRepository).saveOrUpdate(any(Shipping.class));
    }

    @Test
    public void givenValidUniqueIdentifiers_whenUnconfirmShipping_shouldUnconfirmShippingWithRightUniqueIdentifier() {
        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
        shippingService.unconfirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);

        verify(mockShipping).unconfirmShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_TICKET_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }
}
