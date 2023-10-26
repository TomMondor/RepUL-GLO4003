package ca.ulaval.glo4003.repul.config.initializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.domain.Ingredient;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;
import ca.ulaval.glo4003.repul.cooking.domain.Quantity;
import ca.ulaval.glo4003.repul.cooking.domain.Recipe;
import ca.ulaval.glo4003.repul.cooking.domain.RecipesCatalog;
import ca.ulaval.glo4003.repul.cooking.infrastructure.InMemoryKitchenRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CookingContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookingContextInitializer.class);
    private static final String STANDARD_MEALKIT_FILE_PATH = "src/main/resources/standard-meal-box.json";
    private static final String RECIPES_FIELD_NAME_IN_JSON = "recipes";
    private static final String RECIPE_NAME_FIELD_NAME_IN_JSON = "name";
    private static final String RECIPE_CALORIES_FIELD_NAME_IN_JSON = "calories";
    private static final String RECIPE_INGREDIENTS_FIELD_NAME_IN_JSON = "ingredients";
    private static final String RECIPE_INGREDIENT_NAME_FIELD_NAME_IN_JSON = "ingredient";
    private static final String RECIPE_INGREDIENT_QUANTITY_FIELD_NAME_IN_JSON = "quantity";

    private KitchenRepository kitchenRepository = new InMemoryKitchenRepository();
    private List<Order> mealKitsToAdd = List.of();

    public CookingContextInitializer withKitchenRepository(KitchenRepository kitchenRepository) {
        this.kitchenRepository = kitchenRepository;
        return this;
    }

    public CookingContextInitializer withMealKits(List<Order> orders) {
        mealKitsToAdd = orders;
        return this;
    }

    public CookingService createCookingService(RepULEventBus eventBus) {
        LOGGER.info("Creating cooking service");
        initializeKitchen(kitchenRepository);
        CookingService service = new CookingService(kitchenRepository, eventBus);
        eventBus.register(service);
        return service;
    }

    private void initializeKitchen(KitchenRepository kitchenRepository) {
        RecipesCatalog recipesCatalog = new RecipesCatalog(getRecipes());
        Kitchen kitchen = new Kitchen(recipesCatalog);
        mealKitsToAdd.forEach(mealKit -> kitchen.addMealKit(mealKit.getOrderId(), mealKit.getMealKitType(), mealKit.getDeliveryDate()));
        kitchenRepository.saveOrUpdate(kitchen);
    }

    private Map<MealKitType, List<Recipe>> getRecipes() {
        Map<MealKitType, List<Recipe>> recipes = new HashMap<>();
        recipes.put(MealKitType.STANDARD, parseStandardMealKitRecipes());
        return recipes;
    }

    private List<Recipe> parseStandardMealKitRecipes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object listOfRecipesMaps = objectMapper.readValue(new File(STANDARD_MEALKIT_FILE_PATH), new TypeReference<Map<String, Object>>() {
            }).get(RECIPES_FIELD_NAME_IN_JSON);
            return getRecipesFromJsonList(listOfRecipesMaps);
        } catch (IOException e) {
            LOGGER.error("Error while reading " + STANDARD_MEALKIT_FILE_PATH, e);
            throw new RuntimeException("Error while reading " + STANDARD_MEALKIT_FILE_PATH);
        }
    }

    private List<Recipe> getRecipesFromJsonList(Object listOfRecipes) {
        List<Recipe> recipes = new ArrayList<>();
        for (Map<String, Object> map : (List<Map<String, Object>>) listOfRecipes) {
            recipes.add(getRecipeFromMap(map));
        }
        return recipes;
    }

    private Recipe getRecipeFromMap(Map<String, Object> map) {
        String recipeName = (String) map.get(RECIPE_NAME_FIELD_NAME_IN_JSON);
        int recipeCalories = getCaloriesFromString((String) map.get(RECIPE_CALORIES_FIELD_NAME_IN_JSON));
        List<Map<String, Object>> listOfIngredientsMaps = (List<Map<String, Object>>) map.get(RECIPE_INGREDIENTS_FIELD_NAME_IN_JSON);
        List<Ingredient> ingredients = new ArrayList<>();
        for (Map<String, Object> ingredientMap : listOfIngredientsMaps) {
            String ingredientName = (String) ingredientMap.get(RECIPE_INGREDIENT_NAME_FIELD_NAME_IN_JSON);
            Quantity ingredientQuantity = getIngredientQuantityFromString((String) ingredientMap.get(RECIPE_INGREDIENT_QUANTITY_FIELD_NAME_IN_JSON));
            ingredients.add(new Ingredient(ingredientName, ingredientQuantity));
        }
        return new Recipe(recipeName, recipeCalories, ingredients);
    }

    private int getCaloriesFromString(String calories) {
        try {
            return Integer.parseInt(calories);
        } catch (NumberFormatException e) {
            LOGGER.error("Error while parsing calories", e);
            throw new RuntimeException("Error while parsing calories");
        }
    }

    private Quantity getIngredientQuantityFromString(String quantity) {
        String[] quantitySplit = splitQuantity(quantity);
        try {
            return new Quantity(Double.parseDouble(quantitySplit[0]), quantitySplit[1]);
        } catch (NumberFormatException e) {
            LOGGER.error("Error while parsing quantity", e);
            throw new RuntimeException("Error while parsing quantity");
        }
    }

    private String[] splitQuantity(String quantity) {
        Pattern pattern = Pattern.compile("([0-9]+)\\s*([^0-9]*)$");
        Matcher matcher = pattern.matcher(quantity.trim());

        String[] result = new String[2];
        if (matcher.find()) {
            result[0] = matcher.group(1);
            result[1] = matcher.group(2);
        } else {
            result[0] = quantity.trim();
            result[1] = "";
        }

        return result;
    }
}
