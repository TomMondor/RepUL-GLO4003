package ca.ulaval.glo4003.repul.application.subscription.dto;

import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;

public record SubscriptionsDTO(List<Subscription> subscriptions) {
}
