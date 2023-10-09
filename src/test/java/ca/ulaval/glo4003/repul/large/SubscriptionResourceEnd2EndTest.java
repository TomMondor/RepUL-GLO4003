package ca.ulaval.glo4003.repul.large;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;
import ca.ulaval.glo4003.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.api.account.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionCreatedResponse;
import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.catalog.SemesterCode;
import ca.ulaval.glo4003.repul.fixture.CatalogFixture;
import ca.ulaval.glo4003.repul.fixture.LoginRequestFixture;
import ca.ulaval.glo4003.repul.fixture.RegistrationRequestFixture;
import ca.ulaval.glo4003.repul.fixture.SubscriptionRequestFixture;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubscriptionResourceEnd2EndTest {
    private static final String ACCOUNT_EMAIL = "myAccountEmail@ulaval.ca";
    private static final String ACCOUNT_PASSWORD = "secret123";
    private static final String ACCOUNT_IDUL = "ALMAT69";
    private static final String ACCOUNT_NAME = "Bob Math";
    private static final String ACCOUNT_BIRTHDATE = "1969-04-20";
    private static final String ACCOUNT_GENDER = "MAN";
    private static final String SUBSCRIPTION_DAY_OF_WEEK = DayOfWeek.from(LocalDate.now().plusDays(3)).toString();
    private static final String SUBSCRIPTION_LOCATION_ID = "VACHON";
    private static final String SUBSCRIPTION_LUNCHBOX_TYPE = LunchboxType.STANDARD.toString();
    private static final ApplicationContext context = new DevApplicationContext();
    private static final Catalog catalog = new CatalogFixture()
        .withSemesters(List.of(new Semester(new SemesterCode("A23"),
            LocalDate.now().minusDays(30), LocalDate.now().plusDays(30)))).build();

    private ServerFixture server;
    private String accountToken;

    @BeforeEach
    public void setupServer() throws Exception {
        server = new ServerFixture(context);
        server.start();

        accountToken = registerAndLoginAccount();
    }

    @AfterEach
    public void closeServer() throws Exception {
        server.stop();
    }

    @Test
    public void whenCreatingSubscription_shouldReturn201() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestFixture().build();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(subscriptionRequest).when()
            .post(context.getURI() + "api/subscriptions");

        assertEquals(201, response.getStatusCode());
    }

    @Test
    public void whenCreatingSubscription_shouldReturnSubscriptionId() {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequestFixture().build();

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(subscriptionRequest).when()
            .post(context.getURI() + "api/subscriptions");

        assertNotNull(response.getBody().as(SubscriptionCreatedResponse.class).subscriptionId());
    }

    @Test
    public void whenConfirmingLunchbox_shouldReturn200() {
        String subscriptionId = givenAValidSubscription(accountToken);

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "api/subscriptions/" + subscriptionId + "/confirm");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void whenDecliningLunchbox_shouldReturn200() {
        String subscriptionId = givenAValidSubscription(accountToken);

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken)
            .post(context.getURI() + "api/subscriptions/" + subscriptionId + "/decline");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void givenAValidSubscription_whenGetSubscriptions_shouldReturn200() {
        givenAValidSubscription(accountToken);

        Response response =
            given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(context.getURI() + "api/subscriptions");

        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void givenAValidSubscription_whenGetSubscriptions_shouldReturnSubscriptionInformation() {
        String subscriptionId = givenAValidSubscription(accountToken);
        String expectedStartDate = LocalDate.now().toString();
        String expectedSemester = catalog.getCurrentSemester(LocalDate.now()).semesterCode().value();

        Response response =
            given().contentType("application/json").header("Authorization", "Bearer " + accountToken).get(context.getURI() + "api/subscriptions");

        List<SubscriptionResponse> responseBody = response.jsonPath().getList("$", SubscriptionResponse.class);

        assertEquals(subscriptionId, responseBody.get(0).subscriptionId());
        assertEquals(SUBSCRIPTION_DAY_OF_WEEK, responseBody.get(0).dayOfWeek());
        assertEquals(SUBSCRIPTION_LOCATION_ID, responseBody.get(0).locationId());
        assertEquals(expectedStartDate, responseBody.get(0).startDate());
        assertEquals(SUBSCRIPTION_LUNCHBOX_TYPE, responseBody.get(0).lunchboxType());
        assertEquals(expectedSemester, responseBody.get(0).semesterCode());
    }

    private String givenAValidSubscription(String accountToken) {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest();
        subscriptionRequest.dayOfWeek = SUBSCRIPTION_DAY_OF_WEEK;
        subscriptionRequest.locationId = SUBSCRIPTION_LOCATION_ID;
        subscriptionRequest.lunchboxType = SUBSCRIPTION_LUNCHBOX_TYPE;

        Response response = given().contentType("application/json").header("Authorization", "Bearer " + accountToken).body(subscriptionRequest)
            .post(context.getURI() + "api/subscriptions");

        return response.getBody().as(SubscriptionCreatedResponse.class).subscriptionId();
    }

    private String registerAndLoginAccount() {
        RegistrationRequest registrationRequest = createRegistrationRequestWithAccountInformation();
        LoginRequest loginRequest = new LoginRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).build();

        given().contentType("application/json").body(registrationRequest).post(context.getURI() + "api/accounts/register");
        Response loginResponse = given().contentType("application/json").body(loginRequest).post(context.getURI() + "api/auth/login");

        return loginResponse.getBody().as(LoginResponse.class).token();
    }

    private RegistrationRequest createRegistrationRequestWithAccountInformation() {
        return new RegistrationRequestFixture().withEmail(ACCOUNT_EMAIL).withPassword(ACCOUNT_PASSWORD).withBirthdate(ACCOUNT_BIRTHDATE)
            .withGender(ACCOUNT_GENDER).withName(ACCOUNT_NAME).withIdul(ACCOUNT_IDUL).build();
    }
}
