package ca.ulaval.glo4003.repul.small.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.fixture.AccountFixture;
import ca.ulaval.glo4003.repul.fixture.CatalogFixture;
import ca.ulaval.glo4003.repul.fixture.IngredientFixture;
import ca.ulaval.glo4003.repul.fixture.LunchboxFixture;
import ca.ulaval.glo4003.repul.fixture.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.RecipeFixture;
import ca.ulaval.glo4003.repul.fixture.RepULFixture;
import ca.ulaval.glo4003.repul.fixture.SubscriptionFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepULTest {
    private static final UniqueIdentifier AN_INVALID_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final LocationId A_LOCATION_ID = new LocationId("VACHON");
    private static final DayOfWeek A_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final LocationId ANOTHER_LOCATION_ID = new LocationId("MYRAND");
    private static final DayOfWeek ANOTHER_DAY_OF_WEEK = DayOfWeek.TUESDAY;
    private static final LunchboxType A_LUNCHBOX_TYPE = LunchboxType.STANDARD;

    @Test
    public void givenNoSubscriptions_whenGettingSubscriptions_shouldReturnEmptyCollection() {
        RepUL repUL = new RepULFixture().build();
        repUL.addAccount(new AccountFixture().withAccountId(AN_ACCOUNT_ID).build());

        List<Subscription> subscriptions = repUL.getSubscriptions(AN_ACCOUNT_ID);

        assertEquals(0, subscriptions.size());
    }

    @Test
    public void givenSubscriptions_whenGettingSubscriptions_shouldReturnMatchingSubscriptions() {
        RepUL repUL = new RepULFixture().withCatalog(
            new CatalogFixture().withPickupLocations(List.of(new PickupLocation(A_LOCATION_ID, "", 13), new PickupLocation(ANOTHER_LOCATION_ID, "", 44)))
                .build()).build();
        repUL.addAccount(new AccountFixture().withAccountId(AN_ACCOUNT_ID).build());
        repUL.createSubscription(AN_ACCOUNT_ID, A_LOCATION_ID, A_DAY_OF_WEEK, A_LUNCHBOX_TYPE);
        repUL.createSubscription(AN_ACCOUNT_ID, ANOTHER_LOCATION_ID, ANOTHER_DAY_OF_WEEK, A_LUNCHBOX_TYPE);

        List<Subscription> subscriptions = repUL.getSubscriptions(AN_ACCOUNT_ID);

        assertEquals(2, subscriptions.size());
        assertEquals(A_LOCATION_ID, subscriptions.get(0).getPickupLocation().getLocationId());
        assertEquals(A_DAY_OF_WEEK, subscriptions.get(0).getFrequency().dayOfWeek());
        assertEquals(A_LUNCHBOX_TYPE, subscriptions.get(0).getLunchboxType());
        assertEquals(ANOTHER_LOCATION_ID, subscriptions.get(1).getPickupLocation().getLocationId());
        assertEquals(ANOTHER_DAY_OF_WEEK, subscriptions.get(1).getFrequency().dayOfWeek());
        assertEquals(A_LUNCHBOX_TYPE, subscriptions.get(1).getLunchboxType());
    }

    @Test
    public void givenALunchboxForTomorrow_whenGetLunchboxesToCook_shouldReturnLunboxToDeliverTomorrow() {
        Lunchbox lunchboxForTomorrow = new LunchboxFixture().build();
        Recipe recipe = new RecipeFixture()
            .withIngredients(List.of(new IngredientFixture()
                .withName("apple")
                    .withQuantity(new Quantity(5.00, "")).build())).build();
        Lunchbox lunchboxForToday = new LunchboxFixture().withRecipes(List.of(recipe)).build();
        Order orderToDeliverTomorrow = new OrderFixture()
            .withOrderStatus(OrderStatus.TO_COOK)
            .withDeliveryDate(LocalDate.now().plusDays(1))
            .withLunchbox(lunchboxForTomorrow).build();
        Order orderToDeliverToday = new OrderFixture()
            .withOrderStatus(OrderStatus.TO_COOK)
            .withDeliveryDate(LocalDate.now())
            .withLunchbox(lunchboxForToday).build();
        Subscription subscription = new SubscriptionFixture()
            .withOrders(List.of(orderToDeliverTomorrow, orderToDeliverToday))
            .build();
        Account account = new AccountFixture().withSubscriptions(List.of(subscription)).build();
        RepUL repUL = new RepULFixture().addAccount(account).build();

        List<Lunchbox> lunchboxes = repUL.getLunchboxesToCook();

        assertEquals(1, lunchboxes.size());
        assertEquals(lunchboxForTomorrow, lunchboxes.get(0));
    }

    @Test
    public void givenNoLunchboxForTomorrow_whenGetLunchboxesToCook_ShouldReturnEmptyList() {
        Lunchbox lunchboxForToday = new LunchboxFixture().build();
        Order orderToDeliverToday = new OrderFixture()
            .withOrderStatus(OrderStatus.TO_COOK)
            .withDeliveryDate(LocalDate.now().plusDays(2))
            .withLunchbox(lunchboxForToday).build();
        Subscription subscription = new SubscriptionFixture()
            .withOrders(List.of(orderToDeliverToday))
            .build();
        Account account = new AccountFixture().withSubscriptions(List.of(subscription)).build();
        RepUL repUL = new RepULFixture().addAccount(account).build();

        List<Lunchbox> lunchboxes = repUL.getLunchboxesToCook();

        assertEquals(0, lunchboxes.size());
    }

    @Test
    public void givenInexistentAccount_whenFindAccountById_shouldThrowAccountNotFoundException() {
        RepUL repUL = new RepULFixture().build();

        assertThrows(AccountNotFoundException.class, () -> {
            repUL.findAccountById(AN_INVALID_ACCOUNT_ID);
        });
    }

    @Test
    public void givenExistentAccount_whenFindAccountById_shouldReturnAccount() {
        Account expectedAccount = new AccountFixture().withAccountId(AN_ACCOUNT_ID).build();
        RepUL repUL = new RepULFixture().addAccount(expectedAccount).build();

        Account actualAccount = repUL.findAccountById(AN_ACCOUNT_ID);

        assertEquals(expectedAccount, actualAccount);
    }

    @Test
    public void whenGettingAccountCurrentOrders_shouldGetCurrentOrders() {
        Order order = new OrderFixture().build();
        Subscription subscription = new SubscriptionFixture().addOrder(order).build();
        Account account = new AccountFixture().addSubscription(subscription).withAccountId(AN_ACCOUNT_ID).build();
        RepUL repUL = new RepULFixture().addAccount(account).build();

        List<Order> orders = repUL.getAccountCurrentOrders(AN_ACCOUNT_ID);

        assertEquals(List.of(order), orders);
    }
}
