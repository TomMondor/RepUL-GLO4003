package ca.ulaval.glo4003.identitymanagement.domain.exception;

public class PasswordNotMatchingException extends IdentityManagementException {
    public PasswordNotMatchingException() {
        super("Provided password does not match the user's password.");
    }
}
