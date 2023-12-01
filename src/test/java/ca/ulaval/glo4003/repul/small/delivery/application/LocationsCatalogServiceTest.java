package ca.ulaval.glo4003.repul.small.delivery.application;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.application.LocationsCatalogService;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;
import ca.ulaval.glo4003.repul.delivery.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystemRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationsCatalogServiceTest {
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Pouliot");
    private static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, "Pouliot", 10);
    private static final List<DeliveryLocation> SOME_DELIVERY_LOCATIONS = List.of(A_DELIVERY_LOCATION);
    private LocationsCatalogService locationsCatalogService;
    @Mock
    private DeliverySystemRepository deliverySystemRepository;
    @Mock
    private DeliverySystem mockDeliverySystem;

    @BeforeEach
    public void createLocationsCatalogService() {
        locationsCatalogService = new LocationsCatalogService(deliverySystemRepository);

        when(deliverySystemRepository.get()).thenReturn(mockDeliverySystem);
    }

    @Test
    public void whenGettingPickupLocations_shouldGetDeliverySystem() {
        when(mockDeliverySystem.getDeliveryLocations()).thenReturn(SOME_DELIVERY_LOCATIONS);

        locationsCatalogService.getDeliveryLocations();

        verify(deliverySystemRepository).get();
    }

    @Test
    public void whenGettingPickupLocations_shouldGetDeliveryLocations() {
        when(mockDeliverySystem.getDeliveryLocations()).thenReturn(SOME_DELIVERY_LOCATIONS);

        locationsCatalogService.getDeliveryLocations();

        verify(mockDeliverySystem).getDeliveryLocations();
    }

    @Test
    public void whenGettingPickupLocations_shouldReturnMatchingLocationsPayload() {
        when(mockDeliverySystem.getDeliveryLocations()).thenReturn(SOME_DELIVERY_LOCATIONS);
        DeliveryLocationsPayload expectedDeliveryLocationPayload = new DeliveryLocationsPayload(List.of(
            new DeliveryLocationPayload(A_DELIVERY_LOCATION.getLocationId(), A_DELIVERY_LOCATION.getName(), A_DELIVERY_LOCATION.getTotalCapacity(),
                A_DELIVERY_LOCATION.getRemainingCapacity())));

        DeliveryLocationsPayload deliveryLocationsPayload = locationsCatalogService.getDeliveryLocations();

        assertEquals(expectedDeliveryLocationPayload, deliveryLocationsPayload);
    }
}
