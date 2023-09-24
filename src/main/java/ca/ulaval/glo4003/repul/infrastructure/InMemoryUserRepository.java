package ca.ulaval.glo4003.repul.infrastructure;

import java.util.HashMap;

import ca.ulaval.glo4003.commons.Email;
import ca.ulaval.glo4003.repul.application.auth.UserRepository;

public class InMemoryUserRepository implements UserRepository {
    private final HashMap<Email, String> users = new HashMap<>();

    @Override
    public String getUserPassword(Email email) {
        return this.users.get(email);
    }

    @Override
    public boolean doesUserExist(Email email) {
        return this.users.get(email) != null;
    }

    @Override
    public void addUser(Email email, String password) {
        this.users.put(email, password);
    }
}
