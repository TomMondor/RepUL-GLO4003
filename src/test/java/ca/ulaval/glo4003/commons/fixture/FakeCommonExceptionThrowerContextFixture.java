package ca.ulaval.glo4003.commons.fixture;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import ca.ulaval.glo4003.commons.api.exception.mapper.CommonExceptionMapper;
import ca.ulaval.glo4003.commons.domain.exception.CommonException;
import ca.ulaval.glo4003.commons.domain.exception.InvalidEmailException;
import ca.ulaval.glo4003.config.ApplicationContext;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class FakeCommonExceptionThrowerContextFixture implements ApplicationContext {

    private static final int PORT = 8183;
    private static final CommonException BAD_REQUEST_EXCEPTION = new InvalidEmailException();

    @Override
    public ResourceConfig initializeResourceConfig() {

        FakeExceptionThrowerResource fakeExceptionThrowerResource = new FakeExceptionThrowerResource();

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(fakeExceptionThrowerResource).to(FakeExceptionThrowerResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003.commons").register(binder).register(new CommonExceptionMapper());
    }

    @Override
    public String getURI() {
        return String.format("http://localhost:%s/", PORT);
    }

    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public static class FakeExceptionThrowerResource {

        @GET
        @Path("/BadRequestCommonException")
        public Response throwBadRequestException() {
            throw BAD_REQUEST_EXCEPTION;
        }
    }
}