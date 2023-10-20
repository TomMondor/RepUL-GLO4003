package ca.ulaval.glo4003.shipping.small.domain;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.CaseId;
import ca.ulaval.glo4003.commons.domain.LocationId;
import ca.ulaval.glo4003.commons.domain.MealkitId;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.shipping.application.query.MealkitShippingInfoQuery;
import ca.ulaval.glo4003.shipping.domain.Shipping;
import ca.ulaval.glo4003.shipping.domain.catalog.ShippingCatalog;
import ca.ulaval.glo4003.shipping.domain.commons.PickupLocation;
import ca.ulaval.glo4003.shipping.domain.commons.ShippingLocation;
import ca.ulaval.glo4003.shipping.domain.exception.InvalidShipperException;
import ca.ulaval.glo4003.shipping.domain.exception.InvalidShippingIdException;
import ca.ulaval.glo4003.shipping.domain.shippingTicket.ShippingStatus;
import ca.ulaval.glo4003.shipping.fixture.ShippingCatalogFixture;
import ca.ulaval.glo4003.shipping.fixture.ShippingFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ShippingTest {
    private static final LocationId A_SHIPPING_LOCATION_ID = new LocationId("A_LOCATION_ID");
    private static final LocationId A_PICKUP_LOCATION_ID = new LocationId("ANOTHER_LOCATION_ID");
    private static final CaseId A_CASE_ID = new CaseId("A_CASE_ID");
    private static final MealkitId A_MEALKIT_ID = new MealkitId("A_MEALKIT_ID");
    private static final MealkitShippingInfoQuery A_MEALKIT_SHIPPING_INFO = new MealkitShippingInfoQuery(A_SHIPPING_LOCATION_ID, A_CASE_ID, A_MEALKIT_ID);
    private static final List<MealkitShippingInfoQuery> SOME_MEALKIT_SHIPPING_INFOS = List.of(A_MEALKIT_SHIPPING_INFO);
    private static final ShippingLocation A_SHIPPING_LOCATION = new ShippingLocation(A_SHIPPING_LOCATION_ID, "A_LOCATION_NAME", 10);
    private static final PickupLocation A_PICKUP_LOCATION = new PickupLocation(A_PICKUP_LOCATION_ID, "ANOTHER_LOCATION_NAME");
    private static final UniqueIdentifier A_SHIPPER_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier ANOTHER_SHIPPER_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_INVALID_SHIPPING_TICKET_ID = new UniqueIdentifier(UUID.randomUUID());

    @Mock
    private ShippingCatalog mockShippingCatalog;

    @Test
    public void givenValidValue_whenCreatingShippingTicket_shouldCreateNewShippingTicket() {
        Shipping shipping = new ShippingFixture().withShippingCatalog(
            new ShippingCatalogFixture().addPickupLocation(A_PICKUP_LOCATION).addShippingLocation(A_SHIPPING_LOCATION).build()).build();

        shipping.createShippingTicket(A_PICKUP_LOCATION_ID, SOME_MEALKIT_SHIPPING_INFOS);

        assertEquals(1, shipping.getShippingTickets().size());
    }

    @Test
    public void whenCreatingShippingTicket_shouldGetPickupLocation() {
        Shipping shipping = new ShippingFixture().withShippingCatalog(mockShippingCatalog).build();

        shipping.createShippingTicket(A_PICKUP_LOCATION_ID, SOME_MEALKIT_SHIPPING_INFOS);

        verify(mockShippingCatalog).getPickupLocation(A_PICKUP_LOCATION_ID);
    }

    @Test
    public void whenCreatingShippingTicket_shouldGetShippingLocation() {
        Shipping shipping = new ShippingFixture().withShippingCatalog(mockShippingCatalog).build();

        shipping.createShippingTicket(A_PICKUP_LOCATION_ID, SOME_MEALKIT_SHIPPING_INFOS);

        verify(mockShippingCatalog).getShippingLocation(A_SHIPPING_LOCATION_ID);
    }

    @Test
    public void whenGetShippingLocation_shouldReturnShippingLocations() {
        Shipping shipping = new ShippingFixture().withShippingCatalog(new ShippingCatalogFixture().addShippingLocation(A_SHIPPING_LOCATION).build()).build();

        List<ShippingLocation> shippingLocations = shipping.getShippingLocations();

        assertEquals(1, shippingLocations.size());
        assertEquals(A_SHIPPING_LOCATION, shippingLocations.get(0));
    }

    @Test
    public void givenShipperId_whenPickupCargo_shouldSetShippingTicketShipperIdToRightValue() {
        Shipping shipping = new ShippingFixture().withShippingCatalog(
            new ShippingCatalogFixture().addPickupLocation(A_PICKUP_LOCATION).addShippingLocation(A_SHIPPING_LOCATION).build()).build();
        shipping.createShippingTicket(A_PICKUP_LOCATION_ID, SOME_MEALKIT_SHIPPING_INFOS);
        UniqueIdentifier ticketId = shipping.getShippingTickets().get(0).getTicketId();

        shipping.pickupCargo(A_SHIPPER_ID, ticketId);

        assertEquals(A_SHIPPER_ID, shipping.getShippingTickets().get(0).getShipperId());
    }

    @Test
    public void whenPickupCargo_shouldSetShippingStatusToInProgress() {
        Shipping shipping = new ShippingFixture().withShippingCatalog(
            new ShippingCatalogFixture().addPickupLocation(A_PICKUP_LOCATION).addShippingLocation(A_SHIPPING_LOCATION).build()).build();
        shipping.createShippingTicket(A_PICKUP_LOCATION_ID, SOME_MEALKIT_SHIPPING_INFOS);
        UniqueIdentifier ticketId = shipping.getShippingTickets().get(0).getTicketId();

        shipping.pickupCargo(A_SHIPPER_ID, ticketId);

        assertEquals(ShippingStatus.IN_PROGRESS, shipping.getShippingTickets().get(0).getMealkitShippingInfos().get(0).getStatus());
    }

    @Test
    public void givenInvalidShippingId_whenPickupCargo_shouldThrowsInvalidShippingIdException() {
        Shipping shipping = new ShippingFixture().build();

        assertThrows(InvalidShippingIdException.class, () -> shipping.pickupCargo(A_SHIPPER_ID, A_INVALID_SHIPPING_TICKET_ID));
    }

    @Test
    public void whenCancelShipping_shouldSetShippingStatusToPending() {
        Shipping shipping = new ShippingFixture().withShippingCatalog(
            new ShippingCatalogFixture().addPickupLocation(A_PICKUP_LOCATION).addShippingLocation(A_SHIPPING_LOCATION).build()).build();
        shipping.createShippingTicket(A_PICKUP_LOCATION_ID, SOME_MEALKIT_SHIPPING_INFOS);
        UniqueIdentifier ticketId = shipping.getShippingTickets().get(0).getTicketId();
        shipping.pickupCargo(A_SHIPPER_ID, ticketId);

        shipping.cancelShipping(A_SHIPPER_ID, ticketId);

        assertEquals(ShippingStatus.PENDING, shipping.getShippingTickets().get(0).getMealkitShippingInfos().get(0).getStatus());
    }

    @Test
    public void givenInvalidShippingId_whenCancelShipping_shouldThrowsInvalidShippingIdException() {
        Shipping shipping = new ShippingFixture().build();

        assertThrows(InvalidShippingIdException.class, () -> shipping.cancelShipping(A_SHIPPER_ID, A_INVALID_SHIPPING_TICKET_ID));
    }

    @Test
    public void givenInvalidShipperId_whenCancelShipping_shouldThrowsInvalidShippingIdException() {
        Shipping shipping = new ShippingFixture().withShippingCatalog(
            new ShippingCatalogFixture().addPickupLocation(A_PICKUP_LOCATION).addShippingLocation(A_SHIPPING_LOCATION).build()).build();
        shipping.createShippingTicket(A_PICKUP_LOCATION_ID, SOME_MEALKIT_SHIPPING_INFOS);
        UniqueIdentifier ticketId = shipping.getShippingTickets().get(0).getTicketId();
        shipping.pickupCargo(A_SHIPPER_ID, ticketId);

        assertThrows(InvalidShipperException.class, () -> shipping.cancelShipping(ANOTHER_SHIPPER_ID, ticketId));
    }
}
