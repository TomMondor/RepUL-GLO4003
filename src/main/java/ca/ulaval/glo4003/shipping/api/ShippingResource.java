package ca.ulaval.glo4003.shipping.api;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Role;
import ca.ulaval.glo4003.identitymanagement.middleware.Roles;
import ca.ulaval.glo4003.identitymanagement.middleware.Secure;
import ca.ulaval.glo4003.shipping.application.ShippingService;

import jakarta.ws.rs.POST;
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
    @Path("/pickup/{shippingId}")
    public Response pickupCargo(@Context ContainerRequestContext context, @PathParam("shippingId") String shippingId) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        shippingService.pickupCargo(accountId, UniqueIdentifier.from(shippingId));

        return Response.ok().build();
    }

    @POST
    @Secure
    @Roles(Role.SHIPPER)
    @Path("/cancel/{shippingId}")
    public Response cancelShipping(@Context ContainerRequestContext context, @PathParam("shippingId") String shippingId) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        shippingService.cancelShipping(accountId, UniqueIdentifier.from(shippingId));

        return Response.ok().build();
    }
}
