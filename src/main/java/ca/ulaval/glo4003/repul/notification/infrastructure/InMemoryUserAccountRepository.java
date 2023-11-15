package ca.ulaval.glo4003.repul.notification.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.notification.domain.UserAccount;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;

public class InMemoryUserAccountRepository implements UserAccountRepository {
    private final Map<UniqueIdentifier, UserAccount> accounts = new HashMap<>();

    @Override
    public Optional<UserAccount> getAccountByMealKitId(UniqueIdentifier mealKitId) {
        for (UserAccount account: accounts.values()) {
            if (account.getMealKitIds().stream().anyMatch(mealKit -> mealKit.equals(mealKitId))) {
                return Optional.of(account);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserAccount> getAccountById(UniqueIdentifier accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public void saveOrUpdate(UserAccount userAccount) {
        accounts.put(userAccount.getAccountId(), userAccount);
    }
}
