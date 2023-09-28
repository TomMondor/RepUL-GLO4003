package ca.ulaval.glo4003.repul.small.api.account.assembler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.api.account.assembler.AccountResponseAssembler;
import ca.ulaval.glo4003.repul.api.account.response.AccountResponse;
import ca.ulaval.glo4003.repul.application.account.payload.AccountInformationPayload;
import ca.ulaval.glo4003.repul.fixture.AccountInformationPayloadFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountResponseAssemblerTest {
    private static final AccountInformationPayload AN_ACCOUNT_INFORMATION_PAYLOAD = new AccountInformationPayloadFixture().build();

    private AccountResponseAssembler accountResponseAssembler;

    @BeforeEach
    public void createAssembler() {
        accountResponseAssembler = new AccountResponseAssembler();
    }

    @Test
    public void givenAccountInformationPayload_whenToAccountResponse_shouldReturnAccountResponse() {
        AccountResponse accountResponse = accountResponseAssembler.toAccountResponse(AN_ACCOUNT_INFORMATION_PAYLOAD);

        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.name().value(), accountResponse.name());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.birthdate().value().toString(), accountResponse.birthdate());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.gender().name(), accountResponse.gender());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.age(), accountResponse.age());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.idul().value(), accountResponse.idul());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.email().value(), accountResponse.email());
    }
}
