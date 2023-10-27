package ca.ulaval.glo4003.repul.large.user;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.ApplicationContext;
import ca.ulaval.glo4003.repul.config.TestApplicationContext;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.user.LoginRequestFixture;
import ca.ulaval.glo4003.repul.fixture.user.RegistrationRequestFixture;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.user.api.response.AccountResponse;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserResourceEnd2EndTest {
    private static final String ACCOUNT_EMAIL = "myAccountEmail@ulaval.ca";
    private static final String ACCOUNT_PASSWORD = "secret123";
    private static final String ACCOUNT_IDUL = "almat69";
    private static final String ACCOUNT_NAME = "Bob Math";
    private static final String ACCOUNT_BIRTHDATE = "1969-04-20";
    private static final String ACCOUNT_GENDER = "MAN";
    private static final ApplicationContext CONTEXT = new TestApplicationContext();

    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        server = new ServerFixture(CONTEXT);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        RestAssured.reset();
        server.stop();
    }

    @Test
    public void whenRegistering_shouldReturn201() {
        RegistrationRequest registrationRequest = new RegistrationRequestFixture().build();

        Response response = given().contentType("application/json").body(registrationRequest).post(CONTEXT.getURI() + "users:register");

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void whenRegistering_shouldReturnLocationHeader() {
        RegistrationRequest registrationRequest = new RegistrationRequestFixture().build();

        Response response = given().contentType("application/json").body(registrationRequest).post(CONTEXT.getURI() + "users:register");

        assertNotNull(response.getHeader("Location"));
    }

    @Test
    public void givenExistingAccount_whenGettingMyAccount_shouldReturn200() {
        String accountToken = registerAndLogin();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "users");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void givenExistingAccount_whenGettingMyAccount_shouldReturnAccountInformation() {
        String accountToken = registerAndLogin();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "users");
        AccountResponse responseBody = response.getBody().as(AccountResponse.class);

        assertEquals(ACCOUNT_NAME, responseBody.name());
        assertEquals(ACCOUNT_BIRTHDATE, responseBody.birthdate());
        assertEquals(ACCOUNT_EMAIL, responseBody.email());
        assertEquals(ACCOUNT_GENDER, responseBody.gender());
        assertEquals(ACCOUNT_IDUL.toUpperCase(), responseBody.idul());
        assertNotNull(responseBody.age());
    }

    private String registerAndLogin() {
        RegistrationRequest registrationRequest = createRegistrationRequest();
        given().contentType("application/json").body(registrationRequest).post(CONTEXT.getURI() + "users:register");

        LoginRequest loginRequest = new LoginRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).build();
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private RegistrationRequest createRegistrationRequest() {
        return new RegistrationRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).withBirthdate(ACCOUNT_BIRTHDATE)
            .withGender(ACCOUNT_GENDER).withName(ACCOUNT_NAME).withIdul(ACCOUNT_IDUL).build();
    }
}
