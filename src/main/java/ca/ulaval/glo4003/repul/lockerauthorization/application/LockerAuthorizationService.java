package ca.ulaval.glo4003.repul.lockerauthorization.application;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemPersister;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;

public class LockerAuthorizationService {
    private final LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister;
    private final RepULEventBus eventBus;

    public LockerAuthorizationService(RepULEventBus eventBus, LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister) {
        this.lockerAuthorizationSystemPersister = lockerAuthorizationSystemPersister;
        this.eventBus = eventBus;
    }

    public void createOrder(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId, MealKitUniqueIdentifier mealKitId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemPersister.get();

        lockerAuthorizationSystem.createOrder(subscriberId, subscriptionId, mealKitId);

        lockerAuthorizationSystemPersister.save(lockerAuthorizationSystem);
    }

    public void assignLockerToMealKit(MealKitUniqueIdentifier mealKitId, LockerId lockerId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemPersister.get();

        lockerAuthorizationSystem.assignLocker(mealKitId, lockerId);

        lockerAuthorizationSystemPersister.save(lockerAuthorizationSystem);
    }

    public void unassignLocker(MealKitUniqueIdentifier mealKitId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemPersister.get();

        lockerAuthorizationSystem.unassignLocker(mealKitId);

        lockerAuthorizationSystemPersister.save(lockerAuthorizationSystem);
    }

    public void registerSubscriberCardNumber(SubscriberUniqueIdentifier subscriberId, SubscriberCardNumber subscriberCardNumber) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemPersister.get();

        lockerAuthorizationSystem.registerSubscriberCardNumber(subscriberId, subscriberCardNumber);

        lockerAuthorizationSystemPersister.save(lockerAuthorizationSystem);
    }

    public void openLocker(OpenLockerQuery openLockerQuery) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemPersister.get();

        MealKitDto mealKitDto = lockerAuthorizationSystem.authorize(openLockerQuery.lockerId(), openLockerQuery.subscriberCardNumber());

        lockerAuthorizationSystemPersister.save(lockerAuthorizationSystem);

        eventBus.publish(new MealKitPickedUpByUserEvent(mealKitDto));
    }
}
