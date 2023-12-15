package ca.ulaval.glo4003.repul.subscription.api;

import java.net.URI;

import ca.ulaval.glo4003.repul.commons.api.UriFactory;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.api.request.SubscriptionRequest;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.Secure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class SubscriptionResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private final SubscriptionService subscriptionService;
    private final UriFactory uriFactory = new UriFactory();

    public SubscriptionResource(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @POST
    @Secure
    @Roles(Role.CLIENT)
    @Path("/subscriptions")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSubscription(@Context ContainerRequestContext context,
                                       @NotNull(message = "The body must not be null.") @Valid SubscriptionRequest subscriptionRequest) {
        String subscriberId = context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString();

        SubscriptionUniqueIdentifier subscriptionId =
            subscriptionService.createSubscription(SubscriptionQuery.from(subscriptionRequest, subscriberId));

        URI location = uriFactory.createURI("/api/subscriptions/" + subscriptionId.getUUID().toString());

        return Response.created(location).build();
    }

    @GET
    @Secure
    @Path("/subscriptions/{subscriptionId}")
    @Roles(Role.CLIENT)
    public Response getSubscription(@Context ContainerRequestContext context, @PathParam("subscriptionId") String subscriptionIdAsString) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        SubscriptionPayload subscriptionPayload = subscriptionService.getSubscriptionById(subscriberId,
            new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom(subscriptionIdAsString));

        return Response.ok(subscriptionPayload).build();
    }

    @GET
    @Secure
    @Path("/subscriptions")
    @Roles(Role.CLIENT)
    public Response getSubscriptions(@Context ContainerRequestContext context) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        SubscriptionsPayload subscriptionsPayload = subscriptionService.getSubscriptions(subscriberId);

        return Response.ok(subscriptionsPayload).build();
    }

    @POST
    @Secure
    @Roles(Role.CLIENT)
    @Path("/subscriptions/{subscriptionId}:confirm")
    public Response confirmMealKit(@Context ContainerRequestContext context, @PathParam("subscriptionId") String subscriptionId) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        subscriptionService.confirmNextMealKitForSubscription(subscriberId,
            new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom(subscriptionId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.CLIENT)
    @Path("/subscriptions/{subscriptionId}:decline")
    public Response declineMealKit(@Context ContainerRequestContext context, @PathParam("subscriptionId") String subscriptionId) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        subscriptionService.declineNextMealKitForSubscription(subscriberId,
            new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom(subscriptionId));

        return Response.noContent().build();
    }

    @GET
    @Secure
    @Roles(Role.CLIENT)
    @Path("/subscriptions:currentOrders")
    public Response getMyCurrentOrders(@Context ContainerRequestContext context) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY).toString());

        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(subscriberId);

        return Response.ok(ordersPayload).build();
    }
}
