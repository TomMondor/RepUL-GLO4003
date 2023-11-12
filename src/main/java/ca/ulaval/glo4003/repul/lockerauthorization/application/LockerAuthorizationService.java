package ca.ulaval.glo4003.repul.lockerauthorization.application;

import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.UserCardAddedEvent;

import com.google.common.eventbus.Subscribe;

public class LockerAuthorizationService {
    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent confirmDeliveryEvent) {
    }

    @Subscribe
    public void handleRecalledDeliveryEvent(RecalledDeliveryEvent recalledDeliveryEvent) {
    }

    @Subscribe
    public void handleUserCardAddedEvent(UserCardAddedEvent userCardAddedEvent) {
    }

    public void openLocker(OpenLockerQuery openLockerQuery) {
    }
}
