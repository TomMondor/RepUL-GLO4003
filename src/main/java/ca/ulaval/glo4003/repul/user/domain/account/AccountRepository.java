package ca.ulaval.glo4003.repul.user.domain.account;

import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;

public interface AccountRepository {
    void saveOrUpdate(Account account);

    Optional<Account> getByIDUL(IDUL idul);

    Optional<Account> getByAccountId(UniqueIdentifier accountId);
}
