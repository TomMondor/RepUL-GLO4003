package ca.ulaval.glo4003.identitymanagement.domain.exception;

public class InvalidCredentialsException extends IdentityManagementException {
    public InvalidCredentialsException() {
        super("Invalid email or password.");
    }
}
