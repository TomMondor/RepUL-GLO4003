package ca.ulaval.glo4003.repul.domain.exception;

public class AccountNotFoundException extends RepULException {
    public AccountNotFoundException() {
        super("The given account was not found.");
    }
}
