package ca.ulaval.glo4003.identitymanagement.small.infrastructure;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Password;
import ca.ulaval.glo4003.identitymanagement.domain.User;
import ca.ulaval.glo4003.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.identitymanagement.domain.exception.UserNotFoundException;
import ca.ulaval.glo4003.identitymanagement.infrastructure.InMemoryUserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryUserRepositoryTest {
    private static final Email AN_INVALID_EMAIL = new Email("sss@hot.lo");
    private static final UniqueIdentifier A_UID = new UniqueIdentifier(UUID.randomUUID());
    private static final Email AN_EMAIL = new Email("anEmail@ulava.ca");
    private static final Password AN_ENCRYPTED_PASSWORD = new Password("encryptedPassword");
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        this.userRepository = new InMemoryUserRepository();
    }

    @Test
    public void givenInexistentUser_whenGettingUser_shouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> this.userRepository.findByEmail(AN_INVALID_EMAIL));
    }

    @Test
    public void givenExistingUser_whenGettingUser_shouldReturnOptionalOfUser() {
        User existingUser = givenExistingUser();
        userRepository.saveOrUpdate(existingUser);

        User user = this.userRepository.findByEmail(AN_EMAIL);

        assertEquals(existingUser, user);
    }

    @Test
    public void givenInexistantUser_whenCheckingIfUserExists_shouldReturnFalse() {
        boolean userExists = this.userRepository.exists(AN_INVALID_EMAIL);

        assertFalse(userExists);
    }

    @Test
    public void givenExistingUser_whenCheckingIfUserExists_shouldReturnTrue() {
        User existingUser = givenExistingUser();
        userRepository.saveOrUpdate(existingUser);

        boolean userExists = this.userRepository.exists(AN_EMAIL);

        assertTrue(userExists);
    }

    @Test
    public void whenSavingUser_shouldSaveUser() {
        User userToSave = givenExistingUser();

        userRepository.saveOrUpdate(userToSave);

        assertEquals(userToSave, this.userRepository.findByEmail(AN_EMAIL));
    }

    private User givenExistingUser() {
        User user = new User(A_UID, AN_EMAIL, AN_ENCRYPTED_PASSWORD);
        return user;
    }
}
