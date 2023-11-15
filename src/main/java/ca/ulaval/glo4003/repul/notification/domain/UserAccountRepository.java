package ca.ulaval.glo4003.repul.notification.domain;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface UserAccountRepository {
    void saveOrUpdate(UserAccount account);

    Optional<UserAccount> getAccountByMealKitId(UniqueIdentifier mealKitId);

    Optional<UserAccount> getAccountById(UniqueIdentifier accountId);
}
