package ca.ulaval.glo4003.repul.identitymanagement.domain.exception;

public class TokenVerificationFailedException extends IdentityManagementException {
    public TokenVerificationFailedException() {
        super("Invalid or expired token.");
    }
}
