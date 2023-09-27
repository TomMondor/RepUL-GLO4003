package ca.ulaval.glo4003.repul.api.subscription;

import java.util.List;
import java.util.UUID;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.api.subscription.assembler.SubscriptionsResponseAssembler;
import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.application.subscription.parameter.SubscriptionParams;

import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/subscriptions")
@Produces(MediaType.APPLICATION_JSON)
public class SubscriptionResource {
    private final SubscriptionService subscriptionService;
    private final SubscriptionsResponseAssembler subscriptionsResponseAssembler = new SubscriptionsResponseAssembler();

    public SubscriptionResource(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @POST
    public Response createSubscription(@Valid SubscriptionRequest subscriptionRequest) {
        SubscriptionParams subscriptionParams = SubscriptionParams.from(subscriptionRequest.locationId, subscriptionRequest.dayOfWeek);
        subscriptionService.createSubscription(subscriptionParams);
        return Response.ok().build();
    }

    @GET
    public Response getSubscriptions() {
        List<SubscriptionResponse> subscriptions = subscriptionsResponseAssembler.toSubscriptionsResponse(subscriptionService.getSubscriptions());
        return Response.ok(subscriptions).build();
    }

    @POST
    @Path("/{subscriptionId}/confirm")
    public Response confirmLunchbox(@PathParam("subscriptionId") String subscriptionId) {
        // TODO MODIFY ACCOUNTID UNIQUE IDENTIFIER FOR CORRECT ONE
        subscriptionService.confirmNextLunchboxForSubscription(new UniqueIdentifier(UUID.randomUUID()), UniqueIdentifier.from(subscriptionId));
        return Response.ok().build();
    }

    @POST
    @Path("/{subscriptionId}/decline")
    public Response declineLunchbox(@PathParam("subscriptionId") String subscriptionId) {
        subscriptionService.declineNextLunchboxForSubscription(new UniqueIdentifier(UUID.randomUUID()), UniqueIdentifier.from(subscriptionId));
        return Response.ok().build();
    }
}
