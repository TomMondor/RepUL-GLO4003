package ca.ulaval.glo4003.repul.api.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.api.account.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.application.account.AccountService;
import ca.ulaval.glo4003.repul.application.account.query.RegistrationQuery;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountResource.class);

    private final AccountService accountService;

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/register")
    public Response register(@Valid RegistrationRequest registrationRequest) {
        LOGGER.info("Registering new account for {}", registrationRequest.idul);

        accountService.register(
            RegistrationQuery.from(registrationRequest.idul, registrationRequest.email, registrationRequest.password, registrationRequest.name,
                registrationRequest.birthdate, registrationRequest.gender));

        return Response.status(Response.Status.CREATED).build();
    }
}
