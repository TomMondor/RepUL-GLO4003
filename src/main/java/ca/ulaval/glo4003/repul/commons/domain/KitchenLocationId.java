package ca.ulaval.glo4003.repul.commons.domain;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;

public record KitchenLocationId(
    String value
) {
    public KitchenLocationId {
        if (value == null || value.isBlank()) {
            throw new InvalidLocationIdException();
        }
    }
}
