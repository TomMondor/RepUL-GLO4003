package ca.ulaval.glo4003.repul.health.api;

import ca.ulaval.glo4003.repul.health.api.dto.HealthDto;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/health")
public class HealthResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHealth() {
        return Response.status(Response.Status.OK).entity(new HealthDto("OK - Everything is alright!")).build();
    }
}
