package ca.ulaval.glo4003.repul.large.shipping;

import java.util.Arrays;
import java.util.List;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.ApplicationContext;
import ca.ulaval.glo4003.repul.config.TestApplicationContext;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.shipping.api.response.LocationResponse;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocationsCatalogResourceEnd2EndTest {
    private static final ApplicationContext context = new TestApplicationContext();
    private static final String BASE_URL = context.getURI() + "catalog/";
    private static final LocationResponse EXPECTED_LOCATION = new LocationResponse("MYRAND", "Secteur EST", 12);

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
    public void whenGettingLocations_shouldReturn200() {
        Response response = when().get(BASE_URL + "locations");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingLocations_shouldReturnLocationsBody() {
        Response response = when().get(BASE_URL + "locations");
        List<LocationResponse> actualBody = Arrays.asList(response.getBody().as(LocationResponse[].class));

        assertEquals(EXPECTED_LOCATION, actualBody.get(0));
    }
}
