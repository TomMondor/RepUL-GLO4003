package ca.ulaval.glo4003.repul.domain.catalog;

import ca.ulaval.glo4003.repul.domain.exception.InvalidLocationIdException;

public record LocationId(String value) {
    public LocationId {
        if (value == null) {
            throw new InvalidLocationIdException();
        } else if (value.isBlank()) {
            throw new InvalidLocationIdException();
        }
    }
}
