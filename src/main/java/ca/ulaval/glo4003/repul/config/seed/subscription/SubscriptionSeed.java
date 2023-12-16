package ca.ulaval.glo4003.repul.config.seed.subscription;

import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;

public abstract class SubscriptionSeed implements Seed {
    protected final SubscriberRepository subscriberRepository;

    protected SubscriptionSeed(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }
}
