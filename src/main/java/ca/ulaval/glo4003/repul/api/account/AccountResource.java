package ca.ulaval.glo4003.repul.api.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.identitymanagement.domain.Role;
import ca.ulaval.glo4003.identitymanagement.middleware.Roles;
import ca.ulaval.glo4003.identitymanagement.middleware.Secure;
import ca.ulaval.glo4003.repul.api.account.assembler.AccountResponseAssembler;
import ca.ulaval.glo4003.repul.api.account.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.api.account.response.AccountResponse;
import ca.ulaval.glo4003.repul.application.account.AccountService;
import ca.ulaval.glo4003.repul.application.account.query.RegistrationQuery;

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

@Path("/api/accounts")
public class AccountResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountResource.class);
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";

    private final AccountService accountService;
    private final AccountResponseAssembler accountResponseAssembler = new AccountResponseAssembler();

    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(@Valid RegistrationRequest registrationRequest) {
        LOGGER.info("Registering new account for {}", registrationRequest.idul);

        accountService.register(
            RegistrationQuery.from(registrationRequest.idul, registrationRequest.email, registrationRequest.password, registrationRequest.name,
                registrationRequest.birthdate, registrationRequest.gender));

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Secure
    @Roles(Role.CLIENT)
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    public Response me(@Context ContainerRequestContext requestContext) {
        UniqueIdentifier accountId = (UniqueIdentifier) requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);

        AccountResponse accountResponse = accountResponseAssembler.toAccountResponse(accountService.getAccount(accountId));

        return Response.status(Response.Status.OK).entity(accountResponse).build();
    }
}
