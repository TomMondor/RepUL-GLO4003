package ca.ulaval.glo4003.repul.notification.application;

import java.time.LocalTime;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;

public class MessageFactory {

    public String createReadyToBeDeliveredMessage(UniqueIdentifier cargoId, KitchenLocationId locationId,
                                                  List<MealKitDto> mealKitDtos) {
        String message = "Your meal kits (cargo id: " + cargoId.value().toString() + ") are ready to be fetched from " + locationId.value() + ".\n";
        message += "Here is the list of meal kits to be delivered:\n";
        for (MealKitDto mealKitDto : mealKitDtos) {
            String lockerId;
            if (mealKitDto.lockerId().isPresent()) {
                lockerId = Integer.toString(mealKitDto.lockerId().get().lockerNumber());
            } else {
                lockerId = "To Be Determined";
            }
            message += "MealKit ID " + mealKitDto.mealKitId().value() + " to " + mealKitDto.deliveryLocationId().value() + " in box " +
                lockerId + "\n";
        }
        return message;
    }

    public String createDeliveredMessage(UniqueIdentifier mealKitId,
                                         DeliveryLocationId deliveryLocationId, LocalTime localTime, LockerId lockerId) {
        String message = "Your meal kit with id " + mealKitId.value().toString();
        message += " is ready to be fetched from " + deliveryLocationId.value();
        message += " in the locker " + lockerId.lockerNumber();
        message += ".\n";
        message += "It arrived at " + localTime.toString() + ".\n";
        message += "Come get it!";
        return message;
    }
}
