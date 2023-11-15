package ca.ulaval.glo4003.repul.small.delivery.api.assembler;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.KitchenLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.delivery.api.assembler.CargosResponseAssembler;
import ca.ulaval.glo4003.repul.delivery.api.response.CargoResponse;
import ca.ulaval.glo4003.repul.delivery.api.response.MealKitResponse;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargoPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.CargosPayload;
import ca.ulaval.glo4003.repul.delivery.application.payload.MealKitPayload;
import ca.ulaval.glo4003.repul.delivery.domain.KitchenLocation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CargosResponseAssemblerTest {

    private static final DeliveryLocationId DELIVERY_LOCATION_ID = new DeliveryLocationId("VANDRY");
    private static final String A_LOCKER_NUMBER = "1";
    private static final String ANOTHER_LOCKER_NUMBER = "2";
    private static final String A_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_MEAL_KIT_UNIQUE_IDENTIFIER = new UniqueIdentifierFactory().generateFrom(A_MEAL_KIT_ID);
    private static final MealKitPayload A_MEAL_KIT_PAYLOAD = new MealKitPayload(A_MEAL_KIT_UNIQUE_IDENTIFIER, DELIVERY_LOCATION_ID.value(), A_LOCKER_NUMBER);
    private static final String ANOTHER_MEAL_KIT_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier ANOTHER_MEAL_KIT_UNIQUE_IDENTIFIER = new UniqueIdentifierFactory().generateFrom(ANOTHER_MEAL_KIT_ID);
    private static final MealKitPayload ANOTHER_MEAL_KIT_PAYLOAD =
        new MealKitPayload(ANOTHER_MEAL_KIT_UNIQUE_IDENTIFIER, DELIVERY_LOCATION_ID.value(), ANOTHER_LOCKER_NUMBER);
    private static final String A_CARGO_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier A_CARGO_UNIQUE_IDENTIFIER = new UniqueIdentifierFactory().generateFrom(A_CARGO_ID);
    private static final String ANOTHER_CARGO_ID = UUID.randomUUID().toString();
    private static final UniqueIdentifier ANOTHER_CARGO_UNIQUE_IDENTIFIER = new UniqueIdentifierFactory().generateFrom(ANOTHER_CARGO_ID);
    private static final KitchenLocationId KITCHEN_LOCATION_ID = new KitchenLocationId("VACHON");
    private static final String KITCHEN_LOCATION_NAME = "Pavillon Alexandre-Vachon";
    private static final KitchenLocation KITCHEN_LOCATION = new KitchenLocation(KITCHEN_LOCATION_ID, KITCHEN_LOCATION_NAME);
    private static final CargoPayload A_CARGO_PAYLOAD =
        new CargoPayload(A_CARGO_UNIQUE_IDENTIFIER, KITCHEN_LOCATION.getLocationId().value(), List.of(A_MEAL_KIT_PAYLOAD));
    private static final CargoPayload ANOTHER_CARGO_PAYLOAD =
        new CargoPayload(ANOTHER_CARGO_UNIQUE_IDENTIFIER, KITCHEN_LOCATION.getLocationId().value(), List.of(ANOTHER_MEAL_KIT_PAYLOAD));
    private static final CargosPayload A_CARGOS_PAYLOAD = new CargosPayload(List.of(A_CARGO_PAYLOAD, ANOTHER_CARGO_PAYLOAD));

    private CargosResponseAssembler cargosResponseAssembler;

    @BeforeEach
    public void createAssembler() {
        cargosResponseAssembler = new CargosResponseAssembler();
    }

    @Test
    public void givenCargosPayload_whenToCargosResponse_shouldReturnListOfCargoResponse() {
        CargoResponse expectedFirstCargoResponse = new CargoResponse(A_CARGO_ID, KITCHEN_LOCATION_ID.value(),
            List.of(new MealKitResponse(A_MEAL_KIT_ID, DELIVERY_LOCATION_ID.value(), A_LOCKER_NUMBER)));
        CargoResponse expectedSecondCargoResponse = new CargoResponse(ANOTHER_CARGO_ID, KITCHEN_LOCATION_ID.value(),
            List.of(new MealKitResponse(ANOTHER_MEAL_KIT_ID, DELIVERY_LOCATION_ID.value(), ANOTHER_LOCKER_NUMBER)));
        List<CargoResponse> cargoResponses = cargosResponseAssembler.toCargosResponse(A_CARGOS_PAYLOAD);

        assertEquals(2, cargoResponses.size());
        assertEquals(expectedFirstCargoResponse, cargoResponses.get(0));
        assertEquals(expectedSecondCargoResponse, cargoResponses.get(1));
    }
}
