package ca.ulaval.glo4003.repul.large.lockerauthorization;

import java.util.List;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.seed.cooking.TestCookingSeed;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.TestIdentityManagementSeed;
import ca.ulaval.glo4003.repul.config.seed.lockerauthorization.TestLockerAuthorizationSeed;
import ca.ulaval.glo4003.repul.cooking.api.request.SelectionRequest;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargoPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.identitymanagement.LoginRequestFixture;
import ca.ulaval.glo4003.repul.fixture.lockerauthorization.OpenLockerRequestFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.AddCardRequestFixture;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.lockerauthorization.api.request.OpenLockerRequest;
import ca.ulaval.glo4003.repul.subscription.api.request.AddCardRequest;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LockerAuthorizationResourceEnd2EndTest {
    private static final ApplicationContext CONTEXT = new TestApplicationContext();
    private static final String A_USER_CARD_NUMBER = "987654321";
    private final String apiKey = EnvParser.getInstance().readVariable("LOCKER_API_KEY");
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
    public void whenOpeningLocker_shouldReturn204() {
        addCardToUserAccount(A_USER_CARD_NUMBER);
        cookMealKit(TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID().toString());
        String lockerId = deliverMealKit(TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID().toString());
        OpenLockerRequest openLockerRequest = new OpenLockerRequestFixture().withSubscriberCardNumber(A_USER_CARD_NUMBER).withLockerId(lockerId).build();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + apiKey).body(openLockerRequest)
            .post(CONTEXT.getURI() + "locker:open");

        assertEquals(204, response.getStatusCode());
    }

    private String login() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestIdentityManagementSeed.CLIENT_EMAIL).withPassword(TestIdentityManagementSeed.CLIENT_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private void addCardToUserAccount(String cardNumber) {
        String accountToken = login();
        AddCardRequest addCardRequest = new AddCardRequestFixture().withCardNumber(cardNumber).build();

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(addCardRequest)
            .post(CONTEXT.getURI() + "accounts:addCard");
    }

    private void cookMealKit(String mealKitId) {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestIdentityManagementSeed.COOK_EMAIL).withPassword(TestIdentityManagementSeed.COOK_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");
        String accountToken = loginResponse.getBody().as(LoginResponse.class).token();

        SelectionRequest selectionRequest = new SelectionRequest();
        selectionRequest.ids = List.of(mealKitId);

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(selectionRequest)
            .post(CONTEXT.getURI() + "mealKits:select");
        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "mealKits/" + mealKitId + ":confirmPreparation");
    }

    private String deliverMealKit(String mealKitId) {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(TestIdentityManagementSeed.DELIVERY_PERSON_EMAIL)
            .withPassword(TestIdentityManagementSeed.DELIVERY_PERSON_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(CONTEXT.getURI() + "users:login");
        String accountToken = loginResponse.getBody().as(LoginResponse.class).token();

        Response response = given().header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "cargos:toPickUp");
        CargosPayload cargoResponses = response.getBody().as(CargosPayload.class);
        CargoPayload cargoResponse = cargoResponses.cargoPayloads().stream()
            .filter(cargo -> cargo.mealKitsPayload().stream().anyMatch(mealKitPayload -> mealKitPayload.mealKitId().equals(mealKitId))).findFirst().get();
        String cargoId = cargoResponse.cargoId();
        MealKitPayload mealKitResponse = cargoResponse.mealKitsPayload().stream().filter(mealKit -> mealKit.mealKitId().equals(mealKitId)).findFirst().get();
        String lockerId = TestLockerAuthorizationSeed.A_DELIVERY_LOCATION.getName() + " " + mealKitResponse.lockerNumber();
        pickUpCargo(accountToken, cargoId);

        given().header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + "/mealKits/" + mealKitId + ":confirm");
        return lockerId;
    }

    private void pickUpCargo(String accountToken, String cargoId) {
        given().header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + ":pickUp");
    }
}
