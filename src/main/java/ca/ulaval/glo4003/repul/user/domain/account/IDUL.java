package ca.ulaval.glo4003.repul.user.domain.account;

import java.util.regex.Pattern;

import ca.ulaval.glo4003.repul.user.domain.account.exception.InvalidIDULException;

public record IDUL(
    String value
) {
    private static final Pattern IDUL_PATTERN = Pattern.compile("^[A-Z]{2,5}[1-9][0-9]{0,2}$");
    public IDUL {
        if (!IDUL_PATTERN.matcher(value).matches()) {
            throw new InvalidIDULException();
        }
    }
}
