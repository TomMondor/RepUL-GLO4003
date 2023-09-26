package ca.ulaval.glo4003.repul.api.subscription.assembler;

import java.util.List;

import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.application.subscription.dto.SubscriptionsDTO;

public class SubscriptionsResponseAssembler {
    public List<SubscriptionResponse> toSubscriptionsResponse(SubscriptionsDTO subscriptionsDTO) {
        return subscriptionsDTO.subscriptions().stream().map(
            subscription -> new SubscriptionResponse(
                subscription.getFrequency().dayOfWeek().name(),
                subscription.getPickupLocation().getLocationId().value(),
                subscription.getLunchboxType().name(),
                subscription.getStartDate().toString())).toList();
    }
}
