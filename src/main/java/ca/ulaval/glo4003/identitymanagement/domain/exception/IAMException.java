package ca.ulaval.glo4003.identitymanagement.domain.exception;

public abstract class IAMException extends RuntimeException {
    public IAMException(String message) {
        super(message);
    }
}
