package ca.ulaval.glo4003.repul.small.delivery.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.delivery.application.exception.DeliverySystemNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.DeliverySystem;
import ca.ulaval.glo4003.repul.delivery.infrastructure.InMemoryDeliverySystemRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InMemoryDeliverySystemRepositoryTest {
    private InMemoryDeliverySystemRepository inMemoryDeliverySystemRepository;

    @Mock
    private DeliverySystem deliverySystem;

    @BeforeEach
    public void createDeliverySystemRepository() {
        inMemoryDeliverySystemRepository = new InMemoryDeliverySystemRepository();
    }

    @Test
    public void whenSavingDeliverySystemAndGettingDeliverySystem_shouldReturnTheRightDeliverySystem() {
        inMemoryDeliverySystemRepository.save(deliverySystem);
        DeliverySystem deliverySystemFound = inMemoryDeliverySystemRepository.get();

        assertEquals(deliverySystem, deliverySystemFound);
    }

    @Test
    public void givenNoDeliverySystem_whenGettingDeliverySystem_shouldThrowDeliverySystemNotFoundException() {
        assertThrows(DeliverySystemNotFoundException.class, () -> inMemoryDeliverySystemRepository.get());
    }
}
