package ca.ulaval.glo4003.repul.cooking.api;

import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.api.assembler.MealKitsResponseAssembler;
import ca.ulaval.glo4003.repul.cooking.api.request.ConfirmCookedRequest;
import ca.ulaval.glo4003.repul.cooking.api.request.SelectionRequest;
import ca.ulaval.glo4003.repul.cooking.api.response.SelectionResponse;
import ca.ulaval.glo4003.repul.cooking.api.response.ToCookResponse;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.application.payload.MealKitsPayload;
import ca.ulaval.glo4003.repul.user.domain.identitymanagment.Role;
import ca.ulaval.glo4003.repul.user.middleware.Roles;
import ca.ulaval.glo4003.repul.user.middleware.Secure;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
public class MealKitResource {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";

    private final CookingService cookingService;
    private final MealKitsResponseAssembler mealKitsResponseAssembler = new MealKitsResponseAssembler();

    public MealKitResource(CookingService cookingService) {
        this.cookingService = cookingService;
    }

    @GET
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:toCook")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMealKitsToCook() {
        MealKitsPayload mealKitsPayload = cookingService.getMealKitsToCook();

        ToCookResponse toCookResponse = mealKitsResponseAssembler.toToCookResponse(mealKitsPayload);

        return Response.ok(toCookResponse).build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:select")
    public Response select(@Context ContainerRequestContext context, SelectionRequest request) {
        UniqueIdentifier cookId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
        cookingService.select(cookId, UniqueIdentifier.from(request.ids));

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:getSelection")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelection(@Context ContainerRequestContext context) {
        UniqueIdentifier cookId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
        List<UniqueIdentifier> mealKitIds = cookingService.getSelection(cookId);

        SelectionResponse selectionResponse = mealKitsResponseAssembler.toSelectionResponse(mealKitIds);

        return Response.ok(selectionResponse).build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits/{mealKitId}:cancelSelection")
    public Response cancelOneSelection(@Context ContainerRequestContext context, @PathParam("mealKitId") String mealKitId) {
        UniqueIdentifier cookId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
        cookingService.cancelOneSelected(cookId, UniqueIdentifier.from(mealKitId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:cancelSelection")
    public Response cancelAllSelection(@Context ContainerRequestContext context) {
        UniqueIdentifier cookId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
        cookingService.cancelAllSelected(cookId);

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits/{mealKitId}:confirmCooked")
    public Response confirmCooked(@Context ContainerRequestContext context, @PathParam("mealKitId") String mealKitId) {
        UniqueIdentifier cookId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
        cookingService.confirmCooked(cookId, UniqueIdentifier.from(mealKitId));

        return Response.noContent().build();
    }

    @POST
    @Secure
    @Roles(Role.COOK)
    @Path("/mealKits:confirmCooked")
    public Response confirmCooked(@Context ContainerRequestContext context, ConfirmCookedRequest request) {
        UniqueIdentifier cookId = (UniqueIdentifier) context.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY);
        cookingService.confirmCooked(cookId, UniqueIdentifier.from(request.ids));

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
