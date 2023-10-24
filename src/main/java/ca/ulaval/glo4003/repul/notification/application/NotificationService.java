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
import ca.ulaval.glo4003.repul.user.application.event.DeliveryPersonAccountCreatedEvent;

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
    public void handleDeliveryAccountCreated(DeliveryPersonAccountCreatedEvent deliveryPersonAccountCreatedEvent) {
        Account account = new Account(deliveryPersonAccountCreatedEvent.accountId, deliveryPersonAccountCreatedEvent.email);
        accountRepository.saveOrUpdate(account);
    }

    @Subscribe
    public void handleMealKitReceivedForDeliveryEvent(MealKitReceivedForDeliveryEvent mealKitReceivedForDeliveryEvent) {
        String message = createReadyToBeDeliveredMessage(mealKitReceivedForDeliveryEvent.cargoId, mealKitReceivedForDeliveryEvent.kitchenLocationId,
            mealKitReceivedForDeliveryEvent.mealKitDeliveryInfos);
        for (UniqueIdentifier availableShipperId : mealKitReceivedForDeliveryEvent.availableShippers) {
            Account account = accountRepository.getByAccountId(availableShipperId).orElseThrow(AccountNotFoundException::new);
            notificationSender.send(account, message);
        }
    }

    private String createReadyToBeDeliveredMessage(UniqueIdentifier cargoId, KitchenLocationId locationId,
                                                   List<MealKitDeliveryInfoDto> mealKitDeliveryInfoDtos) {
        String message = "Your meal kits (cargo id: " + cargoId.value().toString() + ") are ready to be fetched from " + locationId.value() + "\n";
        message += "Here is the list of meal kits to be delivered:\n";
        for (MealKitDeliveryInfoDto mealKitDeliveryInfoDto : mealKitDeliveryInfoDtos) {
            String lockerId;
            if (mealKitDeliveryInfoDto.lockerId() == null) {
                lockerId = "To Be Determined";
            } else {
                lockerId = Integer.toString(mealKitDeliveryInfoDto.lockerId().lockerNumber());
            }
            message += "MealKit ID " + mealKitDeliveryInfoDto.mealkitId().value() + " to " + mealKitDeliveryInfoDto.shippingLocationId().value() + " in box " +
                lockerId + "\n";
        }
        return message;
    }
}
