package ca.ulaval.glo4003.shipping.small.application;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.shipping.application.ShippingService;
import ca.ulaval.glo4003.shipping.domain.Shipping;
import ca.ulaval.glo4003.shipping.domain.ShippingRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShippingServiceTest {
    private static final String A_SHIPPING_ID = UUID.randomUUID().toString();
    private static final String A_SHIPPER_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_SHIPPER_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_SHIPPER_ID);
    private static final UniqueIdentifier A_SHIPPING_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_SHIPPING_ID);

    private ShippingService shippingService;
    @Mock
    private ShippingRepository shippingRepository;
    @Mock
    private Shipping mockShipping;

    @BeforeEach
    public void createShippingService() {
        shippingService = new ShippingService(shippingRepository);

        when(shippingRepository.get()).thenReturn(Optional.of(mockShipping));
    }

    @Test
    public void whenPickupCargo_shouldGetShipping() {
        shippingService.pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(shippingRepository).get();
    }

    @Test
    public void whenPickupCargo_shouldSaveOrUpdateShipping() {
        shippingService.pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(shippingRepository).saveOrUpdate(any(Shipping.class));
    }

    @Test
    public void givenValidUniqueIdentifier_whenPickupCargo_shouldPickupCargoWithRightUniqueIdentifier() {
        shippingService.pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(mockShipping).pickupCargo(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenCancelShipping_shouldGetShipping() {
        shippingService.cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(shippingRepository).get();
    }

    @Test
    public void whenCancelShipping_shouldSaveOrUpdateShipping() {
        shippingService.cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(shippingRepository).saveOrUpdate(any(Shipping.class));
    }

    @Test
    public void givenValidUniqueIdentifier_whenCancelShipping_shouldPickupCargoWithRightUniqueIdentifier() {
        shippingService.cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);

        verify(mockShipping).cancelShipping(A_SHIPPER_UNIQUE_IDENTIFIER, A_SHIPPING_UNIQUE_IDENTIFIER);
    }
}
