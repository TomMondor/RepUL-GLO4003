package ca.ulaval.glo4003.repul.small.application;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.application.catalog.CatalogService;
import ca.ulaval.glo4003.repul.application.catalog.payload.LocationsPayload;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CatalogServiceTest {
    private static final List<PickupLocation> SOME_PICKUP_LOCATIONS = List.of();
    private CatalogService catalogService;
    @Mock
    private RepULRepository repULRepository;
    @Mock
    private RepUL mockRepUL;

    @BeforeEach
    public void createCatalogService() {
        catalogService = new CatalogService(repULRepository);

        when(repULRepository.get()).thenReturn(mockRepUL);
    }

    @Test
    public void whenGettingPickupLocations_shouldGetRepUL() {
        when(mockRepUL.getPickupLocations()).thenReturn(SOME_PICKUP_LOCATIONS);

        catalogService.getPickupLocations();

        verify(repULRepository).get();
    }

    @Test
    public void whenGettingPickupLocations_shouldGetPickupLocations() {
        when(mockRepUL.getPickupLocations()).thenReturn(SOME_PICKUP_LOCATIONS);

        catalogService.getPickupLocations();

        verify(mockRepUL).getPickupLocations();
    }

    @Test
    public void whenGettingPickupLocations_shouldReturnMatchingLocationsPayload() {
        when(mockRepUL.getPickupLocations()).thenReturn(SOME_PICKUP_LOCATIONS);

        LocationsPayload locationsPayload = catalogService.getPickupLocations();

        assertEquals(SOME_PICKUP_LOCATIONS, locationsPayload.pickupLocations());
    }
}
