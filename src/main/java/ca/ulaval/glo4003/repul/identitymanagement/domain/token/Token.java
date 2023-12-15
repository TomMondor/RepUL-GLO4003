package ca.ulaval.glo4003.repul.identitymanagement.domain.token;

import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.InvalidTokenException;

public record Token(
    String value,
    int expiresIn
) {
    public Token {
        if (value.isBlank() || expiresIn < 0) {
            throw new InvalidTokenException();
        }
    }
}
