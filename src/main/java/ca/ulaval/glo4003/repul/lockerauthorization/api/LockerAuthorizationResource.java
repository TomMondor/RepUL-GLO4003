package ca.ulaval.glo4003.repul.lockerauthorization.api;

import ca.ulaval.glo4003.repul.lockerauthorization.api.query.OpenLockerQuery;
import ca.ulaval.glo4003.repul.lockerauthorization.api.request.OpenLockerRequest;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.Secure;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LockerAuthorizationResource {
    private final LockerAuthorizationService lockerAuthorizationService;

    public LockerAuthorizationResource(LockerAuthorizationService lockerAuthorizationService) {
        this.lockerAuthorizationService = lockerAuthorizationService;
    }

    @POST
    @Secure
    @Roles(Role.LOCKER)
    @Path("/locker:open")
    public Response openLocker(OpenLockerRequest request) {
        OpenLockerQuery query = OpenLockerQuery.from(
            request.userCardNumber,
            request.deliveryLocationId,
            request.lockerNumber);

        lockerAuthorizationService.openLocker(query);

        return Response.noContent().build();
    }
}
