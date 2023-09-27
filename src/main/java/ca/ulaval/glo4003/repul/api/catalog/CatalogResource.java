package ca.ulaval.glo4003.repul.api.catalog;

import java.util.List;

import ca.ulaval.glo4003.repul.api.catalog.assembler.CatalogResponseAssembler;
import ca.ulaval.glo4003.repul.api.catalog.response.LocationResponse;
import ca.ulaval.glo4003.repul.application.catalog.CatalogService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogResource {
    private final CatalogService catalogService;
    private final CatalogResponseAssembler catalogResponseAssembler = new CatalogResponseAssembler();

    public CatalogResource(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GET
    @Path("/locations")
    public Response getLocations() {
        List<LocationResponse> locations = catalogResponseAssembler.toLocationsResponse(catalogService.getPickupLocations());
        return Response.ok(locations).build();
    }
}
