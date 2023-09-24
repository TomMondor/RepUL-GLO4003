package ca.ulaval.glo4003.identitymanagement.application.request;

import ca.ulaval.glo4003.commons.Email;
import ca.ulaval.glo4003.identitymanagement.domain.Password;

public record RegistrationRequest(Email email, Password password) {
    public RegistrationRequest(String email, String password) {
        this(new Email(email), new Password(password));
    }

    public static RegistrationRequest from(String email, String password) {
        return new RegistrationRequest(email, password);
    }
}
