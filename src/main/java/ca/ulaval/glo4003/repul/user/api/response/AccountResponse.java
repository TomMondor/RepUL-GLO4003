package ca.ulaval.glo4003.repul.user.api.response;

public record AccountResponse(
    String name,
    String birthdate,
    String gender,
    int age,
    String idul,
    String email
) {
}
