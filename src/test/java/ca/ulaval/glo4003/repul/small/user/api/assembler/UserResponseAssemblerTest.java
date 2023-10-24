package ca.ulaval.glo4003.repul.small.user.api.assembler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.fixture.user.AccountInformationPayloadFixture;
import ca.ulaval.glo4003.repul.user.api.assembler.UserResponseAssembler;
import ca.ulaval.glo4003.repul.user.api.response.AccountResponse;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.user.application.payload.AccountInformationPayload;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserResponseAssemblerTest {
    private static final String A_TOKEN_VALUE = "a-token";
    private static final int A_TOKEN_EXPIRATION = 1234;
    private static final Token A_TOKEN = new Token(A_TOKEN_VALUE, A_TOKEN_EXPIRATION);
    private static final AccountInformationPayload AN_ACCOUNT_INFORMATION_PAYLOAD = new AccountInformationPayloadFixture().build();

    private UserResponseAssembler userResponseAssembler;

    @BeforeEach
    public void createLoginResponseAssembler() {
        userResponseAssembler = new UserResponseAssembler();
    }

    @Test
    public void whenAssembling_shouldReturnLoginResponse() {
        LoginResponse response = userResponseAssembler.toLoginResponse(A_TOKEN);

        assertEquals(A_TOKEN_VALUE, response.token());
        assertEquals(A_TOKEN_EXPIRATION, response.expiresIn());
    }

    @Test
    public void givenAccountInformationPayload_whenToAccountResponse_shouldReturnAccountResponse() {
        AccountResponse accountResponse = userResponseAssembler.toAccountResponse(AN_ACCOUNT_INFORMATION_PAYLOAD);

        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.name().value(), accountResponse.name());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.birthdate().value().toString(), accountResponse.birthdate());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.gender().name(), accountResponse.gender());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.age(), accountResponse.age());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.idul().value(), accountResponse.idul());
        assertEquals(AN_ACCOUNT_INFORMATION_PAYLOAD.email().value(), accountResponse.email());
    }
}
