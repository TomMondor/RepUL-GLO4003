package ca.ulaval.glo4003.repul.application.subscription;

import java.util.List;

import ca.ulaval.glo4003.repul.application.subscription.dto.SubscriptionsDTO;
import ca.ulaval.glo4003.repul.application.subscription.parameter.SubscriptionParams;
import ca.ulaval.glo4003.repul.domain.PaymentHandler;
import ca.ulaval.glo4003.repul.domain.account.subscription.SubscriptionId;

public class SubscriptionService {

    private final PaymentHandler paymentHandler;

    public SubscriptionService(PaymentHandler paymentHandler) {
        this.paymentHandler = paymentHandler;
    }

    public void createSubscription(SubscriptionParams subscriptionParams) {
    }

    public SubscriptionsDTO getSubscriptions() {
        return new SubscriptionsDTO(List.of());
    }

    public void confirmNextLunchboxForSubscription(SubscriptionId subscriptionId) {
    }

    public void declineNextLunchboxForSubscription(SubscriptionId subscriptionId) {
    }
}
