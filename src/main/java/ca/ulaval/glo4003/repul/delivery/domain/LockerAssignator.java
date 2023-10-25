package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public class LockerAssignator {
    private final Map<DeliveryLocation, List<MealKit>> waitingMealKitsByDeliveryLocation;

    public LockerAssignator(List<DeliveryLocation> deliveryLocations) {
        this.waitingMealKitsByDeliveryLocation = new HashMap();
        deliveryLocations.forEach(deliveryLocation -> waitingMealKitsByDeliveryLocation.put(deliveryLocation, new ArrayList<>()));
    }

    public void assignLocker(MealKit mealKit) {
        DeliveryLocation deliveryLocation = mealKit.getDeliveryLocation();

        Optional<Locker> availableLocker = deliveryLocation.findNextAvailableLocker();

        if (availableLocker.isPresent()) {
            availableLocker.get().assign(mealKit.getMealKitId());
            mealKit.assignLocker(Optional.of(availableLocker.get().getLockerId()));
        } else {
            waitingMealKitsByDeliveryLocation.get(deliveryLocation).add(mealKit);
        }
    }
}
