package ca.ulaval.glo4003.repul.api.subscription;

import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/api/subscription")
public class SubscriptionResource {
    private final SubscriptionService subscriptionService;

    public SubscriptionResource(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @POST
    public Response createSubscription(@Valid SubscriptionRequest subscriptionRequest) {
        return Response.ok().build();
    }
}
