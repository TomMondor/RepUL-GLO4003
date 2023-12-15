package ca.ulaval.glo4003.repul.subscription.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubscriptionRequest {

    @NotNull(message = "The subscriptionType must be specified")
    public String subscriptionType;

    public String locationId;
    public String dayOfWeek;
    public String mealKitType;
}
