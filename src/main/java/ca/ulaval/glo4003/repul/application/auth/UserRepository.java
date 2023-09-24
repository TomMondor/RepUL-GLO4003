package ca.ulaval.glo4003.repul.application.auth;

import ca.ulaval.glo4003.commons.Email;

public interface UserRepository {
    String getUserPassword(Email email);
    boolean doesUserExist(Email email);
    void addUser(Email email, String password);
}
