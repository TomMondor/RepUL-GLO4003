package ca.ulaval.glo4003.repul.small.cooking.api;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.api.MealKitEventHandler;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MealKitEventHandlerTest {
    private static final UniqueIdentifier A_MEAL_KIT_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory().generate();
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("Vachon");
    private static final LocalDate A_DATE = LocalDate.now();
    private static final MealKitConfirmedEvent A_MEAL_KIT_CONFIRMED_EVENT =
        new MealKitConfirmedEvent(A_MEAL_KIT_ID, A_SUBSCRIPTION_ID, AN_ACCOUNT_ID,
            A_MEAL_KIT_TYPE, A_DELIVERY_LOCATION_ID, A_DATE);
    private static final PickedUpCargoEvent A_PICKED_UP_CARGO_EVENT =
        new PickedUpCargoEvent(List.of(A_MEAL_KIT_ID));
    private MealKitEventHandler mealKitEventHandler;

    @Mock
    private CookingService cookingService;

    @BeforeEach
    public void createMealKitEventHandler() {
        this.mealKitEventHandler = new MealKitEventHandler(cookingService);
    }

    @Test
    public void whenHandlingMealKitConfirmedEvent_shouldCallCookingService() {
        mealKitEventHandler.handleMealKitConfirmedEvent(A_MEAL_KIT_CONFIRMED_EVENT);

        verify(cookingService, times(1)).receiveMealKitInKitchen(A_MEAL_KIT_ID, MealKitType.STANDARD, A_DATE);
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldCallCookingService() {
        mealKitEventHandler.handlePickedUpCargoEvent(A_PICKED_UP_CARGO_EVENT);

        verify(cookingService, times(1)).giveMealKitToDelivery(List.of(A_MEAL_KIT_ID));
    }
}
