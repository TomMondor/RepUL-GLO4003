package ca.ulaval.glo4003.repul.small.delivery.api;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.delivery.api.CargoResource;
import ca.ulaval.glo4003.repul.delivery.api.response.CargoResponse;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CargoResourceTest {
    private static final String ACCOUNT_ID_CONTEXT_PROPERTY = "uid";
    private static final String A_DELIVERY_PERSON_ID = UUID.randomUUID().toString();
    private static final String A_CARGO_ID = UUID.randomUUID().toString();
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_DELIVERY_PERSON_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_DELIVERY_PERSON_ID);
    private static final UniqueIdentifier A_CARGO_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_CARGO_ID);
    private static final UniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER = UniqueIdentifier.from(A_MEAL_KIT_ID);
    private CargoResource cargoResource;
    @Mock
    private DeliveryService deliveryService;
    @Mock
    private ContainerRequestContext requestContext;

    @BeforeEach
    public void createCargoResource() {
        cargoResource = new CargoResource(deliveryService);
    }

    @Test
    public void whenCancellingCargo_shouldReturnNoContent() {
        given(requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);

        Response response = cargoResource.cancelCargo(requestContext, A_CARGO_ID);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenCancellingCargo_shouldCancelCargo() {
        given(requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);

        cargoResource.cancelCargo(requestContext, A_CARGO_ID);

        verify(deliveryService).cancelCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenPickingUpCargo_shouldReturnNoContent() {
        given(requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);

        Response response = cargoResource.pickupCargo(requestContext, A_CARGO_ID);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenPickingUpCargo_shouldPickupCargo() {
        given(requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);

        cargoResource.pickupCargo(requestContext, A_CARGO_ID);

        verify(deliveryService).pickupCargo(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenConfirmingDelivery_shouldReturn204() {
        given(requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);

        Response response = cargoResource.confirmDelivery(requestContext, A_CARGO_ID, A_MEAL_KIT_ID);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenConfirmingDelivery_shouldConfirmDelivery() {
        given(requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);

        cargoResource.confirmDelivery(requestContext, A_CARGO_ID, A_MEAL_KIT_ID);

        verify(deliveryService).confirmDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenRecallingDelivery_shouldReturn200() {
        given(requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);

        Response response = cargoResource.recallDelivery(requestContext, A_CARGO_ID, A_MEAL_KIT_ID);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenRecallingDelivery_shouldRecallDelivery() {
        given(requestContext.getProperty(ACCOUNT_ID_CONTEXT_PROPERTY)).willReturn(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER);

        cargoResource.recallDelivery(requestContext, A_CARGO_ID, A_MEAL_KIT_ID);

        verify(deliveryService).recallDelivery(A_DELIVERY_PERSON_UNIQUE_IDENTIFIER, A_CARGO_UNIQUE_IDENTIFIER, A_MEAL_KIT_UNIQUE_IDENTIFIER);
    }

    @Test
    public void whenGettingCargosReadyToPickUp_shouldReturnOK() {
        CargosPayload cargosPayload = new CargosPayload(Collections.emptyList());
        when(deliveryService.getCargosReadyToPickUp()).thenReturn(cargosPayload);

        Response response = cargoResource.getCargosReadyToPickUp();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void whenGettingCargosReadyToPickUp_shouldReturnListOfCargoResponse() {
        CargosPayload cargosPayload = new CargosPayload(Collections.emptyList());
        when(deliveryService.getCargosReadyToPickUp()).thenReturn(cargosPayload);
        List<CargoResponse> expectedCargoResponses = Collections.emptyList();

        Response response = cargoResource.getCargosReadyToPickUp();

        assertEquals(expectedCargoResponses, response.getEntity());
    }
}