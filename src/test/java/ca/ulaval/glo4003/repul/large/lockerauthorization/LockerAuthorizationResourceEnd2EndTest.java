package ca.ulaval.glo4003.repul.large.lockerauthorization;

import java.util.Arrays;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.cooking.api.request.SelectionRequest;
import ca.ulaval.glo4003.repul.delivery.api.response.CargoResponse;
import ca.ulaval.glo4003.repul.delivery.api.response.MealKitResponse;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.lockerauthorization.OpenLockerRequestFixture;
import ca.ulaval.glo4003.repul.fixture.user.AddCardRequestFixture;
import ca.ulaval.glo4003.repul.fixture.user.LoginRequestFixture;
import ca.ulaval.glo4003.repul.lockerauthorization.api.request.OpenLockerRequest;
import ca.ulaval.glo4003.repul.user.api.request.AddCardRequest;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LockerAuthorizationResourceEnd2EndTest {
    private static final ApplicationContext CONTEXT = new TestApplicationContext();
    private static final String A_USER_CARD_NUMBER = "123456789";
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
        cookMealKit(TestApplicationContext.FIRST_MEAL_KIT_ID.value().toString());
        String lockerId = deliverMealKit(TestApplicationContext.FIRST_MEAL_KIT_ID.value().toString());
        OpenLockerRequest openLockerRequest = new OpenLockerRequestFixture().withUserCardNumber(A_USER_CARD_NUMBER).withLockerId(lockerId).build();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + apiKey).body(openLockerRequest)
            .post(CONTEXT.getURI() + "locker:open");

        assertEquals(204, response.getStatusCode());
    }

    private String login() {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(TestApplicationContext.CLIENT_EMAIL)
            .withPassword(TestApplicationContext.CLIENT_PASSWORD).build();
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(CONTEXT.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private void addCardToUserAccount(String cardNumber) {
        String accountToken = login();
        AddCardRequest addCardRequest = new AddCardRequestFixture().withCardNumber(cardNumber).build();

        given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(addCardRequest)
            .post(CONTEXT.getURI() + "users:addCard");
    }

    private void cookMealKit(String mealKitId) {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(TestApplicationContext.COOK_EMAIL)
            .withPassword(TestApplicationContext.COOK_PASSWORD).build();
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(CONTEXT.getURI() + "users:login");
        String accountToken = loginResponse.getBody().as(LoginResponse.class).token();

        SelectionRequest selectionRequest = new SelectionRequest();
        selectionRequest.ids = List.of(mealKitId);

        given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(selectionRequest)
            .post(CONTEXT.getURI() + "mealKits:select");
        given().contentType("application/json").header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "mealKits/" + mealKitId + ":confirmCooked");
    }

    private String deliverMealKit(String mealKitId) {
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(TestApplicationContext.DELIVERY_PERSON_EMAIL)
            .withPassword(TestApplicationContext.DELIVERY_PERSON_PASSWORD).build();
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(CONTEXT.getURI() + "users:login");
        String accountToken = loginResponse.getBody().as(LoginResponse.class).token();

        Response response = given().header("Authorization", "Bearer " + accountToken).get(CONTEXT.getURI() + "cargos:toPickUp");
        List<CargoResponse> cargoResponses = Arrays.asList(response.getBody().as(CargoResponse[].class));
        CargoResponse cargoResponse = cargoResponses.stream().filter(cargo -> cargo.mealKitsResponse().stream()
            .anyMatch(mealKitResponse -> mealKitResponse.mealKitId().equals(mealKitId))).findFirst().get();
        String cargoId = cargoResponse.cargoId();
        MealKitResponse mealKitResponse = cargoResponse.mealKitsResponse().stream().filter(mealKit -> mealKit.mealKitId().equals(mealKitId)).findFirst().get();
        String lockerId = TestApplicationContext.A_DELIVERY_LOCATION.getName() + " " + mealKitResponse.lockerNumber();
        pickupCargo(accountToken, cargoId);

        given().header("Authorization", "Bearer " + accountToken)
            .post(CONTEXT.getURI() + "cargos/" + cargoId + "/mealKits/" + mealKitId + ":confirm");
        return lockerId;
    }

    private void pickupCargo(String accountToken, String cargoId) {
        given().header("Authorization", "Bearer " + accountToken).post(CONTEXT.getURI() + "cargos/" + cargoId + ":pickUp");
    }
}