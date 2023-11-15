package ca.ulaval.glo4003.repul.notification.domain;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class DeliveryPersonAccount extends Account {
    public DeliveryPersonAccount(UniqueIdentifier accountId, Email email) {
        super(accountId, email);
    }
}
