package ca.ulaval.glo4003.repul.api;

import ca.ulaval.glo4003.repul.api.dto.HealthDto;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/health")
public class HealthResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HealthDto getHealth() {
        return new HealthDto("OK - Everything is alright!");
    }
}
