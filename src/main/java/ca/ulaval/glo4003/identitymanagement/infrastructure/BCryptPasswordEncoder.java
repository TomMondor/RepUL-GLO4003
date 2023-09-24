package ca.ulaval.glo4003.identitymanagement.infrastructure;

import ca.ulaval.glo4003.identitymanagement.domain.Password;
import ca.ulaval.glo4003.identitymanagement.domain.PasswordEncoder;

public class BCryptPasswordEncoder implements PasswordEncoder {
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    @Override
    public Password encode(Password password) {
        return new Password(encoder.encode(password.value()));
    }

    @Override
    public boolean matches(Password password, Password encodedPassword) {
        return encoder.matches(password.value(), encodedPassword.value());
    }
}
