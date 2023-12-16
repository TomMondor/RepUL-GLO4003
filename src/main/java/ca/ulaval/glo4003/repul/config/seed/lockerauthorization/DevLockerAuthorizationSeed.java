package ca.ulaval.glo4003.repul.config.seed.lockerauthorization;

import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemPersister;

public class DevLockerAuthorizationSeed extends LockerAuthorizationSeed {
    public DevLockerAuthorizationSeed(LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister) {
        super(lockerAuthorizationSystemPersister);
    }

    @Override
    public void populate() {
        // No data to populate
    }
}
