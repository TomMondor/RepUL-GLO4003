package ca.ulaval.glo4003.repul.api.subscription;

import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.application.subscription.parameter.SubscriptionParams;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api/subscriptions")
public class SubscriptionResource {
    private final SubscriptionService subscriptionService;

    public SubscriptionResource(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @POST
    public Response createSubscription(@Valid SubscriptionRequest subscriptionRequest) {
        SubscriptionParams subscriptionParams = SubscriptionParams.from(subscriptionRequest.locationId, subscriptionRequest.dayOfWeek);
        subscriptionService.createSubscription(subscriptionParams);
        return Response.ok().build();
    }
}