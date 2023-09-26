package ca.ulaval.glo4003.identitymanagement.domain.exception;

public class UserAlreadyExistsException extends IdentityManagementException {
    public UserAlreadyExistsException() {
        super("An account with this email address already exists.");
    }
}
