package ca.ulaval.glo4003.repul.delivery.api;

import ca.ulaval.glo4003.repul.delivery.application.LocationsCatalogService;
import ca.ulaval.glo4003.repul.delivery.application.payload.DeliveryLocationsPayload;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/locations")
public class LocationResource {
    private final LocationsCatalogService locationsCatalogService;

    public LocationResource(LocationsCatalogService locationsCatalogService) {
        this.locationsCatalogService = locationsCatalogService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeliveryLocations() {
        DeliveryLocationsPayload locations = locationsCatalogService.getDeliveryLocations();
        return Response.ok(locations).build();
    }
}
