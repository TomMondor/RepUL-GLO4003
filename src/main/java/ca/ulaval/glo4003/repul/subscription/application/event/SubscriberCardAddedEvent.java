package ca.ulaval.glo4003.repul.subscription.application.event;

import ca.ulaval.glo4003.repul.commons.application.RepULEvent;
import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;

public class SubscriberCardAddedEvent extends RepULEvent {
    public final SubscriberUniqueIdentifier subscriberId;
    public final SubscriberCardNumber subscriberCardNumber;

    public SubscriberCardAddedEvent(SubscriberUniqueIdentifier subscriberId, SubscriberCardNumber subscriberCardNumber) {
        super();
        this.subscriberId = subscriberId;
        this.subscriberCardNumber = subscriberCardNumber;
    }
}
