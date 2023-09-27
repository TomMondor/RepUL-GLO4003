package ca.ulaval.glo4003.identitymanagement.application.query;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.identitymanagement.domain.Password;

public record LoginQuery(Email email, Password password) {
    public static LoginQuery from(String email, String password) {
        return new LoginQuery(new Email(email), new Password(password));
    }
}
