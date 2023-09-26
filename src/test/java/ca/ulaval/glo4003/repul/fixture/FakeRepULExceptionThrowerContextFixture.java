package ca.ulaval.glo4003.repul.fixture;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.repul.api.exception.mapper.RepULExceptionMapper;
import ca.ulaval.glo4003.repul.domain.exception.InvalidFrequencyException;
import ca.ulaval.glo4003.repul.domain.exception.RepULException;
import ca.ulaval.glo4003.repul.domain.exception.RepULNotFoundException;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class FakeRepULExceptionThrowerContextFixture implements ApplicationContext {

    private static final int PORT = 8183;
    private static final RepULException BAD_REQUEST_EXCEPTION = new InvalidFrequencyException();
    private static final RepULException NOT_FOUND_EXCEPTION = new RepULNotFoundException();

    @Override
    public ResourceConfig initializeResourceConfig() {

        FakeExceptionThrowerResource fakeExceptionThrowerResource = new FakeExceptionThrowerResource();

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(fakeExceptionThrowerResource).to(FakeExceptionThrowerResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003.repul").register(binder).register(new RepULExceptionMapper());
    }

    @Override
    public String getURI() {
        return String.format("http://localhost:%s/", PORT);
    }

    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public static class FakeExceptionThrowerResource {

        @GET
        @Path("/BadRequestRepULException")
        public Response throwBadRequestException() {
            throw BAD_REQUEST_EXCEPTION;
        }

        @GET
        @Path("/NotFoundRepULException")
        public Response throwNotFoundException() {
            throw NOT_FOUND_EXCEPTION;
        }
    }
}
