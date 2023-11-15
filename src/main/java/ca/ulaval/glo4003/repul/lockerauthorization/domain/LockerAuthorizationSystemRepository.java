package ca.ulaval.glo4003.repul.lockerauthorization.domain;

import java.util.Optional;

public interface LockerAuthorizationSystemRepository {
    void saveOrUpdate(LockerAuthorizationSystem deliverySystem);

    Optional<LockerAuthorizationSystem> get();
}
