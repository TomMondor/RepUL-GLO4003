package ca.ulaval.glo4003.repul.lockerauthorization.application;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.lockerauthorization.application.exception.LockerAuthorizationSystemNotFoundException;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystem;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemRepository;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.UserCardAddedEvent;

import com.google.common.eventbus.Subscribe;

public class LockerAuthorizationService {
    private final LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository;
    private final RepULEventBus eventBus;

    public LockerAuthorizationService(RepULEventBus eventBus, LockerAuthorizationSystemRepository lockerAuthorizationSystemRepository) {
        this.lockerAuthorizationSystemRepository = lockerAuthorizationSystemRepository;
        this.eventBus = eventBus;
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get()
            .orElseThrow(LockerAuthorizationSystemNotFoundException::new);

        lockerAuthorizationSystem.createOrder(mealKitConfirmedEvent.accountId, mealKitConfirmedEvent.mealKitId);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent confirmedDeliveryEvent) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get()
            .orElseThrow(LockerAuthorizationSystemNotFoundException::new);

        lockerAuthorizationSystem.assignLocker(
            confirmedDeliveryEvent.mealKitId,
            new LockerId(confirmedDeliveryEvent.lockerId.id()));

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    @Subscribe
    public void handleRecalledDeliveryEvent(RecalledDeliveryEvent recalledDeliveryEvent) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get()
            .orElseThrow(LockerAuthorizationSystemNotFoundException::new);

        lockerAuthorizationSystem.unassignLocker(recalledDeliveryEvent.mealKitId);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    @Subscribe
    public void handleUserCardAddedEvent(UserCardAddedEvent userCardAddedEvent) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get()
            .orElseThrow(LockerAuthorizationSystemNotFoundException::new);

        lockerAuthorizationSystem.registerUserCardNumber(userCardAddedEvent.accountId, userCardAddedEvent.userCardNumber);

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);
    }

    public void openLocker(OpenLockerQuery openLockerQuery) {
        LockerAuthorizationSystem lockerAuthorizationSystem = lockerAuthorizationSystemRepository.get()
            .orElseThrow(LockerAuthorizationSystemNotFoundException::new);

        UniqueIdentifier mealKitId = lockerAuthorizationSystem.authorize(openLockerQuery.lockerId(), openLockerQuery.userCardNumber());

        lockerAuthorizationSystemRepository.save(lockerAuthorizationSystem);

        eventBus.publish(new MealKitPickedUpByUserEvent(mealKitId));
    }
}
