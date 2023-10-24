package ca.ulaval.glo4003.repul.payment.application;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.domain.Amount;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import com.google.common.eventbus.Subscribe;

public class PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);
    private final Map<MealKitType, Amount> mealKitPrices;

    public PaymentService() {
        this.mealKitPrices = new HashMap<>();
        mealKitPrices.put(MealKitType.STANDARD, new Amount(75));
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        LOGGER.info("The account with id {} has been debited ${} for a meal kit of type {} to be delivered on {}",
            mealKitConfirmedEvent.accountId.toString(),
            mealKitPrices.get(mealKitConfirmedEvent.mealKitType).getValue(),
            mealKitConfirmedEvent.mealKitType.toString(),
            mealKitConfirmedEvent.deliveryDate.toString());
    }
}
