package ca.ulaval.glo4003.identitymanagement.domain;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;

public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UniqueIdentifier uid, Email email, Password password) {
        return new User(uid, email, encryptPassword(password), passwordEncoder);
    }

    private Password encryptPassword(Password password) {
        return passwordEncoder.encode(password);
    }
}
