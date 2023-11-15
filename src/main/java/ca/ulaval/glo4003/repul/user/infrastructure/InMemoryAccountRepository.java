package ca.ulaval.glo4003.repul.user.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.UserCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.user.domain.account.Account;
import ca.ulaval.glo4003.repul.user.domain.account.AccountRepository;
import ca.ulaval.glo4003.repul.user.domain.account.IDUL;

public class InMemoryAccountRepository implements AccountRepository {
    private final Map<IDUL, Account> accounts = new HashMap<>();

    @Override
    public void saveOrUpdate(Account account) {
        accounts.put(account.getIdul(), account);
    }

    @Override
    public Optional<Account> getByIDUL(IDUL idul) {
        return Optional.ofNullable(accounts.get(idul));
    }

    @Override
    public Optional<Account> getByAccountId(UniqueIdentifier accountId) {
        return accounts.values().stream().filter(account -> account.getAccountId().equals(accountId)).findFirst();
    }

    @Override
    public boolean isUserCardNumberUsed(UserCardNumber cardNumber) {
        return accounts.values().stream().anyMatch(account -> account.getCardNumber().isPresent() && account.getCardNumber().get().equals(cardNumber));
    }
}
