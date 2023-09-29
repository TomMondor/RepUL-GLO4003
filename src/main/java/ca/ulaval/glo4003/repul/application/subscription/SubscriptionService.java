package ca.ulaval.glo4003.repul.application.subscription;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.application.subscription.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;

public class SubscriptionService {
    private final RepULRepository repULRepository;
    private final PaymentHandler paymentHandler;

    public SubscriptionService(RepULRepository repULRepository, PaymentHandler paymentHandler) {
        this.repULRepository = repULRepository;
        this.paymentHandler = paymentHandler;
    }

    public UniqueIdentifier createSubscription(UniqueIdentifier accountId, SubscriptionQuery subscriptionQuery) {
        RepUL repUL = repULRepository.get();

        UniqueIdentifier subscriptionId = repUL.createSubscription(
            accountId,
            subscriptionQuery.locationId(),
            subscriptionQuery.dayOfWeek(),
            subscriptionQuery.lunchboxType());

        repULRepository.saveOrUpdate(repUL);

        return subscriptionId;
    }

    public SubscriptionsPayload getSubscriptions(UniqueIdentifier accountId) {
        RepUL repUL = repULRepository.get();

        return new SubscriptionsPayload(repUL.getSubscriptions(accountId));
    }

    public void confirmNextLunchboxForSubscription(UniqueIdentifier accountId, UniqueIdentifier subscriptionId) {
        RepUL repUL = repULRepository.get();

        repUL.confirmNextLunchboxForSubscription(accountId, subscriptionId);

        repULRepository.saveOrUpdate(repUL);
    }

    public void declineNextLunchboxForSubscription(UniqueIdentifier accountId, UniqueIdentifier subscriptionId) {
        RepUL repUL = repULRepository.get();

        repUL.declineNextLunchboxForSubscription(accountId, subscriptionId);

        repULRepository.saveOrUpdate(repUL);
    }
}
