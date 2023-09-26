package ca.ulaval.glo4003.identitymanagement.infrastructure;

import java.util.Date;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.token.Token;
import ca.ulaval.glo4003.identitymanagement.domain.token.TokenGenerator;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

public class JWTTokenGenerator implements TokenGenerator {
    private static final int EXPIRE_TIME_IN_SECONDS = 3600;
    private final Algorithm algorithm = Algorithm.HMAC256("secret");

    @Override
    public Token generate(UniqueIdentifier uid) {
        long sixtyMinutesFromNow = System.currentTimeMillis() + EXPIRE_TIME_IN_SECONDS * 1000;
        Date expireTime = new Date(sixtyMinutesFromNow);

        return new Token(createJWT(uid, expireTime), EXPIRE_TIME_IN_SECONDS);
    }

    private String createJWT(UniqueIdentifier uid, Date expirationTime) {
        return JWT.create().withClaim("UID", uid.value().toString()).withExpiresAt(expirationTime).sign(algorithm);
    }
}
