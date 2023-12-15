package ca.ulaval.glo4003.repul.identitymanagement.domain.exception;

public class InvalidTokenException extends IdentityManagementException {
    public InvalidTokenException() {
        super("The token is invalid.");
    }
}
