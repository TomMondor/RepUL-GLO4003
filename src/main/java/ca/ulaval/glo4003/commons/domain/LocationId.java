package ca.ulaval.glo4003.commons.domain;

import ca.ulaval.glo4003.commons.domain.exception.InvalidLocationIdException;

public record LocationId(String value) {
    public LocationId {
        if (value == null || value.isBlank()) {
            throw new InvalidLocationIdException();
        }
    }
}
