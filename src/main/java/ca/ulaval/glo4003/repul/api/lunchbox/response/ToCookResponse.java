package ca.ulaval.glo4003.repul.api.lunchbox.response;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;

public record ToCookResponse(List<Lunchbox> lunchboxes, List<Ingredient> totalIngredients) {
}
