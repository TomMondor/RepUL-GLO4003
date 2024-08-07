package ca.ulaval.glo4003.repul.large.cooking;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.config.context.ApplicationContext;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.config.seed.cooking.TestCookingSeed;
import ca.ulaval.glo4003.repul.config.seed.identitymanagement.TestIdentityManagementSeed;
import ca.ulaval.glo4003.repul.cooking.api.request.ConfirmCookedRequest;
import ca.ulaval.glo4003.repul.cooking.api.request.SelectionRequest;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.SelectionPayload;
import ca.ulaval.glo4003.repul.fixture.commons.ServerFixture;
import ca.ulaval.glo4003.repul.fixture.identitymanagement.LoginRequestFixture;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.identitymanagement.api.response.LoginResponse;

import jakarta.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MealKitResourceEnd2EndTest {
    private ApplicationContext context;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private ServerFixture server;

    @BeforeEach
    public void startServer() throws Exception {
        RestAssured.urlEncodingEnabled = false;
        System.setOut(new PrintStream(outputStream));
        context = new TestApplicationContext();
        server = new ServerFixture(context);
        server.start();
    }

    @AfterEach
    public void closeServer() throws Exception {
        loginAndCancelAllSelection();
        RestAssured.reset();
        System.setOut(System.out);
        server.stop();
    }

    @Test
    public void whenGettingMealKitsToPrepare_shouldReturn200() {
        String accountToken = loginAsACook();

        Response response = given().contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accountToken).get(context.getURI() + "mealKits:toPrepare");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingMealKitsToPrepare_shouldReturnMealKitsToPrepare() {
        String accountToken = loginAsACook();

        Response response = given().contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accountToken).get(context.getURI() + "mealKits:toPrepare");
        MealKitsPayload responseBody = response.getBody().as(MealKitsPayload.class);

        assertFalse(responseBody.mealKits().isEmpty());
        assertFalse(responseBody.totalIngredients().isEmpty());
    }

    @Test
    public void whenSelectingMealKitsToPrepare_shouldReturn204() {
        String accountToken = loginAsACook();
        SelectionRequest selectionRequest = new SelectionRequest();
        selectionRequest.ids = List.of(TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID().toString());

        Response response = given().contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accountToken).body(selectionRequest)
            .post(context.getURI() + "mealKits:select");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenGettingSelection_shouldReturn200() {
        String accountToken = loginAsACook();
        selectMealKitsToPrepare(accountToken, List.of(TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID().toString()));

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(context.getURI() + "mealKits:getSelection");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenGettingSelection_shouldReturnSelection() {
        String accountToken = loginAsACook();
        selectMealKitsToPrepare(accountToken, List.of(TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID().toString()));

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(context.getURI() + "mealKits:getSelection");
        SelectionPayload responseBody = response.getBody().as(SelectionPayload.class);

        assertFalse(responseBody.mealKitSelectionIds().isEmpty());
        assertEquals(TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID().toString(), responseBody.mealKitSelectionIds().get(0));
    }

    @Test
    public void whenCancelingOneSelection_shouldReturn204() {
        String accountToken = loginAsACook();
        selectMealKitsToPrepare(accountToken, List.of(TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID().toString()));

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "mealKits/" + TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID() + ":cancelSelection");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenCancelingCompleteSelection_shouldReturn204() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToPrepare(accountToken,
            List.of(TestCookingSeed.FIRST_MEAL_KIT_ID.getUUID().toString(), TestCookingSeed.SECOND_MEAL_KIT_ID.getUUID().toString()));

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "mealKits:cancelSelection");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenConfirmingOneCooked_shouldReturn204() {
        String accountToken = loginAsACook();
        selectMealKitsToPrepare(accountToken, List.of(TestCookingSeed.SECOND_MEAL_KIT_ID.getUUID().toString()));

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "mealKits/" + TestCookingSeed.SECOND_MEAL_KIT_ID.getUUID() + ":confirmPreparation");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenConfirmingOneCooked_shouldNotifyDeliveryMan() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToPrepare(accountToken, List.of(TestCookingSeed.THIRD_MEAL_KIT_ID.getUUID().toString()));

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "mealKits/" + TestCookingSeed.THIRD_MEAL_KIT_ID.getUUID() + ":confirmPreparation");
        String sentMessage = outputStream.toString().trim();

        assertTrue(sentMessage.contains("Sending notification to: " + TestIdentityManagementSeed.DELIVERY_PERSON_EMAIL));
    }

    @Test
    public void whenConfirmingMultipleCooked_shouldReturn204() {
        String accountToken = loginAsACook();
        selectMealKitsToPrepare(accountToken,
            List.of(TestCookingSeed.FOURTH_MEAL_KIT_ID.getUUID().toString(), TestCookingSeed.FIFTH_MEAL_KIT_ID.getUUID().toString()));
        ConfirmCookedRequest confirmCookedRequest = new ConfirmCookedRequest();
        confirmCookedRequest.ids = List.of(TestCookingSeed.FOURTH_MEAL_KIT_ID.getUUID().toString(), TestCookingSeed.FIFTH_MEAL_KIT_ID.getUUID().toString());

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(confirmCookedRequest)
            .post(context.getURI() + "mealKits:confirmPreparation");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenConfirmingMultipleCooked_shouldNotifyDeliveryMan() {
        String accountToken = loginAsACook();
        selectMealKitsToPrepare(accountToken,
            List.of(TestCookingSeed.SIXTH_MEAL_KIT_ID.getUUID().toString(), TestCookingSeed.SEVENTH_MEAL_KIT_ID.getUUID().toString()));
        ConfirmCookedRequest confirmCookedRequest = new ConfirmCookedRequest();
        confirmCookedRequest.ids = List.of(TestCookingSeed.SIXTH_MEAL_KIT_ID.getUUID().toString(), TestCookingSeed.SEVENTH_MEAL_KIT_ID.getUUID().toString());

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(confirmCookedRequest)
            .post(context.getURI() + "mealKits:confirmPreparation");
        String sentMessage = outputStream.toString().trim();

        assertTrue(sentMessage.contains("Sending notification to: " + TestIdentityManagementSeed.DELIVERY_PERSON_EMAIL));
    }

    @Test
    public void whenUnconfirmingPreparationOneCooked_shouldReturn204() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToPrepare(accountToken, List.of(TestCookingSeed.EIGHTH_MEAL_KIT_ID.getUUID().toString()));

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "mealKits/" + TestCookingSeed.EIGHTH_MEAL_KIT_ID.getUUID() + ":unconfirmPreparation");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenUnconfirmingPreparationOneCooked_shouldPutItBackInRequestingCookSelection() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToPrepare(accountToken, List.of(TestCookingSeed.EIGHTH_MEAL_KIT_ID.getUUID().toString()));

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "mealKits/" + TestCookingSeed.EIGHTH_MEAL_KIT_ID.getUUID() + ":unconfirmPreparation");

        Response response =
            given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).get(context.getURI() + "mealKits:getSelection");
        SelectionPayload responseBody = response.getBody().as(SelectionPayload.class);
        assertFalse(responseBody.mealKitSelectionIds().isEmpty());
        assertEquals(TestCookingSeed.EIGHTH_MEAL_KIT_ID.getUUID().toString(), responseBody.mealKitSelectionIds().get(0));
    }

    @Test
    public void whenPickingUpMealKit_shouldReturn204() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToPrepare(accountToken, List.of(TestCookingSeed.SPORADIC_MEAL_KIT_ID.getUUID().toString()));
        String userAccountToken = loginAsUser();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + userAccountToken)
            .post(context.getURI() + "mealKits/" + TestCookingSeed.SPORADIC_MEAL_KIT_ID.getUUID() + ":pickUp");

        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void whenPickingUpMealKit_shouldBeRemovedFromKitchenAndSoNotUnconfirmableAnymore() {
        String accountToken = loginAsACook();
        selectAndConfirmMealKitsToPrepare(accountToken, List.of(TestCookingSeed.SPORADIC_MEAL_KIT_ID.getUUID().toString()));
        String userAccountToken = loginAsUser();

        Response response = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + userAccountToken)
            .post(context.getURI() + "mealKits/" + TestCookingSeed.SPORADIC_MEAL_KIT_ID.getUUID() + ":pickUp");

        Response recallResponse = given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "mealKits/" + TestCookingSeed.SPORADIC_MEAL_KIT_ID.getUUID() + ":unconfirmPreparation");
        assertEquals(404, recallResponse.getStatusCode());
    }

    private String loginAsACook() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestIdentityManagementSeed.COOK_EMAIL).withPassword(TestIdentityManagementSeed.COOK_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(context.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private void loginAndCancelAllSelection() {
        String accountToken = loginAsACook();
        Response response = given().contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + accountToken).post(context.getURI() + "mealKits:cancelSelection");

        assertEquals(204, response.getStatusCode());
    }

    private void selectMealKitsToPrepare(String accountToken, List<String> mealKitIds) {
        SelectionRequest selectionRequest = new SelectionRequest();
        selectionRequest.ids = mealKitIds;

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(selectionRequest)
            .post(context.getURI() + "mealKits:select");
    }

    private void selectAndConfirmMealKitsToPrepare(String accountToken, List<String> mealKitIds) {
        selectMealKitsToPrepare(accountToken, mealKitIds);

        ConfirmCookedRequest confirmCookedRequest = new ConfirmCookedRequest();
        confirmCookedRequest.ids = mealKitIds;

        given().contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + accountToken).body(confirmCookedRequest)
            .post(context.getURI() + "mealKits:confirmPreparation");
    }

    private String loginAsUser() {
        LoginRequest loginRequest =
            new LoginRequestFixture().withEmail(TestIdentityManagementSeed.CLIENT_EMAIL).withPassword(TestIdentityManagementSeed.CLIENT_PASSWORD).build();
        Response loginResponse = given().contentType(MediaType.APPLICATION_JSON).body(loginRequest).post(context.getURI() + "users:login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }
}
