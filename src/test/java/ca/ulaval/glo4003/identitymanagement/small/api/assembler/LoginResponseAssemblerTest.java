package ca.ulaval.glo4003.identitymanagement.small.api.assembler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.identitymanagement.api.assembler.LoginResponseAssembler;
import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.identitymanagement.domain.token.Token;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginResponseAssemblerTest {
    private static final String A_TOKEN_VALUE = "a-token";
    private static final int A_TOKEN_EXPIRATION = 1234;
    private static final Token A_TOKEN = new Token(A_TOKEN_VALUE, A_TOKEN_EXPIRATION);

    private LoginResponseAssembler loginResponseAssembler;

    @BeforeEach
    public void createLoginResponseAssembler() {
        loginResponseAssembler = new LoginResponseAssembler();
    }

    @Test
    public void whenAssembling_shouldReturnLoginResponse() {
        LoginResponse response = loginResponseAssembler.toLoginResponse(A_TOKEN);

        assertEquals(A_TOKEN_VALUE, response.token());
        assertEquals(A_TOKEN_EXPIRATION, response.expiresIn());
    }
}
