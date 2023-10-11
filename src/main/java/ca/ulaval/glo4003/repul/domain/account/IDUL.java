package ca.ulaval.glo4003.repul.domain.account;

import java.util.regex.Pattern;

import ca.ulaval.glo4003.repul.domain.exception.InvalidIDULException;

public record IDUL(String value) {
    private static final Pattern IDUL_PATTERN = Pattern.compile("^[A-Za-z]{4,5}[0-9]{0,3}$");
    public IDUL {
        if (!IDUL_PATTERN.matcher(value).matches()) {
            throw new InvalidIDULException();
        }
    }
}
