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
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;

public class LockerAuthorizationService {
    private final LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository;
    private final RepULEventBus eventBus;

    public LockerAuthorizationService(RepULEventBus eventBus, LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository) {
        this.lockerAuthorizationSystemRepository = lockerAuthorizationSystemRepository;
        this.eventBus = eventBus;
    }

    public void createOrder(SubscriberUniqueIdentifier subscriberId, SubscriptionUniqueIdentifier subscriptionId, MealKitUniqueIdentifier mealKitId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        lockerAuthorizationSystem.createOrder(subscriberId, subscriptionId, mealKitId);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void assignLockerToMealKit(MealKitUniqueIdentifier mealKitId, LockerId lockerId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        lockerAuthorizationSystem.assignLocker(mealKitId, lockerId);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void unassignLocker(MealKitUniqueIdentifier mealKitId) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        lockerAuthorizationSystem.unassignLocker(mealKitId);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void registerSubscriberCardNumber(SubscriberUniqueIdentifier subscriberId, SubscriberCardNumber subscriberCardNumber) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        lockerAuthorizationSystem.registerSubscriberCardNumber(subscriberId, subscriberCardNumber);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void openLocker(OpenLockerQuery openLockerQuery) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get();

        MealKitDto mealKitDto = lockerAuthorizationSystem.authorize(openLockerQuery.lockerId(), openLockerQuery.subscriberCardNumber());

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);

        eventBus.publish(new MealKitPickedUpByUserEvent(mealKitDto));
    }
}
