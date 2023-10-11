package ca.ulaval.glo4003.repul.api.lunchbox;

import java.util.List;

import ca.ulaval.glo4003.identitymanagement.domain.Role;
import ca.ulaval.glo4003.identitymanagement.middleware.Roles;
import ca.ulaval.glo4003.identitymanagement.middleware.Secure;
import ca.ulaval.glo4003.repul.api.lunchbox.assembler.LunchboxesResponseAssembler;
import ca.ulaval.glo4003.repul.api.lunchbox.response.ToCookResponse;
import ca.ulaval.glo4003.repul.application.lunchbox.LunchboxService;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/lunchboxes")
public class LunchboxResource {
    private final LunchboxService lunchboxService;
    private final LunchboxesResponseAssembler lunchboxesResponseAssembler = new LunchboxesResponseAssembler();

    public LunchboxResource(LunchboxService lunchboxService) {
        this.lunchboxService = lunchboxService;
    }

    @GET
    @Secure
    @Roles(Role.COOK)
    @Path("/to-cook")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLunchboxesToCook() {
        List<Lunchbox> lunchboxes = lunchboxService.getLunchboxesToCook();

        ToCookResponse toCookResponse = lunchboxesResponseAssembler.toToCookResponse(lunchboxes);

        return Response.ok(toCookResponse).build();
    }
}
