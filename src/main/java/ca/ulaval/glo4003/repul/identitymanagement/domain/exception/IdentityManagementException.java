package ca.ulaval.glo4003.repul.identitymanagement.domain.exception;

import ca.ulaval.glo4003.repul.commons.domain.exception.RepULException;

public abstract class IdentityManagementException extends RepULException {
    public IdentityManagementException(String message) {
        super(message);
    }
}
