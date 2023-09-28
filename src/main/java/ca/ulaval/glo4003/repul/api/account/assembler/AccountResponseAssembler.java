package ca.ulaval.glo4003.repul.api.account.assembler;

import ca.ulaval.glo4003.repul.api.account.response.AccountResponse;
import ca.ulaval.glo4003.repul.application.account.payload.AccountInformationPayload;

public class AccountResponseAssembler {
    public AccountResponse toAccountResponse(AccountInformationPayload accountInformationPayload) {
        return new AccountResponse(accountInformationPayload.name().value(), accountInformationPayload.birthdate().value().toString(),
            accountInformationPayload.gender().toString(), accountInformationPayload.age(), accountInformationPayload.idul().value(),
            accountInformationPayload.email().value());
    }
}
