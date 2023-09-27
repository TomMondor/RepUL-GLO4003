package ca.ulaval.glo4003.commons.domain.uid;

import java.util.UUID;

public class UniqueIdentifierFactory {
    public UniqueIdentifier generate() {
        return new UniqueIdentifier(UUID.randomUUID());
    }

    public UniqueIdentifier generateFrom(String id) {
        return new UniqueIdentifier(UUID.fromString(id));
    }
}
