package ca.ulaval.glo4003.identitymanagement.application.query;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.identitymanagement.domain.Password;

public record RegistrationQuery(Email email, Password password) {
    public static RegistrationQuery from(String email, String password) {
        return new RegistrationQuery(new Email(email), new Password(password));
    }
}
