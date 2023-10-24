package ca.ulaval.glo4003.repul.cooking.domain;

import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidQuantityUnitException;
import ca.ulaval.glo4003.repul.cooking.domain.exception.InvalidQuantityValueException;

public record Quantity(double value, String unit) {
    public Quantity {
        if (value <= 0) {
            throw new InvalidQuantityValueException();
        }
    }

    public Quantity add(Quantity quantityToAdd) {
        if (unit.equals(quantityToAdd.unit())) {
            return new Quantity(this.value + quantityToAdd.value(), unit);
        }
        throw new InvalidQuantityUnitException();
    }
}
