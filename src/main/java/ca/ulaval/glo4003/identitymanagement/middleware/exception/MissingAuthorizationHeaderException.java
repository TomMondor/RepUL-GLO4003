package ca.ulaval.glo4003.identitymanagement.middleware.exception;

import ca.ulaval.glo4003.identitymanagement.domain.exception.IdentityManagementException;

public class MissingAuthorizationHeaderException extends IdentityManagementException {
    public MissingAuthorizationHeaderException() {
        super("Missing Bearer token in Authorization header.");
    }
}
