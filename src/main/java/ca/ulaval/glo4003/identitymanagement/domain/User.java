package ca.ulaval.glo4003.identitymanagement.domain;

import ca.ulaval.glo4003.commons.Email;
import ca.ulaval.glo4003.commons.uid.UniqueIdentifier;

public class User {
    private final UniqueIdentifier uid;
    private final Email email;
    private final Password password;

    public User(UniqueIdentifier uid, Email email, Password password) {
        this.uid = uid;
        this.email = email;
        this.password = password;
    }

    public UniqueIdentifier getUid() {
        return uid;
    }

    public Email getEmail() {
        return email;
    }
}
