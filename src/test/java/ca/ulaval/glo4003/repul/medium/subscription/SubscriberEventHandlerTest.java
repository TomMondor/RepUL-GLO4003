package ca.ulaval.glo4003.repul.medium.subscription;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.identitymanagement.application.event.UserCreatedEvent;
import ca.ulaval.glo4003.repul.subscription.api.SubscriberEventHandler;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrdersFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SubscriberEventHandlerTest {
    private static final UniqueIdentifier A_USER_ID = new UniqueIdentifierFactory<>(UniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.now().minusYears(18));
    private static final Gender A_GENDER = Gender.MAN;
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2));
    private static final DeliveryLocationId A_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DeliveryLocationId ANOTHER_LOCATION_ID = DeliveryLocationId.PEPS;

    private SubscriberEventHandler subscriberEventHandler;
    private RepULEventBus eventBus;
    private SubscriberService subscriberService;
    private SubscriberRepository subscriberRepository;

    @BeforeEach
    public void createUserEventHandler() {
        subscriberRepository = new InMemorySubscriberRepository();
        eventBus = new GuavaEventBus();
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        OrdersFactory ordersFactory = new OrdersFactory(mealKitUniqueIdentifierFactory);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class), ordersFactory, List.of(CURRENT_SEMESTER),
                List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriberService =
            new SubscriberService(subscriberRepository, new SubscriberFactory(), subscriptionFactory, eventBus, new LogPaymentService(), ordersFactory);
        subscriberEventHandler = new SubscriberEventHandler(subscriberService);
        eventBus.register(subscriberEventHandler);
    }

    @Test
    public void whenHandlingUserCreatedEvent_shouldAddSubscriberToRepository() {
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(A_USER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);

        eventBus.publish(userCreatedEvent);

        SubscriberUniqueIdentifier subscriberId = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(A_USER_ID);
        assertDoesNotThrow(() -> subscriberRepository.getById(subscriberId));
    }
}
