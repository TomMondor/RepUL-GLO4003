package ca.ulaval.glo4003.repul.identitymanagement.domain.token;

public interface TokenDecoder {
    DecodedToken decode(String token);
}
