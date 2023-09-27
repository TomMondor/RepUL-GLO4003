package ca.ulaval.glo4003.identitymanagement.small.domain;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Password;
import ca.ulaval.glo4003.identitymanagement.domain.PasswordEncoder;
import ca.ulaval.glo4003.identitymanagement.domain.User;
import ca.ulaval.glo4003.identitymanagement.domain.exception.PasswordNotMatchingException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    private static final UniqueIdentifier A_UID = new UniqueIdentifier(UUID.randomUUID());
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final Password AN_ENCRYPTED_PASSWORD = new Password("encryptedPassword");
    private static final Password A_WRONG_PASSWORD = new Password("a@*nfF8KA1");

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void givenIncorrectCorrectPassword_whenCheckingPassword_shouldThrowPasswordNotMatchingException() {
        given(passwordEncoder.matches(A_WRONG_PASSWORD, AN_ENCRYPTED_PASSWORD)).willReturn(false);
        User user = new User(A_UID, AN_EMAIL, AN_ENCRYPTED_PASSWORD, passwordEncoder);

        assertThrows(PasswordNotMatchingException.class, () -> user.checkPasswordMatches(A_WRONG_PASSWORD));
    }
}
