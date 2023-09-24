package ca.ulaval.glo4003.identitymanagement.domain;

import ca.ulaval.glo4003.commons.Email;

public interface UserRepository {
    User findByEmail(Email email);

    boolean exists(Email email);

    void saveOrUpdate(User user);
}
