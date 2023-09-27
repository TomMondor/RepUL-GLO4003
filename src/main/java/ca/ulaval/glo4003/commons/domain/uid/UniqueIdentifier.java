package ca.ulaval.glo4003.commons.domain.uid;

import java.util.UUID;

public record UniqueIdentifier(UUID value) {
    public static UniqueIdentifier from(String id) {
        return new UniqueIdentifier(UUID.fromString(id));
    }
}
