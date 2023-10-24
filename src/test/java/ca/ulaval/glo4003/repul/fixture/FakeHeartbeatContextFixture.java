package ca.ulaval.glo4003.repul.fixture;

import org.glassfish.jersey.server.ResourceConfig;

import ca.ulaval.glo4003.repul.config.ApplicationContext;
import ca.ulaval.glo4003.repul.config.http.CORSResponseFilter;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

public class FakeHeartbeatContextFixture implements ApplicationContext {
    private static final int PORT = 8181; // To avoid conflict with the real application port in e2e tests

    @Override
    public ResourceConfig initializeResourceConfig() {
        return new ResourceConfig().register(new FakeHeartbeatResource()).register(new CORSResponseFilter());
    }

    @Override
    public String getURI() {
        return String.format("http://localhost:%s/", PORT);
    }

    @Path("/api/heartbeat")
    public static class FakeHeartbeatResource {
        @GET
        public Response getHeartbeat() {
            return Response.ok().build();
        }
    }
}
