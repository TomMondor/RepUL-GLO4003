package ca.ulaval.glo4003.identitymanagement.infrastructure;

import java.util.HashMap;
import java.util.Optional;

import ca.ulaval.glo4003.commons.domain.Email;
import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.User;
import ca.ulaval.glo4003.identitymanagement.domain.UserRepository;
import ca.ulaval.glo4003.identitymanagement.domain.exception.UserNotFoundException;

public class InMemoryUserRepository implements UserRepository {
    private final HashMap<Email, User> users = new HashMap<>();

    @Override
    public User findByEmail(Email email) {
        return Optional.ofNullable(users.get(email)).orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public User findByUid(UniqueIdentifier uid) {
        return users.values().stream().filter(user -> user.getUid().equals(uid)).findFirst().orElseThrow(() -> new UserNotFoundException());
    }

    @Override
    public boolean exists(Email email) {
        return users.containsKey(email);
    }

    @Override
    public void saveOrUpdate(User user) {
        users.put(user.getEmail(), user);
    }
}
