package ca.ulaval.glo4003.repul.subscription.api;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.identitymanagement.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;

import com.google.common.eventbus.Subscribe;

public class SubscriberEventHandler {
    private final SubscriberService subscriberService;

    public SubscriberEventHandler(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Subscribe
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(userCreatedEvent.userId);
        subscriberService.createSubscriber(subscriberId, userCreatedEvent.idul, userCreatedEvent.name, userCreatedEvent.birthdate,
            userCreatedEvent.gender, userCreatedEvent.email);
    }

    @Subscribe
    public void handlePickedUpCargoEvent(PickedUpCargoEvent event) {
        subscriberService.updateOrdersToInDelivery(event.mealKitDtos.stream().map(MealKitDto::mealKitId).toList());
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent event) {
        subscriberService.updateOrderToReadyToPickUp(event.mealKitDto.mealKitId());
    }

    @Subscribe
    public void handleRecalledDeliveryEvent(RecalledDeliveryEvent event) {
        subscriberService.updateOrdersToInPreparation(List.of(event.mealKitDto.mealKitId()));
    }

    @Subscribe
    public void handleMealKitPickedUpByUserEvent(MealKitPickedUpByUserEvent event) {
        subscriberService.updateOrderToPickedUp(event.mealKitDto.mealKitId());
    }
}
