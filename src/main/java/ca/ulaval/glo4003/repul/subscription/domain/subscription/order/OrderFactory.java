package ca.ulaval.glo4003.repul.subscription.domain.subscription.order;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;

public interface OrderFactory {
    Order createSporadicOrder(Semester semester, MealKitType mealKitType);
}
