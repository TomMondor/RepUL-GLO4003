package ca.ulaval.glo4003.repul.cooking.api.assembler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.api.response.IngredientResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.MealKitResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.QuantityResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.RecipeResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.SelectionResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.ToCookResponse;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;

public class MealKitsResponseAssembler {
    public SelectionResponse toSelectionResponse(List<UniqueIdentifier> mealKitIds) {
        return new SelectionResponse(mealKitIds.stream().map(Record::toString).collect(Collectors.toList()));
    }

    public ToCookResponse toToCookResponse(MealKitsPayload mealKitsPayload) {
        List<Ingredient> combinedIngredients = new ArrayList<>();
        List<Ingredient> allIngredients = getAllIngredients(mealKitsPayload);
        for (Ingredient ingredient : allIngredients) {
            boolean foundInList = false;
            for (Ingredient ingredientInCombinedIngredients : combinedIngredients) {
                if (ingredient.name().equals(ingredientInCombinedIngredients.name())) {
                    foundInList = true;
                    Ingredient ingredientWithNewQuantity = ingredientInCombinedIngredients.add(ingredient.quantity());
                    combinedIngredients.remove(ingredientInCombinedIngredients);
                    combinedIngredients.add(ingredientWithNewQuantity);
                    break;
                }
            }
            if (!foundInList) {
                combinedIngredients.add(ingredient);
            }
        }

        List<MealKitResponse> mealKitResponses =
            mealKitsPayload.mealKitPayloads().stream().map(mealKitPayload -> toMealKitResponse(mealKitPayload)).collect(Collectors.toList());
        List<IngredientResponse> combinedIngredientsResponses =
            combinedIngredients.stream().map(ingredient -> toIngredientResponse(ingredient)).collect(Collectors.toList());
        return new ToCookResponse(mealKitResponses, combinedIngredientsResponses);
    }

    private List<Ingredient> getAllIngredients(MealKitsPayload mealKitsPayload) {
        List<Ingredient> allIngredients = new ArrayList<>();
        for (MealKitPayload mealKitPayload : mealKitsPayload.mealKitPayloads()) {
            for (Recipe recipe : mealKitPayload.recipes()) {
                allIngredients.addAll(recipe.ingredients());
            }
        }
        return allIngredients;
    }

    private MealKitResponse toMealKitResponse(MealKitPayload mealKitPayload) {
        return new MealKitResponse(mealKitPayload.mealKitId().value().toString(),
            mealKitPayload.recipes().stream().map(recipe -> toRecipeResponse(recipe)).collect(Collectors.toList()), mealKitPayload.deliveryDate().toString());
    }

    private RecipeResponse toRecipeResponse(Recipe recipes) {
        return new RecipeResponse(recipes.name(), recipes.calories(),
            recipes.ingredients().stream().map(ingredient -> toIngredientResponse(ingredient)).collect(Collectors.toList()));
    }

    private IngredientResponse toIngredientResponse(Ingredient ingredient) {
        return new IngredientResponse(ingredient.name(), new QuantityResponse(ingredient.quantity().value(), ingredient.quantity().unit()));
    }
}
