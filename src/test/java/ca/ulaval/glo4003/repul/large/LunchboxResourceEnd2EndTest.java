package ca.ulaval.glo4003.repul.large;

import java.util.ArrayList;

import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.fixture.ServerFixture;
import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;
import ca.ulaval.glo4003.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.identitymanagement.fixture.LoginRequestFixture;
import ca.ulaval.glo4003.repul.api.account.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.api.lunchbox.response.ToCookResponse;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.fixture.RegistrationRequestFixture;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LunchboxResourceEnd2EndTest {
    private static final ApplicationContext context = new DevApplicationContext();
    private static final String ACCOUNT_EMAIL = "myAccountEmail@ulaval.ca";
    private static final String ACCOUNT_PASSWORD = "secret123";
    private static final String ACCOUNT_IDUL = "ALMAT69";
    private static final String ACCOUNT_NAME = "Bob Math";
    private static final String ACCOUNT_BIRTHDATE = "1969-04-20";
    private static final String ACCOUNT_GENDER = "MAN";

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
    public void whenGettingLunchboxToCook_shouldReturn200WithEmptyLunchboxAndIngredient() {
        String accountToken = registerAndLoginAccount();

        Response response = given().header("Authorization", "Bearer " + accountToken).get(context.getURI() + "api/lunchboxes/to-cook");

        assertEquals(200, response.getStatusCode());
        assertEquals(new ArrayList<Lunchbox>(), response.getBody().as(ToCookResponse.class).lunchboxes());
        assertEquals(new ArrayList<Ingredient>(), response.getBody().as(ToCookResponse.class).totalIngredients());
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
