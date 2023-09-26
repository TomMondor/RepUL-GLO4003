package ca.ulaval.glo4003.repul.domain.catalog;

import ca.ulaval.glo4003.repul.domain.exception.InvalidIngredientTypeException;

public record IngredientInformation(String name, Amount price) {
    public IngredientInformation {
        if (name.isBlank()) {
            throw new InvalidIngredientTypeException();
        }
    }
}
