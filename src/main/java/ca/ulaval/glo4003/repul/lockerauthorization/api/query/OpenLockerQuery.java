package ca.ulaval.glo4003.repul.lockerauthorization.api.query;

import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerId;

public record OpenLockerQuery(
    SubscriberCardNumber subscriberCardNumber,
    LockerId lockerId
) {
    public static OpenLockerQuery from(String subscriberCardNumber, String lockerId) {
        return new OpenLockerQuery(
            new SubscriberCardNumber(subscriberCardNumber),
            new LockerId(lockerId));
    }
}
