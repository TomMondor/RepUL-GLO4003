package ca.ulaval.glo4003.repul.delivery.api;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.payload.LockerPayload;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.Secure;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/cargos")
@Produces(MediaType.APPLICATION_JSON)
public class DeliveryResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private final DeliveryService deliveryService;

    public DeliveryResource(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @POST
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/{cargoId}:pickup")
    public Response pickupCargo(@Context ContainerRequestContext context, @PathParam("cargoId") String cargoId) {
        UniqueIdentifier deliveryPersonId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        deliveryService.pickupCargo(deliveryPersonId, UniqueIdentifier.from(cargoId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/{cargoId}:cancel")
    public Response cancelCargo(@Context ContainerRequestContext context, @PathParam("cargoId") String cargoId) {
        UniqueIdentifier deliveryPersonId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        deliveryService.cancelCargo(deliveryPersonId, UniqueIdentifier.from(cargoId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/{cargoId}/mealKits/{mealKitId}:confirm")
    public Response confirmDelivery(@Context ContainerRequestContext context, @PathParam("cargoId") String cargoId, @PathParam("mealKitId") String mealKitId) {
        UniqueIdentifier deliveryPersonId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        deliveryService.confirmDelivery(deliveryPersonId, UniqueIdentifier.from(cargoId), UniqueIdentifier.from(mealKitId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.DELIVERY_PERSON)
    @Path("/{cargoId}/mealKits/{mealKitId}:recall")
    public Response recallDelivery(@Context ContainerRequestContext context, @PathParam("cargoId") String cargoId, @PathParam("mealKitId") String mealKitId) {
        UniqueIdentifier deliveryPersonId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        LockerPayload lockerPayload =
            LockerPayload.from(deliveryService.recallDelivery(deliveryPersonId, UniqueIdentifier.from(cargoId), UniqueIdentifier.from(mealKitId)));

        return Response.ok(lockerPayload).build();
    }
}
