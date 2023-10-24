package ca.ulaval.glo4003.repul.user.api.assembler;

import ca.ulaval.glo4003.repul.user.api.response.AccountResponse;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.user.application.payload.AccountInformationPayload;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;

public class UserResponseAssembler {
    public LoginResponse toLoginResponse(Token token) {
        return new LoginResponse(token.value(), token.expiresIn());
    }

    public AccountResponse toAccountResponse(AccountInformationPayload accountInformationPayload) {
        return new AccountResponse(accountInformationPayload.name().value(), accountInformationPayload.birthdate().value().toString(),
            accountInformationPayload.gender().toString(), accountInformationPayload.age(), accountInformationPayload.idul().value(),
            accountInformationPayload.email().value());
    }
}
