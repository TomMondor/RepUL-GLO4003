package ca.ulaval.glo4003.repul.subscription.api.request;

import jakarta.validation.constraints.NotNull;

public class SubscriptionRequest {
    @NotNull(message = "Location Id may not be null")
    public String locationId;

    @NotNull(message = "Day of week may not be null")
    public String dayOfWeek;

    @NotNull(message = "Lunchbox type may not be null")
    public String lunchboxType;
}