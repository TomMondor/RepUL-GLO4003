package ca.ulaval.glo4003.repul.small.api.lunchbox;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.api.lunchbox.assembler.LunchboxesResponseAssembler;
import ca.ulaval.glo4003.repul.api.lunchbox.response.ToCookResponse;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.fixture.IngredientFixture;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;
import ca.ulaval.glo4003.repul.fixture.RecipeFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LunchboxesResponseAssemblerTest {
    private static final List<Ingredient> DEFAULT_INGREDIENT = List.of(new IngredientFixture().build());
    private static final Lunchbox A_LUNCHBOX = new LunchboxFixture()
        .withRecipes(List.of(new RecipeFixture().withIngredients(DEFAULT_INGREDIENT).build())).build();
    private LunchboxesResponseAssembler lunchboxesResponseAssembler;

    @BeforeEach
    public void createLunchboxesResponseAssembler() {
        this.lunchboxesResponseAssembler = new LunchboxesResponseAssembler();
    }

    @Test
    public void givenALunchbox_whenAssemblingResponse_shouldReturnToCookResponseWithLunchboxAndIngredient() {
        ToCookResponse toCookResponse = lunchboxesResponseAssembler.toToCookResponse(List.of(A_LUNCHBOX));

        assertEquals(toCookResponse.lunchboxes(), List.of(A_LUNCHBOX));
        assertEquals(toCookResponse.totalIngredients(), A_LUNCHBOX.recipes().get(0).ingredients());
    }

    @Test
    public void givenMultipleLunchboxWithSameIngredient_whenAssemblingResponse_shouldAddIngredientQuantity() {
        ToCookResponse toCookResponse = lunchboxesResponseAssembler.toToCookResponse(List.of(
            A_LUNCHBOX, A_LUNCHBOX));
        Ingredient expectedIngredient = new Ingredient(A_LUNCHBOX.recipes().get(0).ingredients().get(0).name(),
            new Quantity(A_LUNCHBOX.recipes().get(0).ingredients().get(0).quantity().value() * 2,
                A_LUNCHBOX.recipes().get(0).ingredients().get(0).quantity().unit()));

        assertEquals(toCookResponse.lunchboxes(), List.of(A_LUNCHBOX, A_LUNCHBOX));
        assertEquals(toCookResponse.totalIngredients(), List.of(expectedIngredient));
    }
}
