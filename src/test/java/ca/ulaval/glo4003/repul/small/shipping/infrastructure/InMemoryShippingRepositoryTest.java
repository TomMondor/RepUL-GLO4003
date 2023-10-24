package ca.ulaval.glo4003.repul.small.shipping.infrastructure;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.shipping.domain.Shipping;
import ca.ulaval.glo4003.repul.shipping.infrastructure.InMemoryShippingRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InMemoryShippingRepositoryTest {
    private InMemoryShippingRepository shippingRepository;

    @Mock
    private Shipping shipping;

    @BeforeEach
    public void createShippingRepository() {
        shippingRepository = new InMemoryShippingRepository();
    }

    @Test
    public void givenValidShipping_whenSaveOrUpdate_shouldSaveInRepository() {
        shippingRepository.saveOrUpdate(shipping);

        assertEquals(shipping, shippingRepository.get().get());
    }

    @Test
    public void givenNoShipping_whenGetting_shouldReturnEmptyOptional() {
        assertEquals(Optional.empty(), shippingRepository.get());
    }

    @Test
    public void givenExistingShipping_whenGet_shouldReturnRightShipping() {
        shippingRepository.saveOrUpdate(shipping);

        assertEquals(shipping, shippingRepository.get().get());
    }
}
