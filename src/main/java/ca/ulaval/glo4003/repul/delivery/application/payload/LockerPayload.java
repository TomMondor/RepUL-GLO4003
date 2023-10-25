package ca.ulaval.glo4003.repul.delivery.application.payload;

import java.util.Optional;

import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public record LockerPayload(String lockerId) {
    public static LockerPayload from(Optional<LockerId> lockerId) {
        return lockerId.map(id -> new LockerPayload(id.id())).orElseGet(() -> new LockerPayload("To be determined"));
    }
}
