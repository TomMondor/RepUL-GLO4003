package ca.ulaval.glo4003.repul.shipping.api;

import java.util.List;

import ca.ulaval.glo4003.repul.shipping.api.assembler.ShippingCatalogResponseAssembler;
import ca.ulaval.glo4003.repul.shipping.api.response.LocationResponse;
import ca.ulaval.glo4003.repul.shipping.application.ShippingCatalogService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/catalog")
public class ShippingCatalogResource {
    private final ShippingCatalogService shippingCatalogService;
    private final ShippingCatalogResponseAssembler shippingCatalogResponseAssembler = new ShippingCatalogResponseAssembler();

    public ShippingCatalogResource(ShippingCatalogService shippingCatalogService) {
        this.shippingCatalogService = shippingCatalogService;
    }

    @GET
    @Path("/locations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocations() {
        List<LocationResponse> locations = shippingCatalogResponseAssembler.toLocationsResponse(shippingCatalogService.getShippingLocations());
        return Response.ok(locations).build();
    }
}
