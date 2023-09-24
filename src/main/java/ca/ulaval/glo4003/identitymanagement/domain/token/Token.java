package ca.ulaval.glo4003.identitymanagement.domain.token;

import ca.ulaval.glo4003.identitymanagement.domain.exception.InvalidTokenException;

public record Token(String value, int expiresIn) {
    public Token {
        if (value.isBlank() || expiresIn < 0) {
            throw new InvalidTokenException();
        }
    }
}
