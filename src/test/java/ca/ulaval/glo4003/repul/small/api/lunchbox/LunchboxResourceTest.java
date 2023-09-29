package ca.ulaval.glo4003.repul.small.api.lunchbox;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.api.lunchbox.LunchboxResource;
import ca.ulaval.glo4003.repul.api.lunchbox.response.ToCookResponse;
import ca.ulaval.glo4003.repul.application.lunchbox.LunchboxService;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.fixture.IngredientFixture;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;
import ca.ulaval.glo4003.repul.fixture.RecipeFixture;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LunchboxResourceTest {
    private static final List<Ingredient> DEFAULT_INGREDIENT = List.of(new IngredientFixture().build());
    private static final List<Lunchbox> LUNCHBOXES = List.of(new LunchboxFixture()
        .withRecipes(List.of(new RecipeFixture().withIngredients(DEFAULT_INGREDIENT).build())).build());
    private static final ToCookResponse A_TO_COOK_RESPONSE =  new ToCookResponse(
        LUNCHBOXES, DEFAULT_INGREDIENT);
    private LunchboxResource lunchboxResource;
    @Mock
    private LunchboxService lunchboxService;

    @BeforeEach
    public void createLunchboxResource() {
        lunchboxResource = new LunchboxResource(lunchboxService);
    }

    @Test
    public void whenGettingLunchboxesToCook_shouldGetLunchBoxes() {
        when(lunchboxService.getLunchboxesToCook()).thenReturn(LUNCHBOXES);

        lunchboxResource.getLunchboxesToCook();

        verify(lunchboxService).getLunchboxesToCook();
    }

    @Test
    public void whenGettingLunchboxesToCook_shouldReturn200OkWithToCookResponse() {
        when(lunchboxService.getLunchboxesToCook()).thenReturn(LUNCHBOXES);

        Response response = lunchboxResource.getLunchboxesToCook();

        assertEquals(response.getStatus(), 200);
        assertEquals(response.getEntity(), A_TO_COOK_RESPONSE);
    }
}
