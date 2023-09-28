package ca.ulaval.glo4003.repul.small.domain;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.exception.AccountNotFoundException;
import ca.ulaval.glo4003.repul.fixture.AccountFixture;
import ca.ulaval.glo4003.repul.fixture.CatalogFixture;
import ca.ulaval.glo4003.repul.fixture.RepULFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepULTest {
    private static final UniqueIdentifier AN_INVALID_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final LocationId A_LOCATION_ID = new LocationId("VACHON");
    private static final DayOfWeek A_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final LocationId ANOTHER_LOCATION_ID = new LocationId("MYRAND");
    private static final DayOfWeek ANOTHER_DAY_OF_WEEK = DayOfWeek.TUESDAY;

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
        repUL.createSubscription(AN_ACCOUNT_ID, A_LOCATION_ID, A_DAY_OF_WEEK);
        repUL.createSubscription(AN_ACCOUNT_ID, ANOTHER_LOCATION_ID, ANOTHER_DAY_OF_WEEK);

        List<Subscription> subscriptions = repUL.getSubscriptions(AN_ACCOUNT_ID);

        assertEquals(2, subscriptions.size());
        assertEquals(A_LOCATION_ID, subscriptions.get(0).getPickupLocation().getLocationId());
        assertEquals(A_DAY_OF_WEEK, subscriptions.get(0).getFrequency().dayOfWeek());
        assertEquals(ANOTHER_LOCATION_ID, subscriptions.get(1).getPickupLocation().getLocationId());
        assertEquals(ANOTHER_DAY_OF_WEEK, subscriptions.get(1).getFrequency().dayOfWeek());
    }

    @Test
    public void givenInexistantAccount_whenFindAccountById_shouldThrowAccountNotFoundException() {
        RepUL repUL = new RepULFixture().build();

        assertThrows(AccountNotFoundException.class, () -> {
            repUL.findAccountById(AN_INVALID_ACCOUNT_ID);
        });
    }

    @Test
    public void givenExistantAccount_whenFindAccountById_shouldReturnAccount() {
        RepUL repUL = new RepULFixture().addAccount(new AccountFixture().withAccountId(AN_ACCOUNT_ID).build()).build();

        repUL.findAccountById(AN_ACCOUNT_ID);
    }
}
