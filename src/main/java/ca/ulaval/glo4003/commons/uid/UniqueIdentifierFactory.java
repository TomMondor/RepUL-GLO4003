package ca.ulaval.glo4003.commons.uid;

import java.util.UUID;

public class UniqueIdentifierFactory {
    public UniqueIdentifier generate() {
        return new UniqueIdentifier(UUID.randomUUID());
    }
}
