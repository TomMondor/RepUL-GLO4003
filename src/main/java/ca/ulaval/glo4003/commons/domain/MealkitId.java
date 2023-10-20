package ca.ulaval.glo4003.commons.domain;

import ca.ulaval.glo4003.commons.domain.exception.InvalidMealkitIdException;

public record MealkitId(String value) {
    public MealkitId {
        if (value == null || value.isBlank()) {
            throw new InvalidMealkitIdException();
        }
    }
}
