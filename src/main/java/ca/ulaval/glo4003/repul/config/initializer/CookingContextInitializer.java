package ca.ulaval.glo4003.repul.config.initializer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.config.seed.SeedFactory;
import ca.ulaval.glo4003.repul.cooking.api.MealKitEventHandler;
import ca.ulaval.glo4003.repul.cooking.api.MealKitResource;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.domain.KitchenPersister;
import ca.ulaval.glo4003.repul.cooking.domain.RecipesCatalog;
import ca.ulaval.glo4003.repul.cooking.domain.recipe.Recipe;
import ca.ulaval.glo4003.repul.cooking.infrastructure.InMemoryKitchenPersister;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CookingContextInitializer implements ContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CookingContextInitializer.class);
    private static final String STANDARD_MEALKIT_FILE_PATH = "src/main/resources/standard-meal-box.json";
    private static final String RECIPES_FIELD_NAME_IN_JSON = "recipes";
    private final RepULEventBus eventBus;
    private final SeedFactory seedFactory;
    private final KitchenPersister kitchenPersister = new InMemoryKitchenPersister();

    public CookingContextInitializer(RepULEventBus eventBus, SeedFactory seedFactory) {
        this.eventBus = eventBus;
        this.seedFactory = seedFactory;
    }

    @Override
    public void initialize(ResourceConfig resourceConfig) {
        RecipesCatalog.getInstance().setRecipes(getRecipes());
        CookingService cookingService = new CookingService(kitchenPersister, eventBus);
        MealKitResource mealKitResource = new MealKitResource(cookingService);
        createMealKitEventHandler(cookingService);
        populate();

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(mealKitResource).to(MealKitResource.class);
            }
        };

        resourceConfig.register(binder);
    }

    private void populate() {
        Seed seed = seedFactory.createCookingSeed(kitchenPersister);
        seed.populate();
    }

    private void createMealKitEventHandler(CookingService cookingService) {
        MealKitEventHandler mealKitEventHandler = new MealKitEventHandler(cookingService);
        eventBus.register(mealKitEventHandler);
    }

    private Map<MealKitType, List<Recipe>> getRecipes() {
        Map<MealKitType, List<Recipe>> recipes = new HashMap<>();
        recipes.put(MealKitType.STANDARD, parseStandardMealKitRecipes());
        return recipes;
    }

    private List<Recipe> parseStandardMealKitRecipes() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File standardMealKitFile = new File(STANDARD_MEALKIT_FILE_PATH);

            List<Recipe> listOfRecipesMaps = objectMapper.readValue(standardMealKitFile, new TypeReference<Map<String, List<Recipe>>>() {
            }).get(RECIPES_FIELD_NAME_IN_JSON);

            return listOfRecipesMaps;
        } catch (IOException e) {
            LOGGER.error("Error while reading " + STANDARD_MEALKIT_FILE_PATH, e);
            throw new RuntimeException("Error while reading " + STANDARD_MEALKIT_FILE_PATH);
        }
    }
}
