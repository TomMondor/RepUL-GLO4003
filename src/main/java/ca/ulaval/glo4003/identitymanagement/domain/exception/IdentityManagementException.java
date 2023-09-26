package ca.ulaval.glo4003.identitymanagement.domain.exception;

public abstract class IdentityManagementException extends RuntimeException {
    public IdentityManagementException(String message) {
        super(message);
    }
}
