package ca.ulaval.glo4003.repul.commons.domain;

import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;

public enum DeliveryLocationId {
    VACHON,
    PEPS,
    DESJARDINS,
    VANDRY,
    MYRAND,
    PYRAMIDE,
    CASAULT,
    PLACE_STE_FOY;

    public static DeliveryLocationId from(String deliveryLocationId) {
        if (DeliveryLocationId.contains(deliveryLocationId)) {
            return DeliveryLocationId.valueOf(deliveryLocationId);
        }
        throw new InvalidLocationIdException();
    }

    private static boolean contains(String other) {
        for (DeliveryLocationId deliveryLocationId : DeliveryLocationId.values()) {
            if (deliveryLocationId.name().equals(other)) {
                return true;
            }
        }
        return false;
    }
}
