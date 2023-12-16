package ca.ulaval.glo4003.repul.config.seed.subscription;

import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;

public class DevSubscriptionSeed extends SubscriptionSeed {
    public DevSubscriptionSeed(SubscriberRepository subscriberRepository) {
        super(subscriberRepository);
    }

    @Override
    public void populate() {
        // No data to populate
    }
}
