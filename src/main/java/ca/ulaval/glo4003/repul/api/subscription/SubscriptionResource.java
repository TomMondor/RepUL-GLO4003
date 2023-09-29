package ca.ulaval.glo4003.repul.api.subscription;

import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.middleware.Secure;
import ca.ulaval.glo4003.repul.api.subscription.assembler.SubscriptionsResponseAssembler;
import ca.ulaval.glo4003.repul.api.subscription.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionCreatedResponse;
import ca.ulaval.glo4003.repul.api.subscription.response.SubscriptionResponse;
import ca.ulaval.glo4003.repul.application.subscription.SubscriptionService;
import ca.ulaval.glo4003.repul.application.subscription.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.application.subscription.query.SubscriptionQuery;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/subscriptions")
@Produces(MediaType.APPLICATION_JSON)
public class SubscriptionResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private final SubscriptionService subscriptionService;
    private final SubscriptionsResponseAssembler subscriptionsResponseAssembler = new SubscriptionsResponseAssembler();

    public SubscriptionResource(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @POST
    @Secure
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSubscription(@Context ContainerRequestContext context, @Valid SubscriptionRequest subscriptionRequest) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
        SubscriptionQuery subscriptionQuery = SubscriptionQuery.from(subscriptionRequest.locationId, subscriptionRequest.dayOfWeek,
            subscriptionRequest.lunchboxType);

        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(accountId, subscriptionQuery);

        return Response.status(Response.Status.CREATED).entity(new SubscriptionCreatedResponse(subscriptionId.value().toString())).build();
    }

    @GET
    @Secure
    public Response getSubscriptions(@Context ContainerRequestContext context) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        SubscriptionsPayload subscriptions = subscriptionService.getSubscriptions(accountId);

        List<SubscriptionResponse> subscriptionsResponse = subscriptionsResponseAssembler.toSubscriptionsResponse(subscriptions);
        return Response.ok(subscriptionsResponse).build();
    }

    @POST
    @Secure
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{subscriptionId}/confirm")
    public Response confirmLunchbox(@Context ContainerRequestContext context, @PathParam("subscriptionId") String subscriptionId) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        subscriptionService.confirmNextLunchboxForSubscription(accountId, UniqueIdentifier.from(subscriptionId));
        return Response.ok().build();
    }

    @POST
    @Secure
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{subscriptionId}/decline")
    public Response declineLunchbox(@Context ContainerRequestContext context, @PathParam("subscriptionId") String subscriptionId) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        subscriptionService.declineNextLunchboxForSubscription(accountId, UniqueIdentifier.from(subscriptionId));
        return Response.ok().build();
    }
}
