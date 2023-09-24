package ca.ulaval.glo4003.repul.domain.subscription;

import ca.ulaval.glo4003.repul.domain.exception.InvalidSubscriptionIdException;

public record SubscriptionId(String value) {
    public SubscriptionId {
        if (value.isEmpty()) {
            throw new InvalidSubscriptionIdException();
        }
    }
}
