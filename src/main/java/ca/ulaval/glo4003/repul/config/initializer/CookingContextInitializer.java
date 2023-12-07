package ca.ulaval.glo4003.repul.config.initializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.cooking.api.MealKitEventHandler;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.domain.Kitchen;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenRepository;
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
    private final List<Order> mealKitsToAdd = new ArrayList<>();
    private KitchenRepository kitchenRepository = new InMemoryKitchenRepository();

    public CookingContextInitializer withEmptyKitchenRepository(KitchenRepository kitchenRepository) {
        this.kitchenRepository = kitchenRepository;
        return this;
    }

    public CookingContextInitializer withMealKits(List<Order> orders) {
        mealKitsToAdd.addAll(orders);
        return this;
    }

    public CookingService createCookingService(RepULEventBus eventBus) {
        LOGGER.info("Creating cooking service");
        initializeKitchen(kitchenRepository);
        return new CookingService(kitchenRepository, eventBus);
    }

    public void createMealKitEventHandler(CookingService cookingService, RepULEventBus eventBus) {
        MealKitEventHandler mealKitEventHandler = new MealKitEventHandler(cookingService);
        eventBus.register(mealKitEventHandler);
    }

    private void initializeKitchen(KitchenRepository kitchenRepository) {
        RecipesCatalog recipesCatalog = new RecipesCatalog(getRecipes());
        Kitchen kitchen = new Kitchen(recipesCatalog);
        mealKitsToAdd.forEach(mealKit -> kitchen.addMealKit(mealKit.getOrderId(), mealKit.getMealKitType(), mealKit.getDeliveryDate()));
        kitchenRepository.save(kitchen);
    }

    private Map<MealKitType, List<Recipe>> getRecipes() {
        Map<MealKitType, List<Recipe>> recipes = new HashMap<>();
        recipes.put(MealKitType.STANDARD, parseStandardMealKitRecipes());
        return recipes;
    }

    private List<Recipe> parseStandardMealKitRecipes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Recipe> listOfRecipesMaps = objectMapper.readValue(new File(STANDARD_MEALKIT_FILE_PATH), new TypeReference<Map<String, List<Recipe>>>() {
            }).get(RECIPES_FIELD_NAME_IN_JSON);
            return listOfRecipesMaps;
        } catch (IOException e) {
            LOGGER.error("Error while reading " + STANDARD_MEALKIT_FILE_PATH, e);
            throw new RuntimeException("Error while reading " + STANDARD_MEALKIT_FILE_PATH);
        }
    }
}
