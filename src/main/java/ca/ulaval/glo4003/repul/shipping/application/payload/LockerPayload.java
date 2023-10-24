package ca.ulaval.glo4003.repul.shipping.application.payload;

import ca.ulaval.glo4003.repul.shipping.domain.LockerId;

public record LockerPayload(String lockerId) {
    public static LockerPayload from(LockerId lockerId) {
        if (lockerId == null) {
            return new LockerPayload("To be determined");
        } else {
            return new LockerPayload(lockerId.id());
        }
    }
}
