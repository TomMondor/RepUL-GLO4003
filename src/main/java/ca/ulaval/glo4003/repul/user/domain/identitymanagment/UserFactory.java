package ca.ulaval.glo4003.repul.user.domain.identitymanagment;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UniqueIdentifier uid, Email email, Role role, Password password) {
        return new User(uid, email, encryptPassword(password), role, passwordEncoder);
    }

    private Password encryptPassword(Password password) {
        return passwordEncoder.encode(password);
    }
}
