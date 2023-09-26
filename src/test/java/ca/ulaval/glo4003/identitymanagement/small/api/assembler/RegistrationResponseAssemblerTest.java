package ca.ulaval.glo4003.identitymanagement.small.api.assembler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.identitymanagement.api.assembler.RegistrationResponseAssembler;
import ca.ulaval.glo4003.identitymanagement.api.response.RegistrationResponse;
import ca.ulaval.glo4003.identitymanagement.domain.token.Token;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationResponseAssemblerTest {
    private static final String A_TOKEN_VALUE = "a-token";
    private static final int A_TOKEN_EXPIRATION = 1234;
    private static final Token A_TOKEN = new Token(A_TOKEN_VALUE, A_TOKEN_EXPIRATION);

    private RegistrationResponseAssembler registrationResponseAssembler;

    @BeforeEach
    public void setUp() {
        registrationResponseAssembler = new RegistrationResponseAssembler();
    }

    @Test
    public void whenAssemblingRegistrationResponse_shouldReturnRegistrationResponse() {
        RegistrationResponse response = registrationResponseAssembler.toRegistrationResponse(A_TOKEN);

        assertEquals(A_TOKEN_VALUE, response.token());
        assertEquals(A_TOKEN_EXPIRATION, response.expiresIn());
    }
}
