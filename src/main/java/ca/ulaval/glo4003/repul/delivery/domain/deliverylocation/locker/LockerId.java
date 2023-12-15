package ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.locker;

import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidLockerIdException;

public record LockerId(
    String id,
    int lockerNumber
) {
    public LockerId {
        if (id == null || id.isBlank() || lockerNumber <= 0) {
            throw new InvalidLockerIdException();
        }
    }
}
