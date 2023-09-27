package ca.ulaval.glo4003.identitymanagement.api.assembler;

import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.identitymanagement.domain.token.Token;

public class LoginResponseAssembler {
    public LoginResponse toLoginResponse(Token token) {
        return new LoginResponse(token.value(), token.expiresIn());
    }
}
