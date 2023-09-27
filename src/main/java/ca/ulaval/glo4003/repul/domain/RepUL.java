package ca.ulaval.glo4003.repul.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.catalog.Semester;
import ca.ulaval.glo4003.repul.domain.exception.AccountNotFoundException;

public class RepUL {
    private final Catalog catalog;
    private final SubscriptionFactory subscriptionFactory;
    private final List<Account> accounts = new ArrayList<>();

    public RepUL(Catalog catalog, SubscriptionFactory subscriptionFactory) {
        this.catalog = catalog;
        this.subscriptionFactory = subscriptionFactory;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void createSubscription(UniqueIdentifier accountId, LocationId locationId, DayOfWeek dayOfWeek) {
        Account account = findAccountById(accountId);

        PickupLocation pickupLocation = catalog.getPickupLocation(locationId);
        Lunchbox lunchbox = catalog.getLunchbox();
        Semester semester = catalog.getCurrentSemester();
        Subscription subscription =
            subscriptionFactory.createSubscription(LocalDate.now(), semester.endDate(), pickupLocation, lunchbox, dayOfWeek, LunchboxType.STANDARD);

        account.addSubscription(subscription);
    }

    public void confirmNextLunchboxForSubscription(UniqueIdentifier accountId, UniqueIdentifier subscriptionId) {
        Account account = this.findAccountById(accountId);
        account.confirmNextLunchbox(subscriptionId);
    }

    public void declineNextLunchboxForSubscription(UniqueIdentifier accountId, UniqueIdentifier subscriptionId) {
        Account account = this.findAccountById(accountId);
        account.declineNextLunchbox(subscriptionId);
    }

    private Account findAccountById(UniqueIdentifier accountId) {
        return accounts.stream().filter(account -> account.getAccountId().equals(accountId)).findFirst().orElseThrow(AccountNotFoundException::new);
    }

    public List<PickupLocation> getPickupLocations() {
        return catalog.getPickupLocations();
    }
}
