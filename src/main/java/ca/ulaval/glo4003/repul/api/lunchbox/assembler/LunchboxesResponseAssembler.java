package ca.ulaval.glo4003.repul.api.lunchbox.assembler;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.api.lunchbox.response.ToCookResponse;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;

public class LunchboxesResponseAssembler {
    public ToCookResponse toToCookResponse(List<Lunchbox> lunchboxes) {
        List<Ingredient> combinedIngredients = new ArrayList<>();
        List<Ingredient> allIngredients = getAllIngredients(lunchboxes);
        for (Ingredient ingredient : allIngredients) {
            boolean foundInList = false;
            for (Ingredient ingredientInTotalIngredients : combinedIngredients) {
                if (ingredient.name().equals(ingredientInTotalIngredients.name())) {
                    foundInList = true;
                    Ingredient ingredientWithNewQuantity = ingredientInTotalIngredients.add(ingredient);
                    combinedIngredients.remove(ingredientInTotalIngredients);
                    combinedIngredients.add(ingredientWithNewQuantity);
                    break;
                }
            }
            if (!foundInList) {
                combinedIngredients.add(ingredient);
            }
        }
        return new ToCookResponse(lunchboxes, combinedIngredients);
    }

    private List<Ingredient> getAllIngredients(List<Lunchbox> lunchboxes) {
        List<Ingredient> allIngredients = new ArrayList<>();
        for (Lunchbox lunchbox : lunchboxes) {
            for (Recipe recipe : lunchbox.recipes()) {
                allIngredients.addAll(recipe.ingredients());
            }
        }
        return allIngredients;
    }
}
