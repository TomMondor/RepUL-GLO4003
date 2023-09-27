package ca.ulaval.glo4003.repul.api.lunchbox;

import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.middleware.Secure;
import ca.ulaval.glo4003.repul.api.lunchbox.asembler.LunchboxesResponseAssembler;
import ca.ulaval.glo4003.repul.api.lunchbox.response.LunchboxResponse;
import ca.ulaval.glo4003.repul.application.lunchbox.LunchboxService;
import ca.ulaval.glo4003.repul.application.lunchbox.dto.LunchboxesPayload;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Path("/api/lunchboxes")
public class LunchboxResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private final LunchboxService lunchboxService;
    private final LunchboxesResponseAssembler lunchboxesResponseAssembler = new LunchboxesResponseAssembler();

    public LunchboxResource(LunchboxService lunchboxService) {
        this.lunchboxService = lunchboxService;
    }

    @GET
    @Secure
    @Path("/me")
    public Response getMyLunchboxes(@Context ContainerRequestContext context) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        LunchboxesPayload lunchboxes = lunchboxService.getLunchboxes(accountId);

        List<LunchboxResponse> lunchboxesResponse = lunchboxesResponseAssembler.toLunchboxesResponse(lunchboxes);

        return Response.ok(lunchboxesResponse).build();
    }
}
