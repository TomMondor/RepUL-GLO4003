package ca.ulaval.glo4003.identitymanagement.api.assembler;

import ca.ulaval.glo4003.identitymanagement.api.response.RegistrationResponse;
import ca.ulaval.glo4003.identitymanagement.domain.token.Token;

public class RegistrationResponseAssembler {
    public RegistrationResponse toRegistrationResponse(Token token) {
        return new RegistrationResponse(token.value(), token.expiresIn());
    }
}
