package ca.ulaval.glo4003.repul.config.seed.identitymanagement;

import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.UserRepository;

public abstract class IdentityManagementSeed implements Seed {
    protected final UserFactory userFactory;
    protected final UserRepository userRepository;

    protected IdentityManagementSeed(UserFactory userFactory, UserRepository userRepository) {
        this.userFactory = userFactory;
        this.userRepository = userRepository;
    }
}
