package ca.ulaval.glo4003.repul.lockerauthorization.domain;

public interface LockerAuthorizationSystemPersister {
    void save(LockerAuthorizationSystem deliverySystem);

    LockerAuthorizationSystem get();
}
