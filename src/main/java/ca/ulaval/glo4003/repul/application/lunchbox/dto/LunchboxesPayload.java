package ca.ulaval.glo4003.repul.application.lunchbox.dto;

import java.util.Map;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;

public record LunchboxesPayload(Map<LunchboxType, Lunchbox> lunchboxes) {
}
