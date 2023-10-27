package ca.ulaval.glo4003.repul.user.api;

import java.net.URI;

import ca.ulaval.glo4003.repul.commons.api.UriFactory;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.user.api.assembler.UserResponseAssembler;
import ca.ulaval.glo4003.repul.user.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.user.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.user.api.response.AccountResponse;
import ca.ulaval.glo4003.repul.user.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.user.application.UserService;
import ca.ulaval.glo4003.repul.user.application.query.LoginQuery;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.token.Token;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.Secure;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";

    private final UserService userService;
    private final UserResponseAssembler userResponseAssembler = new UserResponseAssembler();
    private final UriFactory uriFactory = new UriFactory();

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/users:register")
    public Response register(@Valid RegistrationRequest registrationRequest) {
        userService.register(RegistrationQuery.from(registrationRequest.email, registrationRequest.password, registrationRequest.idul, registrationRequest.name,
            registrationRequest.birthdate, registrationRequest.gender));
        URI location = uriFactory.createURI("/api/users");

        return Response.created(location).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/users:login")
    public Response login(@Valid LoginRequest loginRequest) {
        Token token = userService.login(LoginQuery.from(loginRequest.email, loginRequest.password));
        LoginResponse loginResponse = userResponseAssembler.toLoginResponse(token);

        return Response.ok(loginResponse).build();
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Secure
    @Roles(Role.CLIENT)
    @Path("/users")
    public Response getMyAccount(@Context ContainerRequestContext context) {
        UniqueIdentifier accountId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        AccountResponse accountResponse = userResponseAssembler.toAccountResponse(userService.getAccount(accountId));

        return Response.ok(accountResponse).build();
    }
}
