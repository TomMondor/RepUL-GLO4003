package ca.ulaval.glo4003.repul.delivery.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.MealKit;

public class DeliveryLocations {
    private final Map<DeliveryLocationId, DeliveryLocation> deliveryLocations;

    public DeliveryLocations(List<DeliveryLocation> deliveryLocations) {
        this.deliveryLocations = new HashMap<>();
        deliveryLocations.forEach(deliveryLocation -> this.deliveryLocations.put(deliveryLocation.getLocationId(), deliveryLocation));
    }

    public List<DeliveryLocation> getDeliveryLocations() {
        return deliveryLocations.values().stream().toList();
    }

    public void assignLocker(DeliveryLocationId deliveryLocationId, MealKit mealKit) {
        deliveryLocations.get(deliveryLocationId).assignLocker(mealKit);
    }

    public void unassignLocker(MealKit mealKit) {
        deliveryLocations.get(mealKit.getDeliveryLocationId()).unassignLocker(mealKit);
    }
}
