package ca.ulaval.glo4003.repul.small.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.Email;
import ca.ulaval.glo4003.repul.infrastructure.InMemoryUserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryUserRepositoryTest {
    private static final Email AN_EMAIL = new Email("steve@gmail.lo");
    private static final Email AN_INVALID_EMAIL = new Email("sss@hot.lo");
    private static final String A_PASSWORD = "a@*nfF8KA1";
    private InMemoryUserRepository inMemoryUserRepository;

    @BeforeEach
    public void setup() {
        this.inMemoryUserRepository = new InMemoryUserRepository();
    }

    @Test
    public void givenAUser_whenGettingUserExistence_shouldReturnTrue() {
        this.inMemoryUserRepository.addUser(AN_EMAIL, A_PASSWORD);

        assertTrue(this.inMemoryUserRepository.doesUserExist(AN_EMAIL));
    }

    @Test
    public void givenAnInvalidEmail_whenGettingUserExistence_shouldReturnFalse() {
        this.inMemoryUserRepository.addUser(AN_EMAIL, A_PASSWORD);

        assertFalse(this.inMemoryUserRepository.doesUserExist(AN_INVALID_EMAIL));
    }

    @Test
    public void givenValidEmail_whenGettingUsersPassword_shouldReturnPassword() {
        this.inMemoryUserRepository.addUser(AN_EMAIL, A_PASSWORD);

        assertEquals(A_PASSWORD, this.inMemoryUserRepository.getUserPassword(AN_EMAIL));
    }

    @Test
    public void givenInvalidEmail_whenGettingUsersPassword_shouldReturnNull() {
        this.inMemoryUserRepository.addUser(AN_EMAIL, A_PASSWORD);

        assertNull(this.inMemoryUserRepository.getUserPassword(AN_INVALID_EMAIL));
    }
}
