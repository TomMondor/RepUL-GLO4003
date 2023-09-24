package ca.ulaval.glo4003.repul.application.auth.parameter;

import ca.ulaval.glo4003.commons.Email;

public record LoginParams(Email email, String password) {
}
