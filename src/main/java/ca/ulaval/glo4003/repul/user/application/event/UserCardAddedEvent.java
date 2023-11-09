package ca.ulaval.glo4003.repul.user.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public class UserCardAddedEvent extends RepULEvent {
    public final UniqueIdentifier accountId;
    public final UserCardNumber userCardNumber;

    public UserCardAddedEvent(UniqueIdentifier accountId, UserCardNumber userCardNumber) {
        super();
        this.accountId = accountId;
        this.userCardNumber = userCardNumber;
    }
}
