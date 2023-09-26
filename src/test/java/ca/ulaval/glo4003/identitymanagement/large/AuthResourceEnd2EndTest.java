package ca.ulaval.glo4003.identitymanagement.large;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;
import ca.ulaval.glo4003.identitymanagement.api.request.RegistrationRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.RegistrationResponse;
import ca.ulaval.glo4003.identitymanagement.fixture.RegistrationRequestFixture;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthResourceEnd2EndTest {
    private static final int EXPIRES_IN = 3600;
    private static final ApplicationContext context = new DevApplicationContext();

    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        server = new ServerFixture(context);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenRegistering_shouldReturn201() {
        RegistrationRequest registrationRequest = new RegistrationRequestFixture().build();

        Response response = given().contentType("application/json").body(registrationRequest).post(context.getURI() + "api/auth/register");

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void whenRegistering_shouldReturnAuthToken() {
        RegistrationRequest registrationRequest = new RegistrationRequestFixture().build();

        Response response = given().contentType("application/json").body(registrationRequest).post(context.getURI() + "api/auth/register");
        RegistrationResponse actualBody = response.getBody().as(RegistrationResponse.class);

        assertNotNull(actualBody.token());
        assertEquals(EXPIRES_IN, actualBody.expiresIn());
    }
}
