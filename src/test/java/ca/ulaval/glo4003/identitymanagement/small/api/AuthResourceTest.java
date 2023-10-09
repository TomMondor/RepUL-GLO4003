package ca.ulaval.glo4003.identitymanagement.small.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.identitymanagement.api.AuthResource;
import ca.ulaval.glo4003.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.identitymanagement.application.AuthService;
import ca.ulaval.glo4003.identitymanagement.application.query.LoginQuery;
import ca.ulaval.glo4003.identitymanagement.domain.token.Token;
import ca.ulaval.glo4003.repul.fixture.LoginRequestFixture;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthResourceTest {
    private static final LoginRequest A_LOGIN_REQUEST = new LoginRequestFixture().build();
    private static final Token A_TOKEN = new Token("aToken", 3600);
    private AuthResource authResource;

    @Mock
    private AuthService authService;

    @BeforeEach
    public void createAuthResource() {
        authResource = new AuthResource(authService);
    }

    @Test
    public void whenLogin_shouldLogin() {
        LoginQuery loginQuery = LoginQuery.from(A_LOGIN_REQUEST.email, A_LOGIN_REQUEST.password);
        given(authService.login(loginQuery)).willReturn(A_TOKEN);

        authResource.login(A_LOGIN_REQUEST);

        verify(authService).login(loginQuery);
    }

    @Test
    public void whenLogin_shouldReturn200() {
        given(authService.login(any())).willReturn(A_TOKEN);

        Response response = authResource.login(A_LOGIN_REQUEST);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenLogin_shouldReturnTokenAndExpiresIn() {
        given(authService.login(any())).willReturn(A_TOKEN);

        Response response = authResource.login(A_LOGIN_REQUEST);
        LoginResponse loginResponse = (LoginResponse) response.getEntity();

        assertEquals(A_TOKEN.value(), loginResponse.token());
        assertEquals(A_TOKEN.expiresIn(), loginResponse.expiresIn());
    }
}
