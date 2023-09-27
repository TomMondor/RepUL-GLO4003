package ca.ulaval.glo4003.repul.domain;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.Account;
import ca.ulaval.glo4003.repul.domain.catalog.Catalog;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.exception.AccountNotFoundException;

public class RepUL {
    private final Catalog catalog;
    private final List<Account> accounts = new ArrayList<>();

    public RepUL(Catalog catalog) {
        this.catalog = catalog;
    }

    public void addAccount(Account account) {
        accounts.add(account);
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
