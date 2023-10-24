package ca.ulaval.glo4003.repul.notification.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.notification.domain.Account;
import ca.ulaval.glo4003.repul.notification.domain.AccountRepository;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<UniqueIdentifier, Account> accounts = new HashMap<>();

    @Override
    public void saveOrUpdate(Account account) {
        accounts.put(account.accountId(), account);
    }

    @Override
    public Optional<Account> getByAccountId(UniqueIdentifier accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
}
