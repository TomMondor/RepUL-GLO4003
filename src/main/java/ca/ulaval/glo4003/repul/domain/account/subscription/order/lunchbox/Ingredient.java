package ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox;

import java.util.Objects;

import ca.ulaval.glo4003.repul.domain.exception.IngredientsMissmatchException;

public record Ingredient(String name, Quantity quantity) {
    public Ingredient add(Ingredient ingredientToAdd) {
        if (!Objects.equals(ingredientToAdd.name(), name)) {
            throw new IngredientsMissmatchException();
        }
        return new Ingredient(name, quantity.add(ingredientToAdd.quantity()));
    }
}
