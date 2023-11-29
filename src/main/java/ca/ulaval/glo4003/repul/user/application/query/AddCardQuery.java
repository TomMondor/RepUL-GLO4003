package ca.ulaval.glo4003.repul.user.application.query;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;

public record AddCardQuery(
    UserCardNumber cardNumber
) {
    public static AddCardQuery from(String cardNumber) {
        return new AddCardQuery(new UserCardNumber(cardNumber));
    }
}
