package ca.ulaval.glo4003.repul.api.subscription;

import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.application.subscription.parameter.SubscriptionParams;
import ca.ulaval.glo4003.repul.domain.account.subscription.SubscriptionId;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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

    @POST
    @Path("/{subscriptionId}/confirm")
    public Response confirmLunchbox(@PathParam("subscriptionId") String subscriptionId) {
        subscriptionService.confirmNextLunchboxForSubscription(new SubscriptionId(subscriptionId));
        return Response.ok().build();
    }

    @POST
    @Path("/{subscriptionId}/decline")
    public Response declineLunchbox(@PathParam("subscriptionId") String subscriptionId) {
        subscriptionService.declineNextLunchboxForSubscription(new SubscriptionId(subscriptionId));
        return Response.ok().build();
    }
}
