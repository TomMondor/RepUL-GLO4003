package ca.ulaval.glo4003.repul.subscription.domain;

import ca.ulaval.glo4003.repul.subscription.application.exception.InvalidSubscriptionTypeException;

public enum SubscriptionType {
    WEEKLY,
    SPORADIC;

    public static SubscriptionType from(String type) {
        try {
            return SubscriptionType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidSubscriptionTypeException();
        }
    }
}
