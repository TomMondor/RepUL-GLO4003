package ca.ulaval.glo4003.repul.small.domain.account.subscription.order.lunchbox;

import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;
import ca.ulaval.glo4003.repul.domain.exception.InvalidLunchboxException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class LunchboxTest {
    private static final List<Recipe> EMPTY_RECIPES = List.of();

    @Test
    public void givenEmptyRecipes_whenCreateLunchbox_shouldThrowInvalidLunchboxException() {
        assertThrows(InvalidLunchboxException.class, () -> new Lunchbox(EMPTY_RECIPES));
    }
}
