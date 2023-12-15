package ca.ulaval.glo4003.repul.identitymanagement.api.response;

public record LoginResponse(
    String token,
    int expiresIn
) {
}
