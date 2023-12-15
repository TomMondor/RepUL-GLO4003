package ca.ulaval.glo4003.repul.identitymanagement.domain.exception;

public class InvalidPasswordException extends IdentityManagementException {
    public InvalidPasswordException() {
        super("The given password is invalid.");
    }
}
