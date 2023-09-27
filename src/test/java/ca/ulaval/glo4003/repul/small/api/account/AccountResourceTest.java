package ca.ulaval.glo4003.repul.small.api.account;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.api.account.AccountResource;
import ca.ulaval.glo4003.repul.api.account.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.application.account.AccountService;
import ca.ulaval.glo4003.repul.application.account.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.fixture.RegistrationRequestFixture;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountResourceTest {
    private static final RegistrationRequest A_REGISTRATION_REQUEST = new RegistrationRequestFixture().build();
    private static final UniqueIdentifier A_UID = new UniqueIdentifier(UUID.randomUUID());

    private AccountResource accountResource;

    @Mock
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        accountResource = new AccountResource(accountService);
    }

    @Test
    public void whenRegistering_shouldRegisterUser() {
        RegistrationQuery registrationQuery =
            RegistrationQuery.from(A_REGISTRATION_REQUEST.idul, A_REGISTRATION_REQUEST.email, A_REGISTRATION_REQUEST.password, A_REGISTRATION_REQUEST.name,
                A_REGISTRATION_REQUEST.birthdate, A_REGISTRATION_REQUEST.gender);
        accountResource.register(A_REGISTRATION_REQUEST);

        verify(accountService).register(registrationQuery);
    }

    @Test
    public void whenRegistering_shouldReturn201() {
        Response response = accountResource.register(A_REGISTRATION_REQUEST);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }
}
