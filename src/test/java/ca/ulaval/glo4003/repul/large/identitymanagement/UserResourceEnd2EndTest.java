package ca.ulaval.glo4003.repul.large.identitymanagement;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.identitymanagement.RegistrationRequestFixture;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.RegistrationRequest;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserResourceEnd2EndTest {
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

        Response response = given().contentType(MediaType.APPLICATION_JSON).body(registrationRequest).post(CONTEXT.getURI() + "users:register");

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void whenRegistering_shouldReturnLocationHeader() {
        RegistrationRequest registrationRequest = new RegistrationRequestFixture().build();

        Response response = given().contentType(MediaType.APPLICATION_JSON).body(registrationRequest).post(CONTEXT.getURI() + "users:register");

        assertNotNull(response.getHeader("Location"));
    }
}