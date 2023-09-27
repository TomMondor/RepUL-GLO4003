package ca.ulaval.glo4003.identitymanagement.fixture;

import ca.ulaval.glo4003.identitymanagement.api.request.LoginRequest;

public class LoginRequestFixture {
    private String email = "anEmail@ulaval.ca";
    private String password = "aPassword";

    public LoginRequestFixture withEmail(String email) {
        this.email = email;
        return this;
    }

    public LoginRequestFixture withPassword(String password) {
        this.password = password;
        return this;
    }

    public LoginRequest build() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.email = email;
        loginRequest.password = password;

        return loginRequest;
    }
}
