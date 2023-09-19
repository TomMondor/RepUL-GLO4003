package ca.ulaval.glo4003.repul.api.subscription.request;

import jakarta.validation.constraints.NotNull;

public class SubscriptionRequest {
    @NotNull(message = "Frequence may not be null")
    public int frequence;

    @NotNull(message = "Location Id may not be null")
    public String locationId;

    @NotNull(message = "Lunch box type may not be null")
    public String lunchBoxType;

    @NotNull(message = "Start date may not be null")
    public String startDate;
}
