package ca.ulaval.glo4003.repul.application.subscription;

import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.subscription.dto.SubscriptionsDTO;
import ca.ulaval.glo4003.repul.application.subscription.parameter.SubscriptionParams;
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

    public void createSubscription(SubscriptionParams subscriptionParams) {
    }

    public SubscriptionsDTO getSubscriptions() {
        return new SubscriptionsDTO(List.of());
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
