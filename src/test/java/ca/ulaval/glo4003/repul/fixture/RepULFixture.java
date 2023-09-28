package ca.ulaval.glo4003.repul.fixture;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.domain.RepUL;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.account.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;

public class RepULFixture {
    private Catalog catalog;
    private List<Account> accounts;
    private SubscriptionFactory subscriptionFactory;

    public RepULFixture() {
        this.accounts = new ArrayList<>();
        this.catalog = new CatalogFixture().build();
        this.subscriptionFactory = new SubscriptionFactory(new UniqueIdentifierFactory());
    }

    public RepULFixture withAccounts(List<Account> accounts) {
        this.accounts = accounts;
        return this;
    }

    public RepULFixture addAccount(Account account) {
        this.accounts.add(account);
        return this;
    }

    public RepULFixture withCatalog(Catalog catalog) {
        this.catalog = catalog;
        return this;
    }

    public RepULFixture withSubscriptionFactory(SubscriptionFactory subscriptionFactory) {
        this.subscriptionFactory = subscriptionFactory;
        return this;
    }

    public RepUL build() {
        RepUL repUL = new RepUL(catalog, subscriptionFactory);
        accounts.forEach(repUL::addAccount);
        return repUL;
    }
}
