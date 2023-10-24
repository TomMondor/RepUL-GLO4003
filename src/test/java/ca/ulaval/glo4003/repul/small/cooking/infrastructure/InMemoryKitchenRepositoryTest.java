package ca.ulaval.glo4003.repul.small.cooking.infrastructure;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;
import ca.ulaval.glo4003.repul.cooking.infrastructure.InMemoryKitchenRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InMemoryKitchenRepositoryTest {
    private KitchenRepository kitchenRepository;

    @Mock
    private Kitchen kitchen;

    @BeforeEach
    public void createKitchenRepository() {
        this.kitchenRepository = new InMemoryKitchenRepository();
    }

    @Test
    public void whenSaveOrUpdate_shouldSaveOrUpdate() {
        kitchenRepository.saveOrUpdate(kitchen);

        assertEquals(Optional.of(kitchen), kitchenRepository.get());
    }

    @Test
    public void whenGetWithKitchen_shouldReturnKitchen() {
        kitchenRepository.saveOrUpdate(kitchen);

        assertEquals(Optional.of(kitchen), kitchenRepository.get());
    }
}
