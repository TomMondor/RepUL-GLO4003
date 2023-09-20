package ca.ulaval.glo4003.repul.small.infrastructure;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.Exception.RepULNotFoundException;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.infrastructure.InMemoryRepULRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryRepULRepositoryTest {
    private static final RepUL A_REPUL = new RepUL(new Catalog(new ArrayList<>()));

    private RepULRepository repULRepository;

    @BeforeEach
    public void createRepULRepository() {
        repULRepository = new InMemoryRepULRepository();
    }

    @Test
    public void givenValidRepUL_whenSaveOrUpdate_shouldSave() {
        repULRepository.saveOrUpdate(A_REPUL);

        assertEquals(A_REPUL, repULRepository.get());
    }

    @Test
    public void givenExistingRepUL_whenGettingRepUL_shouldReturnRepUL() {
        repULRepository.saveOrUpdate(A_REPUL);

        RepUL repUL = repULRepository.get();

        assertEquals(A_REPUL, repUL);
    }

    @Test
    public void givenNoRepUL_whenGettingRepUL_shouldThrowRepULNotFoundException() {
        assertThrows(RepULNotFoundException.class, () -> repULRepository.get());
    }
}
