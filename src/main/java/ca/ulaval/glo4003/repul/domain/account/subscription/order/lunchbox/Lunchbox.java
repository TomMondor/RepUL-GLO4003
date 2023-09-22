package ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.exception.InvalidLunchboxException;

public record Lunchbox(List<Recipe> recipes, LunchboxType type) {
    public Lunchbox {
        if (recipes.isEmpty()) {
            throw new InvalidLunchboxException();
        }
    }
}
