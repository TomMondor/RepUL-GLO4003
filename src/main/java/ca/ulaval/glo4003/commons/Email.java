package ca.ulaval.glo4003.commons;

import java.util.regex.Pattern;

import ca.ulaval.glo4003.commons.exception.InvalidEmailException;

public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    public Email {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException();
        }
    }
}
