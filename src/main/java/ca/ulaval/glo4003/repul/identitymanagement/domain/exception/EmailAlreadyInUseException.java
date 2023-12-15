package ca.ulaval.glo4003.repul.identitymanagement.domain.exception;

public class EmailAlreadyInUseException extends IdentityManagementException {
    public EmailAlreadyInUseException() {
        super("An account with this email address already exists.");
    }
}
