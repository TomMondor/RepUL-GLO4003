package ca.ulaval.glo4003.repul.medium;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.repul.fixture.FakeRepULExceptionThrowerContextFixture;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepULExceptionMapperTest {

    private static final Integer BAD_REQUEST_CODE = 400;
    private static final Integer NOT_FOUND_CODE = 404;

    private static String baseUrl;

    private static ServerFixture server;

    @BeforeAll
    public static void startServer() throws Exception {
        ApplicationContext context = new FakeRepULExceptionThrowerContextFixture();
        baseUrl = context.getURI();
        server = new ServerFixture(context);
        server.start();
    }

    @AfterAll
    public static void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenThrowingBadRequestRepULExceptionType_shouldGiveBadRequest() {
        Response response = when().get(baseUrl + "BadRequestRepULException");

        assertEquals(BAD_REQUEST_CODE, response.getStatusCode());
    }

    @Test
    public void whenThrowingNotFoundRepULException_shouldGiveNotFound() {
        Response response = when().get(baseUrl + "NotFoundRepULException");

        assertEquals(NOT_FOUND_CODE, response.getStatusCode());
    }
}
