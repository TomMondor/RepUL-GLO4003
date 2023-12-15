package ca.ulaval.glo4003.repul.identitymanagement.domain.exception;

public class IDULAlreadyInUseException extends IdentityManagementException {
    public IDULAlreadyInUseException() {
        super("An account with this IDUL already exists.");
    }
}
