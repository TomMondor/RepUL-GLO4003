package ca.ulaval.glo4003.repul.small.subscription.infrastructure;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class InMemorySubscriptionRepositoryTest {
    private static final UniqueIdentifier A_SUBSCRIPTION_VALID_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier ANOTHER_SUBSCRIPTION_VALID_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier A_SUBSCRIBER_VALID_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier AN_ORDER_ID = new UniqueIdentifierFactory().generate();
    @Mock
    private Subscription subscription;

    private InMemorySubscriptionRepository inMemorySubscriptionRepository;

    @BeforeEach
    public void createSubscriptionRepository() {
        inMemorySubscriptionRepository = new InMemorySubscriptionRepository();
    }

    @Test
    public void whenSavingSubscriptionAndGettingSubscriptionById_shouldReturnOptionalOfRightSubscription() {
        given(subscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);

        inMemorySubscriptionRepository.save(subscription);
        Optional<Subscription> subscriptionFound = inMemorySubscriptionRepository.getById(A_SUBSCRIPTION_VALID_ID);

        assertEquals(Optional.of(subscription), subscriptionFound);
    }

    @Test
    public void givenNoSubscription_whenGettingSubscriptionById_shouldReturnOptionalOfEmpty() {
        Optional<Subscription> subscriptionFound = inMemorySubscriptionRepository.getById(A_SUBSCRIPTION_VALID_ID);

        assertTrue(subscriptionFound.isEmpty());
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
    public void givenNoSubscriptionWithSpecificOrder_whenGettingSubscriptionByOrderId_shouldReturnOptionalOfEmpty() {
        Optional<Subscription> subscriptionFound = inMemorySubscriptionRepository.getSubscriptionByOrderId(AN_ORDER_ID);

        assertTrue(subscriptionFound.isEmpty());
    }

    @Test
    public void givenSubscriptionWithSpecificOrder_whenGettingSubscriptionByOrderId_shouldReturnMatchingSubscription() {
        given(subscription.getSubscriptionId()).willReturn(A_SUBSCRIPTION_VALID_ID);
        Order anOrder = mock(Order.class);
        given(anOrder.getOrderId()).willReturn(AN_ORDER_ID);
        given(subscription.findCurrentOrder()).willReturn(Optional.of(anOrder));
        inMemorySubscriptionRepository.save(subscription);

        Optional<Subscription> subscriptionFound = inMemorySubscriptionRepository.getSubscriptionByOrderId(AN_ORDER_ID);

        assertEquals(Optional.of(subscription), subscriptionFound);
    }
}
