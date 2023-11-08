package ca.ulaval.glo4003.repul.user.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class AccountCreatedEvent extends RepULEvent {
    public final UniqueIdentifier accountId;
    public final Email email;

    public AccountCreatedEvent(UniqueIdentifier accountId, Email email) {
        super();
        this.accountId = accountId;
        this.email = email;
    }
}
