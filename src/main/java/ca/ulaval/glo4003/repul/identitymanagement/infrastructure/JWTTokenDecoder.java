package ca.ulaval.glo4003.repul.identitymanagement.infrastructure;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Role;
import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.TokenVerificationFailedException;
import ca.ulaval.glo4003.repul.identitymanagement.domain.token.DecodedToken;
import ca.ulaval.glo4003.repul.identitymanagement.domain.token.TokenDecoder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTTokenDecoder implements TokenDecoder {
    private Algorithm algorithm;

    public JWTTokenDecoder(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public DecodedToken decode(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();

        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            Role role = Role.valueOf(decodedJWT.getClaim("Role").asString());
            UniqueIdentifier uniqueIdentifier = new UniqueIdentifierFactory<>(UniqueIdentifier.class).generateFrom(decodedJWT.getClaim("UID").asString());
            return new DecodedToken(uniqueIdentifier, role);
        } catch (Exception e) {
            throw new TokenVerificationFailedException();
        }
    }
}
