package ca.ulaval.glo4003.repul.notification.application;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.notification.application.exception.DeliveryPersonAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.application.exception.UserAccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccount;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.user.application.event.AccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

import com.google.common.eventbus.Subscribe;

public class NotificationService {
    private final UserAccountRepository userAccountRepository;
    private final DeliveryPersonAccountRepository deliveryPersonAccountRepository;
    private final NotificationSender notificationSender;
    private final MessageFactory messageFactory = new MessageFactory();

    public NotificationService(UserAccountRepository userAccountRepository,
                               DeliveryPersonAccountRepository deliveryPersonAccountRepository,
                               NotificationSender notificationSender) {
        this.userAccountRepository = userAccountRepository;
        this.deliveryPersonAccountRepository = deliveryPersonAccountRepository;
        this.notificationSender = notificationSender;
    }

    @Subscribe
    public void handleUserAccountCreated(AccountCreatedEvent accountCreatedEvent) {
        UserAccount userAccount = new UserAccount(accountCreatedEvent.accountId, accountCreatedEvent.email);
        userAccountRepository.saveOrUpdate(userAccount);
    }

    @Subscribe
    public void handleDeliveryAccountCreated(DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent) {
        DeliveryPersonAccount deliveryPersonAccount =
            new DeliveryPersonAccount(deliveryPersonAccountCreatedEvent.accountId, deliveryPersonAccountCreatedEvent.email);
        deliveryPersonAccountRepository.saveOrUpdate(deliveryPersonAccount);
    }

    @Subscribe
    public void handleMealKitReceivedForDeliveryEvent(MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent) {
        String message = messageFactory.createReadyToBeDeliveredMessage(mealKitReceivedForDeliveryEvent.cargoId,
            mealKitReceivedForDeliveryEvent.kitchenLocationId,
            mealKitReceivedForDeliveryEvent.mealKitDtos);
        for (UniqueIdentifier availableShipperId : mealKitReceivedForDeliveryEvent.availableDeliveryPeople) {
            Account account = deliveryPersonAccountRepository.getByAccountId(availableShipperId)
                .orElseThrow(DeliveryPersonAccountNotFoundException::new);
            notificationSender.send(account, message);
        }
    }

    @Subscribe
    public void handleMealKitDeliveredEvent(ConfirmedDeliveryEvent confirmedDeliveryEvent) {
        String message = messageFactory.createDeliveredMessage(confirmedDeliveryEvent.mealKitId,
            confirmedDeliveryEvent.deliveryLocationId, confirmedDeliveryEvent.deliveryTime,
            confirmedDeliveryEvent.lockerId);
        UserAccount userAccount = userAccountRepository
            .getAccountByMealKitId(confirmedDeliveryEvent.mealKitId)
            .orElseThrow(UserAccountNotFoundException::new);
        notificationSender.send(userAccount, message);
    }

    @Subscribe
    public void handleMealKitConfirmedEvent(MealKitConfirmedEvent mealKitConfirmedEvent) {
        UserAccount userAccount = userAccountRepository
            .getAccountById(mealKitConfirmedEvent.accountId)
            .orElseThrow(UserAccountNotFoundException::new);
        userAccount.addMealKit(mealKitConfirmedEvent.mealKitId);
        userAccountRepository.saveOrUpdate(userAccount);
    }
}
