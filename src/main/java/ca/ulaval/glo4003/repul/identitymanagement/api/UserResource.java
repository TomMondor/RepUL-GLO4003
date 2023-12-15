package ca.ulaval.glo4003.repul.identitymanagement.api;

import java.net.URI;

import ca.ulaval.glo4003.repul.commons.api.UriFactory;
import ca.ulaval.glo4003.repul.commons.domain.DateParser;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.identitymanagement.api.assembler.UserResponseAssembler;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.LoginRequest;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.identitymanagement.api.response.LoginResponse;
import ca.ulaval.glo4003.repul.identitymanagement.application.UserService;
import ca.ulaval.glo4003.repul.identitymanagement.domain.Password;
import ca.ulaval.glo4003.repul.identitymanagement.domain.token.Token;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserService userService;
    private final UserResponseAssembler userResponseAssembler = new UserResponseAssembler();
    private final UriFactory uriFactory = new UriFactory();

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Path("/users:register")
    public Response register(@NotNull(message = "The body must not be null.") @Valid RegistrationRequest registrationRequest) {
        userService.register(new Email(registrationRequest.email),
            new Password(registrationRequest.password),
            new IDUL(registrationRequest.idul.toUpperCase()),
            new Name(registrationRequest.name),
            new Birthdate(DateParser.localDateFrom(registrationRequest.birthdate)),
            Gender.from(registrationRequest.gender));

        URI location = uriFactory.createURI("/api/users");
        return Response.created(location).build();
    }

    @POST
    @Path("/users:login")
    public Response login(@NotNull(message = "The body must not be null.") @Valid LoginRequest loginRequest) {
        Token token = userService.login(new Email(loginRequest.email),
            new Password(loginRequest.password));
        LoginResponse loginResponse = userResponseAssembler.toLoginResponse(token);

        return Response.ok(loginResponse).build();
    }
}
