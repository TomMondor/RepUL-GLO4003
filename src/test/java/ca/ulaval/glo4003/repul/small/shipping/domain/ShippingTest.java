package ca.ulaval.glo4003.repul.small.shipping.domain;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.fixture.shipping.LocationsCatalogFixture;
import ca.ulaval.glo4003.repul.fixture.shipping.ShippingFixture;
import ca.ulaval.glo4003.repul.shipping.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.KitchenLocation;
import ca.ulaval.glo4003.repul.shipping.domain.LockerId;
import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.DeliveryStatus;
import ca.ulaval.glo4003.repul.shipping.domain.cargo.MealKit;
import ca.ulaval.glo4003.repul.shipping.domain.catalog.LocationsCatalog;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidMealKitIdException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShipperException;
import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidShippingIdException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingTest {
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("A_LOCATION_ID");
    private static final KitchenLocationId A_KITCHEN_LOCATION_ID = new KitchenLocationId("ANOTHER_LOCATION_ID");
    private static final UniqueIdentifier A_MEALKIT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_CARGO_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final String A_LOCATION_NAME = "A_LOCATION_NAME";
    private static final String ANOTHER_LOCATION_NAME = "ANOTHER_LOCATION_NAME";
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 100);
    private static final DeliveryLocation ANOTHER_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 10);
    private static final DeliveryLocation A_ONE_PLACE_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 1);
    private static final KitchenLocation A_KITCHEN_LOCATION = new KitchenLocation(A_KITCHEN_LOCATION_ID, ANOTHER_LOCATION_NAME);
    private static final UniqueIdentifier ANOTHER_MEALKIT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_DELIVERY_PERSON_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier ANOTHER_SHIPPER_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_INVALID_CARGO_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final LockerId A_LOCKER_ID = new LockerId("A_LOCATION_NAME 1", 1);

    @Mock
    private LocationsCatalog mockLocationsCatalog;

    @Test
    public void givenValidValue_whenCreateMealKit_shouldCreateMealKit() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(new LocationsCatalogFixture().addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();

        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        assertDoesNotThrow(() -> shipping.getMealKit(A_MEALKIT_ID));
    }

    @Test
    public void whenCreateMealKit_shouldGetShippingLocation() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(mockLocationsCatalog).build();

        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        verify(mockLocationsCatalog).getDeliveryLocation(A_DELIVERY_LOCATION_ID);
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldCreateCargo() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(1, shipping.getCargos().size());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldMarkAsReadyToDeliver() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(DeliveryStatus.READY_TO_BE_DELIVERED, shipping.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldAssignLockerToMealKit() {
        DeliveryLocation deliveryLocation = new DeliveryLocation(A_DELIVERY_LOCATION_ID, A_LOCATION_NAME, 10);
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(ANOTHER_DELIVERY_LOCATION).build()).build();

        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        assertEquals(A_LOCKER_ID, shipping.getCargos().get(0).getMealKits().get(0).getLockerId());
    }

    @Test
    public void whenReceiveReadyToDeliverMealKit_shouldGetPickupLocation() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(mockLocationsCatalog).build();
        when(mockLocationsCatalog.getDeliveryLocation(A_DELIVERY_LOCATION_ID)).thenReturn(A_DELIVERY_LOCATION);
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);

        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));

        verify(mockLocationsCatalog).getKitchenLocation(A_KITCHEN_LOCATION_ID);
    }

    @Test
    public void whenGetShippingLocation_shouldReturnShippingLocations() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(new LocationsCatalogFixture().addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();

        List<DeliveryLocation> deliveryLocations = shipping.getShippingLocations();

        assertEquals(1, deliveryLocations.size());
        assertEquals(A_DELIVERY_LOCATION, deliveryLocations.get(0));
    }

    @Test
    public void givenShipperId_whenPickupCargo_shouldSetCargoShipperIdToRightValue() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(A_DELIVERY_PERSON_ID, shipping.getCargos().get(0).getShipperId());
    }

    @Test
    public void whenPickupCargo_shouldSetShippingStatusToInProgress() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(DeliveryStatus.IN_DELIVERY, shipping.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void whenPickupCargo_shouldReturnListOfMealKit() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        List<MealKit> mealKits = shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(A_MEALKIT_ID, mealKits.get(0).getMealKitId());
    }

    @Test
    public void givenInvalidShippingId_whenPickupCargo_shouldThrowInvalidShippingIdException() {
        Shipping shipping = new ShippingFixture().build();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);

        assertThrows(InvalidShippingIdException.class, () -> shipping.pickupCargo(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID));
    }

    @Test
    public void whenCancelShipping_shouldSetShippingStatusToReadyToDeliver() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        shipping.cancelShipping(A_DELIVERY_PERSON_ID, cargoId);

        assertEquals(DeliveryStatus.READY_TO_BE_DELIVERED, shipping.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenInvalidShippingId_whenCancelShipping_shouldThrowInvalidShippingIdException() {
        Shipping shipping = new ShippingFixture().build();

        assertThrows(InvalidShippingIdException.class, () -> shipping.cancelShipping(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID));
    }

    @Test
    public void givenInvalidShipperId_whenCancelShipping_shouldThrowInvalidShippingIdException() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidShipperException.class, () -> shipping.cancelShipping(ANOTHER_SHIPPER_ID, cargoId));
    }

    @Test
    public void whenConfirmShipping_shouldSetShippingStatusToDelivered() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        shipping.confirmShipping(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertEquals(DeliveryStatus.DELIVERED, shipping.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenInvalidShippingId_whenConfirmShipping_shouldThrowInvalidShippingIdException() {
        Shipping shipping = new ShippingFixture().build();

        assertThrows(InvalidShippingIdException.class, () -> shipping.confirmShipping(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidShipperId_whenConfirmShipping_shouldThrowInvalidShipperException() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidShipperException.class, () -> shipping.confirmShipping(ANOTHER_SHIPPER_ID, cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidMealKitId_whenConfirmShipping_shouldThrowInvalidMealKitIdException() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertThrows(InvalidMealKitIdException.class, () -> shipping.confirmShipping(A_DELIVERY_PERSON_ID, cargoId, ANOTHER_MEALKIT_ID));
    }

    @Test
    public void whenUnconfirmShipping_shouldSetShippingStatusToInDelivery() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        shipping.confirmShipping(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        shipping.unconfirmShipping(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertEquals(DeliveryStatus.IN_DELIVERY, shipping.getCargos().get(0).getMealKits().get(0).getStatus());
    }

    @Test
    public void givenInvalidShippingId_whenUnconfirmShipping_shouldThrowInvalidShippingIdException() {
        Shipping shipping = new ShippingFixture().build();

        assertThrows(InvalidShippingIdException.class, () -> shipping.unconfirmShipping(A_DELIVERY_PERSON_ID, A_INVALID_CARGO_ID, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidShipperId_whenUnconfirmShipping_shouldThrowInvalidShipperException() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        shipping.confirmShipping(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertThrows(InvalidShipperException.class, () -> shipping.unconfirmShipping(ANOTHER_SHIPPER_ID, cargoId, A_MEALKIT_ID));
    }

    @Test
    public void givenInvalidMealKitId_whenUnconfirmShipping_shouldThrowInvalidMealKitIdException() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);
        shipping.confirmShipping(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertThrows(InvalidMealKitIdException.class, () -> shipping.unconfirmShipping(A_DELIVERY_PERSON_ID, cargoId, ANOTHER_MEALKIT_ID));
    }

    @Test
    public void givenWaitingMealKit_whenConfirmShipping_shouldAssignLockerToWaitingMealKit() {
        Shipping shipping = new ShippingFixture().withLocationsCatalog(
            new LocationsCatalogFixture().addKitchenLocation(A_KITCHEN_LOCATION).addDeliveryLocation(A_ONE_PLACE_DELIVERY_LOCATION).build()).build();
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, A_MEALKIT_ID);
        shipping.createMealKit(A_DELIVERY_LOCATION_ID, ANOTHER_MEALKIT_ID);
        shipping.receiveReadyToBeDeliveredMealKit(A_KITCHEN_LOCATION_ID, List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        UniqueIdentifier cargoId = shipping.getCargos().get(0).getCargoId();
        shipping.addDeliveryPerson(A_DELIVERY_PERSON_ID);
        shipping.pickupCargo(A_DELIVERY_PERSON_ID, cargoId);

        assertNull(shipping.getCargos().get(0).getMealKits().get(1).getLockerId());

        shipping.confirmShipping(A_DELIVERY_PERSON_ID, cargoId, A_MEALKIT_ID);

        assertEquals(A_LOCKER_ID, shipping.getCargos().get(0).getMealKits().get(1).getLockerId());
    }

    @Test
    public void givenInvalidDeliveryPerson_whenPickupCargo_shouldThrowDeliveryPersonNotFoundException() {
        Shipping shipping = new ShippingFixture().build();

        assertThrows(DeliveryPersonNotFoundException.class, () -> shipping.pickupCargo(A_DELIVERY_PERSON_ID, A_CARGO_ID));
    }

    @Test
    public void whenAddingDeliveryPerson_shouldBeAbleToGet() {
        Shipping shipping = new ShippingFixture().build();
        UniqueIdentifier expectedId = new UniqueIdentifier(UUID.randomUUID());

        shipping.addDeliveryPerson(expectedId);
        List<UniqueIdentifier> deliveryPersonIds = shipping.getDeliveryPeople();

        assertEquals(1, deliveryPersonIds.size());
        assertEquals(expectedId, deliveryPersonIds.get(0));
    }
}
