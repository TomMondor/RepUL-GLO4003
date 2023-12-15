package ca.ulaval.glo4003.repul.delivery.domain.deliveryperson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.glo4003.repul.commons.domain.uid.CargoUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.exception.DeliveryPersonNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.cargo.Cargo;
import ca.ulaval.glo4003.repul.delivery.domain.exception.CargoNotFoundException;
import ca.ulaval.glo4003.repul.delivery.domain.exception.InvalidMealKitIdException;

public class DeliveryPeople {
    private final Map<DeliveryPersonUniqueIdentifier, DeliveryPerson> deliveryPeople = new HashMap<>();

    public void addDeliveryPerson(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        DeliveryPerson deliveryPerson = new DeliveryPerson(deliveryPersonId);
        deliveryPeople.put(deliveryPersonId, deliveryPerson);
    }

    public List<Cargo> getCargosInDelivery() {
        List<Cargo> cargosInDelivery = new ArrayList<>();
        deliveryPeople.values().forEach(deliveryPerson -> cargosInDelivery.addAll(deliveryPerson.getCargosInPossession()));
        return cargosInDelivery;
    }

    public DeliveryPerson findDeliveryPersonThatHasMealKitWithMealKitId(MealKitUniqueIdentifier mealKitId) {
        return deliveryPeople.values().stream()
            .filter(deliveryPerson -> deliveryPerson.hasMealKit(mealKitId))
            .findFirst()
            .orElseThrow(InvalidMealKitIdException::new);
    }

    public DeliveryPerson findDeliveryPersonThatHasCargoWithCargoId(CargoUniqueIdentifier cargoId) {
        return deliveryPeople.values().stream()
            .filter(deliveryPerson -> deliveryPerson.hasCargo(cargoId))
            .findFirst()
            .orElseThrow(CargoNotFoundException::new);
    }

    public DeliveryPerson findDeliveryPerson(DeliveryPersonUniqueIdentifier deliveryPersonId) {
        if (!deliveryPeople.containsKey(deliveryPersonId)) {
            throw new DeliveryPersonNotFoundException();
        }
        return deliveryPeople.get(deliveryPersonId);
    }

    public List<DeliveryPersonUniqueIdentifier> getDeliveryPeopleIds() {
        return deliveryPeople.keySet().stream().toList();
    }
}
