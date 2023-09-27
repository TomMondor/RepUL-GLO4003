package ca.ulaval.glo4003.identitymanagement.api;

import ca.ulaval.glo4003.identitymanagement.api.assembler.LoginResponseAssembler;
import ca.ulaval.glo4003.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.identitymanagement.application.AuthService;
import ca.ulaval.glo4003.identitymanagement.application.query.LoginQuery;
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
    private final LoginResponseAssembler loginResponseAssembler = new LoginResponseAssembler();

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Response login(@Valid LoginRequest loginRequest) {
        Token token = authService.login(LoginQuery.from(loginRequest.email, loginRequest.password));
        LoginResponse loginResponse = loginResponseAssembler.toLoginResponse(token);

        return Response.status(Response.Status.OK).entity(loginResponse).build();
    }
}
