package ca.ulaval.glo4003.repul.large.delivery;

import java.util.Optional;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.config.seed.cooking.TestCookingSeed;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.TestIdentityManagementSeed;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargoPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.identitymanagement.LoginRequestFixture;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.identitymanagement.api.response.LoginResponse;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CargoResourceEnd2EndTest {
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
    public void whenGettingCargosReadyToPickUp_shouldReturn200() {
        String accountToken = loginAsADeliveryPerson();

        Response response = given().header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "cargos:toPickUp");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingCargosReadyToPickUp_shouldReturnCargosAvailableForPickUp() {
        String accountToken = loginAsADeliveryPerson();

        Response response = given().header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "cargos:toPickUp");
        CargosPayload actualResponse = response.getBody().as(CargosPayload.class);
        Optional<CargoPayload> actualCargo = actualResponse.cargoPayloads().stream().findFirst();

        assertTrue(actualCargo.isPresent());
        assertFalse(actualCargo.get().mealKitsPayload().isEmpty());
    }

    @Test
    public void whenPickingUpCargo_shouldReturn204() {
        String accountToken = loginAsADeliveryPerson();
        String cargoId = getCargoIdAvailableForPickUp(accountToken);

        Response response = given().header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + ":pickUp");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenConfirmingDelivery_shouldReturn204() {
        String accountToken = loginAsADeliveryPerson();
        String cargoId = getCargoIdAvailableForPickUp(accountToken);
        pickUpCargo(accountToken, cargoId);

        Response response = given().header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "cargos/" + cargoId + "/mealKits/" + TestCookingSeed.TENTH_MEAL_KIT_ID.getUUID().toString() + ":confirm");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenRecallingMealKitInCargo_shouldReturn200() {
        String accountToken = loginAsADeliveryPerson();
        String cargoId = getCargoIdAvailableForPickUp(accountToken);
        pickUpCargo(accountToken, cargoId);
        confirmDelivery(accountToken, cargoId);

        Response response = given().header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "cargos/" + cargoId + "/mealKits/" + TestCookingSeed.TENTH_MEAL_KIT_ID.getUUID().toString() + ":recall");

        assertEquals(200, response.getStatusCode());
    }

    private String loginAsADeliveryPerson() {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(TestIdentityManagementSeed.DELIVERY_PERSON_EMAIL)
            .withPassword(TestIdentityManagementSeed.DELIVERY_PERSON_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).when().post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private String loginAsADifferentDeliveryPerson() {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(TestIdentityManagementSeed.SECOND_DELIVERY_PERSON_EMAIL)
            .withPassword(TestIdentityManagementSeed.SECOND_DELIVERY_PERSON_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private String getCargoIdAvailableForPickUp(String accountToken) {
        Response response = given().header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "cargos:toPickUp");
        CargosPayload actualResponse = response.getBody().as(CargosPayload.class);

        return actualResponse.cargoPayloads().stream().findFirst().get().cargoId();
    }

    private void pickUpCargo(String accountToken, String cargoId) {
        given().header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + ":pickUp");
    }

    private void confirmDelivery(String accountToken, String cargoId) {
        given().header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "cargos/" + cargoId + "/mealKits/" + TestCookingSeed.TENTH_MEAL_KIT_ID.getUUID().toString() + ":confirm");
    }
}
