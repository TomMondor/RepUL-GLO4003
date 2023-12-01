package ca.ulaval.glo4003.repul.small.subscription.infrastructure;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.application.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class InMemorySubscriptionRepositoryTest {
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_VALID_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier ANOTHER_SUBSCRIPTION_VALID_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_VALID_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier AN_ORDER_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    @Mock
    private Subscription subscription;

    private InMemorySubscriptionRepository inMemorySubscriptionRepository;

    @BeforeEach
    public void createSubscriptionRepository() {
        inMemorySubscriptionRepository = new InMemorySubscriptionRepository();
    }

    @Test
    public void whenSavingSubscriptionAndGettingSubscriptionById_shouldReturnTheRightSubscription() {
        given(subscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);

        inMemorySubscriptionRepository.save(subscription);
        Subscription subscriptionFound = inMemorySubscriptionRepository.getById(A_SUBSCRIPTION_VALID_ID);

        assertEquals(subscription, subscriptionFound);
    }

    @Test
    public void givenNoSubscription_whenGettingSubscriptionById_shouldThrowSubscriptionNotFoundException() {
        assertThrows(SubscriptionNotFoundException.class, () -> inMemorySubscriptionRepository.getById(A_SUBSCRIPTION_VALID_ID));
    }

    @Test
    public void givenNoSubscriptionsForSubscriber_whenGettingSubscriptionsBySubscriberId_shouldReturnEmptyList() {
        List<Subscription> subscriptionsFound = inMemorySubscriptionRepository.getBySubscriberId(A_SUBSCRIBER_VALID_ID);

        assertTrue(subscriptionsFound.isEmpty());
    }

    @Test
    public void givenSubscriptionsForSubscriber_whenGettingSubscriptionsBySubscriberId_shouldReturnMatchingSubscriptions() {
        Subscription anotherSubscription = mock(Subscription.class);
        given(subscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);
        given(subscription.getSubscriberId()).willReturn(A_SUBSCRIBER_VALID_ID);
        given(anotherSubscription.getSubscriptionId()).willReturn(ANOTHER_SUBSCRIPTION_VALID_ID);
        given(anotherSubscription.getSubscriberId()).willReturn(A_SUBSCRIBER_VALID_ID);
        inMemorySubscriptionRepository.save(subscription);
        inMemorySubscriptionRepository.save(anotherSubscription);

        List<Subscription> subscriptionsFound = inMemorySubscriptionRepository.getBySubscriberId(A_SUBSCRIBER_VALID_ID);

        assertTrue(subscriptionsFound.contains(subscription));
        assertTrue(subscriptionsFound.contains(anotherSubscription));
        assertEquals(2, subscriptionsFound.size());
    }

    @Test
    public void givenNoSubscriptionWithSpecificOrder_whenGettingSubscriptionByOrderId_shouldThrowOrderNotFoundException() {
        assertThrows(OrderNotFoundException.class, () -> inMemorySubscriptionRepository.getSubscriptionByOrderId(AN_ORDER_ID));
    }

    @Test
    public void givenSubscriptionWithSpecificOrder_whenGettingSubscriptionByOrderId_shouldReturnMatchingSubscription() {
        given(subscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);
        Order anOrder = mock(Order.class);
        given(anOrder.getOrderId()).willReturn(AN_ORDER_ID);
        given(subscription.findCurrentOrder()).willReturn(Optional.of(anOrder));
        inMemorySubscriptionRepository.save(subscription);

        Subscription subscriptionFound = inMemorySubscriptionRepository.getSubscriptionByOrderId(AN_ORDER_ID);

        assertEquals(subscription, subscriptionFound);
    }
}
