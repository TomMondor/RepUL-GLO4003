package ca.ulaval.glo4003.repul.config.seed.lockerauthorization;

import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemPersister;

public abstract class LockerAuthorizationSeed implements Seed {
    protected final LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister;

    protected LockerAuthorizationSeed(LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister) {
        this.lockerAuthorizationSystemPersister = lockerAuthorizationSystemPersister;
    }
}
