package ca.ulaval.glo4003.commons.domain;

import java.util.regex.Pattern;

import ca.ulaval.glo4003.commons.domain.exception.InvalidEmailException;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@ulaval\\.ca$");
    public Email {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException();
        }
    }
}
