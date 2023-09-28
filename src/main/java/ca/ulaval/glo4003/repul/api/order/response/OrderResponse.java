package ca.ulaval.glo4003.repul.api.order.response;

import java.util.List;

public record OrderResponse(String deliveryDate, String orderStatus, List<String> recipeNames) {
}
