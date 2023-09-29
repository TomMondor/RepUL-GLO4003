package ca.ulaval.glo4003.identitymanagement.domain.token;

public interface TokenDecoder {
    DecodedToken decode(String token);
}
