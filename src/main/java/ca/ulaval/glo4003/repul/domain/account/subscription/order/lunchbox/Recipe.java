package ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.exception.InvalidRecipeException;

public record Recipe(String name, int calories, List<Ingredient> ingredients) {
    public Recipe {
        if (name.isBlank() || calories <= 0 || ingredients.isEmpty()) {
            throw new InvalidRecipeException();
        }
    }
}
