package ca.ulaval.glo4003.commons.medium;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.fixture.FakeCommonExceptionThrowerContextFixture;
import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonExceptionMapperTest {
    private static final Integer BAD_REQUEST_CODE = 400;
    private static String baseUrl;

    private static ServerFixture server;

    @BeforeAll
    public static void startServer() throws Exception {
        ApplicationContext context = new FakeCommonExceptionThrowerContextFixture();
        baseUrl = context.getURI();
        server = new ServerFixture(context);
        server.start();
    }

    @AfterAll
    public static void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenThrowingBadRequestCommonExceptionType_shouldGiveBadRequest() {
        Response response = when().get(baseUrl + "BadRequestCommonException");

        assertEquals(BAD_REQUEST_CODE, response.getStatusCode());
    }
}
