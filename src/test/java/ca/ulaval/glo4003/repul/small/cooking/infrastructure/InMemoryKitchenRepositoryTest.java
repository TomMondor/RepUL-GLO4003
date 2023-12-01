package ca.ulaval.glo4003.repul.small.cooking.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.exception.KitchenNotFoundException;
import ca.ulaval.glo4003.repul.cooking.infrastructure.InMemoryKitchenRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InMemoryKitchenRepositoryTest {
    private InMemoryKitchenRepository inMemorykitchenRepository;

    @Mock
    private Kitchen kitchen;

    @BeforeEach
    public void createKitchenRepository() {
        this.inMemorykitchenRepository = new InMemoryKitchenRepository();
    }

    @Test
    public void whenSavingKitchenAndGettingKitchen_shouldReturnTheRightKitchen() {
        inMemorykitchenRepository.save(kitchen);
        Kitchen kitchenFound = inMemorykitchenRepository.get();

        assertEquals(kitchen, kitchenFound);
    }

    @Test
    public void givenNoKitchen_whenGettingKitchen_shouldThrowKitchenNotFoundException() {
        assertThrows(KitchenNotFoundException.class, () -> inMemorykitchenRepository.get());
    }
}
