package ca.ulaval.glo4003.repul.notification.application;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.notification.application.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.AccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitDeliveryInfoDto;
import ca.ulaval.glo4003.repul.shipping.application.event.MealKitReceivedForDeliveryEvent;
import ca.ulaval.glo4003.repul.user.application.event.AccountCreatedEvent;
import ca.ulaval.glo4003.repul.user.application.event.DeliveryAccountCreatedEvent;

import com.google.common.eventbus.Subscribe;

public class NotificationService {
    private final AccountRepository accountRepository;
    private final NotificationSender notificationSender;

    public NotificationService(AccountRepository accountRepository, NotificationSender notificationSender) {
        this.accountRepository = accountRepository;
        this.notificationSender = notificationSender;
    }

    @Subscribe
    public void handleAccountCreated(AccountCreatedEvent accountCreatedEvent) {
        Account account = new Account(accountCreatedEvent.accountId, accountCreatedEvent.email);
        accountRepository.saveOrUpdate(account);
    }

    @Subscribe
    public void handleDeliveryAccountCreated(DeliveryAccountCreatedEvent deliveryAccountCreatedEvent) {
        Account account = new Account(deliveryAccountCreatedEvent.accountId, deliveryAccountCreatedEvent.email);
        accountRepository.saveOrUpdate(account);
    }

    @Subscribe
    public void handleMealKitReceivedForDeliveryEvent(MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent) {
        String message = createReadyToBeDeliveredMessage(mealKitReceivedForDeliveryEvent.ticketId, mealKitReceivedForDeliveryEvent.kitchenLocationId,
            mealKitReceivedForDeliveryEvent.mealKitDeliveryInfos);
        for (UniqueIdentifier availableShipperId : mealKitReceivedForDeliveryEvent.availableShippers) {
            Account account = accountRepository.getByAccountId(availableShipperId).orElseThrow(AccountNotFoundException::new);
            notificationSender.send(account, message);
        }
    }

    private String createReadyToBeDeliveredMessage(UniqueIdentifier ticketId, KitchenLocationId locationId,
                                                   List<MealKitDeliveryInfoDto> mealKitDeliveryInfoDtos) {
        String message = "Your meal kits (ticket:" + ticketId.value().toString() + ") are ready to be fetched from " + locationId.value() + "\n";
        message += "Here is the list of meal kits to be delivered:\n";
        for (MealKitDeliveryInfoDto mealKitDeliveryInfoDto : mealKitDeliveryInfoDtos) {
            String caseId;
            if (mealKitDeliveryInfoDto.caseId() == null) {
                caseId = "To Be Determined";
            } else {
                caseId = Integer.toString(mealKitDeliveryInfoDto.caseId().caseNumber());
            }
            message += "MealKit ID " + mealKitDeliveryInfoDto.mealkitId().value() + " to " + mealKitDeliveryInfoDto.shippingLocationId().value() + " in box " +
                caseId + "\n";
        }
        return message;
    }
}
