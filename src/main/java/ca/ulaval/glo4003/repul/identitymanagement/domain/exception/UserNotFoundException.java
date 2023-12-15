package ca.ulaval.glo4003.repul.identitymanagement.domain.exception;

public class UserNotFoundException extends IdentityManagementException {
    public UserNotFoundException() {
        super("Could not find a user with this email.");
    }
}
