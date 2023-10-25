package ca.ulaval.glo4003.repul.small.delivery.infrastructure;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.infrastructure.InMemoryDeliverySystemRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InMemoryDeliverySystemRepositoryTest {
    private InMemoryDeliverySystemRepository deliveryRepository;

    @Mock
    private DeliverySystem deliverySystem;

    @BeforeEach
    public void createDeliverySystemRepository() {
        deliveryRepository = new InMemoryDeliverySystemRepository();
    }

    @Test
    public void givenValidDeliverySystem_whenSaveOrUpdate_shouldSaveInRepository() {
        deliveryRepository.saveOrUpdate(deliverySystem);

        assertEquals(deliverySystem, deliveryRepository.get().get());
    }

    @Test
    public void givenNoDeliverySystem_whenGetting_shouldReturnEmptyOptional() {
        assertEquals(Optional.empty(), deliveryRepository.get());
    }

    @Test
    public void givenExistingDeliverySystem_whenGet_shouldReturnRightDeliverySystem() {
        deliveryRepository.saveOrUpdate(deliverySystem);

        assertEquals(deliverySystem, deliveryRepository.get().get());
    }
}
