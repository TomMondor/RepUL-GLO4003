package ca.ulaval.glo4003.identitymanagement.api;

import ca.ulaval.glo4003.identitymanagement.api.assembler.RegistrationResponseAssembler;
import ca.ulaval.glo4003.identitymanagement.api.request.RegistrationRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.RegistrationResponse;
import ca.ulaval.glo4003.identitymanagement.application.AuthService;
import ca.ulaval.glo4003.identitymanagement.application.query.RegistrationQuery;
import ca.ulaval.glo4003.identitymanagement.domain.token.Token;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    private final AuthService authService;
    private final RegistrationResponseAssembler registrationResponseAssembler = new RegistrationResponseAssembler();

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response register(@Valid RegistrationRequest registrationRequest) {
        Token token = authService.register(RegistrationQuery.from(registrationRequest.email, registrationRequest.password));
        RegistrationResponse registrationResponse = registrationResponseAssembler.toRegistrationResponse(token);

        return Response.status(Response.Status.CREATED).entity(registrationResponse).build();
    }
}
