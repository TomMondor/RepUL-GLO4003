package ca.ulaval.glo4003.repul.application.subscription;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.application.subscription.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.RepULRepository;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.catalog.Amount;

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

        List<Subscription> subscriptions = repUL.getSubscriptions(accountId);

        List<SubscriptionPayload> subscriptionPayloadList = new ArrayList<>();

        for (Subscription subscription: subscriptions) {
            subscriptionPayloadList.add(SubscriptionPayload
                .from(subscription, repUL.getSemester(subscription.getStartDate())));
        }

        return new SubscriptionsPayload(subscriptionPayloadList);
    }

    public void confirmNextLunchboxForSubscription(UniqueIdentifier accountId, UniqueIdentifier subscriptionId) {
        RepUL repUL = repULRepository.get();

        repUL.confirmNextLunchboxForSubscription(accountId, subscriptionId);
        Amount lunchboxPrice = repUL.getLunchboxPrice(accountId, subscriptionId);
        paymentHandler.makeTransaction(lunchboxPrice);

        repULRepository.saveOrUpdate(repUL);
    }

    public void declineNextLunchboxForSubscription(UniqueIdentifier accountId, UniqueIdentifier subscriptionId) {
        RepUL repUL = repULRepository.get();

        repUL.declineNextLunchboxForSubscription(accountId, subscriptionId);

        repULRepository.saveOrUpdate(repUL);
    }
}
