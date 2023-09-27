package ca.ulaval.glo4003.repul.small.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.exception.RepULNotFoundException;
import ca.ulaval.glo4003.repul.infrastructure.InMemoryRepULRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InMemoryRepULRepositoryTest {
    @Mock
    private RepUL aRepUL;

    private RepULRepository repULRepository;

    @BeforeEach
    public void createRepULRepository() {
        repULRepository = new InMemoryRepULRepository();
    }

    @Test
    public void givenValidRepUL_whenSaveOrUpdate_shouldSave() {
        repULRepository.saveOrUpdate(aRepUL);

        assertEquals(aRepUL, repULRepository.get());
    }

    @Test
    public void givenExistingRepUL_whenGettingRepUL_shouldReturnRepUL() {
        repULRepository.saveOrUpdate(aRepUL);

        RepUL repUL = repULRepository.get();

        assertEquals(aRepUL, repUL);
    }

    @Test
    public void givenNoRepUL_whenGettingRepUL_shouldThrowRepULNotFoundException() {
        assertThrows(RepULNotFoundException.class, () -> repULRepository.get());
    }
}
