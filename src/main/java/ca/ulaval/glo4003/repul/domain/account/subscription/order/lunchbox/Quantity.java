package ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox;

import ca.ulaval.glo4003.repul.domain.exception.InvalidQuantityException;

public record Quantity(double value, String unit) {
    public Quantity {
        if (value <= 0) {
            throw new InvalidQuantityException();
        }
    }

    public Quantity add(Quantity quantityToAdd) {
        if (unit.equals(quantityToAdd.unit())) {
            return new Quantity(this.value + quantityToAdd.value(), unit);
        }
        throw new InvalidQuantityException();
    }
}
