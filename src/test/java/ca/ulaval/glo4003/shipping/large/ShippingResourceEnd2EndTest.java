package ca.ulaval.glo4003.shipping.large;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;
import ca.ulaval.glo4003.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.fixture.LoginRequestFixture;
import ca.ulaval.glo4003.shipping.api.response.LocationResponse;

import static io.restassured.RestAssured.given;

public class ShippingResourceEnd2EndTest {

    private static final String ACCOUNT_EMAIL = "shipper@ulaval.ca";
    private static final String ACCOUNT_PASSWORD = "shipper";
    private static final ApplicationContext context = new DevApplicationContext();
    private static final String BASE_URL = context.getURI() + "api/shipping/";
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

//TODO à finir plus tard
//    @Test
//    public void whenCancelingShipping_shouldReturn200() {
//        String accountToken = registerAndLoginShipper();
//        String shippingId = "1234";
//
//        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).post(BASE_URL + "/pickup/"+ shippingId);
//
//        assertEquals(200, response.getStatusCode());
//    }
    private String registerAndLoginShipper() {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).build();
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(context.getURI() + "api/auth/login");
        return loginResponse.getBody().as(LoginResponse.class).token();
    }
}
