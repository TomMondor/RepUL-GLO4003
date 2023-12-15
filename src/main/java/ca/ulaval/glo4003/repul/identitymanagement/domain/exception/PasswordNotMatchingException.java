package ca.ulaval.glo4003.repul.identitymanagement.domain.exception;

public class PasswordNotMatchingException extends IdentityManagementException {
    public PasswordNotMatchingException() {
        super("Provided password does not match the user's password.");
    }
}
