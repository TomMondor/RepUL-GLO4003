package ca.ulaval.glo4003.repul.domain.account.subscription;

import ca.ulaval.glo4003.repul.domain.exception.InvalidSubscriptionIdException;

public record SubscriptionId(String value) {
    public SubscriptionId {
        if (value.isBlank()) {
            throw new InvalidSubscriptionIdException();
        }
    }
}
