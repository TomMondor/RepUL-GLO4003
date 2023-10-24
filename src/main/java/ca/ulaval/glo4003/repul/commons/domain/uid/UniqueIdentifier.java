package ca.ulaval.glo4003.repul.commons.domain.uid;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record UniqueIdentifier(UUID value) {
    public static UniqueIdentifier from(String id) {
        return new UniqueIdentifier(UUID.fromString(id));
    }

    public static List<UniqueIdentifier> from(List<String> ids) {
        return ids.stream().map(UniqueIdentifier::from).collect(Collectors.toList());
    }
}
