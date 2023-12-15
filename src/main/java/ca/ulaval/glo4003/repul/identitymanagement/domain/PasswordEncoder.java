package ca.ulaval.glo4003.repul.identitymanagement.domain;

public interface PasswordEncoder {
    Password encode(Password password);

    boolean matches(Password providedPassword, Password encodedPassword);
}
