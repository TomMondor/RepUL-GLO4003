package ca.ulaval.glo4003.repul.shipping.domain;

import ca.ulaval.glo4003.repul.shipping.domain.exception.InvalidLockerIdException;

public record LockerId(String id, int lockerNumber) {
    public LockerId {
        if (id == null || id.isBlank() || lockerNumber <= 0) {
            throw new InvalidLockerIdException();
        }
    }
}
