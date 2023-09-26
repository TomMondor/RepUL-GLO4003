package ca.ulaval.glo4003.identitymanagement.small.domain;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Password;
import ca.ulaval.glo4003.identitymanagement.domain.PasswordEncoder;
import ca.ulaval.glo4003.identitymanagement.domain.UserFactory;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserFactoryTest {
    private static final UniqueIdentifier A_UID = new UniqueIdentifier(UUID.randomUUID());
    private static final Email AN_EMAIL = new Email("anEmail@ulava.ca");
    private static final Password A_PASSWORD = new Password("a@*nfF8KA1");
    private static final Password AN_ENCRYPTED_PASSWORD = new Password("encryptedPassword");

    private UserFactory userFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userFactory = new UserFactory(passwordEncoder);
    }

    @Test
    public void whenCreatingUser_shouldEncryptPassword() {
        given(passwordEncoder.encode(A_PASSWORD)).willReturn(AN_ENCRYPTED_PASSWORD);

        userFactory.createUser(A_UID, AN_EMAIL, A_PASSWORD);

        verify(passwordEncoder).encode(A_PASSWORD);
    }
}
