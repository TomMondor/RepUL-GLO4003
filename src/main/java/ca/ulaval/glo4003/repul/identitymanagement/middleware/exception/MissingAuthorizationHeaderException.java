package ca.ulaval.glo4003.repul.identitymanagement.middleware.exception;

import ca.ulaval.glo4003.repul.identitymanagement.domain.exception.IdentityManagementException;

public class MissingAuthorizationHeaderException extends IdentityManagementException {
    public MissingAuthorizationHeaderException() {
        super("Missing Bearer token in Authorization header.");
    }
}
