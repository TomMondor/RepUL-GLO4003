package ca.ulaval.glo4003.repul.commons.domain;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;

public record DeliveryLocationId(String value) {
    public DeliveryLocationId {
        if (value == null || value.isBlank()) {
            throw new InvalidLocationIdException();
        }
    }
}
