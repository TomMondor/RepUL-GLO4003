package ca.ulaval.glo4003.repul.user.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class DeliveryAccountCreatedEvent extends RepULEvent {
    public final UniqueIdentifier accountId;
    public final Email email;

    public DeliveryAccountCreatedEvent(UniqueIdentifier accountId, Email email) {
        this.accountId = accountId;
        this.email = email;
    }
}
