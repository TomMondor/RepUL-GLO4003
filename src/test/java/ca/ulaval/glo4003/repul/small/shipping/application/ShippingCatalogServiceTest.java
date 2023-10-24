package ca.ulaval.glo4003.repul.small.shipping.application;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.shipping.application.ShippingCatalogService;
import ca.ulaval.glo4003.repul.shipping.application.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.shipping.domain.DeliveryLocation;
import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.domain.ShippingRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingCatalogServiceTest {
    private static final List<DeliveryLocation> SOME_SHIPPING_LOCATIONS = List.of();
    private ShippingCatalogService shippingCatalogService;
    @Mock
    private ShippingRepository shippingRepository;
    @Mock
    private Shipping mockShipping;

    @BeforeEach
    public void createShippingCatalogService() {
        shippingCatalogService = new ShippingCatalogService(shippingRepository);

        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
    }

    @Test
    public void whenGettingPickupLocations_shouldGetShipping() {
        when(mockShipping.getShippingLocations()).thenReturn(SOME_SHIPPING_LOCATIONS);

        shippingCatalogService.getShippingLocations();

        verify(shippingRepository).get();
    }

    @Test
    public void whenGettingPickupLocations_shouldGetShippingLocations() {
        when(mockShipping.getShippingLocations()).thenReturn(SOME_SHIPPING_LOCATIONS);

        shippingCatalogService.getShippingLocations();

        verify(mockShipping).getShippingLocations();
    }

    @Test
    public void whenGettingPickupLocations_shouldReturnMatchingLocationsPayload() {
        when(mockShipping.getShippingLocations()).thenReturn(SOME_SHIPPING_LOCATIONS);

        LocationsPayload locationsPayload = shippingCatalogService.getShippingLocations();

        assertEquals(SOME_SHIPPING_LOCATIONS, locationsPayload.shippingLocations());
    }
}
