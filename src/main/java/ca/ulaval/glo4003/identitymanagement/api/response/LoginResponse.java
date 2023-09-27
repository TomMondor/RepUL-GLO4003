package ca.ulaval.glo4003.identitymanagement.api.response;

public record LoginResponse(String token, int expiresIn) {
}
