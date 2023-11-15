package ca.ulaval.glo4003.repul.commons.domain.uid;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UniqueIdentifierFactory {
    public UniqueIdentifier generate() {
        return new UniqueIdentifier(UUID.randomUUID());
    }

    public UniqueIdentifier generateFrom(String id) {
        return new UniqueIdentifier(UUID.fromString(id));
    }

    public List<UniqueIdentifier> generateFrom(List<String> ids) {
        return ids.stream().map(this::generateFrom).collect(Collectors.toList());
    }
}
