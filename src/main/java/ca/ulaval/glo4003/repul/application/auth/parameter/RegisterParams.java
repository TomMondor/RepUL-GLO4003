package ca.ulaval.glo4003.repul.application.auth.parameter;

import ca.ulaval.glo4003.repul.domain.account.Email;

public record RegisterParams(Email email, String password) {
}