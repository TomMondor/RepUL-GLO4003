package ca.ulaval.glo4003.repul.shipping.api;

import java.util.List;

import ca.ulaval.glo4003.repul.shipping.api.assembler.LocationsCatalogResponseAssembler;
import ca.ulaval.glo4003.repul.shipping.api.response.LocationResponse;
import ca.ulaval.glo4003.repul.shipping.application.LocationsCatalogService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/catalog")
public class LocationsCatalogResource {
    private final LocationsCatalogService locationsCatalogService;
    private final LocationsCatalogResponseAssembler locationsCatalogResponseAssembler = new LocationsCatalogResponseAssembler();

    public LocationsCatalogResource(LocationsCatalogService locationsCatalogService) {
        this.locationsCatalogService = locationsCatalogService;
    }

    @GET
    @Path("/locations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocations() {
        List<LocationResponse> locations = locationsCatalogResponseAssembler.toLocationsResponse(locationsCatalogService.getShippingLocations());
        return Response.ok(locations).build();
    }
}
