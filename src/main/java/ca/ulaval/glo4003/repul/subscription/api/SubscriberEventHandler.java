package ca.ulaval.glo4003.repul.subscription.api;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;

import com.google.common.eventbus.Subscribe;

public class SubscriberEventHandler {
    private final SubscriberService subscriberService;
    private final SubscriptionService subscriptionService;

    public SubscriberEventHandler(SubscriberService subscriberService, SubscriptionService subscriptionService) {
        this.subscriberService = subscriberService;
        this.subscriptionService = subscriptionService;
    }

    @Subscribe
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        SubscriberUniqueIdentifier subscriberUniqueIdentifier =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(userCreatedEvent.userId);
        subscriberService.createSubscriber(subscriberUniqueIdentifier, userCreatedEvent.idul, userCreatedEvent.name, userCreatedEvent.birthdate,
            userCreatedEvent.gender, userCreatedEvent.email);
    }

    @Subscribe
    public void handleMealKitPickedUpByUserEvent(MealKitPickedUpByUserEvent event) {
        subscriptionService.orderPickedUpByUser(event.mealKitId);
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent event) {
        subscriptionService.markMealKitAsToDeliver(event.mealKits);
    }

    @Subscribe
    public void handleRecallCookedMealKitEvent(RecallCookedMealKitEvent recallCookedMealKitEvent) {
        subscriptionService.recallMealKitToKitchen(recallCookedMealKitEvent.mealKitId);
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        subscriptionService.markMealKitAsInDelivery(event.mealKitIds);
    }

    @Subscribe
    public void handleCanceledCargoEvent(CanceledCargoEvent event) {
        subscriptionService.recallMealKitInDelivery(event.mealKitIds);
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent event) {
        subscriptionService.confirmMealKitDelivery(event.mealKitId);
    }

    @Subscribe
    public void handleRecalledDeliveryEvent(RecalledDeliveryEvent event) {
        subscriptionService.recallMealKitDelivered(event.mealKitId);
    }
}
