package ca.ulaval.glo4003.identitymanagement.middleware;

import java.io.IOException;
import java.util.Map;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenDecoder;
import ca.ulaval.glo4003.identitymanagement.middleware.exception.MissingAuthorizationHeaderException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

@Secure
@Priority(Priorities.AUTHENTICATION)
public class AuthGuard implements ContainerRequestFilter {
    private final TokenDecoder tokenDecoder;

    public AuthGuard(TokenDecoder tokenDecoder) {
        this.tokenDecoder = tokenDecoder;
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            throw new MissingAuthorizationHeaderException();
        }

        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            UniqueIdentifier uid = tokenDecoder.decode(token);

            containerRequestContext.setProperty("uid", uid);
        } catch (Exception e) {
            Response response = Response.status(Response.Status.UNAUTHORIZED).entity(Map.of("message", "Could not validate identity.")).build();
            containerRequestContext.abortWith(response);
        }
    }
}
