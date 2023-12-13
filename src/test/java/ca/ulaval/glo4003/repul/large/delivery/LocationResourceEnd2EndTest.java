package ca.ulaval.glo4003.repul.large.delivery;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;

import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocationResourceEnd2EndTest {
    private static final ApplicationContext CONTEXT = new TestApplicationContext();
    private static final DeliveryLocationId LOCATION_ID = DeliveryLocationId.MYRAND;
    private static final DeliveryLocationPayload EXPECTED_LOCATION = new DeliveryLocationPayload(LOCATION_ID.toString(), "Secteur EST", 12, 12);

    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        server = new ServerFixture(CONTEXT);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenGettingLocations_shouldReturn200() {
        Response response = when().get(CONTEXT.getURI() + "locations");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingLocations_shouldReturnLocations() {
        Response response = when().get(CONTEXT.getURI() + "locations");
        DeliveryLocationsPayload actualBody = response.getBody().as(DeliveryLocationsPayload.class);
        assertFalse(actualBody.deliveryLocations().isEmpty());
        assertTrue(actualBody.deliveryLocations().contains(EXPECTED_LOCATION));
    }
}
