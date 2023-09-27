package ca.ulaval.glo4003.identitymanagement.domain;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.exception.PasswordNotMatchingException;

public class User {
    private final UniqueIdentifier uid;
    private final Email email;
    private final Password password;
    private final PasswordEncoder passwordEncoder;

    public User(UniqueIdentifier uid, Email email, Password password, PasswordEncoder passwordEncoder) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.passwordEncoder = passwordEncoder;
    }

    public UniqueIdentifier getUid() {
        return uid;
    }

    public Email getEmail() {
        return email;
    }

    public void checkPasswordMatches(Password providedPassword) {
        if (!passwordEncoder.matches(providedPassword, password)) {
            throw new PasswordNotMatchingException();
        }
    }
}
