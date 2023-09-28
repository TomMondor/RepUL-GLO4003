package ca.ulaval.glo4003.identitymanagement.domain;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;

public interface UserRepository {
    User findByEmail(Email email);

    User findByUid(UniqueIdentifier uid);

    boolean exists(Email email);

    void saveOrUpdate(User user);
}
