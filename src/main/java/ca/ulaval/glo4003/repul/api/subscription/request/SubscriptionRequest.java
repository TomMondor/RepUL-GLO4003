package ca.ulaval.glo4003.repul.api.subscription.request;

import jakarta.validation.constraints.NotNull;

public class SubscriptionRequest {
    @NotNull(message = "Location Id may not be null")
    public String locationId;

    @NotNull(message = "Day of week may not be null")
    public String dayOfWeek;
}
