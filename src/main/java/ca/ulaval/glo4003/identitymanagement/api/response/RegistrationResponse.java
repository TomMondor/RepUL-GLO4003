package ca.ulaval.glo4003.identitymanagement.api.response;

public record RegistrationResponse(String token, int expiresIn) {
}
