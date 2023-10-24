package ca.ulaval.glo4003.repul.small.subscription.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemorySubscriptionRepositoryTest {
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    public void createSubscriptionRepository() {
        subscriptionRepository = new InMemorySubscriptionRepository();
    }

    @Test
    public void whenSavingSubscription_shouldSaveSubscription() {
        Subscription subscriptionToSave = new SubscriptionFixture().build();

        subscriptionRepository.saveOrUpdate(subscriptionToSave);

        assertTrue(subscriptionRepository.getById(subscriptionToSave.getSubscriptionId()).isPresent());
    }

    @Test
    public void givenExistingSubscription_whenGettingSubscriptionById_shouldReturnOptionalOfSubscription() {
        Subscription existingSubscription = new SubscriptionFixture().build();
        subscriptionRepository.saveOrUpdate(existingSubscription);

        Optional<Subscription> subscription = subscriptionRepository.getById(existingSubscription.getSubscriptionId());

        assertEquals(existingSubscription, subscription.get());
    }

    @Test
    public void givenNonexistentSubscription_whenGettingSubscriptionById_shouldReturnEmptyOptional() {
        Optional<Subscription> subscription = subscriptionRepository.getById(new UniqueIdentifier(UUID.randomUUID()));

        assertEquals(Optional.empty(), subscription);
    }

    @Test
    public void givenNoSubscriptionsForSubscriber_whenGettingSubscriptionsBySubscriberId_shouldReturnEmptyCollection() {
        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(new UniqueIdentifier(UUID.randomUUID()));

        assertTrue(subscriptions.isEmpty());
    }

    @Test
    public void givenSubscriptionsForSubscriber_whenGettingSubscriptionsBySubscriberId_shouldReturnMatchingSubscriptions() {
        UniqueIdentifier subscriberId = new UniqueIdentifier(UUID.randomUUID());
        Subscription subscription = new SubscriptionFixture().withSubscriberId(subscriberId).build();
        Subscription anotherSubscription = new SubscriptionFixture().withSubscriberId(subscriberId).build();
        subscriptionRepository.saveOrUpdate(subscription);
        subscriptionRepository.saveOrUpdate(anotherSubscription);

        List<Subscription> subscriptions = subscriptionRepository.getBySubscriberId(subscriberId);

        assertTrue(subscriptions.contains(subscription));
        assertTrue(subscriptions.contains(anotherSubscription));
        assertEquals(2, subscriptions.size());
    }

    @Test
    public void whenGettingSubscriptionByOrderId_shouldReturnMatchingSubscription() {
        UniqueIdentifier orderId = new UniqueIdentifier(UUID.randomUUID());
        Order order = new Order(orderId, MealKitType.STANDARD, LocalDate.now().plusDays(2));
        Subscription expectedSubscription = new SubscriptionFixture().withOrders(List.of(order)).build();
        subscriptionRepository.saveOrUpdate(expectedSubscription);

        Optional<Subscription> subscription = subscriptionRepository.getSubscriptionByOrderId(orderId);

        Assertions.assertEquals(subscription.get().findCurrentOrder().get(), order);
    }
}
