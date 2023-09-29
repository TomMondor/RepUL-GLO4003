package ca.ulaval.glo4003.identitymanagement.infrastructure;

import java.util.UUID;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Role;
import ca.ulaval.glo4003.identitymanagement.domain.exception.TokenVerificationFailedException;
import ca.ulaval.glo4003.identitymanagement.domain.token.DecodedToken;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenDecoder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTTokenDecoder implements TokenDecoder {
    private final Algorithm algorithm = Algorithm.HMAC256("secret");

    @Override
    public DecodedToken decode(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            UniqueIdentifier uniqueIdentifier = new UniqueIdentifier(UUID.fromString(decodedJWT.getClaim("UID").asString()));
            Role role = Role.valueOf(decodedJWT.getClaim("Role").asString());

            return new DecodedToken(uniqueIdentifier, role);
        } catch (Exception e) {
            throw new TokenVerificationFailedException();
        }
    }
}
