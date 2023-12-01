package ca.ulaval.glo4003.repul.payment.application;

import java.util.HashMap;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.Amount;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import com.google.common.eventbus.Subscribe;

public class PaymentService {
    private final Map<MealKitType, Amount> mealKitPrices;

    public PaymentService() {
        this.mealKitPrices = new HashMap<>();
        mealKitPrices.put(MealKitType.STANDARD, new Amount(75));
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        String message = String.format("The account with id %s has been debited $%s for a meal kit of type %s to be delivered on %s",
            mealKitConfirmedEvent.subscriberId.getUUID().toString(), mealKitPrices.get(mealKitConfirmedEvent.mealKitType).getValue(),
            mealKitConfirmedEvent.mealKitType.toString(), mealKitConfirmedEvent.deliveryDate.toString());
        System.out.println(message);
    }
}
