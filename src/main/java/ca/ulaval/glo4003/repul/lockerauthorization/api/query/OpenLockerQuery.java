package ca.ulaval.glo4003.repul.lockerauthorization.api.query;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;

public record OpenLockerQuery(UserCardNumber userCardNumber, DeliveryLocationId deliveryLocationId, int lockerNumber) {
    public static OpenLockerQuery from(String userCardNumber, String deliveryLocationId, int lockerNumber) {
        return new OpenLockerQuery(
            new UserCardNumber(userCardNumber),
            new DeliveryLocationId(deliveryLocationId),
            lockerNumber);
    }
}
