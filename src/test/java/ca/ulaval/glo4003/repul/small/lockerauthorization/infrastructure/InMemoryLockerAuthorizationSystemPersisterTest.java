package ca.ulaval.glo4003.repul.small.lockerauthorization.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.lockerauthorization.application.exception.LockerAuthorizationSystemNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.infrastructure.InMemoryLockerAuthorizationSystemPersister;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class InMemoryLockerAuthorizationSystemPersisterTest {
    @Mock
    private LockerAuthorizationSystem lockerAuthorizationSystem;
    private InMemoryLockerAuthorizationSystemPersister inMemoryLockerAuthorizationSystemPersister;

    @BeforeEach
    public void createRepo() {
        inMemoryLockerAuthorizationSystemPersister = new InMemoryLockerAuthorizationSystemPersister();
    }

    @Test
    public void whenSavingAndGettingLockerAuthorizationSystem_shouldReturnTheLockerAuthorizationSystem() {
        inMemoryLockerAuthorizationSystemPersister.save(lockerAuthorizationSystem);
        LockerAuthorizationSystem lockerAuthorizationSystemFound = inMemoryLockerAuthorizationSystemPersister.get();

        assertEquals(lockerAuthorizationSystem, lockerAuthorizationSystemFound);
    }

    @Test
    public void givenNoLockerAuthorizationSystem_whenGettingLockerAuthorizationSystem_shouldThrowLockerAuthorizationSystemNotFoundException() {
        assertThrows(LockerAuthorizationSystemNotFoundException.class, () -> inMemoryLockerAuthorizationSystemPersister.get());
    }
}
