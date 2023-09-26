package ca.ulaval.glo4003.identitymanagement.small.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.identitymanagement.api.AuthResource;
import ca.ulaval.glo4003.identitymanagement.api.request.RegistrationRequest;
import ca.ulaval.glo4003.identitymanagement.application.AuthService;
import ca.ulaval.glo4003.identitymanagement.application.query.RegistrationQuery;
import ca.ulaval.glo4003.identitymanagement.domain.token.Token;
import ca.ulaval.glo4003.identitymanagement.fixture.RegistrationRequestFixture;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthResourceTest {
    private static final RegistrationRequest A_REGISTRATION_REQUEST = new RegistrationRequestFixture().build();
    private static final Token A_TOKEN = new Token("aToken", 3600);
    private AuthResource authResource;

    @Mock
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        authResource = new AuthResource(authService);
    }

    @Test
    public void whenRegistering_shouldRegisterUser() {
        RegistrationQuery registrationQuery = RegistrationQuery.from(A_REGISTRATION_REQUEST.email, A_REGISTRATION_REQUEST.password);
        given(authService.register(registrationQuery)).willReturn(A_TOKEN);

        authResource.register(A_REGISTRATION_REQUEST);

        verify(authService).register(registrationQuery);
    }

    @Test
    public void whenRegistering_shouldReturn201() {
        given(authService.register(any())).willReturn(A_TOKEN);

        Response response = authResource.register(A_REGISTRATION_REQUEST);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }
}
