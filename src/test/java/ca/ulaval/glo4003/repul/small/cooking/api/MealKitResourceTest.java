package ca.ulaval.glo4003.repul.small.cooking.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.cooking.api.MealKitResource;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.cooking.domain.mealkit.MealKit;
import ca.ulaval.glo4003.repul.cooking.domain.recipe.Ingredient;
import ca.ulaval.glo4003.repul.fixture.cooking.IngredientFixture;
import ca.ulaval.glo4003.repul.fixture.cooking.MealKitFixture;
import ca.ulaval.glo4003.repul.fixture.cooking.RecipeFixture;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MealKitResourceTest {
    private static final List<Ingredient> DEFAULT_INGREDIENT = List.of(new IngredientFixture().build());
    private static final MealKit MEAL_KIT = new MealKitFixture().withRecipes(List.of(new RecipeFixture().withIngredients(DEFAULT_INGREDIENT).build())).build();
    private static final MealKitsPayload MEAL_KITS_PAYLOAD = MealKitsPayload.from(List.of(MEAL_KIT));
    private MealKitResource mealKitResource;
    @Mock
    private CookingService cookingService;

    @BeforeEach
    public void createCookingResource() {
        mealKitResource = new MealKitResource(cookingService);
    }

    @Test
    public void whenGettingMealKitsToPrepare_shouldReturn200OkWithToMealKitsPayload() {
        when(cookingService.getMealKitsToPrepare()).thenReturn(MEAL_KITS_PAYLOAD);

        Response response = mealKitResource.getMealKitsToPrepare();

        assertEquals(response.getStatus(), 200);
        assertEquals(response.getEntity(), MEAL_KITS_PAYLOAD);
    }
}
