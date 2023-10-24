package ca.ulaval.glo4003.repul.shipping.api;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.shipping.application.ShippingService;
import ca.ulaval.glo4003.repul.shipping.application.payload.CasePayload;
import ca.ulaval.glo4003.repul.shipping.application.payload.MealKitShippingStatusPayload;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.Secure;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Path("/api/shipping")
public class ShippingResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private final ShippingService shippingService;

    public ShippingResource(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @POST
    @Secure
    @Roles(Role.SHIPPER)
    @Path("/{shippingId}:pickup")
    public Response pickupCargo(@Context ContainerRequestContext context, @PathParam("shippingId") String shippingId) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        shippingService.pickupCargo(accountId, UniqueIdentifier.from(shippingId));

        return Response.ok().build();
    }

    @PUT
    @Secure
    @Roles(Role.SHIPPER)
    @Path("/{shippingId}:cancel")
    public Response cancelShipping(@Context ContainerRequestContext context, @PathParam("shippingId") String shippingId) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        shippingService.cancelShipping(accountId, UniqueIdentifier.from(shippingId));

        return Response.ok().build();
    }

    @PUT
    @Secure
    @Roles(Role.SHIPPER)
    @Path("/confirm/{ticketId}/{mealKitId}")
    public Response confirmShipping(@Context ContainerRequestContext context, @PathParam("ticketId") String ticketId,
                                    @PathParam("mealKitId") String mealKitId) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        shippingService.confirmShipping(accountId, UniqueIdentifier.from(ticketId), UniqueIdentifier.from(mealKitId));

        return Response.noContent().build();
    }

    @PUT
    @Secure
    @Roles(Role.SHIPPER)
    @Path("/unconfirm/{ticketId}/{mealKitId}")
    public Response unconfirmShipping(@Context ContainerRequestContext context, @PathParam("ticketId") String ticketId,
                                      @PathParam("mealKitId") String mealKitId) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        CasePayload casePayload =
            CasePayload.from(shippingService.unconfirmShipping(accountId, UniqueIdentifier.from(ticketId), UniqueIdentifier.from(mealKitId)));

        return Response.ok(casePayload).build();
    }

    @GET
    @Secure
    @Roles(Role.SHIPPER)
    @Path("/getStatus/{mealKitId}")
    public Response getMealKitShippingStatus(@PathParam("mealKitId") String mealKitId) {
        MealKitShippingStatusPayload shippingStatusPayload = shippingService.getShippingStatus(UniqueIdentifier.from(mealKitId));

        return Response.ok(shippingStatusPayload).build();
    }
}
