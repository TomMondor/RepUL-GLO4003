package ca.ulaval.glo4003.repul.notification.application;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitCookedDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.identitymanagement.application.event.DeliveryPersonAccountCreatedEvent;
import ca.ulaval.glo4003.repul.identitymanagement.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationMessage;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import com.google.common.eventbus.Subscribe;

public class NotificationService {
    private final UserAccountRepository userAccountRepository;
    private final DeliveryPersonAccountRepository deliveryPersonAccountRepository;
    private final NotificationSender notificationSender;
    private final MessageFactory messageFactory = new MessageFactory();

    public NotificationService(UserAccountRepository userAccountRepository, DeliveryPersonAccountRepository deliveryPersonAccountRepository,
                               NotificationSender notificationSender) {
        this.userAccountRepository = userAccountRepository;
        this.deliveryPersonAccountRepository = deliveryPersonAccountRepository;
        this.notificationSender = notificationSender;
    }

    @Subscribe
    public void handleUserCreated(UserCreatedEvent userCreatedEvent) {
        UserAccount userAccount = new UserAccount(userCreatedEvent.userId, userCreatedEvent.email);
        userAccountRepository.save(userAccount);
    }

    @Subscribe
    public void handleDeliveryAccountCreated(DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent) {
        DeliveryPersonAccount deliveryPersonAccount =
            new DeliveryPersonAccount(deliveryPersonAccountCreatedEvent.accountId, deliveryPersonAccountCreatedEvent.email);
        deliveryPersonAccountRepository.save(deliveryPersonAccount);
    }

    @Subscribe
    public void handleMealKitReceivedForDeliveryEvent(MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent) {
        NotificationMessage message =
            messageFactory.createReadyToBeDeliveredMessage(mealKitReceivedForDeliveryEvent.cargoId, mealKitReceivedForDeliveryEvent.kitchenLocationId,
                mealKitReceivedForDeliveryEvent.mealKitToDeliverDtos);
        for (DeliveryPersonUniqueIdentifier availableShipperId : mealKitReceivedForDeliveryEvent.availableDeliveryPeople) {
            Account account = deliveryPersonAccountRepository.getByAccountId(availableShipperId);
            notificationSender.send(account, message);
        }
    }

    @Subscribe
    public void handleConfirmedDeliveryEvent(ConfirmedDeliveryEvent confirmedDeliveryEvent) {
        NotificationMessage message = messageFactory.createDeliveredMessage(confirmedDeliveryEvent.mealKitDto.mealKitId(),
            confirmedDeliveryEvent.deliveryLocationId, confirmedDeliveryEvent.deliveryTime, confirmedDeliveryEvent.lockerId);
        UserAccount userAccount = userAccountRepository.getAccountByMealKitId(confirmedDeliveryEvent.mealKitDto.mealKitId());
        notificationSender.send(userAccount, message);
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        UserAccount userAccount = userAccountRepository.getAccountById(mealKitConfirmedEvent.subscriberId);
        userAccount.addMealKit(mealKitConfirmedEvent.mealKitId);
        userAccountRepository.save(userAccount);
    }

    @Subscribe
    public void handleMealKitsCookedEvent(MealKitsCookedEvent event) {
        KitchenLocationId kitchenLocationId = KitchenLocationId.valueOf(event.kitchenLocationId);

        for (MealKitCookedDto mealKitCookedDto : event.mealKits) {
            if (!mealKitCookedDto.isToBeDelivered()) {
                MealKitUniqueIdentifier mealKitId = mealKitCookedDto.mealKitDto().mealKitId();
                NotificationMessage message = messageFactory.createMealKitAvailableForPickUpMessage(mealKitId, kitchenLocationId);
                UserAccount userAccount = userAccountRepository.getAccountByMealKitId(mealKitId);
                notificationSender.send(userAccount, message);
            }
        }
    }
}
