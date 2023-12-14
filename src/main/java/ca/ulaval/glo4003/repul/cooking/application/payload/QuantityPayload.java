package ca.ulaval.glo4003.repul.cooking.application.payload;

import ca.ulaval.glo4003.repul.cooking.domain.Quantity;

public record QuantityPayload(
    double value,
    String unit
) {
    public static QuantityPayload from(Quantity quantity) {
        return new QuantityPayload(
            quantity.getValue(),
            quantity.getUnit()
        );
    }
}
