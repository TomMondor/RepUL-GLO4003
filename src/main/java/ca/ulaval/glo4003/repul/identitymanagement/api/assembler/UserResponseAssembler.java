package ca.ulaval.glo4003.repul.identitymanagement.api.assembler;

import ca.ulaval.glo4003.repul.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.identitymanagement.domain.token.Token;

public class UserResponseAssembler {
    public LoginResponse toLoginResponse(Token token) {
        return new LoginResponse(token.value(), token.expiresIn());
    }
}
