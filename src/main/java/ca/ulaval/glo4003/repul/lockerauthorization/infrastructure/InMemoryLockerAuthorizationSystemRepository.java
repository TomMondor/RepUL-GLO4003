package ca.ulaval.glo4003.repul.lockerauthorization.infrastructure;

import java.util.Optional;

import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;

public class InMemoryLockerAuthorizationSystemRepository implements LockerAuthorizationSystemRepository {
    private Optional<LockerAuthorizationSystem> lockerAuthorizationSystem = Optional.empty();

    @Override
    public void saveOrUpdate(LockerAuthorizationSystem lockerAuthorizationSystem) {
        this.lockerAuthorizationSystem = Optional.of(lockerAuthorizationSystem);
    }

    @Override
    public Optional<LockerAuthorizationSystem> get() {
        return lockerAuthorizationSystem;
    }
}
