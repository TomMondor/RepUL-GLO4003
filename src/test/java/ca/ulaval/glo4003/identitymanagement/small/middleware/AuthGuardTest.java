package ca.ulaval.glo4003.identitymanagement.small.middleware;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.exception.TokenVerificationFailedException;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenDecoder;
import ca.ulaval.glo4003.identitymanagement.middleware.AuthGuard;
import ca.ulaval.glo4003.identitymanagement.middleware.exception.MissingAuthorizationHeaderException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthGuardTest {
    private static final UniqueIdentifier A_UID = new UniqueIdentifier(UUID.randomUUID());
    private static final String A_TOKEN = "valid-token-yolo";
    private static final String A_NOT_BEARER_AUTHORIZATION_HEADER = "Ours uehriwhriehwiuhriwhr";
    private static final String A_VALID_BEARER_AUTHORIZATION_HEADER = "Bearer " + A_TOKEN;
    private static final String AN_INVALID_BEARER_AUTHORIZATION_HEADER = "Bearer " + A_TOKEN + "-invalid";

    private AuthGuard authGuard;

    @Mock
    private TokenDecoder tokenDecoder;

    @Mock
    private ContainerRequestContext containerRequestContext;

    @BeforeEach
    public void createAuthGuard() {
        authGuard = new AuthGuard(tokenDecoder);
    }

    @Test
    public void givenNoAuthorizationHeader_whenFilter_shouldThrowMissingAuthorizationHeaderException() {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(null);

        assertThrows(MissingAuthorizationHeaderException.class, () -> {
            authGuard.filter(containerRequestContext);
        });
    }

    @Test
    public void givenAuthorizationHeaderWithoutBearer_whenFilter_shouldThrowMissingAuthorizationHeaderException() {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_NOT_BEARER_AUTHORIZATION_HEADER);

        assertThrows(MissingAuthorizationHeaderException.class, () -> {
            authGuard.filter(containerRequestContext);
        });
    }

    @Test
    public void givenBearerToken_whenFilter_shouldDecodeToken() throws IOException {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_VALID_BEARER_AUTHORIZATION_HEADER);
        given(tokenDecoder.decode(A_TOKEN)).willReturn(A_UID);

        authGuard.filter(containerRequestContext);

        verify(tokenDecoder).decode(A_TOKEN);
    }

    @Test
    public void givenInvalidBearerToken_whenFilter_shouldAbortRequest() throws IOException {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(AN_INVALID_BEARER_AUTHORIZATION_HEADER);
        given(tokenDecoder.decode(A_TOKEN)).willThrow(new TokenVerificationFailedException());

        authGuard.filter(containerRequestContext);

        verify(containerRequestContext).abortWith(any());
    }

    @Test
    public void givenBearerToken_whenFilter_shouldPutUIDInContext() throws IOException {
        given(containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION)).willReturn(A_VALID_BEARER_AUTHORIZATION_HEADER);
        given(tokenDecoder.decode(A_TOKEN)).willReturn(A_UID);

        authGuard.filter(containerRequestContext);

        verify(containerRequestContext).setProperty("uid", A_UID);
    }
}
