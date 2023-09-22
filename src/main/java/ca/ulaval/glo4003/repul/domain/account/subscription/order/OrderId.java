package ca.ulaval.glo4003.repul.domain.account.subscription.order;

import ca.ulaval.glo4003.repul.domain.exception.InvalidOrderIdException;

public record OrderId(String value) {
    public OrderId {
        if (value.isBlank()) {
            throw new InvalidOrderIdException();
        }
    }
}
