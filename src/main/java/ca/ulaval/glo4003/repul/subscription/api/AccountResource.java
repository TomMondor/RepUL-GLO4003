package ca.ulaval.glo4003.repul.subscription.api;

import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Role;
import ca.ulaval.glo4003.repul.identitymanagement.middleware.Roles;
import ca.ulaval.glo4003.repul.identitymanagement.middleware.Secure;
import ca.ulaval.glo4003.repul.subscription.api.request.AddCardRequest;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource {
    private static final String USER_ID_CONTEXT_PROPERTY = "uid";

    private final SubscriberService subscriberService;

    public AccountResource(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GET
    @Secure
    @Roles(Role.CLIENT)
    @Path("/accounts")
    public Response getAccount(@Context ContainerRequestContext context) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(context.getProperty(USER_ID_CONTEXT_PROPERTY).toString());

        ProfilePayload profilePayload = subscriberService.getSubscriberProfile(subscriberId);

        return Response.ok(profilePayload).build();
    }

    @POST
    @Secure
    @Roles(Role.CLIENT)
    @Path("/accounts:addCard")
    public Response addCard(@Context ContainerRequestContext context, @NotNull(message = "The body must not be null.") @Valid AddCardRequest addCardRequest) {
        SubscriberUniqueIdentifier subscriberId =
            new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(context.getProperty(USER_ID_CONTEXT_PROPERTY).toString());

        subscriberService.addCard(subscriberId, new SubscriberCardNumber(addCardRequest.cardNumber));

        return Response.noContent().build();
    }
}
